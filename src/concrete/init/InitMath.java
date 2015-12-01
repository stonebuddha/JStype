package concrete.init;

import concrete.Utils;
import concrete.Domains;
import immutable.FHashMap;
import ir.JSClass;

/**
 * Created by Hwhitetooth on 15/11/11.
 */
public class InitMath {
    public static Domains.Object Math_Obj = InitUtils.createObj(
            FHashMap.build(
                    new Domains.Str("E"), new Domains.Num(2.7182818284590452354),
                    new Domains.Str("LN10"), new Domains.Num(2.302585092994046),
                    new Domains.Str("LN2"), new Domains.Num(0.6931471805599453),
                    new Domains.Str("LOG2E"), new Domains.Num(1.4426950408889634),
                    new Domains.Str("LOG10E"), new Domains.Num(0.4342944819032518),
                    new Domains.Str("PI"), new Domains.Num(3.1415926535897932),
                    new Domains.Str("SQRT1_2"), new Domains.Num(0.7071067811865476),
                    new Domains.Str("SQRT2"), new Domains.Num(1.4142135623730951),
                    new Domains.Str("abs"), Init.Math_abs_Addr,
                    new Domains.Str("acos"), Init.Math_acos_Addr,
                    new Domains.Str("asin"), Init.Math_asin_Addr,
                    new Domains.Str("atan"), Init.Math_atan_Addr,
                    new Domains.Str("atan2"), Init.Math_atan2_Addr,
                    new Domains.Str("ceil"), Init.Math_ceil_Addr,
                    new Domains.Str("cos"), Init.Math_cos_Addr,
                    new Domains.Str("exp"), Init.Math_exp_Addr,
                    new Domains.Str("floor"), Init.Math_floor_Addr,
                    new Domains.Str("log"), Init.Math_log_Addr,
                    new Domains.Str("max"), Init.Math_max_Addr,
                    new Domains.Str("min"), Init.Math_min_Addr,
                    new Domains.Str("pow"), Init.Math_pow_Addr,
                    new Domains.Str("random"), Init.Math_random_Addr,
                    new Domains.Str("round"), Init.Math_round_Addr,
                    new Domains.Str("sin"), Init.Math_sin_Addr,
                    new Domains.Str("sqrt"), Init.Math_sqrt_Addr,
                    new Domains.Str("tan"), Init.Math_tan_Addr),
            FHashMap.build(Utils.Fields.classname, JSClass.CMath_Obj)
    );

    public static Domains.Object Math_abs_Obj = InitUtils.makeMath(Math::abs);
    public static Domains.Object Math_acos_Obj = InitUtils.makeMath(Math::acos);
    public static Domains.Object Math_asin_Obj = InitUtils.makeMath(Math::asin);
    public static Domains.Object Math_atan_Obj = InitUtils.makeMath(Math::atan);
    public static Domains.Object Math_atan2_Obj = InitUtils.approx_num;
    public static Domains.Object Math_ceil_Obj = InitUtils.makeMath(Math::ceil);
    public static Domains.Object Math_cos_Obj = InitUtils.makeMath(Math::cos);
    public static Domains.Object Math_exp_Obj = InitUtils.makeMath(Math::exp);
    public static Domains.Object Math_floor_Obj = InitUtils.makeMath(Math::floor);
    public static Domains.Object Math_log_Obj = InitUtils.makeMath(Math::log);
    public static Domains.Object Math_max_Obj = InitUtils.approx_num;
    public static Domains.Object Math_min_Obj = InitUtils.approx_num;
    public static Domains.Object Math_pow_Obj = InitUtils.approx_num;
    public static Domains.Object Math_random_Obj = InitUtils.approx_num;
    public static Domains.Object Math_round_Obj = InitUtils.makeMath(x -> 1.0 * Math.round(x));
    public static Domains.Object Math_sin_Obj = InitUtils.makeMath(Math::sin);
    public static Domains.Object Math_sqrt_Obj = InitUtils.makeMath(Math::sqrt);
    public static Domains.Object Math_tan_Obj = InitUtils.makeMath(Math::tan);
}
