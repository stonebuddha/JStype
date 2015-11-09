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
                        P.p(Dummy_Addr, Utils.createObj(TreeMap.empty(Ord.hashEqualsOrd()))))); // TODO
        return new Interpreter.State(
                new Domains.StmtTerm(s),
                env,
                store,
                Domains.Scratchpad.apply(0),
                new Domains.KontStack(List.list(Domains.HaltKont)));
    }

    public static final TreeMap<JSClass, Set<Domains.Str>> noenum = TreeMap.treeMap(Ord.hashEqualsOrd()); // TODO

    public static final TreeMap<JSClass, Set<Domains.Str>> nodelete = TreeMap.treeMap(Ord.hashEqualsOrd()); // TODO

    public static final TreeMap<JSClass, Set<Domains.Str>> noupdate = TreeMap.treeMap(Ord.hashEqualsOrd()); // TODO

    public static final TreeMap<Domains.Address, JSClass> classFromAddress = TreeMap.treeMap(Ord.hashEqualsOrd(),
            P.p(Function_Addr, JSClass.CFunction),
            P.p(Array_Addr, JSClass.CArray),
            P.p(String_Addr, JSClass.CString),
            P.p(Boolean_Addr, JSClass.CBoolean),
            P.p(Number_Addr, JSClass.CNumber),
            P.p(Date_Addr, JSClass.CDate),
            P.p(Error_Addr, JSClass.CError),
            P.p(RegExp_Addr, JSClass.CRegExp),
            P.p(Arguments_Addr, JSClass.CArguments));
}
