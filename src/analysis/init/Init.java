package analysis.init;

import analysis.Domains;
import analysis.Interpreter;
import analysis.Traces.Trace;
import fj.Ord;
import fj.P;
import fj.data.List;
import fj.data.Seq;
import fj.data.Set;
import fj.data.TreeMap;
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
        Domains.Env initEnv = new Domains.Env(TreeMap.treeMap(Ord.hashEqualsOrd(), P.p(window_Variable, Domains.AddressSpace.Addresses.apply(window_binding_Addr))));

        Domains.Store initStore = new Domains.Store(
                TreeMap.treeMap(Ord.hashEqualsOrd(), P.p(window_binding_Addr, Domains.AddressSpace.Address.inject(window_Addr))),
                TreeMap.treeMap(Ord.hashEqualsOrd(),
                        P.p(window_Addr, InitGlobal.window_Obj)),
                //TODO
                TreeMap.empty(Ord.hashEqualsOrd()),
                Set.empty(Ord.<Domains.AddressSpace.Address>hashEqualsOrd())
        );

        return new Interpreter.State(new Domains.StmtTerm(s), initEnv, initStore, new Domains.Scratchpad(Seq.<Domains.BValue>empty()), new Domains.KontStack(List.<Domains.Kont>nil()), trace);
    }

    public static final Set<Domains.AddressSpace.Address> keepInStore = Set.set(Ord.<Domains.AddressSpace.Address>hashEqualsOrd(), Dummy_Arguments_Addr, Number_Addr);

    public static final TreeMap<JSClass, Set<Domains.Str>> noenum = TreeMap.treeMap(Ord.<JSClass>hashEqualsOrd(),
            P.p(JSClass.CFunction, Set.set(Ord.<Domains.Str>hashEqualsOrd(), Domains.Str.alpha("length"))),
            P.p(JSClass.CArray, Set.set(Ord.<Domains.Str>hashEqualsOrd(), Domains.Str.alpha("length"))),
            P.p(JSClass.CString, Set.set(Ord.<Domains.Str>hashEqualsOrd(), Domains.Str.alpha("length"))),
            P.p(JSClass.CArguments, Set.set(Ord.<Domains.Str>hashEqualsOrd(), Domains.Str.alpha("length"))),
            P.p(JSClass.CRegexp, Set.set(Ord.<Domains.Str>hashEqualsOrd(), Domains.Str.alpha("source"), Domains.Str.alpha("global"), Domains.Str.alpha("ignoreCase"), Domains.Str.alpha("multiline"), Domains.Str.alpha("lastIndex"))),
            P.p(JSClass.CObject_Obj, Set.set(Ord.<Domains.Str>hashEqualsOrd(),
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
            )),
            P.p(JSClass.CObject_prototype_Obj, Set.set(Ord.<Domains.Str>hashEqualsOrd(),
                    Domains.Str.alpha("constructor"),
                    Domains.Str.alpha("valueOf"),
                    Domains.Str.alpha("toString"),
                    Domains.Str.alpha("isPrototypeOf"),
                    Domains.Str.alpha("propertyIsEnumerable"),
                    Domains.Str.alpha("hasOwnProperty"),
                    Domains.Str.alpha("toLocaleString")
            )),
            P.p(JSClass.CArray_Obj, Set.set(Ord.<Domains.Str>hashEqualsOrd(),
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("isArray"),
                    Domains.Str.alpha("length")
            )),
            P.p(JSClass.CArray_prototype_Obj, Set.set(Ord.<Domains.Str>hashEqualsOrd(),
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
            )),
            P.p(JSClass.CFunction_Obj, Set.set(Ord.<Domains.Str>hashEqualsOrd(),
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length")
            )),
            P.p(JSClass.CFunction_prototype_Obj, Set.set(Ord.<Domains.Str>hashEqualsOrd(),
                    Domains.Str.alpha("constructor"),
                    Domains.Str.alpha("apply"),
                    Domains.Str.alpha("call"),
                    Domains.Str.alpha("toString"),
                    Domains.Str.alpha("length")
            )),
            P.p(JSClass.CMath_Obj, Set.set(Ord.<Domains.Str>hashEqualsOrd(),
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
            )),
            P.p(JSClass.CNumber_Obj, Set.set(Ord.<Domains.Str>hashEqualsOrd(),
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length"),
                    Domains.Str.alpha("MAX_VALUE"),
                    Domains.Str.alpha("MIN_VALUE"),
                    Domains.Str.alpha("NaN"),
                    Domains.Str.alpha("NEGATIVE_INFINITY"),
                    Domains.Str.alpha("POSITIVE_INFINITY")
            )),
            P.p(JSClass.CNumber_prototype_Obj, Set.set(Ord.<Domains.Str>hashEqualsOrd(),
                    Domains.Str.alpha("constructor"),
                    Domains.Str.alpha("toString"),
                    Domains.Str.alpha("toLocaleString"),
                    Domains.Str.alpha("valueOf"),
                    Domains.Str.alpha("toFixed"),
                    Domains.Str.alpha("toExponential"),
                    Domains.Str.alpha("toPrecision")
            )),
            P.p(JSClass.CString_Obj, Set.set(Ord.<Domains.Str>hashEqualsOrd(),
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length"),
                    Domains.Str.alpha("fromCharCode")
            )),
            P.p(JSClass.CString_prototype_Obj, Set.set(Ord.<Domains.Str>hashEqualsOrd(),
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
            )));

    public static final TreeMap<JSClass, Set<Domains.Str>> nodelete = TreeMap.treeMap(Ord.<JSClass>hashEqualsOrd(),
            P.p(JSClass.CFunction, Set.set(Ord.<Domains.Str>hashEqualsOrd(), Domains.Str.alpha("length"), Domains.Str.alpha("prototype"))),
            P.p(JSClass.CArray, Set.set(Ord.<Domains.Str>hashEqualsOrd(), Domains.Str.alpha("length"))),
            P.p(JSClass.CString, Set.set(Ord.<Domains.Str>hashEqualsOrd(), Domains.Str.alpha("length"))),
            P.p(JSClass.CRegexp, Set.set(Ord.<Domains.Str>hashEqualsOrd(), Domains.Str.alpha("source"), Domains.Str.alpha("global"), Domains.Str.alpha("ignoreCase"), Domains.Str.alpha("multiline"), Domains.Str.alpha("lastIndex"))),
            // Note that the prototypes do not typically have _any_ non-modifiable properties.
            P.p(JSClass.CObject_Obj, Set.set(Ord.<Domains.Str>hashEqualsOrd(),
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length")
            )),
            P.p(JSClass.CArray_Obj, Set.set(Ord.<Domains.Str>hashEqualsOrd(),
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length")
            )),
            P.p(JSClass.CFunction_Obj, Set.set(Ord.<Domains.Str>hashEqualsOrd(),
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length")
            )),
            P.p(JSClass.CMath_Obj, Set.set(Ord.<Domains.Str>hashEqualsOrd(),
                    Domains.Str.alpha("E"),
                    Domains.Str.alpha("LN10"),
                    Domains.Str.alpha("LN2"),
                    Domains.Str.alpha("LOG2E"),
                    Domains.Str.alpha("LOG10E"),
                    Domains.Str.alpha("PI"),
                    Domains.Str.alpha("SQRT1_2"),
                    Domains.Str.alpha("SQRT2")
            )),
            P.p(JSClass.CNumber_Obj, Set.set(Ord.<Domains.Str>hashEqualsOrd(),
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length")
            )),
            P.p(JSClass.CString_Obj, Set.set(Ord.<Domains.Str>hashEqualsOrd(),
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length")
            )));

    public static final TreeMap<JSClass, Set<Domains.Str>> noupdate = TreeMap.treeMap(Ord.<JSClass>hashEqualsOrd(),
            P.p(JSClass.CFunction, Set.set(Ord.<Domains.Str>hashEqualsOrd(), Domains.Str.alpha("length"))),
            P.p(JSClass.CString, Set.set(Ord.<Domains.Str>hashEqualsOrd(), Domains.Str.alpha("length"))),
            P.p(JSClass.CRegexp, Set.set(Ord.<Domains.Str>hashEqualsOrd(), Domains.Str.alpha("source"), Domains.Str.alpha("global"), Domains.Str.alpha("ignoreCase"), Domains.Str.alpha("multiline"))),
            // Note that the prototypes do not typically have _any_ non-modifiable properties.
            P.p(JSClass.CObject_Obj, Set.set(Ord.<Domains.Str>hashEqualsOrd(),
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length")
            )),
            P.p(JSClass.CArray_Obj, Set.set(Ord.<Domains.Str>hashEqualsOrd(),
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length")
            )),
            P.p(JSClass.CFunction_Obj, Set.set(Ord.<Domains.Str>hashEqualsOrd(),
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length")
            )),
            P.p(JSClass.CMath_Obj, Set.set(Ord.<Domains.Str>hashEqualsOrd(),
                    Domains.Str.alpha("E"),
                    Domains.Str.alpha("LN10"),
                    Domains.Str.alpha("LN2"),
                    Domains.Str.alpha("LOG2E"),
                    Domains.Str.alpha("LOG10E"),
                    Domains.Str.alpha("PI"),
                    Domains.Str.alpha("SQRT1_2"),
                    Domains.Str.alpha("SQRT2")
            )),
            P.p(JSClass.CNumber_Obj, Set.set(Ord.<Domains.Str>hashEqualsOrd(),
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length")
            )),
            P.p(JSClass.CString_Obj, Set.set(Ord.<Domains.Str>hashEqualsOrd(),
                    Domains.Str.alpha("prototype"),
                    Domains.Str.alpha("length")
            )));

    public static final TreeMap<Domains.AddressSpace.Address, JSClass> classFromAddress = TreeMap.treeMap(Ord.<Domains.AddressSpace.Address>hashEqualsOrd(),
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
