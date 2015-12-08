package analysis.init;

import analysis.Domains;
import analysis.Utils;
import immutable.FHashMap;
import ir.JSClass;

/**
 * Created by wayne on 15/12/7.
 */
public class InitDate {
    public static final Domains.Object Date_Obj = InitUtils.createInitFunctionObj(
            new Domains.Native(
                    (selfAddr, argArrayAddr, x, env, store, pad, ks, tr) -> {
                        return null; // TODO
                    }
            ),
            FHashMap.build(
                    "length", Domains.Num.inject(Domains.Num.alpha(7.0)),
                    "prototype", Domains.AddressSpace.Address.inject(Init.Date_prototype_Addr),
                    "now", Domains.AddressSpace.Address.inject(Init.Date_now_Addr),
                    "parse", Domains.AddressSpace.Address.inject(Init.Date_parse_Addr)
            )
    );

    public static final Domains.Object Date_now_Obj = InitUtils.unimplemented("Date.now");
    public static final Domains.Object Date_parse_Obj = InitUtils.unimplemented("Date.parse");

    public static final Domains.Object Date_prototype_Obj = InitUtils.createInitObj(
            FHashMap.build(
                    "toString", Domains.AddressSpace.Address.inject(Init.Date_prototype_toString_Addr),
                    "valueOf", Domains.AddressSpace.Address.inject(Init.Date_prototype_valueOf_Addr),
                    "toLocaleString", Domains.AddressSpace.Address.inject(Init.Date_prototype_toLocaleString_Addr)
            ),
            FHashMap.build(
                    Utils.Fields.value, Domains.Num.inject(Domains.Num.NaN),
                    Utils.Fields.classname, JSClass.CDate
            )
    );

    public static final Domains.Object Date_prototype_toString_Obj = InitUtils.unimplemented("Date.prototype.toString");
    public static final Domains.Object Date_prototype_valueOf_Obj = InitUtils.unimplemented("Date.prototype.valueOf");
    public static final Domains.Object Date_prototype_toLocaleString_Obj = InitUtils.unimplemented("Date.prototype.toLocaleString");
}
