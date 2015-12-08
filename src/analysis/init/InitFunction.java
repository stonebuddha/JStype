package analysis.init;

import analysis.Domains;
import analysis.Utils;
import immutable.FHashMap;
import immutable.FHashSet;
import ir.JSClass;

/**
 * Created by wayne on 15/12/7.
 */
public class InitFunction {
    public static final Domains.Object Function_prototype_Obj = InitUtils.createInitObj(
            FHashMap.build(
                    "length", Domains.Num.inject(Domains.Num.alpha(0.0)),
                    "apply", Domains.AddressSpace.Address.inject(Init.Function_prototype_apply_Addr),
                    "call", Domains.AddressSpace.Address.inject(Init.Function_prototype_call_Addr),
                    "toString", Domains.AddressSpace.Address.inject(Init.Function_prototype_toString_Addr),
                    "constructor", Domains.AddressSpace.Address.inject(Init.Function_Addr)
            ),
            FHashMap.build(
                    Utils.Fields.proto, Domains.AddressSpace.Address.inject(Init.Object_prototype_Addr),
                    Utils.Fields.classname, JSClass.CFunction_prototype_Obj,
                    Utils.Fields.code, FHashSet.build(new Domains.Native(
                            (selfAddr, argArrayAddr, x, env, store, pad, ks, tr) -> {
                                return InitUtils.makeState(Domains.Undef.BV, x, env, store, pad, ks, tr);
                            }
                    ))
            )
    );

    public static final Domains.Object Function_prototype_toString_Obj = InitUtils.unimplemented("Function.prototype.toString");
    public static final Domains.Object Function_prototype_apply_Obj = InitUtils.unimplemented("Function.prototype.apply");
    public static final Domains.Object Function_prototype_call_Obj = InitUtils.unimplemented("Function.prototype.call");

}
