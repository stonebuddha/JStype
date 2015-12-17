package analysis.init;

import analysis.Domains;
import analysis.Utils;
import fj.F;
import fj.data.List;
import immutable.FHashMap;
import immutable.FHashSet;
import ir.JSClass;

/**
 * Created by wayne on 15/12/7.
 */
public class InitMath {
    public static final Domains.Object Math_Obj = InitUtils.createInitObj(
            FHashMap.build(
                    "E", Domains.Num.inject(Domains.Num.NReal),
                    "LN10", Domains.Num.inject(Domains.Num.NReal),
                    "LN2", Domains.Num.inject(Domains.Num.NReal),
                    "LOG2E", Domains.Num.inject(Domains.Num.NReal),
                    "LOG10E", Domains.Num.inject(Domains.Num.NReal),
                    "PI", Domains.Num.inject(Domains.Num.NReal),
                    "SQRT1_2", Domains.Num.inject(Domains.Num.NReal),
                    "SQRT2", Domains.Num.inject(Domains.Num.NReal),
                    "abs", Domains.AddressSpace.Address.inject(Init.Math_abs_Addr),
                    "acos", Domains.AddressSpace.Address.inject(Init.Math_acos_Addr),
                    "asin", Domains.AddressSpace.Address.inject(Init.Math_asin_Addr),
                    "atan", Domains.AddressSpace.Address.inject(Init.Math_atan_Addr),
                    "atan2", Domains.AddressSpace.Address.inject(Init.Math_atan2_Addr),
                    "ceil", Domains.AddressSpace.Address.inject(Init.Math_ceil_Addr),
                    "cos", Domains.AddressSpace.Address.inject(Init.Math_cos_Addr),
                    "exp", Domains.AddressSpace.Address.inject(Init.Math_exp_Addr),
                    "floor", Domains.AddressSpace.Address.inject(Init.Math_floor_Addr),
                    "log", Domains.AddressSpace.Address.inject(Init.Math_log_Addr),
                    "max", Domains.AddressSpace.Address.inject(Init.Math_max_Addr),
                    "min", Domains.AddressSpace.Address.inject(Init.Math_min_Addr),
                    "pow", Domains.AddressSpace.Address.inject(Init.Math_pow_Addr),
                    "random", Domains.AddressSpace.Address.inject(Init.Math_random_Addr),
                    "round", Domains.AddressSpace.Address.inject(Init.Math_round_Addr),
                    "sin", Domains.AddressSpace.Address.inject(Init.Math_sin_Addr),
                    "sqrt", Domains.AddressSpace.Address.inject(Init.Math_sqrt_Addr),
                    "tan", Domains.AddressSpace.Address.inject(Init.Math_tan_Addr)
            ),
            FHashMap.build(
                    Utils.Fields.classname, JSClass.CMath_Obj
            )
    );

    public static Domains.Object easyMathFunctionObj(F<Double, Double> mathFun) {
        return InitUtils.pureFunctionObj(unaryMathSig, bvs -> {
            if (bvs.length() == 2) {
                Domains.BValue bv = bvs.tail().head();
                assert bv.defNum() : "type conversion should guarantee math functions only get nums";
                Domains.BValue ret;
                if (bv.n instanceof Domains.NConst) {
                    ret = Domains.Num.inject(Domains.Num.alpha(((Domains.NConst)bv.n).d));
                } else {
                    ret = Domains.Num.inject(bv.n);
                }
                return FHashSet.build(ret);
            } else {
                throw new RuntimeException("arity mismatch!");
            }
        });
    }

    public static final InitUtils.Sig unaryMathSig = InitUtils.ezSig(InitUtils.NoConversion, List.list(InitUtils.NumberHint));

    public static final Domains.Object approxUnaryMathFunctionObj = InitUtils.constFunctionObj(unaryMathSig, Domains.Num.inject(Domains.Num.NTop));
    public static final Domains.Object approxBinaryMathFunctionObj = InitUtils.constFunctionObj(InitUtils.ezSig(InitUtils.NoConversion, List.list(InitUtils.NumberHint, InitUtils.NumberHint)), Domains.Num.inject(Domains.Num.NTop));
    public static final Domains.Object variadicMathFunctionObj = InitUtils.constFunctionObj(new InitUtils.VarSig(InitUtils.NoConversion, InitUtils.NumberHint, 2), Domains.Num.inject(Domains.Num.NTop));

    public static final Domains.Object Math_abs_Obj = easyMathFunctionObj(Math::abs);
    public static final Domains.Object Math_acos_Obj = approxUnaryMathFunctionObj;
    public static final Domains.Object Math_asin_Obj = approxUnaryMathFunctionObj;
    public static final Domains.Object Math_atan_Obj = approxUnaryMathFunctionObj;
    public static final Domains.Object Math_atan2_Obj = approxBinaryMathFunctionObj;
    public static final Domains.Object Math_ceil_Obj = easyMathFunctionObj(Math::ceil);
    public static final Domains.Object Math_cos_Obj = approxUnaryMathFunctionObj;
    public static final Domains.Object Math_exp_Obj = approxBinaryMathFunctionObj;
    public static final Domains.Object Math_floor_Obj = easyMathFunctionObj(Math::floor);
    public static final Domains.Object Math_log_Obj = approxUnaryMathFunctionObj;
    public static final Domains.Object Math_max_Obj = variadicMathFunctionObj;
    public static final Domains.Object Math_min_Obj = variadicMathFunctionObj;
    public static final Domains.Object Math_pow_Obj = approxBinaryMathFunctionObj;
    public static final Domains.Object Math_random_Obj = InitUtils.constFunctionObj(
            InitUtils.ezSig(InitUtils.NoConversion, List.list()),
            Domains.Num.inject(Domains.Num.NReal)
    );
    public static final Domains.Object Math_round_Obj = easyMathFunctionObj(d -> {
        if (d.isInfinite()) {
            return d;
        } else if (d.isNaN()) {
            return d;
        } else {
            return Math.round(d) * 1.0;
        }
    });
    public static final Domains.Object Math_sin_Obj = approxUnaryMathFunctionObj;
    public static final Domains.Object Math_sqrt_Obj = approxUnaryMathFunctionObj;
    public static final Domains.Object Math_tan_Obj = approxUnaryMathFunctionObj;

}
