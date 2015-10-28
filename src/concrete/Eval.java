package concrete;

import ir.*;

/**
 * Created by wayne on 15/10/28.
 */
public class Eval {

    public static Domains.BValue eval(IRExp exp, Domains.Env env, Domains.Store store, Domains.Scratchpad pad) {
        class InnerEvalV implements IRExpVisitor {
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

                } else if (op.equals(Bop.LessEqual)) {

                } else if (op.equals(Bop.And)) {

                } else if (op.equals(Bop.Or)) {

                } else if (op.equals(Bop.Xor)) {

                } else if (op.equals(Bop.LogicalAnd)) {

                } else if (op.equals(Bop.LogicalOr)) {

                } else if (op.equals(Bop.StrConcat)) {

                } else if (op.equals(Bop.StrLessThan)) {

                } else if (op.equals(Bop.StrLessEqual)) {

                } else if (op.equals(Bop.StrictEqual)) {

                } else if (op.equals(Bop.NonStrictEqual)) {

                } else if (op.equals(Bop.Access)) {

                } else if (op.equals(Bop.InstanceOf)) {

                } else if (op.equals(Bop.In)) {

                } else {
                    throw new RuntimeException("translator reneged");
                }
            }
            @Override
            public Object forUnop(IRUnop irUnop) {
                Uop op = irUnop.op;
                IRExp e = irUnop.e;
                Domains.BValue bv = (Domains.BValue)e.accept(this);

                if (op.equals(Uop.Negation)) {

                } else if (op.equals(Uop.Not)) {

                } else if (op.equals(Uop.LogicalNot)) {

                } else if (op.equals(Uop.TypeOf)) {

                } else if (op.equals(Uop.ToBool)) {

                } else if (op.equals(Uop.IsPrim)) {

                } else if (op.equals(Uop.ToStr)) {

                } else if (op.equals(Uop.ToNum)) {

                } else {
                    throw new RuntimeException("translator reneged");
                }
            }
        }

        InnerEvalV innerEval = new InnerEvalV();
        return (Domains.BValue)exp.accept(innerEval);
    }
}
