package analysis.init;

import analysis.Domains;
import analysis.Utils;
import fj.P2;
import fj.data.List;
import fj.data.Option;
import immutable.FHashMap;

/**
 * Created by wayne on 15/12/17.
 */
public class InitTypedArrays {

    public static final Domains.Object ArrayBuffer_Obj = InitUtils.createInitFunctionObj(
            new Domains.Native(
                    (selfAddr, argArrayAddr, x, env, store, pad, ks, tr) -> {
                        assert selfAddr.defAddr() && selfAddr.as.size() == 1 : "We don't currently support mixing of ArrayBuffers with other objects";
                        assert argArrayAddr.defAddr() && argArrayAddr.as.size() == 1 : "Arguments array refers to non-addresses or multiple addresses";
                        Domains.Object argsObj = store.getObj(argArrayAddr.as.head());
                        Domains.BValue input = argsObj.apply(Domains.Str.alpha("0")).orSome(Domains.Num.inject(Domains.Num.Zero));
                        Domains.Object oldObj = store.getObj(selfAddr.as.head());
                        Domains.Object updatedObj = StringHelpers.newArrayBuffer(input, oldObj);
                        return InitUtils.makeState(selfAddr, x, env, store.putObj(selfAddr.as.head(), updatedObj), pad, ks, tr);
                    }
            ),
            FHashMap.build(
                    "prototype", Domains.AddressSpace.Address.inject(Init.ArrayBuffer_prototype_Addr),
                    "length", Domains.Num.inject(Domains.Num.alpha(1.0))
            )
    );

    public static Domains.Object createTypedArrayObj(Domains.AddressSpace.Address proto, String name) {
        return InitUtils.createInitFunctionObj(
                new Domains.Native(
                        (selfAddr, argArrayAddr, x, env, store, pad, ks, tr) -> {
                            assert selfAddr.defAddr() && selfAddr.as.size() == 1 :  "We don't currently support mixing of .. with other objects";
                            Domains.Object oldObj = store.getObj(selfAddr.as.head());
                            Domains.Object updatedObj = StringHelpers.newArray(Domains.Num.NReal, List.list(), Option.some(Domains.Num.inject(Domains.Num.NReal)), oldObj, true);
                            return InitUtils.makeState(selfAddr, x, env, store.putObj(selfAddr.as.head(), updatedObj), pad, ks, tr);
                        }
                ),
                FHashMap.build(
                        "prototype", Domains.AddressSpace.Address.inject(proto),
                        "length", Domains.Num.inject(Domains.Num.U32)
                )
        );
    }

    public static final Domains.Object Int8Array_Obj = createTypedArrayObj(Init.Int8Array_prototype_Addr, "Int8Array");

    public static final Domains.Object Uint8Array_Obj = createTypedArrayObj(Init.Uint8Array_prototype_Addr, "Uint8Array");

    public static final Domains.Object Int16Array_Obj = createTypedArrayObj(Init.Int16Array_prototype_Addr, "Int16Array");

    public static final Domains.Object Uint16Array_Obj = createTypedArrayObj(Init.Uint16Array_prototype_Addr, "Uint16Array");

    public static final Domains.Object Int32Array_Obj = createTypedArrayObj(Init.Int32Array_prototype_Addr, "Int32Array");

    public static final Domains.Object Uint32Array_Obj = createTypedArrayObj(Init.Uint32Array_prototype_Addr, "Uint32Array");

    public static final Domains.Object Float32Array_Obj = createTypedArrayObj(Init.Float32Array_prototype_Addr, "Float32Array");

    public static final Domains.Object Float64Array_Obj = createTypedArrayObj(Init.Float64Array_prototype_Addr, "Float64Array");

    public static final Domains.Object ArrayBuffer_prototype_Obj = InitUtils.createObj();

    public static final Domains.Object Int8Array_prototype_Obj = InitUtils.createInitObj( FHashMap.build(
            "set", Domains.AddressSpace.Address.inject(Init.Int8Array_prototype_set_Addr),
            "subarray", Domains.AddressSpace.Address.inject(Init.Int8Array_prototype_subarray_Addr)));

    public static final Domains.Object Uint8Array_prototype_Obj = InitUtils.createInitObj( FHashMap.build(
            "set", Domains.AddressSpace.Address.inject(Init.Uint8Array_prototype_set_Addr),
            "subarray", Domains.AddressSpace.Address.inject(Init.Uint8Array_prototype_subarray_Addr)));

    public static final Domains.Object Int16Array_prototype_Obj = InitUtils.createInitObj( FHashMap.build(
            "set", Domains.AddressSpace.Address.inject(Init.Int16Array_prototype_set_Addr),
            "subarray", Domains.AddressSpace.Address.inject(Init.Int16Array_prototype_subarray_Addr)));

    public static final Domains.Object Uint16Array_prototype_Obj = InitUtils.createInitObj( FHashMap.build(
            "set", Domains.AddressSpace.Address.inject(Init.Uint16Array_prototype_set_Addr),
            "subarray", Domains.AddressSpace.Address.inject(Init.Uint16Array_prototype_subarray_Addr)));

    public static final Domains.Object Int32Array_prototype_Obj = InitUtils.createInitObj( FHashMap.build(
            "set", Domains.AddressSpace.Address.inject(Init.Int32Array_prototype_set_Addr),
            "subarray", Domains.AddressSpace.Address.inject(Init.Int32Array_prototype_subarray_Addr)));

    public static final Domains.Object Uint32Array_prototype_Obj = InitUtils.createInitObj( FHashMap.build(
            "set", Domains.AddressSpace.Address.inject(Init.Uint32Array_prototype_set_Addr),
            "subarray", Domains.AddressSpace.Address.inject(Init.Uint32Array_prototype_subarray_Addr)));

    public static final Domains.Object Float32Array_prototype_Obj = InitUtils.createInitObj( FHashMap.build(
            "set", Domains.AddressSpace.Address.inject(Init.Float32Array_prototype_set_Addr),
            "subarray", Domains.AddressSpace.Address.inject(Init.Float32Array_prototype_subarray_Addr)));

    public static final Domains.Object Float64Array_prototype_Obj = InitUtils.createInitObj( FHashMap.build(
            "set", Domains.AddressSpace.Address.inject(Init.Float64Array_prototype_set_Addr),
            "subarray", Domains.AddressSpace.Address.inject(Init.Float64Array_prototype_subarray_Addr)));

    public static Domains.Object createTypedArraySetFunction() {
        return InitUtils.createInitFunctionObj(
                new Domains.Native(
                        (selfAddr, argArrayAddr, x, env, store, pad, ks, tr) -> {
                            return InitUtils.makeState(Domains.Undef.BV, x, env, store, pad, ks, tr);
                        }
                ),
                FHashMap.build("length", Domains.Num.inject(Domains.Num.U32))
        );
    }

    public static Domains.Object createTypedArraySubarrayFunction(Domains.AddressSpace.Address prototype) {
        return InitUtils.createInitFunctionObj(
                new Domains.Native(
                        (selfAddr, argArrayAddr, x, env, store, pad, ks, tr) -> {
                            assert selfAddr.defAddr() && selfAddr.as.size() == 1 : "We don't currently support mixing of Int8Array with other objects";
                            P2<Domains.Store, Domains.BValue> tmp = Utils.allocObj(Domains.AddressSpace.Address.inject(Init.Object_Addr), tr.toAddr(), store, tr);
                            Domains.Store store1 = tmp._1();
                            Domains.BValue subarrBV = tmp._2();
                            Domains.Object oldObj = store1.getObj(subarrBV.as.head());
                            Domains.Object updatedObj = StringHelpers.newArray(Domains.Num.NReal, List.list(), Option.some(Domains.Num.inject(Domains.Num.NReal)), oldObj, true);
                            return InitUtils.makeState(subarrBV, x, env, store1.putObj(subarrBV.as.head(), updatedObj), pad, ks, tr);
                        }
                ),
                FHashMap.build(
                        "prototype", Domains.AddressSpace.Address.inject(prototype),
                        "length", Domains.Num.inject(Domains.Num.U32)
                )
        );
    }

    public static final Domains.Object Int8Array_prototype_set_Obj = createTypedArraySetFunction();
    public static final Domains.Object Int8Array_prototype_subarray_Obj = createTypedArraySubarrayFunction(Init.Int8Array_prototype_Addr);

    public static final Domains.Object Uint8Array_prototype_set_Obj = createTypedArraySetFunction();
    public static final Domains.Object Uint8Array_prototype_subarray_Obj = createTypedArraySubarrayFunction(Init.Uint8Array_prototype_Addr);

    public static final Domains.Object Int16Array_prototype_set_Obj = createTypedArraySetFunction();
    public static final Domains.Object Int16Array_prototype_subarray_Obj = createTypedArraySubarrayFunction(Init.Int16Array_prototype_Addr);

    public static final Domains.Object Uint16Array_prototype_set_Obj = createTypedArraySetFunction();
    public static final Domains.Object Uint16Array_prototype_subarray_Obj = createTypedArraySubarrayFunction(Init.Uint16Array_prototype_Addr);

    public static final Domains.Object Int32Array_prototype_set_Obj = createTypedArraySetFunction();
    public static final Domains.Object Int32Array_prototype_subarray_Obj = createTypedArraySubarrayFunction(Init.Int32Array_prototype_Addr);

    public static final Domains.Object Uint32Array_prototype_set_Obj = createTypedArraySetFunction();
    public static final Domains.Object Uint32Array_prototype_subarray_Obj = createTypedArraySubarrayFunction(Init.Uint32Array_prototype_Addr);

    public static final Domains.Object Float32Array_prototype_set_Obj = createTypedArraySetFunction();
    public static final Domains.Object Float32Array_prototype_subarray_Obj = createTypedArraySubarrayFunction(Init.Float32Array_prototype_Addr);

    public static final Domains.Object Float64Array_prototype_set_Obj = createTypedArraySetFunction();
    public static final Domains.Object Float64Array_prototype_subarray_Obj = createTypedArraySubarrayFunction(Init.Float64Array_prototype_Addr);
}
