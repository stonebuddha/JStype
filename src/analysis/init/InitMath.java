package analysis.init;

import analysis.Domains;
import analysis.Utils;
import immutable.FHashMap;
import ir.JSClass;

/**
 * Created by wayne on 15/12/7.
 */
public class InitMath {
    public static final Domains.Object Math_Obj = InitUtils.createInitObj(
            FHashMap.build(
                    "E", Domains.Num.inject(Domains.NReal),
                    "LN10", Domains.Num.inject(Domains.NReal),
                    "LN2", Domains.Num.inject(Domains.NReal),
                    "LOG2E", Domains.Num.inject(Domains.NReal),
                    "LOG10E", Domains.Num.inject(Domains.NReal),
                    "PI", Domains.Num.inject(Domains.NReal),
                    "SQRT1_2", Domains.Num.inject(Domains.NReal),
                    "SQRT2", Domains.Num.inject(Domains.NReal),
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

    public static final Domains.Object Math_abs_Obj = InitUtils.unimplemented("Math.abs");
    public static final Domains.Object Math_acos_Obj = InitUtils.unimplemented("Math.acos");
    public static final Domains.Object Math_asin_Obj = InitUtils.unimplemented("Math.asin");
    public static final Domains.Object Math_atan_Obj = InitUtils.unimplemented("Math.atan");
    public static final Domains.Object Math_atan2_Obj = InitUtils.unimplemented("Math.atan2");
    public static final Domains.Object Math_ceil_Obj = InitUtils.unimplemented("Math.ceil");
    public static final Domains.Object Math_cos_Obj = InitUtils.unimplemented("Math.cos");
    public static final Domains.Object Math_exp_Obj = InitUtils.unimplemented("Math.exp");
    public static final Domains.Object Math_floor_Obj = InitUtils.unimplemented("Math.floor");
    public static final Domains.Object Math_log_Obj = InitUtils.unimplemented("Math.log");
    public static final Domains.Object Math_max_Obj = InitUtils.unimplemented("Math.max");
    public static final Domains.Object Math_min_Obj = InitUtils.unimplemented("Math.min");
    public static final Domains.Object Math_pow_Obj = InitUtils.unimplemented("Math.pow");
    public static final Domains.Object Math_random_Obj = InitUtils.unimplemented("Math.random");
    public static final Domains.Object Math_round_Obj = InitUtils.unimplemented("Math.round");
    public static final Domains.Object Math_sin_Obj = InitUtils.unimplemented("Math.sin");
    public static final Domains.Object Math_sqrt_Obj = InitUtils.unimplemented("Math.sqrt");
    public static final Domains.Object Math_tan_Obj = InitUtils.unimplemented("Math.tan");

}
