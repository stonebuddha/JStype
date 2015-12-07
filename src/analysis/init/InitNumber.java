package analysis.init;

import analysis.Domains;
import analysis.Utils;
import immutable.FHashMap;
import ir.JSClass;

/**
 * Created by wayne on 15/12/7.
 */
public class InitNumber {
    public static final Domains.Object Number_Obj = InitUtils.createInitFunctionObj(
            new Domains.Native(
                    (selfAddr, argArrayAddr, x, env, store, pad, ks, tr) -> {
                        return null; // TODO
                    }
            ),
            FHashMap.build(
                    "prototype", Domains.AddressSpace.Address.inject(Init.Number_prototype_Addr),
                    "length", Domains.Num.inject(Domains.Num.alpha(1.0)),
                    "MAX_VALUE", Domains.Num.inject(Domains.Num.NReal),
                    "MIN_VALUE", Domains.Num.inject(Domains.Num.NReal),
                    "NEGATIVE_INFINITY", Domains.Num.inject(Domains.Num.NInf),
                    "POSITIVE_INFINITY", Domains.Num.inject(Domains.Num.Inf),
                    "NaN", Domains.Num.inject(Domains.Num.NaN)
            ),
            FHashMap.empty(),
            JSClass.CNumber_Obj
    );

    public static final Domains.Object Number_prototype_Obj = InitUtils.createInitObj(
            FHashMap.build(
                    "constructor", Domains.AddressSpace.Address.inject(Init.Number_Addr),
                    "toString", Domains.AddressSpace.Address.inject(Init.Number_prototype_toString_Addr),
                    "valueOf", Domains.AddressSpace.Address.inject(Init.Number_prototype_valueOf_Addr),
                    "toLocaleString", Domains.AddressSpace.Address.inject(Init.Number_prototype_toLocaleString_Addr),
                    "toFixed", Domains.AddressSpace.Address.inject(Init.Number_prototype_toFixed_Addr),
                    "toExponential", Domains.AddressSpace.Address.inject(Init.Number_prototype_toExponential_Addr),
                    "toPrecision", Domains.AddressSpace.Address.inject(Init.Number_prototype_toPrecision_Addr)
            ),
            FHashMap.build(
                    Utils.Fields.classname, JSClass.CNumber_prototype_Obj,
                    Utils.Fields.value, Domains.Num.inject(Domains.Num.Zero)
            )
    );

    public static final Domains.Object Number_prototype_toString_Obj = InitUtils.unimplemented("Number.prototype.toString");
    public static final Domains.Object Number_prototype_valueOf_Obj = InitUtils.unimplemented("Number.prototype.valueOf");
    public static final Domains.Object Number_prototype_toLocaleString_Obj = InitUtils.unimplemented("Number.prototype.toLocaleString");
    public static final Domains.Object Number_prototype_toFixed_Obj = InitUtils.unimplemented("Number.prototype.toFixed");
    public static final Domains.Object Number_prototype_toExponential_Obj = InitUtils.unimplemented("Number.prototype.toExponential");
    public static final Domains.Object Number_prototype_toPrecision_Obj = InitUtils.unimplemented("Number.prototype.toPrecision");
}
