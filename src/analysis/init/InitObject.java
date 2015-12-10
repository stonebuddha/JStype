package analysis.init;

import analysis.Domains;
import analysis.Traces;
import analysis.Utils;
import fj.P;
import fj.P2;
import fj.P3;
import immutable.FHashMap;
import immutable.FHashSet;
import ir.JSClass;

/**
 * Created by wayne on 15/12/7.
 */
public class InitObject {
    public static final Domains.Object Object_Obj = InitUtils.createInitFunctionObj(
            new Domains.Native(
                    (selfAddr, argArrayAddr, x, env, store, pad, ks, tr) -> {
                        Domains.Object argsObj = store.getObj(argArrayAddr.as.head());
                        Domains.BValue input = argsObj.apply(Domains.Str.alpha("0")).orSome(Domains.Undef.BV);
                        Domains.AddressSpace.Address resAddr = tr.toAddr();
                        P3<Domains.BValue, Domains.Store, FHashSet<Domains.Domain>> tmp = Utils.toObjBody(input, store, tr, resAddr);
                        P2<Domains.Store, Domains.BValue> tmp1;
                        Domains.BValue bv1 = tmp._1();
                        Domains.Store store1 = tmp._2();
                        if (input.nil.equals(Domains.Null.MaybeNull) || input.undef.equals(Domains.Undef.MaybeUndef)) {
                            tmp1 = Utils.allocObj(Domains.AddressSpace.Address.inject(Init.Object_Addr), resAddr, store1, tr);
                        } else {
                            tmp1 = P.p(store1, Domains.BValue.Bot);
                        }
                        Domains.Store store2 = tmp1._1();
                        Domains.BValue bv2 = tmp1._2();
                        return InitUtils.makeState(bv2.merge(bv1), x, env, store2, pad, ks, tr);
                    }
            ),
            FHashMap.build(
                    "prototype", Domains.AddressSpace.Address.inject(Init.Object_prototype_Addr),
                    "length", Domains.Num.inject(Domains.Num.alpha(1.0)),
                    "create", Domains.AddressSpace.Address.inject(Init.Object_create_Addr), // TODO
                    "defineProperties", Domains.AddressSpace.Address.inject(Init.Object_defineProperties_Addr), // TODO
                    "defineProperty", Domains.AddressSpace.Address.inject(Init.Object_defineProperty_Addr), // TODO
                    "freeze", Domains.AddressSpace.Address.inject(Init.Object_freeze_Addr), // TODO
                    "getOwnPropertyDescriptor", Domains.AddressSpace.Address.inject(Init.Object_getOwnPropertyDescriptor_Addr), // TODO
                    "getOwnPropertyNames", Domains.AddressSpace.Address.inject(Init.Object_getOwnPropertyNames_Addr), // TODO
                    "getPrototypeOf", Domains.AddressSpace.Address.inject(Init.Object_getPrototypeOf_Addr), // TODO
                    "isExtensible", Domains.AddressSpace.Address.inject(Init.Object_isExtensible_Addr), // TODO
                    "isFrozen", Domains.AddressSpace.Address.inject(Init.Object_isFrozen_Addr), // TODO
                    "isSealed", Domains.AddressSpace.Address.inject(Init.Object_isSealed_Addr), // TODO
                    "keys", Domains.AddressSpace.Address.inject(Init.Object_keys_Addr), // TODO
                    "preventExtensions", Domains.AddressSpace.Address.inject(Init.Object_preventExtensions_Addr), // TODO
                    "seal", Domains.AddressSpace.Address.inject(Init.Object_seal_Addr)
            ),
            FHashMap.empty(),
            JSClass.CObject_Obj
    );

    public static final Domains.Object Object_prototype_Obj = InitUtils.createInitObj(
            FHashMap.build(
                    "constructor", Domains.AddressSpace.Address.inject(Init.Object_Addr),
                    "toString", Domains.AddressSpace.Address.inject(Init.Object_prototype_toString_Addr),
                    "toLocaleString", Domains.AddressSpace.Address.inject(Init.Object_prototype_toLocaleString_Addr),
                    "valueOf", Domains.AddressSpace.Address.inject(Init.Object_prototype_valueOf_Addr),
                    "hasOwnProperty", Domains.AddressSpace.Address.inject(Init.Object_prototype_hasOwnProperty_Addr),
                    "isPrototypeOf", Domains.AddressSpace.Address.inject(Init.Object_prototype_isPrototypeOf_Addr),
                    "propertyIsEnumerable", Domains.AddressSpace.Address.inject(Init.Object_prototype_propertyIsEnumerable_Addr)
            ),
            FHashMap.build(
                    Utils.Fields.proto, Domains.Null.BV,
                    Utils.Fields.classname, JSClass.CObject_prototype_Obj
            )
    );

    public static final Domains.Object Object_prototype_toString_Obj = InitUtils.unimplemented("Object.prototype.toString");
    public static final Domains.Object Object_prototype_toLocaleString_Obj = InitUtils.unimplemented("Object.prototype.toLocaleString");
    public static final Domains.Object Object_prototype_valueOf_Obj = InitUtils.unimplemented("Object.prototype.valueOf");
    public static final Domains.Object Object_prototype_hasOwnProperty_Obj = InitUtils.unimplemented("Object.prototype.hasOwnProperty");
    public static final Domains.Object Object_prototype_isPrototypeOf_Obj = InitUtils.unimplemented("Object.prototype.isPrototypeOf");
    public static final Domains.Object Object_prototype_propertyIsEnumerable_Obj = InitUtils.unimplemented("Object.prototype.propertyIsEnumerable");
}
