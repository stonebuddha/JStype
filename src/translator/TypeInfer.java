package translator;

import fj.F;
import fj.data.Option;
import ir.*;

/**
 * Created by wayne on 15/11/12.
 */
public class TypeInfer {

    interface JSType {
        JSType merge(JSType type);
        Option<Boolean> isPrim();
    }

    public static JSType Top = new JSType() {
        @Override
        public JSType merge(JSType type) {
            return Top;
        }

        @Override
        public Option<Boolean> isPrim() {
            return Option.none();
        }
    };

    public static JSType AddrType = new JSType() {
        @Override
        public JSType merge(JSType type) {
            if (type.equals(AddrType)) {
                return AddrType;
            } else {
                return Top;
            }
        }

        @Override
        public Option<Boolean> isPrim() {
            return Option.some(false);
        }
    };

    public static abstract class Prim implements JSType {
        @Override
        public Option<Boolean> isPrim() {
            return Option.some(true);
        }
    }

    public static Prim OnlyPrim = new Prim() {
        @Override
        public JSType merge(JSType type) {
            if (type instanceof Prim) {
                return OnlyPrim;
            } else {
                return Top;
            }
        }
    };

    public static Prim NumType = new Prim() {
        @Override
        public JSType merge(JSType type) {
            if (type.equals(NumType)) {
                return NumType;
            } else if (type instanceof Prim) {
                return OnlyPrim;
            } else {
                return Top;
            }
        }
    };

    public static Prim BoolType = new Prim() {
        @Override
        public JSType merge(JSType type) {
            if (type.equals(BoolType)) {
                return BoolType;
            } else if (type instanceof Prim) {
                return OnlyPrim;
            } else {
                return Top;
            }
        }
    };

    public static Prim StrType = new Prim() {
        @Override
        public JSType merge(JSType type) {
            if (type.equals(StrType)) {
                return StrType;
            } else if (type instanceof Prim) {
                return OnlyPrim;
            } else {
                return Top;
            }
        }
    };

    public static Prim NullType = new Prim() {
        @Override
        public JSType merge(JSType type) {
            if (type.equals(NullType)) {
                return NullType;
            } else if (type instanceof Prim) {
                return OnlyPrim;
            } else {
                return Top;
            }
        }
    };

    public static Prim UndefType = new Prim() {
        @Override
        public JSType merge(JSType type) {
            if (type.equals(UndefType)) {
                return UndefType;
            } else if (type instanceof Prim) {
                return OnlyPrim;
            } else {
                return Top;
            }
        }
    };

    public static JSType typeof(IRExp e) {
        return typeof(e, x -> Top);
    }

    public static JSType typeof(IRExp e, F<IRVar, JSType> mapping) {
        IRExpVisitor v = new IRExpVisitor() {
            @Override
            public Object forNum(IRNum irNum) {
                return NumType;
            }
            @Override
            public Object forBool(IRBool irBool) {
                return BoolType;
            }
            @Override
            public Object forStr(IRStr irStr) {
                return StrType;
            }
            @Override
            public Object forUndef(IRUndef irUndef) {
                return UndefType;
            }
            @Override
            public Object forNull(IRNull irNull) {
                return NullType;
            }
            @Override
            public Object forPVar(IRPVar irPVar) {
                return mapping.f(irPVar);
            }
            @Override
            public Object forScratch(IRScratch irScratch) {
                return mapping.f(irScratch);
            }
            @Override
            public Object forBinop(IRBinop irBinop) {
                Bop op = irBinop.op;
                if (op.equals(Bop.Plus) || op.equals(Bop.Minus) || op.equals(Bop.Times)
                        || op.equals(Bop.Divide) || op.equals(Bop.Mod) || op.equals(Bop.SHL)
                        || op.equals(Bop.SAR) || op.equals(Bop.SHR) || op.equals(Bop.And)
                        || op.equals(Bop.Or) || op.equals(Bop.Xor)) {
                    return NumType;
                } else if (op.equals(Bop.StrConcat)) {
                    return StrType;
                } else if (op.equals(Bop.Access)) {
                    return Top;
                } else {
                    return BoolType;
                }
            }
            @Override
            public Object forUnop(IRUnop irUnop) {
                Uop op = irUnop.op;
                if (op.equals(Uop.Negate) || op.equals(Uop.Not) || op.equals(Uop.ToNum)) {
                    return NumType;
                } else if (op.equals(Uop.LogicalNot) || op.equals(Uop.ToBool) || op.equals(Uop.IsPrim)) {
                    return BoolType;
                } else {
                    return StrType;
                }
            }
        };

        return (JSType)e.accept(v);
    }
}
