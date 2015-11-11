package concrete.init;

import concrete.Utils;
import concrete.Domains;
import fj.Ord;
import fj.P;
import fj.data.TreeMap;
import ir.JSClass;

/**
 * Created by Hwhitetooth on 15/11/11.
 */
public class InitMath {
    public static Domains.Object Math_Obj = InitUtils.createObj(
            TreeMap.treeMap(Ord.hashEqualsOrd(),
                    P.p(new Domains.Str("E"), new Domains.Num(2.7182818284590452354)),
                    P.p(new Domains.Str("LN10"), new Domains.Num(2.302585092994046)),
                    P.p(new Domains.Str("LN2"), new Domains.Num(0.6931471805599453)),
                    P.p(new Domains.Str("LOG2E"), new Domains.Num(1.4426950408889634)),
                    P.p(new Domains.Str("LOG10E"), new Domains.Num(0.4342944819032518)),
                    P.p(new Domains.Str("PI"), new Domains.Num(3.1415926535897932)),
                    P.p(new Domains.Str("SQRT1_2"), new Domains.Num(0.7071067811865476)),
                    P.p(new Domains.Str("SQRT2"), new Domains.Num(1.4142135623730951)),
                    P.p(new Domains.Str("abs"), Init.Math_abs_Addr),
                    P.p(new Domains.Str("acos"), Init.Math_acos_Addr),
                    P.p(new Domains.Str("asin"), Init.Math_asin_Addr),
                    P.p(new Domains.Str("atan"), Init.Math_atan_Addr),
                    P.p(new Domains.Str("atan2"), Init.Math_atan2_Addr),
                    P.p(new Domains.Str("ceil"), Init.Math_ceil_Addr),
                    P.p(new Domains.Str("cos"), Init.Math_cos_Addr),
                    P.p(new Domains.Str("exp"), Init.Math_exp_Addr),
                    P.p(new Domains.Str("floor"), Init.Math_floor_Addr),
                    P.p(new Domains.Str("log"), Init.Math_log_Addr),
                    P.p(new Domains.Str("max"), Init.Math_max_Addr),
                    P.p(new Domains.Str("min"), Init.Math_min_Addr),
                    P.p(new Domains.Str("pow"), Init.Math_pow_Addr),
                    P.p(new Domains.Str("random"), Init.Math_random_Addr),
                    P.p(new Domains.Str("round"), Init.Math_round_Addr),
                    P.p(new Domains.Str("sin"), Init.Math_sin_Addr),
                    P.p(new Domains.Str("sqrt"), Init.Math_sqrt_Addr),
                    P.p(new Domains.Str("tan"), Init.Math_tan_Addr)),
            TreeMap.treeMap(Ord.hashEqualsOrd(), P.p(Utils.Fields.classname, JSClass.CMath_Obj))
    );

    //TODO
}
