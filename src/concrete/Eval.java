package concrete;

import fj.F2;
import fj.data.Option;
import ir.*;

/**
 * Created by wayne on 15/10/28.
 */
public class Eval {

    public static Domains.BValue eval(final IRExp exp, final Domains.Env env, final Domains.Store store, final Domains.Scratchpad pad) {
        final Utils.Recursive<F2<Domains.Address, Domains.Address, Domains.Bool>> instance = new Utils.Recursive<>();
        instance.func = (a1, a2) -> {
            final Domains.BValue proto = store.getObj(a1).getProto();
            if (proto instanceof Domains.Address) {
                final Domains.Address a = (Domains.Address)proto;
                if (a.equals(a2)) {
                    return Domains.Bool.True;
                } else {
                    return instance.func.f(a, a2);
                }
            } else if (proto.equals(Domains.Null)) {
                return Domains.Bool.False;
            } else {
                throw new RuntimeException("undefined");
            }
        };

        final Utils.Recursive<F2<Domains.Str, Domains.Address, Domains.Bool>> find = new Utils.Recursive<>();
        find.func = (str, a) -> {
            final Domains.Object obj = store.getObj(a);
            final Option<Domains.BValue> bv = obj.apply(str);
            if (bv.isSome()) {
                return Domains.Bool.True;
            } else {
                final Domains.BValue proto = obj.getProto();
                if (proto instanceof Domains.Address) {
                    return find.func.f(str, (Domains.Address)proto);
                } else {
                    return Domains.Bool.False;
                }
            }
        };

        final IRExpVisitor<Domains.BValue> innerEval = new IRExpVisitor<Domains.BValue>() {
            @Override
            public Domains.BValue forNum(final IRNum irNum) {
                final Double n = irNum.v;
                return new Domains.Num(n);
            }
            @Override
            public Domains.BValue forBool(final IRBool irBool) {
                final Boolean b = irBool.v;
                return Domains.Bool.apply(b);
            }
            @Override
            public Domains.BValue forStr(final IRStr irStr) {
                final String str = irStr.v;
                return new Domains.Str(str);
            }
            @Override
            public Domains.BValue forUndef(final IRUndef irUndef) {
                return Domains.Undef;
            }
            @Override
            public Domains.BValue forNull(final IRNull irNull) {
                return Domains.Null;
            }
            @Override
            public Domains.BValue forPVar(final IRPVar irPVar) {
                return store.apply(env.apply(irPVar));
            }
            @Override
            public Domains.BValue forScratch(final IRScratch irScratch) {
                return pad.apply(irScratch);
            }
            @Override
            public Domains.BValue forBinop(final IRBinop irBinop) {
                final Bop op = irBinop.op;
                final IRExp e1 = irBinop.e1;
                final IRExp e2 = irBinop.e2;
                final Domains.BValue bv1 = e1.accept(this);
                final Domains.BValue bv2 = e2.accept(this);

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
                    return bv1.strictEqual(bv2);
                } else if (op.equals(Bop.NonStrictEqual)) {
                    return bv1.nonStrictEqual(bv2);
                } else if (op.equals(Bop.Access)) {
                    if (bv1 instanceof Domains.Address && bv2 instanceof Domains.Str) {
                        return Utils.lookup(store.getObj((Domains.Address)bv1), (Domains.Str)bv2, store);
                    } else {
                        throw new RuntimeException("translator reneged");
                    }
                } else if (op.equals(Bop.InstanceOf)) {
                    if (bv1 instanceof Domains.Address && bv2 instanceof Domains.Address) {
                        return instance.func.f((Domains.Address)bv1, (Domains.Address)bv2);
                    } else {
                        return Domains.Bool.False;
                    }
                } else if (op.equals(Bop.In)) {
                    if (bv1 instanceof Domains.Str && bv2 instanceof Domains.Address) {
                        return find.func.f((Domains.Str)bv1, (Domains.Address)bv2);
                    } else {
                        throw new RuntimeException("translator reneged");
                    }
                } else {
                    throw new RuntimeException("translator reneged");
                }
            }
            @Override
            public Domains.BValue forUnop(final IRUnop irUnop) {
                final Uop op = irUnop.op;
                final IRExp e = irUnop.e;
                final Domains.BValue bv = e.accept(this);

                if (op.equals(Uop.Negate)) {
                    return bv.negate();
                } else if (op.equals(Uop.Not)) {
                    return bv.not();
                } else if (op.equals(Uop.LogicalNot)) {
                    return bv.logicalNot();
                } else if (op.equals(Uop.TypeOf)) {
                    final Domains.BValueVisitor<Domains.BValue> typeOf = new Domains.BValueVisitor<Domains.BValue>() {
                        @Override
                        public Domains.BValue forNum(final Domains.Num bNum) {
                            return new Domains.Str("number");
                        }
                        @Override
                        public Domains.BValue forBool(final Domains.Bool bBool) {
                            return new Domains.Str("boolean");
                        }
                        @Override
                        public Domains.BValue forStr(final Domains.Str bStr) {
                            return new Domains.Str("string");
                        }
                        @Override
                        public Domains.BValue forNull(final Domains.BValue bNull) {
                            return new Domains.Str("object");
                        }
                        @Override
                        public Domains.BValue forUndef(final Domains.BValue bUndef) {
                            return new Domains.Str("undefined");
                        }
                        @Override
                        public Domains.BValue forAddress(final Domains.Address bAddress) {
                            final Domains.Object obj = store.getObj(bAddress);
                            if (obj.getCode().isSome()) {
                                return new Domains.Str("function");
                            } else {
                                return new Domains.Str("object");
                            }
                        }
                    };
                    return bv.accept(typeOf);
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
