package analysis.init;

import analysis.Domains;
import fj.Ord;
import fj.P;
import fj.data.Set;
import fj.data.TreeMap;
import ir.JSClass;

/**
 * Created by wayne on 15/11/5.
 */
public class Init {

    public static final Domains.AddressSpace.Address window_binding_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address window_Addr = NewAbstractAddress.apply();

    // window properties NewAbstractAddresses
    public static final Domains.AddressSpace.Address decodeURI_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address decodeURIComponent_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address encodeURI_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address encodeURIComponent_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address escape_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address isFinite_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address isNaN_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address parseFloat_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address parseInt_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address unescape_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Array_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Boolean_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Date_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Error_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address EvalError_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address RangeError_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address ReferenceError_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address TypeError_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address URIError_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Function_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address JSON_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Math_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Number_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address RegExp_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address String_Addr = NewAbstractAddress.apply();


    // Arguments NewAbstractAddresses (only for notJS)
    public static final Domains.AddressSpace.Address Arguments_Addr = NewAbstractAddress.apply();

    // Object NewAbstractAddresses
    public static final Domains.AddressSpace.Address Object_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Object_create_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Object_defineProperties_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Object_defineProperty_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Object_freeze_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Object_getOwnPropertyDescriptor_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Object_getOwnPropertyNames_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Object_getPrototypeOf_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Object_isExtensible_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Object_isFrozen_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Object_isSealed_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Object_keys_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Object_preventExtensions_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Object_seal_Addr = NewAbstractAddress.apply();

    // Object.prototype NewAbstractAddresses
    public static final Domains.AddressSpace.Address Object_prototype_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Object_prototype_valueOf_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Object_prototype_toString_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Object_prototype_isPrototypeOf_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Object_prototype_propertyIsEnumerable_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Object_prototype_hasOwnProperty_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Object_prototype_toLocaleString_Addr = NewAbstractAddress.apply();

    // Array NewAbstractAddresses
    public static final Domains.AddressSpace.Address Array_prototype_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Array_isArray_Addr = NewAbstractAddress.apply();

    // Array.prototype NewAbstractAddresses
    public static final Domains.AddressSpace.Address Array_prototype_concat_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Array_prototype_every_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Array_prototype_filter_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Array_prototype_forEach_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Array_prototype_indexOf_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Array_prototype_join_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Array_prototype_lastIndexOf_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Array_prototype_map_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Array_prototype_pop_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Array_prototype_push_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Array_prototype_reduce_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Array_prototype_reduceRight_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Array_prototype_reverse_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Array_prototype_shift_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Array_prototype_slice_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Array_prototype_some_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Array_prototype_sort_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Array_prototype_splice_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Array_prototype_toLocaleString_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Array_prototype_toString_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Array_prototype_unshift_Addr = NewAbstractAddress.apply();


    // Math NewAbstractAddresses
    public static final Domains.AddressSpace.Address Math_abs_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Math_acos_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Math_asin_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Math_atan_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Math_atan2_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Math_ceil_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Math_cos_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Math_exp_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Math_floor_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Math_log_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Math_max_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Math_min_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Math_pow_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Math_random_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Math_round_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Math_sin_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Math_sqrt_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Math_tan_Addr = NewAbstractAddress.apply();

    // Function NewAbstractAddresses
    public static final Domains.AddressSpace.Address Function_prototype_Addr = NewAbstractAddress.apply();

    // Function.prototype NewAbstractAddresses
    public static final Domains.AddressSpace.Address Function_prototype_toString_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Function_prototype_apply_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Function_prototype_call_Addr = NewAbstractAddress.apply();

    // Number NewAbstractAddresses
    public static final Domains.AddressSpace.Address Number_prototype_Addr = NewAbstractAddress.apply();

    // Number prototype NewAbstractAddresses
    public static final Domains.AddressSpace.Address Number_prototype_toString_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Number_prototype_toLocaleString_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Number_prototype_valueOf_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Number_prototype_toFixed_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Number_prototype_toExponential_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Number_prototype_toPrecision_Addr = NewAbstractAddress.apply();

    // String NewAbstractAddresses
    public static final Domains.AddressSpace.Address String_prototype_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address String_fromCharCode_Addr = NewAbstractAddress.apply();

    // String.prototype NewAbstractAddresses
    public static final Domains.AddressSpace.Address String_prototype_charAt_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address String_prototype_charCodeAt_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address String_prototype_concat_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address String_prototype_indexOf_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address String_prototype_lastIndexOf_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address String_prototype_localeCompare_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address String_prototype_match_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address String_prototype_replace_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address String_prototype_search_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address String_prototype_slice_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address String_prototype_split_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address String_prototype_substr_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address String_prototype_substring_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address String_prototype_toLocaleLowerCase_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address String_prototype_toLocaleUpperCase_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address String_prototype_toLowerCase_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address String_prototype_toString_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address String_prototype_toUpperCase_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address String_prototype_trim_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address String_prototype_valueOf_Addr = NewAbstractAddress.apply();

    // Boolean NewAbstractAddresses
    public static final Domains.AddressSpace.Address Boolean_prototype_Addr = NewAbstractAddress.apply();

    // Boolean.prototype NewAbstractAddresses
    public static final Domains.AddressSpace.Address Boolean_prototype_toString_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Boolean_prototype_valueOf_Addr = NewAbstractAddress.apply();

    // Error NewAbstractAddresses
    public static final Domains.AddressSpace.Address Error_prototype_Addr = NewAbstractAddress.apply();

    // Error.prototype NewAbstractAddresses
    public static final Domains.AddressSpace.Address Error_prototype_toString_Addr = NewAbstractAddress.apply();

    // JSON NewAbstractAddresses
    public static final Domains.AddressSpace.Address JSON_parse_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address JSON_stringify_Addr = NewAbstractAddress.apply();

    // Date NewAbstractAddresses
    public static final Domains.AddressSpace.Address Date_now_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Date_parse_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Date_prototype_Addr = NewAbstractAddress.apply();

    // Date.prototype NewAbstractAddresses
    // TODO: Date's prototype has a lot more than just this in it!
    public static final Domains.AddressSpace.Address Date_prototype_toString_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Date_prototype_valueOf_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Date_prototype_toLocaleString_Addr = NewAbstractAddress.apply();

    // RegExp NewAbstractAddresses
    public static final Domains.AddressSpace.Address RegExp_prototype_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address RegExp_prototype_exec_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address RegExp_prototype_test_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address RegExp_prototype_toString_Addr = NewAbstractAddress.apply();

    // Needed for internal functions
    public static final Domains.AddressSpace.Address Dummy_Arguments_Addr = NewAbstractAddress.apply();

    // typed array addresses:
    public static final Domains.AddressSpace.Address ArrayBuffer_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address ArrayBuffer_prototype_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Int8Array_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Uint8Array_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Int16Array_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Uint16Array_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Int32Array_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Uint32Array_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Float32Array_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Float64Array_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Int8Array_prototype_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Uint8Array_prototype_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Int16Array_prototype_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Uint16Array_prototype_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Int32Array_prototype_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Uint32Array_prototype_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Float32Array_prototype_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Float64Array_prototype_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Int8Array_prototype_set_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Uint8Array_prototype_set_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Int16Array_prototype_set_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Uint16Array_prototype_set_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Int32Array_prototype_set_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Uint32Array_prototype_set_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Float32Array_prototype_set_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Float64Array_prototype_set_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Int8Array_prototype_subarray_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Uint8Array_prototype_subarray_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Int16Array_prototype_subarray_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Uint16Array_prototype_subarray_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Int32Array_prototype_subarray_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Uint32Array_prototype_subarray_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Float32Array_prototype_subarray_Addr = NewAbstractAddress.apply();
    public static final Domains.AddressSpace.Address Float64Array_prototype_subarray_Addr = NewAbstractAddress.apply();

    // dummy address used to pass to arguments object
    public static final Domains.AddressSpace.Address Dummy_Addr = NewAbstractAddress.apply();

    public static class NewAbstractAddress {
        private static Integer inverseCounter = -2;
        public static Domains.AddressSpace.Address apply() {
            inverseCounter = inverseCounter - 1;
            return Domains.AddressSpace.Address.apply(inverseCounter);
        }
    }

    public static final TreeMap<JSClass, Set<Domains.Str>> noenum = null;

    public static final TreeMap<JSClass, Set<Domains.Str>> nodelete = null;

    public static final TreeMap<JSClass, Set<Domains.Str>> noupdate = null;

    public static Set<Domains.AddressSpace.Address> keepInStore; // TODO

    public static final TreeMap<Domains.AddressSpace.Address, JSClass> classFromAddress = null;/*TreeMap.treeMap(Ord.hashEqualsOrd(),
            P.p(Function_Addr, JSClass.CFunction),
            P.p(Array_Addr, JSClass.CArray),
            P.p(String_Addr, JSClass.CString),
            P.p(Boolean_Addr, JSClass.CBoolean),
            P.p(Number_Addr, JSClass.CNumber),
            P.p(Date_Addr, JSClass.CDate),
            P.p(Error_Addr, JSClass.CError),
            P.p(RegExp_Addr, JSClass.CRegexp),
            P.p(Arguments_Addr, JSClass.CArguments));*/
}
