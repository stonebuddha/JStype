package concrete;

import ir.*;

import java.util.function.BiFunction;

/**
 * Created by wayne on 15/10/28.
 */
public class Eval {

    public static Domains.BValue eval(IRExp exp, Domains.Env env, Domains.Store store, Domains.Scratchpad pad) {
        BiFunction<Domains.Address, Domains.Address, Domains.Bool> instance = new BiFunction<Domains.Address, Domains.Address, Domains.Bool>() {
            @Override
            public Domains.Bool apply(Domains.Address address, Domains.Address address2) {
                return null;
            }
        };

        BiFunction<Domains.Str, Domains.Address, Domains.Bool> find = new BiFunction<Domains.Str, Domains.Address, Domains.Bool>() {
            @Override
            public Domains.Bool apply(Domains.Str str, Domains.Address address) {
                return null;
            }
        };

        IRExpVisitor innerEval = new IRExpVisitor() {
            @Override
            public Object forNum(IRNum irNum) {
                Double n = irNum.v;
                return new Domains.Num(n);
            }
            @Override
            public Object forBool(IRBool irBool) {
                Boolean b = irBool.v;
                return new Domains.Bool(b);
            }
            @Override
            public Object forStr(IRStr irStr) {
                String str = irStr.v;
                return new Domains.Str(str);
            }
            @Override
            public Object forUndef(IRUndef irUndef) {
                return new Domains.Undef();
            }
            @Override
            public Object forNull(IRNull irNull) {
                return new Domains.Null();
            }
            @Override
            public Object forPVar(IRPVar irPVar) {
                return store.apply(env.apply(irPVar));
            }
            @Override
            public Object forScratch(IRScratch irScratch) {
                return pad.apply(irScratch);
            }
            @Override
            public Object forBinop(IRBinop irBinop) {
                Bop op = irBinop.op;
                IRExp e1 = irBinop.e1;
                IRExp e2 = irBinop.e2;
                Domains.BValue bv1 = (Domains.BValue)e1.accept(this);
                Domains.BValue bv2 = (Domains.BValue)e2.accept(this);

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
                        return instance.apply((Domains.Address)bv1, (Domains.Address)bv2);
                    } else {
                        return Domains.Bool.False;
                    }
                } else if (op.equals(Bop.In)) {
                    if (bv1 instanceof Domains.Str && bv2 instanceof Domains.Address) {
                        return find.apply((Domains.Str)bv1, (Domains.Address)bv2);
                    } else {
                        throw new RuntimeException("translator reneged");
                    }
                } else {
                    throw new RuntimeException("translator reneged");
                }
            }
            @Override
            public Object forUnop(IRUnop irUnop) {
                Uop op = irUnop.op;
                IRExp e = irUnop.e;
                Domains.BValue bv = (Domains.BValue)e.accept(this);

                if (op.equals(Uop.Negate)) {
                    return bv.negate();
                } else if (op.equals(Uop.Not)) {
                    return bv.not();
                } else if (op.equals(Uop.LogicalNot)) {
                    return bv.logicalNot();
                } else if (op.equals(Uop.TypeOf)) {

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

        return (Domains.BValue)exp.accept(innerEval);
    }
}
