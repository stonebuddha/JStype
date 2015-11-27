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
public class InitObject {
    public static Domains.Object Object_Obj = InitUtils.makeNativeValueStore(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object args = store.getObj(argArrayAddr);
                Domains.BValue input = args.apply(new Domains.Str("0")).orSome(Domains.Undef);
                if (input.equals(Domains.Null) || input.equals(Domains.Undef)) {
                    Domains.Address newAddr = Domains.Address.generate();
                    Domains.Object newObj = InitUtils.createObj(TreeMap.empty(Ord.hashEqualsOrd()));
                    Domains.Store newStore = store.putObj(newAddr, newObj);
                    return P.p(newAddr, newStore);
                } else {
                    return InitUtils.ToObject(input, store);
                }
            }, TreeMap.treeMap(Ord.hashEqualsOrd(),
                    P.p(new Domains.Str("prototype"), Init.Object_prototype_Addr),
                    P.p(new Domains.Str("create"), Init.Object_create_Addr),
                    P.p(new Domains.Str("defineProperties"), Init.Object_defineProperties_Addr),
                    P.p(new Domains.Str("defineProperty"), Init.Object_defineProperty_Addr),
                    P.p(new Domains.Str("freeze"), Init.Object_freeze_Addr),
                    P.p(new Domains.Str("getOwnPropertyDescriptor"), Init.Object_getOwnPropertyDescriptor_Addr),
                    P.p(new Domains.Str("getOwnPropertyNames"), Init.Object_getOwnPropertyNames_Addr),
                    P.p(new Domains.Str("getPrototypeOf"), Init.Object_getPrototypeOf_Addr),
                    P.p(new Domains.Str("isExtensible"), Init.Object_isExtensible_Addr),
                    P.p(new Domains.Str("isFrozen"), Init.Object_isFrozen_Addr),
                    P.p(new Domains.Str("isSealed"), Init.Object_isSealed_Addr),
                    P.p(new Domains.Str("keys"), Init.Object_keys_Addr),
                    P.p(new Domains.Str("length"), new Domains.Num(1.0)),
                    P.p(new Domains.Str("preventExtensions"), Init.Object_preventExtensions_Addr),
                    P.p(new Domains.Str("seal"), Init.Object_seal_Addr)),
            JSClass.CObject_Obj
    );

    public static Domains.Object Object_prototype_Obj = new Domains.Object(
            TreeMap.treeMap(Ord.hashEqualsOrd(),
                    P.p(new Domains.Str("constructor"), Init.Object_Addr),
                    P.p(new Domains.Str("valueOf"), Init.Object_prototype_valueOf_Addr),
                    P.p(new Domains.Str("toString"), Init.Object_prototype_toString_Addr),
                    P.p(new Domains.Str("isPrototypeOf"), Init.Object_prototype_isPrototypeOf_Addr),
                    P.p(new Domains.Str("propertyIsEnumerable"), Init.Object_prototype_propertyIsEnumerable_Addr),
                    P.p(new Domains.Str("hasOwnProperty"), Init.Object_prototype_hasOwnProperty_Addr),
                    P.p(new Domains.Str("toLocaleString"), Init.Object_prototype_toLocaleString_Addr)),
            TreeMap.treeMap(Ord.hashEqualsOrd(),
                    P.p(Utils.Fields.proto, Domains.Null),
                    P.p(Utils.Fields.classname, JSClass.CObject_prototype_Obj))
    );

    public static Domains.Object Object_create_Obj = InitUtils.unimplemented;

    public static Domains.Object Object_defineProperties_Obj = InitUtils.unimplemented;

    public static Domains.Object Object_defineProperty_Obj = InitUtils.unimplemented;

    public static Domains.Object Object_freeze_Obj = InitUtils.unimplemented;

    public static Domains.Object Object_getOwnPropertyDescriptor_Obj = InitUtils.unimplemented;

    public static Domains.Object Object_getOwnPropertyNames_Obj = InitUtils.unimplemented;

    public static Domains.Object Object_getPrototypeOf_Obj = InitUtils.unimplemented;

    public static Domains.Object Object_isExtensible_Obj = InitUtils.unimplemented;

    public static Domains.Object Object_isFrozen_Obj = InitUtils.unimplemented;

    public static Domains.Object Object_isSealed_Obj = InitUtils.unimplemented;

    public static Domains.Object Object_keys_Obj = InitUtils.unimplemented;

    public static Domains.Object Object_preventExtensions_Obj = InitUtils.unimplemented;

    public static Domains.Object Object_seal_Obj = InitUtils.unimplemented;

    public static Domains.Object Object_prototype_toString_Obj = InitUtils.makeNativeValue(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object selfObj = store.getObj(selfAddr);
                return InitUtils.Object_toString_helper(selfObj);
            }, TreeMap.treeMap(Ord.hashEqualsOrd(), P.p(new Domains.Str("length"), new Domains.Num(0.0)))
    );

    public static Domains.Object Object_prototype_valueOf_Obj = InitUtils.makeNativeValue(
            (selfAddr, argArrayAddr, store) -> {
                return selfAddr;
            }, TreeMap.treeMap(Ord.hashEqualsOrd(), P.p(new Domains.Str("length"), new Domains.Num(0.0)))
    );

    public static Domains.Object Object_prototype_isPrototypeOf_Obj = InitUtils.unimplemented;

    public static Domains.Object Object_prototype_propertyIsEnumerable_Obj = InitUtils.unimplemented;

    public static Domains.Object Object_prototype_hasOwnProperty_Obj = InitUtils.makeNativeValue(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object args = store.getObj(argArrayAddr);
                Domains.BValue input = args.apply(new Domains.Str("0")).orSome(Domains.Undef);
                Domains.Str istr = InitUtils.ToString(input, store);
                Domains.Object selfObj = store.getObj(selfAddr);
                if (selfObj.extern.contains(istr)) {
                    return Domains.Bool.True;
                } else {
                    return Domains.Bool.False;
                }
            }, TreeMap.treeMap(Ord.hashEqualsOrd(), P.p(new Domains.Str("length"), new Domains.Num(0.0)))
    );

    public static Domains.Object Object_prototype_toLocaleString_Obj = InitUtils.unimplemented;

}
