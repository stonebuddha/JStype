package concrete.init;

import concrete.Domains;
import concrete.Interpreter;
import fj.data.List;
import immutable.FHashMap;
import immutable.FHashSet;
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
        Domains.Env env = new Domains.Env(
                FHashMap.build(window_Variable, window_binding_Addr));
        Domains.Store store = new Domains.Store(
                FHashMap.build(window_binding_Addr, window_Addr),
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
                        Boolean_Addr, InitMisc.Boolean_Obj,
                        Date_Addr, InitMisc.Date_Obj,
                        Error_Addr, InitMisc.Error_Obj,
                        Function_Addr, InitFunction.Function_Obj,
                        Function_prototype_Addr, InitFunction.Function_prototype_Obj,
                        Function_prototype_apply_Addr, InitFunction.Function_prototype_apply_Obj,
                        Function_prototype_call_Addr, InitFunction.Function_prototype_call_Obj,
                        Function_prototype_toString_Addr, InitFunction.Function_prototype_toString_Obj,
                        JSON_Addr, InitMisc.JSON_Obj,
                        Math_Addr, InitMath.Math_Obj,
                        Number_Addr, InitNumber.Number_Obj,
                        RegExp_Addr, InitMisc.RegExp_Obj,
                        String_Addr, InitString.String_Obj,
                        Object_Addr, InitObject.Object_Obj,
                        Object_create_Addr, InitObject.Object_create_Obj,
                        Object_defineProperties_Addr, InitObject.Object_defineProperties_Obj,
                        Object_defineProperty_Addr, InitObject.Object_defineProperty_Obj,
                        Object_freeze_Addr, InitObject.Object_freeze_Obj,
                        Object_getOwnPropertyDescriptor_Addr, InitObject.Object_getOwnPropertyDescriptor_Obj,
                        Object_getOwnPropertyNames_Addr, InitObject.Object_getOwnPropertyNames_Obj,
                        Object_getPrototypeOf_Addr, InitObject.Object_getPrototypeOf_Obj,
                        Object_isExtensible_Addr, InitObject.Object_isExtensible_Obj,
                        Object_isFrozen_Addr, InitObject.Object_isFrozen_Obj,
                        Object_isSealed_Addr, InitObject.Object_isSealed_Obj,
                        Object_keys_Addr, InitObject.Object_keys_Obj,
                        Object_preventExtensions_Addr, InitObject.Object_preventExtensions_Obj,
                        Object_seal_Addr, InitObject.Object_seal_Obj,
                        Object_prototype_Addr, InitObject.Object_prototype_Obj,
                        Object_prototype_valueOf_Addr, InitObject.Object_prototype_valueOf_Obj,
                        Object_prototype_toString_Addr, InitObject.Object_prototype_toString_Obj,
                        Object_prototype_isPrototypeOf_Addr, InitObject.Object_prototype_isPrototypeOf_Obj,
                        Object_prototype_propertyIsEnumerable_Addr, InitObject.Object_prototype_propertyIsEnumerable_Obj,
                        Object_prototype_hasOwnProperty_Addr, InitObject.Object_prototype_hasOwnProperty_Obj,
                        Object_prototype_toLocaleString_Addr, InitObject.Object_prototype_toLocaleString_Obj,
                        Array_prototype_Addr, InitArray.Array_prototype_Obj,
                        Array_isArray_Addr, InitArray.Array_isArray_Obj,
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
                        Boolean_prototype_Addr, InitMisc.Boolean_prototype_Obj,
                        Boolean_prototype_toString_Addr, InitMisc.Boolean_prototype_toString_Obj,
                        Boolean_prototype_valueOf_Addr, InitMisc.Boolean_prototype_valueOf_Obj,
                        Error_prototype_Addr, InitMisc.Error_prototype_Obj,
                        Error_prototype_toString_Addr, InitMisc.Error_prototype_toString_Obj,
                        JSON_parse_Addr, InitMisc.JSON_parse_Obj,
                        JSON_stringify_Addr, InitMisc.JSON_stringify_Obj,
                        Date_now_Addr, InitMisc.Date_now_Obj,
                        Date_parse_Addr, InitMisc.Date_parse_Obj,
                        Date_prototype_Addr, InitMisc.Date_prototype_Obj,
                        RegExp_prototype_Addr, InitMisc.RegExp_prototype_Obj,
                        Arguments_Addr, InitArguments.Arguments_Obj,
                        Dummy_Addr, InitUtils.createObj(FHashMap.empty())));
        return new Interpreter.State(
                new Domains.StmtTerm(s),
                env,
                store,
                Domains.Scratchpad.apply(0),
                new Domains.KontStack(List.list(Domains.HaltKont)));
    }

    public static final FHashMap<JSClass, FHashSet<Domains.Str>> noenum = FHashMap.build(
            JSClass.CFunction, FHashSet.build(new Domains.Str("length")),
            JSClass.CArray, FHashSet.build(new Domains.Str("length")),
            JSClass.CString, FHashSet.build(new Domains.Str("length")),
            JSClass.CArguments, FHashSet.build(new Domains.Str("length")),
            JSClass.CRegexp, FHashSet.build(new Domains.Str("source"),
                    new Domains.Str("global"),
                    new Domains.Str("ignoreCase"),
                    new Domains.Str("multiline"),
                    new Domains.Str("lastIndex")),
            JSClass.CObject_Obj, FHashSet.build(
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
            ),
            JSClass.CObject_prototype_Obj, FHashSet.build(
                    new Domains.Str("constructor"),
                    new Domains.Str("valueOf"),
                    new Domains.Str("toString"),
                    new Domains.Str("isPrototypeOf"),
                    new Domains.Str("propertyIsEnumerable"),
                    new Domains.Str("hasOwnProperty"),
                    new Domains.Str("toLocaleString")
            ),
            JSClass.CArray_Obj, FHashSet.build(
                    new Domains.Str("prototype"),
                    new Domains.Str("isArray"),
                    new Domains.Str("length")
            ),
            JSClass.CArray_prototype_Obj, FHashSet.build(
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
            ),
            JSClass.CFunction_Obj, FHashSet.build(
                    new Domains.Str("prototype"),
                    new Domains.Str("length")
            ),
            JSClass.CFunction_prototype_Obj, FHashSet.build(
                    new Domains.Str("constructor"),
                    new Domains.Str("apply"),
                    new Domains.Str("call"),
                    new Domains.Str("toString"),
                    new Domains.Str("length")
            ),
            JSClass.CMath_Obj, FHashSet.build(
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
            ),
            JSClass.CNumber_Obj, FHashSet.build(
                    new Domains.Str("prototype"),
                    new Domains.Str("length"),
                    new Domains.Str("MAX_VALUE"),
                    new Domains.Str("MIN_VALUE"),
                    new Domains.Str("NaN"),
                    new Domains.Str("NEGATIVE_INFINITY"),
                    new Domains.Str("POSITIVE_INFINITY")
            ),
            JSClass.CNumber_prototype_Obj, FHashSet.build(
                    new Domains.Str("constructor"),
                    new Domains.Str("toString"),
                    new Domains.Str("toLocaleString"),
                    new Domains.Str("valueOf"),
                    new Domains.Str("toFixed"),
                    new Domains.Str("toExponential"),
                    new Domains.Str("toPrecision")
            ),
            JSClass.CString_Obj, FHashSet.build(
                    new Domains.Str("prototype"),
                    new Domains.Str("length"),
                    new Domains.Str("fromCharCode")
            ),
            JSClass.CString_prototype_Obj, FHashSet.build(
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
            )
    );

    public static final FHashMap<JSClass, FHashSet<Domains.Str>> nodelete = FHashMap.build(
            JSClass.CFunction, FHashSet.build(new Domains.Str("length"), new Domains.Str("prototype")),
            JSClass.CArray, FHashSet.build(new Domains.Str("length")),
            JSClass.CString, FHashSet.build(new Domains.Str("length")),
            JSClass.CRegexp, FHashSet.build(
                    new Domains.Str("source"),
                    new Domains.Str("global"),
                    new Domains.Str("ignoreCase"),
                    new Domains.Str("multiline"),
                    new Domains.Str("lastIndex")),
            JSClass.CObject_Obj, FHashSet.build(
                    new Domains.Str("prototype"),
                    new Domains.Str("length")
            ),
            JSClass.CArray_Obj, FHashSet.build(
                    new Domains.Str("prototype"),
                    new Domains.Str("length")
            ),
            JSClass.CFunction_Obj, FHashSet.build(
                    new Domains.Str("prototype"),
                    new Domains.Str("length")
            ),
            JSClass.CMath_Obj, FHashSet.build(
                    new Domains.Str("E"),
                    new Domains.Str("LN10"),
                    new Domains.Str("LN2"),
                    new Domains.Str("LOG2E"),
                    new Domains.Str("LOG10E"),
                    new Domains.Str("PI"),
                    new Domains.Str("SQRT1_2"),
                    new Domains.Str("SQRT2")
            ),
            JSClass.CNumber_Obj, FHashSet.build(
                    new Domains.Str("prototype"),
                    new Domains.Str("length")
            ),
            JSClass.CString_Obj, FHashSet.build(
                    new Domains.Str("prototype"),
                    new Domains.Str("length")
            )
    );

    public static final FHashMap<JSClass, FHashSet<Domains.Str>> noupdate = FHashMap.build(
            JSClass.CFunction, FHashSet.build(new Domains.Str("length")),
            JSClass.CString, FHashSet.build(new Domains.Str("length")),
            JSClass.CRegexp, FHashSet.build(
                    new Domains.Str("source"),
                    new Domains.Str("global"),
                    new Domains.Str("ignoreCase"),
                    new Domains.Str("multiline")),
            JSClass.CObject_Obj, FHashSet.build(
                    new Domains.Str("prototype"),
                    new Domains.Str("length")
            ),
            JSClass.CArray_Obj, FHashSet.build(
                    new Domains.Str("prototype"),
                    new Domains.Str("length")
            ),
            JSClass.CFunction_Obj, FHashSet.build(
                    new Domains.Str("prototype"),
                    new Domains.Str("length")
            ),
            JSClass.CMath_Obj, FHashSet.build(
                    new Domains.Str("E"),
                    new Domains.Str("LN10"),
                    new Domains.Str("LN2"),
                    new Domains.Str("LOG2E"),
                    new Domains.Str("LOG10E"),
                    new Domains.Str("PI"),
                    new Domains.Str("SQRT1_2"),
                    new Domains.Str("SQRT2")
            ),
            JSClass.CNumber_Obj, FHashSet.build(
                    new Domains.Str("prototype"),
                    new Domains.Str("length")
            ),
            JSClass.CString_Obj, FHashSet.build(
                    new Domains.Str("prototype"),
                    new Domains.Str("length")
            )
    );

    public static final FHashMap<Domains.Address, JSClass> classFromAddress = FHashMap.build(
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
