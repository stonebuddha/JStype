package analysis.init;

import analysis.Domains;
import analysis.Utils;
import immutable.FHashMap;
import ir.JSClass;

/**
 * Created by wayne on 15/12/7.
 */
public class InitArray {
    public static final Domains.Object Array_Obj = InitUtils.createInitFunctionObj(
            new Domains.Native(
                    (selfAddr, argArrayAddr, x, env, store, pad, ks, tr) -> {
                        throw new RuntimeException("unimplemented"); // TODO
                    }
            ),
            FHashMap.build(
                    "prototype", Domains.AddressSpace.Address.inject(Init.Array_prototype_Addr),
                    "length", Domains.Num.inject(Domains.Num.alpha(1.0)),
                    "isArray", Domains.AddressSpace.Address.inject(Init.Array_isArray_Addr)),
            FHashMap.empty(),
            JSClass.CArray_Obj
    );

    public static final Domains.Object Array_prototype_Obj = InitUtils.createInitObj(
            FHashMap.build(
                    "constructor",Domains.AddressSpace.Address.inject(Init.Array_Addr),
                    "length", Domains.Num.inject(Domains.Num.alpha(0.0)),
                    "concat",Domains.AddressSpace.Address.inject(Init.Array_prototype_concat_Addr),
                    "indexOf",Domains.AddressSpace.Address.inject(Init.Array_prototype_indexOf_Addr),
                    "join",Domains.AddressSpace.Address.inject(Init.Array_prototype_join_Addr),
                    "lastIndexOf",Domains.AddressSpace.Address.inject(Init.Array_prototype_lastIndexOf_Addr),
                    "pop",Domains.AddressSpace.Address.inject(Init.Array_prototype_pop_Addr),
                    "push",Domains.AddressSpace.Address.inject(Init.Array_prototype_push_Addr),
                    "reverse",Domains.AddressSpace.Address.inject(Init.Array_prototype_reverse_Addr), // TODO
                    "shift",Domains.AddressSpace.Address.inject(Init.Array_prototype_shift_Addr), // TODO
                    "sort",Domains.AddressSpace.Address.inject(Init.Array_prototype_sort_Addr),
                    "splice",Domains.AddressSpace.Address.inject(Init.Array_prototype_splice_Addr),
                    "toString",Domains.AddressSpace.Address.inject(Init.Array_prototype_toString_Addr), // TODO
                    "every",Domains.AddressSpace.Address.inject(Init.Array_prototype_every_Addr), // TODO
                    "filter",Domains.AddressSpace.Address.inject(Init.Array_prototype_filter_Addr), // TODO
                    "forEach",Domains.AddressSpace.Address.inject(Init.Array_prototype_forEach_Addr), // TODO
                    "map",Domains.AddressSpace.Address.inject(Init.Array_prototype_map_Addr), // TODO
                    "reduce",Domains.AddressSpace.Address.inject(Init.Array_prototype_reduce_Addr), // TODO
                    "reduceRight",Domains.AddressSpace.Address.inject(Init.Array_prototype_reduceRight_Addr), // TODO
                    "slice",Domains.AddressSpace.Address.inject(Init.Array_prototype_slice_Addr), // TODO
                    "some",Domains.AddressSpace.Address.inject(Init.Array_prototype_some_Addr), // TODO
                    "toLocaleString",Domains.AddressSpace.Address.inject(Init.Array_prototype_toLocaleString_Addr), // TODO
                    "unshift",Domains.AddressSpace.Address.inject(Init.Array_prototype_unshift_Addr) // TODO
            ),
            FHashMap.build(Utils.Fields.classname, JSClass.CArray_prototype_Obj)
    );

    public static final Domains.Object Array_prototype_join_Obj = InitUtils.unimplemented("Array.prototype.join");
    public static final Domains.Object Array_prototype_pop_Obj = InitUtils.unimplemented("Array.prototype.pop");
    public static final Domains.Object Array_prototype_push_Obj = InitUtils.unimplemented("Array.prototype.push");
    public static final Domains.Object Array_prototype_indexOf_Obj = InitUtils.unimplemented("Array.prototype.indexOf");
    public static final Domains.Object Array_prototype_lastIndexOf_Obj = InitUtils.unimplemented("Array.prototype.lastIndexOf");
    public static final Domains.Object Array_prototype_concat_Obj = InitUtils.unimplemented("Array.prototype.concat");
    public static final Domains.Object Array_prototype_sort_Obj = InitUtils.unimplemented("Array.prototype.sort");
    public static final Domains.Object Array_prototype_splice_Obj = InitUtils.unimplemented("Array.prototype.splice");
    public static final Domains.Object Array_prototype_every_Obj = InitUtils.unimplemented("Array.prototype.every");
    public static final Domains.Object Array_prototype_filter_Obj = InitUtils.unimplemented("Array.prototype.filter");
    public static final Domains.Object Array_prototype_forEach_Obj = InitUtils.unimplemented("Array.prototype.forEach");
    public static final Domains.Object Array_prototype_map_Obj = InitUtils.unimplemented("Array.prototype.map");
    public static final Domains.Object Array_prototype_reduce_Obj = InitUtils.unimplemented("Array.prototype.reduce");
    public static final Domains.Object Array_prototype_reduceRight_Obj = InitUtils.unimplemented("Array.prototype.reduceRight");
    public static final Domains.Object Array_prototype_reverse_Obj = InitUtils.unimplemented("Array.prototype.reverse");
    public static final Domains.Object Array_prototype_shift_Obj = InitUtils.unimplemented("Array.prototype.shift");
    public static final Domains.Object Array_prototype_slice_Obj = InitUtils.unimplemented("Array.prototype.slice");
    public static final Domains.Object Array_prototype_some_Obj = InitUtils.unimplemented("Array.prototype.some");
    public static final Domains.Object Array_prototype_toLocaleString_Obj = InitUtils.unimplemented("Array.prototype.toLocaleString");
    public static final Domains.Object Array_prototype_toString_Obj = InitUtils.unimplemented("Array.prototype.toString");
    public static final Domains.Object Array_prototype_unshift_Obj = InitUtils.unimplemented("Array.prototype.unshift");

}
