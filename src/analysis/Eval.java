package analysis;

import fj.*;
import immutable.FHashSet;
import ir.*;

/**
 * Created by wayne on 15/11/7.
 */
public class Eval {

    public static Domains.BValue eval(IRExp exp, Domains.Env env, Domains.Store store, Domains.Scratchpad pad) {
        Utils.Recursive<F3<FHashSet<Domains.AddressSpace.Address>, FHashSet<Domains.AddressSpace.Address>, FHashSet<Domains.AddressSpace.Address>, Domains.Bool>> instance = new Utils.Recursive<>();
        instance.func = (as1, as2, seen) -> {
            FHashSet<Domains.AddressSpace.Address> as1Unseen = as1.minus(seen);
            if (as1Unseen.isEmpty() || as2.isEmpty()) {
                return Domains.Bool.Bot;
            } else {
                Domains.BValue bv = as1Unseen.toList().foldLeft((acc, a) -> acc.merge(store.getObj(a).getProto()), Domains.BValue.Bot);
                FHashSet<Domains.AddressSpace.Address> protos = bv.as;
                Boolean isNull = (bv.nil.equals(Domains.Null.Top));

                if (!isNull && protos.size() == 1 && protos.equals(as2) && store.isStrong(protos.toList().head())) {
                    return Domains.Bool.True;
                } else if (isNull && protos.isEmpty()) {
                    return Domains.Bool.False;
                } else {
                    Boolean overlap = !protos.intersect(as2).isEmpty();

                    if (isNull && overlap) {
                        return Domains.Bool.Top;
                    } else if (!isNull && overlap) {
                        return Domains.Bool.True.merge(instance.func.f(protos, as2, seen.union(as1Unseen)));
                    } else if (isNull && !protos.isEmpty()) {
                        return Domains.Bool.False.merge(instance.func.f(protos, as2, seen.union(as1Unseen)));
                    } else {
                        return instance.func.f(protos, as2, seen.union(as1Unseen));
                    }
                }
            }
        };

        Utils.Recursive<F2<Domains.Object, Domains.Str, Domains.Bool>> find = new Utils.Recursive<>();
        find.func = (o, str) -> {
            if (o.defField(str)) {
                return Domains.Bool.True;
            } else {
                Domains.BValue bv = o.getProto();
                Boolean notField = o.defNotField(str);
                Boolean maybeField = !notField;

                if (notField && bv.as.isEmpty()) {
                    return Domains.Bool.False;
                } else if (maybeField && bv.nil.equals(Domains.Null.Top)) {
                    return Domains.Bool.Top;
                } else {
                    Domains.Bool proto = bv.as.toList().foldLeft((acc, a) -> {
                        if (acc.equals(Domains.Bool.Top)) {
                            return Domains.Bool.Top;
                        } else {
                            return acc.merge(find.func.f(store.getObj(a), str));
                        }
                    }, Domains.Bool.Bot);

                    if (maybeField && bv.nil.equals(Domains.Null.Bot)) {
                        return Domains.Bool.True.merge(proto);
                    } else if (notField && !bv.as.isEmpty() && bv.nil.equals(Domains.Null.Top)) {
                        return Domains.Bool.False.merge(proto);
                    } else {
                        return proto;
                    }
                }
            }
        };

        F<Domains.BValue, Domains.Str> typeof = bv ->
            bv.types.toList().foldLeft((acc, dom) -> {
                if (dom.equals(Domains.DNum)) {
                    return acc.merge(Domains.Str.alpha("number"));
                } else if (dom.equals(Domains.DBool)) {
                    return acc.merge(Domains.Str.alpha("boolean"));
                } else if (dom.equals(Domains.DStr)) {
                    return acc.merge(Domains.Str.alpha("string"));
                } else if (dom.equals(Domains.DNull)) {
                    return acc.merge(Domains.Str.alpha("object"));
                } else if (dom.equals(Domains.DUndef)) {
                    return acc.merge(Domains.Str.alpha("undefined"));
                } else {
                    Domains.Str otype;
                    if (!bv.as.filter(a -> store.getObj(a).getCode().isEmpty()).isEmpty()) {
                        otype = Domains.Str.alpha("object");
                    } else {
                        otype = Domains.Str.Bot;
                    }
                    Domains.Str ftype;
                    if (!bv.as.filter(a -> !store.getObj(a).getCode().isEmpty()).isEmpty()) {
                        ftype = Domains.Str.alpha("function");
                    } else {
                        ftype = Domains.Str.Bot;
                    }
                    return acc.merge(otype).merge(ftype);
                }
            }, Domains.Str.Bot);

        F2<Domains.BValue, Domains.BValue, Domains.BValue> strictEqual = (bv1, bv2) -> {
            if (bv1.isBot() || bv2.isBot()) {
                return Domains.BValue.Bot;
            } else {
                FHashSet<Domains.Domain> bothDom = bv1.types.intersect(bv2.types);
                if (bothDom.isEmpty()) {
                    return Domains.Bool.FalseBV;
                } else {
                    Domains.Bool diff;
                    if (bv1.types.size() == 1 && bv1.types.equals(bv2.types)) {
                        diff = Domains.Bool.Bot;
                    } else {
                        diff = Domains.Bool.False;
                    }

                    return Domains.Bool.inject(bothDom.toList().foldLeft((acc, dom) -> {
                        if (acc.equals(Domains.Bool.Top)) {
                            return Domains.Bool.Top;
                        } else {
                            Domains.Bool tmp;
                            if (dom.equals(Domains.DNum)) {
                                tmp = bv1.n.strictEqual(bv2.n);
                            } else if (dom.equals(Domains.DBool)) {
                                tmp = bv1.b.strictEqual(bv2.b);
                            } else if (dom.equals(Domains.DStr)) {
                                tmp = bv1.str.strictEqual(bv2.str);
                            } else if (dom.equals(Domains.DAddr)) {
                                if (bv1.as.intersect(bv2.as).isEmpty()) {
                                    tmp = Domains.Bool.False;
                                } else if (bv1.as.size() == 1 && bv1.as.equals(bv2.as) && store.isStrong(bv1.as.toList().head())) {
                                    tmp = Domains.Bool.True;
                                } else {
                                    tmp = Domains.Bool.Top;
                                }
                            } else {
                                tmp = Domains.Bool.True;
                            }
                            return acc.merge(tmp);
                        }
                    }, diff));
                }
            }
        };

        F2<Domains.BValue, Domains.BValue, Domains.BValue> nonStrictEqual = (bv1, bv2) -> {
            Domains.BValue equiv = strictEqual.f(bv1, bv2);
            if (equiv.b.equals(Domains.Bool.False)) {
                Domains.BValue case12, case3, case4;
                if ((bv1.nil.equals(Domains.Null.Top) && bv2.undef.equals(Domains.Undef.Top)) ||
                        (bv1.undef.equals(Domains.Undef.Top) && bv2.nil.equals(Domains.Null.Top))) {
                    case12 = Domains.Bool.TrueBV;
                } else {
                    case12 = Domains.BValue.Bot;
                }
                if (!bv1.n.equals(Domains.Num.Bot) && !bv2.str.equals(Domains.Str.Bot)) {
                    case3 = strictEqual.f(Domains.Num.inject(bv1.n), Domains.Str.inject(bv2.str).toNum());
                } else {
                    case3 = Domains.BValue.Bot;
                }
                if (!bv1.str.equals(Domains.Str.Bot) && !bv2.n.equals(Domains.Num.Bot)) {
                    case4 = strictEqual.f(Domains.Str.inject(bv1.str).toNum(), Domains.Num.inject(bv2.n));
                } else {
                    case4 = Domains.BValue.Bot;
                }
                return equiv.merge(case12).merge(case3).merge(case4);
            } else {
                return equiv;
            }
        };

        IRExpVisitor<Domains.BValue> innerEval = new IRExpVisitor<Domains.BValue>() {
            @Override
            public Domains.BValue forNum(IRNum irNum) {
                Double n = irNum.v;
                return Domains.Num.inject(Domains.Num.alpha(n));
            }
            @Override
            public Domains.BValue forBool(IRBool irBool) {
                Boolean b = irBool.v;
                return Domains.Bool.inject(Domains.Bool.alpha(b));
            }
            @Override
            public Domains.BValue forStr(IRStr irStr) {
                String str = irStr.v;
                return Domains.Str.inject(Domains.Str.alpha(str));
            }
            @Override
            public Domains.BValue forUndef(IRUndef irUndef) {
                return Domains.Undef.BV;
            }
            @Override
            public Domains.BValue forNull(IRNull irNull) {
                return Domains.Null.BV;
            }
            @Override
            public Domains.BValue forPVar(IRPVar irPVar) {
                return store.applyAll(env.apply(irPVar).some());
            }
            @Override
            public Domains.BValue forScratch(IRScratch irScratch) {
                return pad.apply(irScratch);
            }
            @Override
            public Domains.BValue forBinop(IRBinop irBinop) {
                Bop op = irBinop.op;
                IRExp e1 = irBinop.e1;
                IRExp e2 = irBinop.e2;
                Domains.BValue bv1 = e1.accept(this);
                Domains.BValue bv2 = e2.accept(this);

                if (op.equals(Bop.Plus)) {
                    return bv1.plus(bv2);
                } else if (op.equals(Bop.Minus)) {
                    return bv1.minus(bv2);
                } else if (op.equals(Bop.Times)) {
                    return bv1.times(bv2);
                } else if (op.equals(Bop.Divide)) {
                    return bv1.divide(bv2);
                } else if (op.equals(Bop.Mod)) {
                    return bv1.mod(bv2);
                } else if (op.equals(Bop.SHL)) {
                    return bv1.shl(bv2);
                } else if (op.equals(Bop.SAR)) {
                    return bv1.sar(bv2);
                } else if (op.equals(Bop.SHR)) {
                    return bv1.shr(bv2);
                } else if (op.equals(Bop.LessThan)) {
                    return bv1.lessThan(bv2);
                } else if (op.equals(Bop.LessEqual)) {
                    return bv1.lessEqual(bv2);
                } else if (op.equals(Bop.And)) {
                    return bv1.and(bv2);
                } else if (op.equals(Bop.Or)) {
                    return bv1.or(bv2);
                } else if (op.equals(Bop.Xor)) {
                    return bv1.xor(bv2);
                } else if (op.equals(Bop.LogicalAnd)) {
                    return bv1.logicalAnd(bv2);
                } else if (op.equals(Bop.LogicalOr)) {
                    return bv1.logicalOr(bv2);
                } else if (op.equals(Bop.StrConcat)) {
                    return bv1.strConcat(bv2);
                } else if (op.equals(Bop.StrLessThan)) {
                    return bv1.strLessThan(bv2);
                } else if (op.equals(Bop.StrLessEqual)) {
                    return bv1.strLessEqual(bv2);
                } else if (op.equals(Bop.StrictEqual)) {
                    return strictEqual.f(bv1, bv2);
                } else if (op.equals(Bop.NonStrictEqual)) {
                    return nonStrictEqual.f(bv1, bv2);
                } else if (op.equals(Bop.Access)) {
                    return Utils.lookup(bv1.as, bv2.str, store);
                } else if (op.equals(Bop.InstanceOf)) {
                    if (bv1.isBot() || bv2.isBot()) {
                        return Domains.BValue.Bot;
                    } else {
                        return Domains.Bool.inject(
                                (!bv1.defAddr() ? Domains.Bool.False : Domains.Bool.Bot).merge(
                                        instance.func.f(bv1.as, bv2.as, FHashSet.empty())
                                )
                        );
                    }
                } else if (op.equals(Bop.In)) {
                    return Domains.Bool.inject(
                            bv2.as.toList().foldLeft((acc, a) -> {
                                if (acc.equals(Domains.Bool.Top)) {
                                    return Domains.Bool.Top;
                                } else {
                                    return acc.merge(find.func.f(store.getObj(a), bv1.str));
                                }
                            }, Domains.Bool.Bot)
                    );
                } else {
                    throw new RuntimeException("translator reneged");
                }
            }
            @Override
            public Domains.BValue forUnop(IRUnop irUnop) {
                Uop op = irUnop.op;
                IRExp e = irUnop.e;
                Domains.BValue bv = e.accept(this);

                if (op.equals(Uop.Negate)) {
                    return bv.negate();
                } else if (op.equals(Uop.Not)) {
                    return bv.not();
                } else if (op.equals(Uop.LogicalNot)) {
                    return bv.logicalNot();
                } else if (op.equals(Uop.TypeOf)) {
                    return Domains.Str.inject(typeof.f(bv));
                } else if (op.equals(Uop.ToBool)) {
                    return bv.toBool();
                } else if (op.equals(Uop.IsPrim)) {
                    return bv.isPrim();
                } else if (op.equals(Uop.ToStr)) {
                    return bv.toStr();
                } else if (op.equals(Uop.ToNum)) {
                    return bv.toNum();
                } else {
                    throw new RuntimeException("translator reneged");
                }
            }
        };

        return exp.accept(innerEval);
    }
}
