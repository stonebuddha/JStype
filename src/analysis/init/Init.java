package analysis.init;

import analysis.Domains;
import analysis.Interpreter;
import analysis.Traces.Trace;
import fj.data.List;
import fj.data.Seq;
import immutable.FHashMap;
import immutable.FHashSet;
import immutable.FVector;
import ir.IRPVar;
import ir.IRStmt;
import ir.JSClass;

/**
 * Created by wayne on 15/11/5.
 */
public class Init {

    public static final IRPVar window_Variable = new IRPVar(0);

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

    public static Interpreter.State initState(IRStmt s, Trace trace) {
        Domains.Env initEnv = new Domains.Env(FHashMap.build(window_Variable, Domains.AddressSpace.Addresses.apply(window_binding_Addr)));

        Domains.Store initStore = new Domains.Store(
                FHashMap.build(window_binding_Addr, Domains.AddressSpace.Address.inject(window_Addr)),
                FHashMap.build(
                        window_Addr, InitGlobal.window_Obj,
                        decodeURI_Addr, InitGlobal.decodeURI_Obj,
                        decodeURIComponent_Addr, InitGlobal.decodeURIComponent_Obj,
                        encodeURI_Addr, InitGlobal.encodeURI_Obj,
                        encodeURIComponent_Addr, InitGlobal.encodeURIComponent_Obj,
                        escape_Addr, InitGlobal.escape_Obj,
                        isFinite_Addr, InitGlobal.isFinite_Obj,
                        isNaN_Addr, InitGlobal.isNaN_Obj,
                        parseFloat_Addr, InitGlobal.parseFloat_Obj,
                        parseInt_Addr, InitGlobal.parseInt_Obj,
                        unescape_Addr, InitGlobal.unescape_Obj,
                        Array_Addr, InitArray.Array_Obj,
                        Boolean_Addr, InitBoolean.Boolean_Obj,
                        Arguments_Addr, InitArguments.Arguments_Obj,
                        Date_Addr, InitDate.Date_Obj,
                        //
                        JSON_Addr, InitJSON.JSON_Obj,
                        Math_Addr, InitMath.Math_Obj,
                        Number_Addr, InitNumber.Number_Obj,
                        RegExp_Addr, InitRegExp.RegExp_Obj,
                        String_Addr, InitString.String_Obj,
                        Object_Addr, InitObject.Object_Obj,
                        //
                        Object_prototype_Addr, InitObject.Object_prototype_Obj,
                        Object_prototype_valueOf_Addr, InitObject.Object_prototype_valueOf_Obj,
                        Object_prototype_toString_Addr, InitObject.Object_prototype_toString_Obj,
                        Object_prototype_isPrototypeOf_Addr, InitObject.Object_prototype_isPrototypeOf_Obj,
                        Object_prototype_propertyIsEnumerable_Addr, InitObject.Object_prototype_propertyIsEnumerable_Obj,
                        Object_prototype_hasOwnProperty_Addr, InitObject.Object_prototype_hasOwnProperty_Obj,
                        Object_prototype_toLocaleString_Addr, InitObject.Object_prototype_toLocaleString_Obj,
                        Array_prototype_Addr, InitArray.Array_prototype_Obj,
                        //
                        Array_prototype_concat_Addr, InitArray.Array_prototype_concat_Obj,
                        Array_prototype_every_Addr, InitArray.Array_prototype_every_Obj,
                        Array_prototype_filter_Addr, InitArray.Array_prototype_filter_Obj,
                        Array_prototype_forEach_Addr, InitArray.Array_prototype_forEach_Obj,
                        Array_prototype_indexOf_Addr, InitArray.Array_prototype_indexOf_Obj,
                        Array_prototype_join_Addr, InitArray.Array_prototype_join_Obj,
                        Array_prototype_lastIndexOf_Addr, InitArray.Array_prototype_lastIndexOf_Obj,
                        Array_prototype_map_Addr, InitArray.Array_prototype_map_Obj,
                        Array_prototype_pop_Addr, InitArray.Array_prototype_pop_Obj,
                        Array_prototype_push_Addr, InitArray.Array_prototype_push_Obj,
                        Array_prototype_reduce_Addr, InitArray.Array_prototype_reduce_Obj,
                        Array_prototype_reduceRight_Addr, InitArray.Array_prototype_reduceRight_Obj,
                        Array_prototype_reverse_Addr, InitArray.Array_prototype_reverse_Obj,
                        Array_prototype_shift_Addr, InitArray.Array_prototype_shift_Obj,
                        Array_prototype_slice_Addr, InitArray.Array_prototype_slice_Obj,
                        Array_prototype_some_Addr, InitArray.Array_prototype_some_Obj,
                        Array_prototype_sort_Addr, InitArray.Array_prototype_sort_Obj,
                        Array_prototype_splice_Addr, InitArray.Array_prototype_splice_Obj,
                        Array_prototype_toLocaleString_Addr, InitArray.Array_prototype_toLocaleString_Obj,
                        Array_prototype_toString_Addr, InitArray.Array_prototype_toString_Obj,
                        Array_prototype_unshift_Addr, InitArray.Array_prototype_unshift_Obj,
                        Math_abs_Addr, InitMath.Math_abs_Obj,
                        Math_acos_Addr, InitMath.Math_acos_Obj,
                        Math_asin_Addr, InitMath.Math_asin_Obj,
                        Math_atan_Addr, InitMath.Math_atan_Obj,
                        Math_atan2_Addr, InitMath.Math_atan2_Obj,
                        Math_ceil_Addr, InitMath.Math_ceil_Obj,
                        Math_cos_Addr, InitMath.Math_cos_Obj,
                        Math_exp_Addr, InitMath.Math_exp_Obj,
                        Math_floor_Addr, InitMath.Math_floor_Obj,
                        Math_log_Addr, InitMath.Math_log_Obj,
                        Math_max_Addr, InitMath.Math_max_Obj,
                        Math_min_Addr, InitMath.Math_min_Obj,
                        Math_pow_Addr, InitMath.Math_pow_Obj,
                        Math_random_Addr, InitMath.Math_random_Obj,
                        Math_round_Addr, InitMath.Math_round_Obj,
                        Math_sin_Addr, InitMath.Math_sin_Obj,
                        Math_sqrt_Addr, InitMath.Math_sqrt_Obj,
                        Math_tan_Addr, InitMath.Math_tan_Obj,
                        Function_prototype_Addr, InitFunction.Function_prototype_Obj,
                        Function_prototype_toString_Addr, InitFunction.Function_prototype_toString_Obj,
                        Function_prototype_apply_Addr, InitFunction.Function_prototype_apply_Obj,
                        Function_prototype_call_Addr, InitFunction.Function_prototype_call_Obj,
                        Number_prototype_Addr, InitNumber.Number_prototype_Obj,
                        Number_prototype_toString_Addr, InitNumber.Number_prototype_toString_Obj,
                        Number_prototype_toLocaleString_Addr, InitNumber.Number_prototype_toLocaleString_Obj,
                        Number_prototype_valueOf_Addr, InitNumber.Number_prototype_valueOf_Obj,
                        Number_prototype_toFixed_Addr, InitNumber.Number_prototype_toFixed_Obj,
                        Number_prototype_toExponential_Addr, InitNumber.Number_prototype_toExponential_Obj,
                        Number_prototype_toPrecision_Addr, InitNumber.Number_prototype_toPrecision_Obj,
                        String_prototype_Addr, InitString.String_prototype_Obj,
                        String_fromCharCode_Addr, InitString.String_fromCharCode_Obj,
                        String_prototype_charAt_Addr, InitString.String_prototype_charAt_Obj,
                        String_prototype_charCodeAt_Addr, InitString.String_prototype_charCodeAt_Obj,
                        String_prototype_concat_Addr, InitString.String_prototype_concat_Obj,
                        String_prototype_indexOf_Addr, InitString.String_prototype_indexOf_Obj,
                        String_prototype_lastIndexOf_Addr, InitString.String_prototype_lastIndexOf_Obj,
                        String_prototype_localeCompare_Addr, InitString.String_prototype_localeCompare_Obj,
                        String_prototype_match_Addr, InitString.String_prototype_match_Obj,
                        String_prototype_replace_Addr, InitString.String_prototype_replace_Obj,
                        String_prototype_search_Addr, InitString.String_prototype_search_Obj,
                        String_prototype_slice_Addr, InitString.String_prototype_slice_Obj,
                        String_prototype_split_Addr, InitString.String_prototype_split_Obj,
                        String_prototype_substr_Addr, InitString.String_prototype_substr_Obj,
                        String_prototype_substring_Addr, InitString.String_prototype_substring_Obj,
                        String_prototype_toLocaleLowerCase_Addr, InitString.String_prototype_toLocaleLowerCase_Obj,
                        String_prototype_toLocaleUpperCase_Addr, InitString.String_prototype_toLocaleUpperCase_Obj,
                        String_prototype_toLowerCase_Addr, InitString.String_prototype_toLowerCase_Obj,
                        String_prototype_toString_Addr, InitString.String_prototype_toString_Obj,
                        String_prototype_toUpperCase_Addr, InitString.String_prototype_toUpperCase_Obj,
                        String_prototype_trim_Addr, InitString.String_prototype_trim_Obj,
                        String_prototype_valueOf_Addr, InitString.String_prototype_valueOf_Obj,
                        Boolean_prototype_Addr, InitBoolean.Boolean_prototype_Obj,
                        Boolean_prototype_toString_Addr, InitBoolean.Boolean_prototype_toString_Obj,
                        Boolean_prototype_valueOf_Addr, InitBoolean.Boolean_prototype_valueOf_Obj,
                        //
                        JSON_parse_Addr, InitJSON.JSON_parse_Obj,
                        JSON_stringify_Addr, InitJSON.JSON_stringify_Obj,
                        Date_now_Addr, InitDate.Date_now_Obj,
                        Date_parse_Addr, InitDate.Date_parse_Obj,
                        Date_prototype_Addr, InitDate.Date_prototype_Obj,
                        Date_prototype_toString_Addr, InitDate.Date_prototype_toString_Obj,
                        Date_prototype_valueOf_Addr, InitDate.Date_prototype_valueOf_Obj,
                        Date_prototype_toLocaleString_Addr, InitDate.Date_prototype_toLocaleString_Obj,
                        RegExp_prototype_Addr, InitRegExp.RegExp_prototype_Obj,
                        RegExp_prototype_exec_Addr, InitRegExp.RegExp_prototype_exec_Obj,
                        RegExp_prototype_test_Addr, InitRegExp.RegExp_prototype_test_Obj,
                        RegExp_prototype_toString_Addr, InitRegExp.RegExp_prototype_toString_Obj,
                        Dummy_Arguments_Addr, InitArguments.Dummy_Arguments_Obj,
                        ArrayBuffer_Addr, InitTypedArrays.ArrayBuffer_Obj,
                        ArrayBuffer_prototype_Addr, InitTypedArrays.ArrayBuffer_prototype_Obj,
                        Int8Array_Addr, InitTypedArrays.Int8Array_Obj,
                        Uint8Array_Addr, InitTypedArrays.Uint8Array_Obj,
                        Int16Array_Addr, InitTypedArrays.Int16Array_Obj,
                        Uint16Array_Addr, InitTypedArrays.Uint16Array_Obj,
                        Int32Array_Addr, InitTypedArrays.Int32Array_Obj,
                        Uint32Array_Addr, InitTypedArrays.Uint32Array_Obj,
                        Float32Array_Addr, InitTypedArrays.Float32Array_Obj,
                        Float64Array_Addr, InitTypedArrays.Float64Array_Obj,
                        Int8Array_prototype_Addr, InitTypedArrays.Int8Array_prototype_Obj,
                        Uint8Array_prototype_Addr, InitTypedArrays.Uint8Array_prototype_Obj,
                        Int16Array_prototype_Addr, InitTypedArrays.Int16Array_prototype_Obj,
                        Uint16Array_prototype_Addr, InitTypedArrays.Uint16Array_prototype_Obj,
                        Int32Array_prototype_Addr, InitTypedArrays.Int32Array_prototype_Obj,
                        Uint32Array_prototype_Addr, InitTypedArrays.Uint32Array_prototype_Obj,
                        Float32Array_prototype_Addr, InitTypedArrays.Float32Array_prototype_Obj,
                        Float64Array_prototype_Addr, InitTypedArrays.Float64Array_prototype_Obj,
                        Int8Array_prototype_set_Addr, InitTypedArrays.Int8Array_prototype_set_Obj,
                        Uint8Array_prototype_set_Addr, InitTypedArrays.Uint8Array_prototype_set_Obj,
                        Int16Array_prototype_set_Addr, InitTypedArrays.Int16Array_prototype_set_Obj,
                        Uint16Array_prototype_set_Addr, InitTypedArrays.Uint16Array_prototype_set_Obj,
                        Int32Array_prototype_set_Addr, InitTypedArrays.Int32Array_prototype_set_Obj,
                        Uint32Array_prototype_set_Addr, InitTypedArrays.Uint32Array_prototype_set_Obj,
                        Float32Array_prototype_set_Addr, InitTypedArrays.Float32Array_prototype_set_Obj,
                        Float64Array_prototype_set_Addr, InitTypedArrays.Float64Array_prototype_set_Obj,
                        Int8Array_prototype_subarray_Addr, InitTypedArrays.Int8Array_prototype_subarray_Obj,
                        Uint8Array_prototype_subarray_Addr, InitTypedArrays.Uint8Array_prototype_subarray_Obj,
                        Int16Array_prototype_subarray_Addr, InitTypedArrays.Int16Array_prototype_subarray_Obj,
                        Uint16Array_prototype_subarray_Addr, InitTypedArrays.Uint16Array_prototype_subarray_Obj,
                        Int32Array_prototype_subarray_Addr, InitTypedArrays.Int32Array_prototype_subarray_Obj,
                        Uint32Array_prototype_subarray_Addr, InitTypedArrays.Uint32Array_prototype_subarray_Obj,
                        Float32Array_prototype_subarray_Addr, InitTypedArrays.Float32Array_prototype_subarray_Obj,
                        Float64Array_prototype_subarray_Addr, InitTypedArrays.Float64Array_prototype_subarray_Obj,
                        Dummy_Addr, InitArguments.Dummy_Obj
                        ),
                FHashMap.empty(),
                FHashSet.empty()
        );

        return new Interpreter.State(new Domains.StmtTerm(s), initEnv, initStore, new Domains.Scratchpad(FVector.empty()), new Domains.KontStack(List.list(Domains.HaltKont)), trace);
    }

    public static final FHashSet<Domains.AddressSpace.Address> keepInStore = FHashSet.build(Dummy_Arguments_Addr, Number_Addr);

    public static final FHashMap<JSClass, FHashSet<Domains.Str>> noenum = FHashMap.build(
            JSClass.CFunction, FHashSet.build(Domains.Str.alpha("length")),
            JSClass.CArray, FHashSet.build(Domains.Str.alpha("length")),
            JSClass.CString, FHashSet.build(Domains.Str.alpha("length")),
            JSClass.CArguments, FHashSet.build(Domains.Str.alpha("length")),
            JSClass.CRegexp, FHashSet.build(Domains.Str.alpha("source"), Domains.Str.alpha("global"), Domains.Str.alpha("ignoreCase"), Domains.Str.alpha("multiline"), Domains.Str.alpha("lastIndex")),
            JSClass.CObject_Obj, FHashSet.build(
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("create"),
                    Domains.Str.alpha("defineProperties"),
                    Domains.Str.alpha("defineProperty"),
                    Domains.Str.alpha("freeze"),
                    Domains.Str.alpha("getOwnPropertyDescriptor"),
                    Domains.Str.alpha("getOwnPropertyNames"),
                    Domains.Str.alpha("getPrototypeOf"),
                    Domains.Str.alpha("isExtensible"),
                    Domains.Str.alpha("isFrozen"),
                    Domains.Str.alpha("isSealed"),
                    Domains.Str.alpha("keys"),
                    Domains.Str.alpha("length"),
                    Domains.Str.alpha("preventExtensions"),
                    Domains.Str.alpha("seal")
            ),
            JSClass.CObject_prototype_Obj, FHashSet.build(
                    Domains.Str.alpha("constructor"),
                    Domains.Str.alpha("valueOf"),
                    Domains.Str.alpha("toString"),
                    Domains.Str.alpha("isPrototypeOf"),
                    Domains.Str.alpha("propertyIsEnumerable"),
                    Domains.Str.alpha("hasOwnProperty"),
                    Domains.Str.alpha("toLocaleString")
            ),
            JSClass.CArray_Obj, FHashSet.build(
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("isArray"),
                    Domains.Str.alpha("length")
            ),
            JSClass.CArray_prototype_Obj, FHashSet.build(
                    Domains.Str.alpha("constructor"),
                    Domains.Str.alpha("concat"),
                    Domains.Str.alpha("every"),
                    Domains.Str.alpha("filter"),
                    Domains.Str.alpha("forEach"),
                    Domains.Str.alpha("indexOf"),
                    Domains.Str.alpha("join"),
                    Domains.Str.alpha("lastIndexOf"),
                    Domains.Str.alpha("map"),
                    Domains.Str.alpha("pop"),
                    Domains.Str.alpha("push"),
                    Domains.Str.alpha("reduce"),
                    Domains.Str.alpha("reduceRight"),
                    Domains.Str.alpha("reverse"),
                    Domains.Str.alpha("shift"),
                    Domains.Str.alpha("slice"),
                    Domains.Str.alpha("some"),
                    Domains.Str.alpha("sort"),
                    Domains.Str.alpha("splice"),
                    Domains.Str.alpha("toLocaleString"),
                    Domains.Str.alpha("toString"),
                    Domains.Str.alpha("unshift")
            ),
            JSClass.CFunction_Obj, FHashSet.build(
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length")
            ),
            JSClass.CFunction_prototype_Obj, FHashSet.build(
                    Domains.Str.alpha("constructor"),
                    Domains.Str.alpha("apply"),
                    Domains.Str.alpha("call"),
                    Domains.Str.alpha("toString"),
                    Domains.Str.alpha("length")
            ),
            JSClass.CMath_Obj, FHashSet.build(
                    Domains.Str.alpha("E"),
                    Domains.Str.alpha("LN10"),
                    Domains.Str.alpha("LN2"),
                    Domains.Str.alpha("LOG2E"),
                    Domains.Str.alpha("LOG10E"),
                    Domains.Str.alpha("PI"),
                    Domains.Str.alpha("SQRT1_2"),
                    Domains.Str.alpha("SQRT2"),
                    Domains.Str.alpha("abs"),
                    Domains.Str.alpha("acos"),
                    Domains.Str.alpha("asin"),
                    Domains.Str.alpha("atan"),
                    Domains.Str.alpha("atan2"),
                    Domains.Str.alpha("ceil"),
                    Domains.Str.alpha("cos"),
                    Domains.Str.alpha("exp"),
                    Domains.Str.alpha("floor"),
                    Domains.Str.alpha("log"),
                    Domains.Str.alpha("max"),
                    Domains.Str.alpha("min"),
                    Domains.Str.alpha("pow"),
                    Domains.Str.alpha("random"),
                    Domains.Str.alpha("round"),
                    Domains.Str.alpha("sin"),
                    Domains.Str.alpha("sqrt"),
                    Domains.Str.alpha("tan")
            ),
            JSClass.CNumber_Obj, FHashSet.build(
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length"),
                    Domains.Str.alpha("MAX_VALUE"),
                    Domains.Str.alpha("MIN_VALUE"),
                    Domains.Str.alpha("NaN"),
                    Domains.Str.alpha("NEGATIVE_INFINITY"),
                    Domains.Str.alpha("POSITIVE_INFINITY")
            ),
            JSClass.CNumber_prototype_Obj, FHashSet.build(
                    Domains.Str.alpha("constructor"),
                    Domains.Str.alpha("toString"),
                    Domains.Str.alpha("toLocaleString"),
                    Domains.Str.alpha("valueOf"),
                    Domains.Str.alpha("toFixed"),
                    Domains.Str.alpha("toExponential"),
                    Domains.Str.alpha("toPrecision")
            ),
            JSClass.CString_Obj, FHashSet.build(
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length"),
                    Domains.Str.alpha("fromCharCode")
            ),
            JSClass.CString_prototype_Obj, FHashSet.build(
                    Domains.Str.alpha("constructor"),
                    Domains.Str.alpha("charAt"),
                    Domains.Str.alpha("charCodeAt"),
                    Domains.Str.alpha("concat"),
                    Domains.Str.alpha("indexOf"),
                    Domains.Str.alpha("lastIndexOf"),
                    Domains.Str.alpha("localeCompare"),
                    Domains.Str.alpha("match"),
                    Domains.Str.alpha("replace"),
                    Domains.Str.alpha("search"),
                    Domains.Str.alpha("slice"),
                    Domains.Str.alpha("split"),
                    Domains.Str.alpha("substr"),
                    Domains.Str.alpha("substring"),
                    Domains.Str.alpha("toLocaleLowerCase"),
                    Domains.Str.alpha("toLocaleUpperCase"),
                    Domains.Str.alpha("toLowerCase"),
                    Domains.Str.alpha("toString"),
                    Domains.Str.alpha("toUpperCase"),
                    Domains.Str.alpha("trim"),
                    Domains.Str.alpha("valueOf")
            ));

    public static final FHashMap<JSClass, FHashSet<Domains.Str>> nodelete = FHashMap.build(
            JSClass.CFunction, FHashSet.build(Domains.Str.alpha("length"), Domains.Str.alpha("prototype")),
            JSClass.CArray, FHashSet.build(Domains.Str.alpha("length")),
            JSClass.CString, FHashSet.build(Domains.Str.alpha("length")),
            JSClass.CRegexp, FHashSet.build(Domains.Str.alpha("source"), Domains.Str.alpha("global"), Domains.Str.alpha("ignoreCase"), Domains.Str.alpha("multiline"), Domains.Str.alpha("lastIndex")),
            // Note that the prototypes do not typically have _any_ non-modifiable properties.
            JSClass.CObject_Obj, FHashSet.build(
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length")
            ),
            JSClass.CArray_Obj, FHashSet.build(
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length")
            ),
            JSClass.CFunction_Obj, FHashSet.build(
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length")
            ),
            JSClass.CMath_Obj, FHashSet.build(
                    Domains.Str.alpha("E"),
                    Domains.Str.alpha("LN10"),
                    Domains.Str.alpha("LN2"),
                    Domains.Str.alpha("LOG2E"),
                    Domains.Str.alpha("LOG10E"),
                    Domains.Str.alpha("PI"),
                    Domains.Str.alpha("SQRT1_2"),
                    Domains.Str.alpha("SQRT2")
            ),
            JSClass.CNumber_Obj, FHashSet.build(
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length")
            ),
            JSClass.CString_Obj, FHashSet.build(
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length")
            ));

    public static final FHashMap<JSClass, FHashSet<Domains.Str>> noupdate = FHashMap.build(
            JSClass.CFunction, FHashSet.build(Domains.Str.alpha("length")),
            JSClass.CString, FHashSet.build(Domains.Str.alpha("length")),
            JSClass.CRegexp, FHashSet.build(Domains.Str.alpha("source"), Domains.Str.alpha("global"), Domains.Str.alpha("ignoreCase"), Domains.Str.alpha("multiline")),
            // Note that the prototypes do not typically have _any_ non-modifiable properties.
            JSClass.CObject_Obj, FHashSet.build(
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length")
            ),
            JSClass.CArray_Obj, FHashSet.build(
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length")
            ),
            JSClass.CFunction_Obj, FHashSet.build(
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length")
            ),
            JSClass.CMath_Obj, FHashSet.build(
                    Domains.Str.alpha("E"),
                    Domains.Str.alpha("LN10"),
                    Domains.Str.alpha("LN2"),
                    Domains.Str.alpha("LOG2E"),
                    Domains.Str.alpha("LOG10E"),
                    Domains.Str.alpha("PI"),
                    Domains.Str.alpha("SQRT1_2"),
                    Domains.Str.alpha("SQRT2")
            ),
            JSClass.CNumber_Obj, FHashSet.build(
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length")
            ),
            JSClass.CString_Obj, FHashSet.build(
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length")
            ));

    public static final FHashMap<Domains.AddressSpace.Address, JSClass> classFromAddress = FHashMap.build(
            Function_Addr, JSClass.CFunction,
            Array_Addr, JSClass.CArray,
            String_Addr, JSClass.CString,
            Boolean_Addr, JSClass.CBoolean,
            Number_Addr, JSClass.CNumber,
            Date_Addr, JSClass.CDate,
            Error_Addr, JSClass.CError,
            RegExp_Addr, JSClass.CRegexp,
            Arguments_Addr, JSClass.CArguments);

}
