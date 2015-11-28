package concrete.init;

import concrete.Domains;
import concrete.Interpreter;
import fj.Ord;
import fj.P;
import fj.data.List;
import fj.data.Set;
import fj.data.TreeMap;
import ir.IRPVar;
import ir.IRStmt;
import ir.JSClass;

/**
 * Created by wayne on 15/10/29.
 */
public class Init {

    public static final IRPVar window_Variable = new IRPVar(0);

    public static final Domains.Address window_binding_Addr = Domains.Address.generate();
    public static final Domains.Address window_Addr = Domains.Address.generate();

    public static final Domains.Address decodeURI_Addr = Domains.Address.generate();
    public static final Domains.Address decodeURIComponent_Addr = Domains.Address.generate();
    public static final Domains.Address encodeURI_Addr = Domains.Address.generate();
    public static final Domains.Address encodeURIComponent_Addr = Domains.Address.generate();
    public static final Domains.Address escape_Addr = Domains.Address.generate();
    public static final Domains.Address isFinite_Addr = Domains.Address.generate();
    public static final Domains.Address isNaN_Addr = Domains.Address.generate();
    public static final Domains.Address parseFloat_Addr = Domains.Address.generate();
    public static final Domains.Address parseInt_Addr = Domains.Address.generate();
    public static final Domains.Address unescape_Addr = Domains.Address.generate();
    public static final Domains.Address Array_Addr = Domains.Address.generate();
    public static final Domains.Address Boolean_Addr = Domains.Address.generate();
    public static final Domains.Address Date_Addr = Domains.Address.generate();
    public static final Domains.Address Error_Addr = Domains.Address.generate();
    public static final Domains.Address EvalError_Addr = Domains.Address.generate();
    public static final Domains.Address RangeError_Addr = Domains.Address.generate();
    public static final Domains.Address ReferenceError_Addr = Domains.Address.generate();
    public static final Domains.Address TypeError_Addr = Domains.Address.generate();
    public static final Domains.Address URIError_Addr = Domains.Address.generate();
    public static final Domains.Address Function_Addr = Domains.Address.generate();
    public static final Domains.Address JSON_Addr = Domains.Address.generate();
    public static final Domains.Address Math_Addr = Domains.Address.generate();
    public static final Domains.Address Number_Addr = Domains.Address.generate();
    public static final Domains.Address RegExp_Addr = Domains.Address.generate();
    public static final Domains.Address String_Addr = Domains.Address.generate();

    public static final Domains.Address Object_Addr = Domains.Address.generate();
    public static final Domains.Address Object_create_Addr = Domains.Address.generate();
    public static final Domains.Address Object_defineProperties_Addr = Domains.Address.generate();
    public static final Domains.Address Object_defineProperty_Addr = Domains.Address.generate();
    public static final Domains.Address Object_freeze_Addr = Domains.Address.generate();
    public static final Domains.Address Object_getOwnPropertyDescriptor_Addr = Domains.Address.generate();
    public static final Domains.Address Object_getOwnPropertyNames_Addr = Domains.Address.generate();
    public static final Domains.Address Object_getPrototypeOf_Addr = Domains.Address.generate();
    public static final Domains.Address Object_isExtensible_Addr = Domains.Address.generate();
    public static final Domains.Address Object_isFrozen_Addr = Domains.Address.generate();
    public static final Domains.Address Object_isSealed_Addr = Domains.Address.generate();
    public static final Domains.Address Object_keys_Addr = Domains.Address.generate();
    public static final Domains.Address Object_preventExtensions_Addr = Domains.Address.generate();
    public static final Domains.Address Object_seal_Addr = Domains.Address.generate();

    public static final Domains.Address Object_prototype_Addr = Domains.Address.generate();
    public static final Domains.Address Object_prototype_valueOf_Addr = Domains.Address.generate();
    public static final Domains.Address Object_prototype_toString_Addr = Domains.Address.generate();
    public static final Domains.Address Object_prototype_isPrototypeOf_Addr = Domains.Address.generate();
    public static final Domains.Address Object_prototype_propertyIsEnumerable_Addr = Domains.Address.generate();
    public static final Domains.Address Object_prototype_hasOwnProperty_Addr = Domains.Address.generate();
    public static final Domains.Address Object_prototype_toLocaleString_Addr = Domains.Address.generate();

    public static final Domains.Address Array_prototype_Addr = Domains.Address.generate();
    public static final Domains.Address Array_isArray_Addr = Domains.Address.generate();

    public static final Domains.Address Array_prototype_concat_Addr = Domains.Address.generate();
    public static final Domains.Address Array_prototype_every_Addr = Domains.Address.generate();
    public static final Domains.Address Array_prototype_filter_Addr = Domains.Address.generate();
    public static final Domains.Address Array_prototype_forEach_Addr = Domains.Address.generate();
    public static final Domains.Address Array_prototype_indexOf_Addr = Domains.Address.generate();
    public static final Domains.Address Array_prototype_join_Addr = Domains.Address.generate();
    public static final Domains.Address Array_prototype_lastIndexOf_Addr = Domains.Address.generate();
    public static final Domains.Address Array_prototype_map_Addr = Domains.Address.generate();
    public static final Domains.Address Array_prototype_pop_Addr = Domains.Address.generate();
    public static final Domains.Address Array_prototype_push_Addr = Domains.Address.generate();
    public static final Domains.Address Array_prototype_reduce_Addr = Domains.Address.generate();
    public static final Domains.Address Array_prototype_reduceRight_Addr = Domains.Address.generate();
    public static final Domains.Address Array_prototype_reverse_Addr = Domains.Address.generate();
    public static final Domains.Address Array_prototype_shift_Addr = Domains.Address.generate();
    public static final Domains.Address Array_prototype_slice_Addr = Domains.Address.generate();
    public static final Domains.Address Array_prototype_some_Addr = Domains.Address.generate();
    public static final Domains.Address Array_prototype_sort_Addr = Domains.Address.generate();
    public static final Domains.Address Array_prototype_splice_Addr = Domains.Address.generate();
    public static final Domains.Address Array_prototype_toLocaleString_Addr = Domains.Address.generate();
    public static final Domains.Address Array_prototype_toString_Addr = Domains.Address.generate();
    public static final Domains.Address Array_prototype_unshift_Addr = Domains.Address.generate();

    public static final Domains.Address Math_abs_Addr = Domains.Address.generate();
    public static final Domains.Address Math_acos_Addr = Domains.Address.generate();
    public static final Domains.Address Math_asin_Addr = Domains.Address.generate();
    public static final Domains.Address Math_atan_Addr = Domains.Address.generate();
    public static final Domains.Address Math_atan2_Addr = Domains.Address.generate();
    public static final Domains.Address Math_ceil_Addr = Domains.Address.generate();
    public static final Domains.Address Math_cos_Addr = Domains.Address.generate();
    public static final Domains.Address Math_exp_Addr = Domains.Address.generate();
    public static final Domains.Address Math_floor_Addr = Domains.Address.generate();
    public static final Domains.Address Math_log_Addr = Domains.Address.generate();
    public static final Domains.Address Math_max_Addr = Domains.Address.generate();
    public static final Domains.Address Math_min_Addr = Domains.Address.generate();
    public static final Domains.Address Math_pow_Addr = Domains.Address.generate();
    public static final Domains.Address Math_random_Addr = Domains.Address.generate();
    public static final Domains.Address Math_round_Addr = Domains.Address.generate();
    public static final Domains.Address Math_sin_Addr = Domains.Address.generate();
    public static final Domains.Address Math_sqrt_Addr = Domains.Address.generate();
    public static final Domains.Address Math_tan_Addr = Domains.Address.generate();

    public static final Domains.Address Function_prototype_Addr = Domains.Address.generate();

    public static final Domains.Address Function_prototype_apply_Addr = Domains.Address.generate();
    public static final Domains.Address Function_prototype_call_Addr = Domains.Address.generate();
    public static final Domains.Address Function_prototype_toString_Addr = Domains.Address.generate();

    public static final Domains.Address Number_prototype_Addr = Domains.Address.generate();

    public static final Domains.Address Number_prototype_toString_Addr = Domains.Address.generate();
    public static final Domains.Address Number_prototype_toLocaleString_Addr = Domains.Address.generate();
    public static final Domains.Address Number_prototype_valueOf_Addr = Domains.Address.generate();
    public static final Domains.Address Number_prototype_toFixed_Addr = Domains.Address.generate();
    public static final Domains.Address Number_prototype_toExponential_Addr = Domains.Address.generate();
    public static final Domains.Address Number_prototype_toPrecision_Addr = Domains.Address.generate();

    public static final Domains.Address String_prototype_Addr = Domains.Address.generate();
    public static final Domains.Address String_fromCharCode_Addr = Domains.Address.generate();

    public static final Domains.Address String_prototype_charAt_Addr = Domains.Address.generate();
    public static final Domains.Address String_prototype_charCodeAt_Addr = Domains.Address.generate();
    public static final Domains.Address String_prototype_concat_Addr = Domains.Address.generate();
    public static final Domains.Address String_prototype_indexOf_Addr = Domains.Address.generate();
    public static final Domains.Address String_prototype_lastIndexOf_Addr = Domains.Address.generate();
    public static final Domains.Address String_prototype_localeCompare_Addr = Domains.Address.generate();
    public static final Domains.Address String_prototype_match_Addr = Domains.Address.generate();
    public static final Domains.Address String_prototype_replace_Addr = Domains.Address.generate();
    public static final Domains.Address String_prototype_search_Addr = Domains.Address.generate();
    public static final Domains.Address String_prototype_slice_Addr = Domains.Address.generate();
    public static final Domains.Address String_prototype_split_Addr = Domains.Address.generate();
    public static final Domains.Address String_prototype_substr_Addr = Domains.Address.generate();
    public static final Domains.Address String_prototype_substring_Addr = Domains.Address.generate();
    public static final Domains.Address String_prototype_toLocaleLowerCase_Addr = Domains.Address.generate();
    public static final Domains.Address String_prototype_toLocaleUpperCase_Addr = Domains.Address.generate();
    public static final Domains.Address String_prototype_toLowerCase_Addr = Domains.Address.generate();
    public static final Domains.Address String_prototype_toString_Addr = Domains.Address.generate();
    public static final Domains.Address String_prototype_toUpperCase_Addr = Domains.Address.generate();
    public static final Domains.Address String_prototype_trim_Addr = Domains.Address.generate();
    public static final Domains.Address String_prototype_valueOf_Addr = Domains.Address.generate();

    public static final Domains.Address Boolean_prototype_Addr = Domains.Address.generate();

    public static final Domains.Address Boolean_prototype_toString_Addr = Domains.Address.generate();
    public static final Domains.Address Boolean_prototype_valueOf_Addr = Domains.Address.generate();

    public static final Domains.Address Error_prototype_Addr = Domains.Address.generate();

    public static final Domains.Address Error_prototype_toString_Addr = Domains.Address.generate();

    public static final Domains.Address JSON_parse_Addr = Domains.Address.generate();
    public static final Domains.Address JSON_stringify_Addr = Domains.Address.generate();

    public static final Domains.Address Date_now_Addr = Domains.Address.generate();
    public static final Domains.Address Date_parse_Addr = Domains.Address.generate();
    public static final Domains.Address Date_prototype_Addr = Domains.Address.generate();

    public static final Domains.Address RegExp_prototype_Addr = Domains.Address.generate();

    public static final Domains.Address Arguments_Addr = Domains.Address.generate();

    public static final Domains.Address Dummy_Addr = Domains.Address.generate();

    public static Interpreter.State initState(IRStmt s) {
        Domains.Env env = new Domains.Env(TreeMap.treeMap(Ord.hashEqualsOrd(),
                P.p(window_Variable, window_binding_Addr)));
        Domains.Store store = new Domains.Store(
                TreeMap.treeMap(Ord.hashEqualsOrd(),
                        P.p(window_binding_Addr, window_Addr)),
                TreeMap.treeMap(Ord.hashEqualsOrd(),
                        P.<Domains.Address, Domains.Object>p(window_Addr, InitGlobal.window_Obj),
                        P.<Domains.Address, Domains.Object>p(decodeURI_Addr, InitGlobal.decodeURI_Obj),
                        P.<Domains.Address, Domains.Object>p(decodeURIComponent_Addr, InitGlobal.decodeURIComponent_Obj),
                        P.<Domains.Address, Domains.Object>p(encodeURI_Addr, InitGlobal.encodeURI_Obj),
                        P.<Domains.Address, Domains.Object>p(encodeURIComponent_Addr, InitGlobal.encodeURIComponent_Obj),
                        P.<Domains.Address, Domains.Object>p(escape_Addr, InitGlobal.escape_Obj),
                        P.<Domains.Address, Domains.Object>p(isFinite_Addr, InitGlobal.isFinite_Obj),
                        P.<Domains.Address, Domains.Object>p(isNaN_Addr, InitGlobal.isNaN_Obj),
                        P.<Domains.Address, Domains.Object>p(parseFloat_Addr, InitGlobal.parseFloat_Obj),
                        P.<Domains.Address, Domains.Object>p(parseInt_Addr, InitGlobal.parseInt_Obj),
                        P.<Domains.Address, Domains.Object>p(unescape_Addr, InitGlobal.unescape_Obj),
//                        P.<Domains.Address, Domains.Object>p(Array_Addr, Array_Obj),
                        P.<Domains.Address, Domains.Object>p(Boolean_Addr, InitMisc.Boolean_Obj),
                        P.<Domains.Address, Domains.Object>p(Date_Addr, InitMisc.Date_Obj),
                        P.<Domains.Address, Domains.Object>p(Error_Addr, InitMisc.Error_Obj),
                        P.<Domains.Address, Domains.Object>p(Function_Addr, InitFunction.Function_Obj),
                        P.<Domains.Address, Domains.Object>p(Function_prototype_Addr, InitFunction.Function_prototype_Obj),
                        P.<Domains.Address, Domains.Object>p(Function_prototype_apply_Addr, InitFunction.Function_prototype_apply_Obj),
                        P.<Domains.Address, Domains.Object>p(Function_prototype_call_Addr, InitFunction.Function_prototype_call_Obj),
                        P.<Domains.Address, Domains.Object>p(Function_prototype_toString_Addr, InitFunction.Function_prototype_toString_Obj),
                        P.<Domains.Address, Domains.Object>p(JSON_Addr, InitMisc.JSON_Obj),
                        P.<Domains.Address, Domains.Object>p(Math_Addr, InitMath.Math_Obj),
                        P.<Domains.Address, Domains.Object>p(Number_Addr, InitNumber.Number_Obj),
                        P.<Domains.Address, Domains.Object>p(RegExp_Addr, InitMisc.RegExp_Obj),
                        P.<Domains.Address, Domains.Object>p(String_Addr, InitString.String_Obj),
                        P.<Domains.Address, Domains.Object>p(Object_Addr, InitObject.Object_Obj),
                        P.<Domains.Address, Domains.Object>p(Object_create_Addr, InitObject.Object_create_Obj),
                        P.<Domains.Address, Domains.Object>p(Object_defineProperties_Addr, InitObject.Object_defineProperties_Obj),
                        P.<Domains.Address, Domains.Object>p(Object_defineProperty_Addr, InitObject.Object_defineProperty_Obj),
                        P.<Domains.Address, Domains.Object>p(Object_freeze_Addr, InitObject.Object_freeze_Obj),
                        P.<Domains.Address, Domains.Object>p(Object_getOwnPropertyDescriptor_Addr, InitObject.Object_getOwnPropertyDescriptor_Obj),
                        P.<Domains.Address, Domains.Object>p(Object_getOwnPropertyNames_Addr, InitObject.Object_getOwnPropertyNames_Obj),
                        P.<Domains.Address, Domains.Object>p(Object_getPrototypeOf_Addr, InitObject.Object_getPrototypeOf_Obj),
                        P.<Domains.Address, Domains.Object>p(Object_isExtensible_Addr, InitObject.Object_isExtensible_Obj),
                        P.<Domains.Address, Domains.Object>p(Object_isFrozen_Addr, InitObject.Object_isFrozen_Obj),
                        P.<Domains.Address, Domains.Object>p(Object_isSealed_Addr, InitObject.Object_isSealed_Obj),
                        P.<Domains.Address, Domains.Object>p(Object_keys_Addr, InitObject.Object_keys_Obj),
                        P.<Domains.Address, Domains.Object>p(Object_preventExtensions_Addr, InitObject.Object_preventExtensions_Obj),
                        P.<Domains.Address, Domains.Object>p(Object_seal_Addr, InitObject.Object_seal_Obj),
                        P.<Domains.Address, Domains.Object>p(Object_prototype_Addr, InitObject.Object_prototype_Obj),
                        P.<Domains.Address, Domains.Object>p(Object_prototype_valueOf_Addr, InitObject.Object_prototype_valueOf_Obj),
                        P.<Domains.Address, Domains.Object>p(Object_prototype_toString_Addr, InitObject.Object_prototype_toString_Obj),
                        P.<Domains.Address, Domains.Object>p(Object_prototype_isPrototypeOf_Addr, InitObject.Object_prototype_isPrototypeOf_Obj),
                        P.<Domains.Address, Domains.Object>p(Object_prototype_propertyIsEnumerable_Addr, InitObject.Object_prototype_propertyIsEnumerable_Obj),
                        P.<Domains.Address, Domains.Object>p(Object_prototype_hasOwnProperty_Addr, InitObject.Object_prototype_hasOwnProperty_Obj),
                        P.<Domains.Address, Domains.Object>p(Object_prototype_toLocaleString_Addr, InitObject.Object_prototype_toLocaleString_Obj),
//                        P.<Domains.Address, Domains.Object>p(Array_prototype_Addr, Array_prototype_Obj),
//                        P.<Domains.Address, Domains.Object>p(Array_isArray_Addr, Array_isArray_Obj),
//                        P.<Domains.Address, Domains.Object>p(Array_prototype_concat_Addr, Array_prototype_concat_Obj),
//                        P.<Domains.Address, Domains.Object>p(Array_prototype_every_Addr, Array_prototype_every_Obj),
//                        P.<Domains.Address, Domains.Object>p(Array_prototype_filter_Addr, Array_prototype_filter_Obj),
//                        P.<Domains.Address, Domains.Object>p(Array_prototype_forEach_Addr, Array_prototype_forEach_Obj),
//                        P.<Domains.Address, Domains.Object>p(Array_prototype_indexOf_Addr, Array_prototype_indexOf_Obj),
//                        P.<Domains.Address, Domains.Object>p(Array_prototype_join_Addr, Array_prototype_join_Obj),
//                        P.<Domains.Address, Domains.Object>p(Array_prototype_lastIndexOf_Addr, Array_prototype_lastIndexOf_Obj),
//                        P.<Domains.Address, Domains.Object>p(Array_prototype_map_Addr, Array_prototype_map_Obj),
//                        P.<Domains.Address, Domains.Object>p(Array_prototype_pop_Addr, Array_prototype_pop_Obj),
//                        P.<Domains.Address, Domains.Object>p(Array_prototype_push_Addr, Array_prototype_push_Obj),
//                        P.<Domains.Address, Domains.Object>p(Array_prototype_reduce_Addr, Array_prototype_reduce_Obj),
//                        P.<Domains.Address, Domains.Object>p(Array_prototype_reduceRight_Addr, Array_prototype_reduceRight_Obj),
//                        P.<Domains.Address, Domains.Object>p(Array_prototype_reverse_Addr, Array_prototype_reverse_Obj),
//                        P.<Domains.Address, Domains.Object>p(Array_prototype_shift_Addr, Array_prototype_shift_Obj),
//                        P.<Domains.Address, Domains.Object>p(Array_prototype_slice_Addr, Array_prototype_slice_Obj),
//                        P.<Domains.Address, Domains.Object>p(Array_prototype_some_Addr, Array_prototype_some_Obj),
//                        P.<Domains.Address, Domains.Object>p(Array_prototype_sort_Addr, Array_prototype_sort_Obj),
//                        P.<Domains.Address, Domains.Object>p(Array_prototype_splice_Addr, Array_prototype_splice_Obj),
//                        P.<Domains.Address, Domains.Object>p(Array_prototype_toLocaleString_Addr, Array_prototype_toLocaleString_Obj),
//                        P.<Domains.Address, Domains.Object>p(Array_prototype_toString_Addr, Array_prototype_toString_Obj),
//                        P.<Domains.Address, Domains.Object>p(Array_prototype_unshift_Addr, Array_prototype_unshift_Obj),
                        P.<Domains.Address, Domains.Object>p(Math_abs_Addr, InitMath.Math_abs_Obj),
                        P.<Domains.Address, Domains.Object>p(Math_acos_Addr, InitMath.Math_acos_Obj),
                        P.<Domains.Address, Domains.Object>p(Math_asin_Addr, InitMath.Math_asin_Obj),
                        P.<Domains.Address, Domains.Object>p(Math_atan_Addr, InitMath.Math_atan_Obj),
                        P.<Domains.Address, Domains.Object>p(Math_atan2_Addr, InitMath.Math_atan2_Obj),
                        P.<Domains.Address, Domains.Object>p(Math_ceil_Addr, InitMath.Math_ceil_Obj),
                        P.<Domains.Address, Domains.Object>p(Math_cos_Addr, InitMath.Math_cos_Obj),
                        P.<Domains.Address, Domains.Object>p(Math_exp_Addr, InitMath.Math_exp_Obj),
                        P.<Domains.Address, Domains.Object>p(Math_floor_Addr, InitMath.Math_floor_Obj),
                        P.<Domains.Address, Domains.Object>p(Math_log_Addr, InitMath.Math_log_Obj),
                        P.<Domains.Address, Domains.Object>p(Math_max_Addr, InitMath.Math_max_Obj),
                        P.<Domains.Address, Domains.Object>p(Math_min_Addr, InitMath.Math_min_Obj),
                        P.<Domains.Address, Domains.Object>p(Math_pow_Addr, InitMath.Math_pow_Obj),
                        P.<Domains.Address, Domains.Object>p(Math_random_Addr, InitMath.Math_random_Obj),
                        P.<Domains.Address, Domains.Object>p(Math_round_Addr, InitMath.Math_round_Obj),
                        P.<Domains.Address, Domains.Object>p(Math_sin_Addr, InitMath.Math_sin_Obj),
                        P.<Domains.Address, Domains.Object>p(Math_sqrt_Addr, InitMath.Math_sqrt_Obj),
                        P.<Domains.Address, Domains.Object>p(Math_tan_Addr, InitMath.Math_tan_Obj),
                        P.<Domains.Address, Domains.Object>p(Function_prototype_Addr, InitFunction.Function_prototype_Obj),
                        P.<Domains.Address, Domains.Object>p(Number_prototype_Addr, InitNumber.Number_prototype_Obj),
                        P.<Domains.Address, Domains.Object>p(Number_prototype_toString_Addr, InitNumber.Number_prototype_toString_Obj),
                        P.<Domains.Address, Domains.Object>p(Number_prototype_toLocaleString_Addr, InitNumber.Number_prototype_toLocaleString_Obj),
                        P.<Domains.Address, Domains.Object>p(Number_prototype_valueOf_Addr, InitNumber.Number_prototype_valueOf_Obj),
                        P.<Domains.Address, Domains.Object>p(Number_prototype_toFixed_Addr, InitNumber.Number_prototype_toFixed_Obj),
                        P.<Domains.Address, Domains.Object>p(Number_prototype_toExponential_Addr, InitNumber.Number_prototype_toExponential_Obj),
                        P.<Domains.Address, Domains.Object>p(Number_prototype_toPrecision_Addr, InitNumber.Number_prototype_toPrecision_Obj),
                        P.<Domains.Address, Domains.Object>p(String_prototype_Addr, InitString.String_prototype_Obj),
                        P.<Domains.Address, Domains.Object>p(String_fromCharCode_Addr, InitString.String_fromCharCode_Obj),
                        P.<Domains.Address, Domains.Object>p(String_prototype_charAt_Addr, InitString.String_prototype_charAt_Obj),
                        P.<Domains.Address, Domains.Object>p(String_prototype_charCodeAt_Addr, InitString.String_prototype_charCodeAt_Obj),
                        P.<Domains.Address, Domains.Object>p(String_prototype_concat_Addr, InitString.String_prototype_concat_Obj),
                        P.<Domains.Address, Domains.Object>p(String_prototype_indexOf_Addr, InitString.String_prototype_indexOf_Obj),
                        P.<Domains.Address, Domains.Object>p(String_prototype_lastIndexOf_Addr, InitString.String_prototype_lastIndexOf_Obj),
                        P.<Domains.Address, Domains.Object>p(String_prototype_localeCompare_Addr, InitString.String_prototype_localeCompare_Obj),
                        P.<Domains.Address, Domains.Object>p(String_prototype_match_Addr, InitString.String_prototype_match_Obj),
                        P.<Domains.Address, Domains.Object>p(String_prototype_replace_Addr, InitString.String_prototype_replace_Obj),
                        P.<Domains.Address, Domains.Object>p(String_prototype_search_Addr, InitString.String_prototype_search_Obj),
                        P.<Domains.Address, Domains.Object>p(String_prototype_slice_Addr, InitString.String_prototype_slice_Obj),
                        P.<Domains.Address, Domains.Object>p(String_prototype_split_Addr, InitString.String_prototype_split_Obj),
                        P.<Domains.Address, Domains.Object>p(String_prototype_substr_Addr, InitString.String_prototype_substr_Obj),
                        P.<Domains.Address, Domains.Object>p(String_prototype_substring_Addr, InitString.String_prototype_substring_Obj),
                        P.<Domains.Address, Domains.Object>p(String_prototype_toLocaleLowerCase_Addr, InitString.String_prototype_toLocaleLowerCase_Obj),
                        P.<Domains.Address, Domains.Object>p(String_prototype_toLocaleUpperCase_Addr, InitString.String_prototype_toLocaleUpperCase_Obj),
                        P.<Domains.Address, Domains.Object>p(String_prototype_toLowerCase_Addr, InitString.String_prototype_toLowerCase_Obj),
                        P.<Domains.Address, Domains.Object>p(String_prototype_toString_Addr, InitString.String_prototype_toString_Obj),
                        P.<Domains.Address, Domains.Object>p(String_prototype_toUpperCase_Addr, InitString.String_prototype_toUpperCase_Obj),
                        P.<Domains.Address, Domains.Object>p(String_prototype_trim_Addr, InitString.String_prototype_trim_Obj),
                        P.<Domains.Address, Domains.Object>p(String_prototype_valueOf_Addr, InitString.String_prototype_valueOf_Obj),
                        P.<Domains.Address, Domains.Object>p(Boolean_prototype_Addr, InitMisc.Boolean_prototype_Obj),
                        P.<Domains.Address, Domains.Object>p(Boolean_prototype_toString_Addr, InitMisc.Boolean_prototype_toString_Obj),
                        P.<Domains.Address, Domains.Object>p(Boolean_prototype_valueOf_Addr, InitMisc.Boolean_prototype_valueOf_Obj),
                        P.<Domains.Address, Domains.Object>p(Error_prototype_Addr, InitMisc.Error_prototype_Obj),
                        P.<Domains.Address, Domains.Object>p(Error_prototype_toString_Addr, InitMisc.Error_prototype_toString_Obj),
                        P.<Domains.Address, Domains.Object>p(JSON_parse_Addr, InitMisc.JSON_parse_Obj),
                        P.<Domains.Address, Domains.Object>p(JSON_stringify_Addr, InitMisc.JSON_stringify_Obj),
                        P.<Domains.Address, Domains.Object>p(Date_now_Addr, InitMisc.Date_now_Obj),
                        P.<Domains.Address, Domains.Object>p(Date_parse_Addr, InitMisc.Date_parse_Obj),
                        P.<Domains.Address, Domains.Object>p(Date_prototype_Addr, InitMisc.Date_prototype_Obj),
                        P.<Domains.Address, Domains.Object>p(RegExp_prototype_Addr, InitMisc.RegExp_prototype_Obj),
                        P.<Domains.Address, Domains.Object>p(Arguments_Addr, InitArguments.Arguments_Obj),
                        P.<Domains.Address, Domains.Object>p(Dummy_Addr, InitUtils.createObj(TreeMap.empty(Ord.hashEqualsOrd())))));
        return new Interpreter.State(
                new Domains.StmtTerm(s),
                env,
                store,
                Domains.Scratchpad.apply(0),
                new Domains.KontStack(List.list(Domains.HaltKont)));
    }

    public static final TreeMap<JSClass, Set<Domains.Str>> noenum = TreeMap.treeMap(Ord.hashEqualsOrd(),
            P.p(JSClass.CFunction, Set.set(Ord.hashEqualsOrd(), new Domains.Str("length"))),
            P.p(JSClass.CArray, Set.set(Ord.hashEqualsOrd(), new Domains.Str("length"))),
            P.p(JSClass.CString, Set.set(Ord.hashEqualsOrd(), new Domains.Str("length"))),
            P.p(JSClass.CArguments, Set.set(Ord.hashEqualsOrd(), new Domains.Str("length"))),
            P.p(JSClass.CRegexp, Set.set(Ord.hashEqualsOrd(), new Domains.Str("sourse"),
                    new Domains.Str("global"),
                    new Domains.Str("ignoreCase"),
                    new Domains.Str("multiline"),
                    new Domains.Str("lastIndex"))),
            P.p(JSClass.CObject_Obj, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("prototype"),
                    new Domains.Str("create"),
                    new Domains.Str("defineProperties"),
                    new Domains.Str("defineProperty"),
                    new Domains.Str("freeze"),
                    new Domains.Str("getOwnPropertyDescriptor"),
                    new Domains.Str("getOwnPropertyNames"),
                    new Domains.Str("getPrototypeOf"),
                    new Domains.Str("isExtensible"),
                    new Domains.Str("isFrozen"),
                    new Domains.Str("isSealed"),
                    new Domains.Str("keys"),
                    new Domains.Str("length"),
                    new Domains.Str("preventExtensions"),
                    new Domains.Str("seal")
            )),
            P.p(JSClass.CObject_prototype_Obj, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("constructor"),
                    new Domains.Str("valueOf"),
                    new Domains.Str("toString"),
                    new Domains.Str("isPrototypeOf"),
                    new Domains.Str("propertyIsEnumerable"),
                    new Domains.Str("hasOwnProperty"),
                    new Domains.Str("toLocaleString")
            )),
            P.p(JSClass.CArray_Obj, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("prototype"),
                    new Domains.Str("isArray"),
                    new Domains.Str("length")
            )),
            P.p(JSClass.CArray_prototype_Obj, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("constructor"),
                    new Domains.Str("concat"),
                    new Domains.Str("every"),
                    new Domains.Str("filter"),
                    new Domains.Str("forEach"),
                    new Domains.Str("indexOf"),
                    new Domains.Str("join"),
                    new Domains.Str("lastIndexOf"),
                    new Domains.Str("map"),
                    new Domains.Str("pop"),
                    new Domains.Str("push"),
                    new Domains.Str("reduce"),
                    new Domains.Str("reduceRight"),
                    new Domains.Str("reverse"),
                    new Domains.Str("shift"),
                    new Domains.Str("slice"),
                    new Domains.Str("some"),
                    new Domains.Str("sort"),
                    new Domains.Str("splice"),
                    new Domains.Str("toLocaleString"),
                    new Domains.Str("toString"),
                    new Domains.Str("unshift")
            )),
            P.p(JSClass.CFunction_Obj, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("prototype"),
                    new Domains.Str("length")
            )),
            P.p(JSClass.CFunction_prototype_Obj, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("constructor"),
                    new Domains.Str("apply"),
                    new Domains.Str("call"),
                    new Domains.Str("toString"),
                    new Domains.Str("length")
            )),
            P.p(JSClass.CMath_Obj, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("E"),
                    new Domains.Str("LN10"),
                    new Domains.Str("LN2"),
                    new Domains.Str("LOG2E"),
                    new Domains.Str("LOG10E"),
                    new Domains.Str("PI"),
                    new Domains.Str("SQRT1_2"),
                    new Domains.Str("SQRT2"),
                    new Domains.Str("abs"),
                    new Domains.Str("acos"),
                    new Domains.Str("asin"),
                    new Domains.Str("atan"),
                    new Domains.Str("atan2"),
                    new Domains.Str("ceil"),
                    new Domains.Str("cos"),
                    new Domains.Str("exp"),
                    new Domains.Str("floor"),
                    new Domains.Str("log"),
                    new Domains.Str("max"),
                    new Domains.Str("min"),
                    new Domains.Str("pow"),
                    new Domains.Str("random"),
                    new Domains.Str("round"),
                    new Domains.Str("sin"),
                    new Domains.Str("sqrt"),
                    new Domains.Str("tan")
            )),
            P.p(JSClass.CNumber_Obj, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("prototype"),
                    new Domains.Str("length"),
                    new Domains.Str("MAX_VALUE"),
                    new Domains.Str("MIN_VALUE"),
                    new Domains.Str("NaN"),
                    new Domains.Str("NEGATIVE_INFINITY"),
                    new Domains.Str("POSITIVE_INFINITY")
            )),
            P.p(JSClass.CNumber_prototype_Obj, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("constructor"),
                    new Domains.Str("toString"),
                    new Domains.Str("toLocaleString"),
                    new Domains.Str("valueOf"),
                    new Domains.Str("toFixed"),
                    new Domains.Str("toExponential"),
                    new Domains.Str("toPrecision")
            )),
            P.p(JSClass.CString_Obj, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("prototype"),
                    new Domains.Str("length"),
                    new Domains.Str("fromCharCode")
            )),
            P.p(JSClass.CString_prototype_Obj, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("constructor"),
                    new Domains.Str("charAt"),
                    new Domains.Str("charCodeAt"),
                    new Domains.Str("concat"),
                    new Domains.Str("indexOf"),
                    new Domains.Str("lastIndexOf"),
                    new Domains.Str("localeCompare"),
                    new Domains.Str("match"),
                    new Domains.Str("replace"),
                    new Domains.Str("search"),
                    new Domains.Str("slice"),
                    new Domains.Str("split"),
                    new Domains.Str("substr"),
                    new Domains.Str("substring"),
                    new Domains.Str("toLocaleLowerCase"),
                    new Domains.Str("toLocaleUpperCase"),
                    new Domains.Str("toLowerCase"),
                    new Domains.Str("toString"),
                    new Domains.Str("toUpperCase"),
                    new Domains.Str("trim"),
                    new Domains.Str("valueOf")
            ))
    );

    public static final TreeMap<JSClass, Set<Domains.Str>> nodelete = TreeMap.treeMap(Ord.hashEqualsOrd(),
            P.p(JSClass.CFunction, Set.set(Ord.hashEqualsOrd(), new Domains.Str("length"), new Domains.Str("prototype"))),
            P.p(JSClass.CArray, Set.set(Ord.hashEqualsOrd(), new Domains.Str("length"))),
            P.p(JSClass.CString, Set.set(Ord.hashEqualsOrd(), new Domains.Str("length"))),
            P.p(JSClass.CRegexp, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("source"),
                    new Domains.Str("global"),
                    new Domains.Str("ignoreCase"),
                    new Domains.Str("multiline"),
                    new Domains.Str("lastIndex"))),
            P.p(JSClass.CObject_Obj, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("prototype"),
                    new Domains.Str("length")
            )),
            P.p(JSClass.CArray_Obj, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("prototype"),
                    new Domains.Str("length")
            )),
            P.p(JSClass.CFunction_Obj, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("prototype"),
                    new Domains.Str("length")
            )),
            P.p(JSClass.CMath_Obj, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("E"),
                    new Domains.Str("LN10"),
                    new Domains.Str("LN2"),
                    new Domains.Str("LOG2E"),
                    new Domains.Str("LOG10E"),
                    new Domains.Str("PI"),
                    new Domains.Str("SQRT1_2"),
                    new Domains.Str("SQRT2")
            )),
            P.p(JSClass.CNumber_Obj, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("prototype"),
                    new Domains.Str("length")
            )),
            P.p(JSClass.CString_Obj, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("prototype"),
                    new Domains.Str("length")
            ))
    );

    public static final TreeMap<JSClass, Set<Domains.Str>> noupdate = TreeMap.treeMap(Ord.hashEqualsOrd(),
            P.p(JSClass.CFunction, Set.set(Ord.hashEqualsOrd(), new Domains.Str("length"))),
            P.p(JSClass.CString, Set.set(Ord.hashEqualsOrd(), new Domains.Str("length"))),
            P.p(JSClass.CRegexp, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("source"),
                    new Domains.Str("global"),
                    new Domains.Str("ignoreCase"),
                    new Domains.Str("multiline"))),
            P.p(JSClass.CObject_Obj, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("prototype"),
                    new Domains.Str("length")
            )),
            P.p(JSClass.CArray_Obj, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("prototype"),
                    new Domains.Str("length")
            )),
            P.p(JSClass.CFunction_Obj, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("prototype"),
                    new Domains.Str("length")
            )),
            P.p(JSClass.CMath_Obj, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("E"),
                    new Domains.Str("LN10"),
                    new Domains.Str("LN2"),
                    new Domains.Str("LOG2E"),
                    new Domains.Str("LOG10E"),
                    new Domains.Str("PI"),
                    new Domains.Str("SQRT1_2"),
                    new Domains.Str("SQRT2")
            )),
            P.p(JSClass.CNumber_Obj, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("prototype"),
                    new Domains.Str("length")
            )),
            P.p(JSClass.CString_Obj, Set.set(Ord.hashEqualsOrd(),
                    new Domains.Str("prototype"),
                    new Domains.Str("length")
            ))
    );

    public static final TreeMap<Domains.Address, JSClass> classFromAddress = TreeMap.treeMap(Ord.hashEqualsOrd(),
            P.p(Function_Addr, JSClass.CFunction),
            P.p(Array_Addr, JSClass.CArray),
            P.p(String_Addr, JSClass.CString),
            P.p(Boolean_Addr, JSClass.CBoolean),
            P.p(Number_Addr, JSClass.CNumber),
            P.p(Date_Addr, JSClass.CDate),
            P.p(Error_Addr, JSClass.CError),
            P.p(RegExp_Addr, JSClass.CRegexp),
            P.p(Arguments_Addr, JSClass.CArguments));
}
