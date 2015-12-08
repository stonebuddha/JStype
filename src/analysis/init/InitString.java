package analysis.init;

import analysis.Domains;
import analysis.Utils;
import immutable.FHashMap;
import ir.JSClass;

/**
 * Created by wayne on 15/12/7.
 */
public class InitString {
    public static final Domains.Object String_Obj = InitUtils.createInitFunctionObj(
            new Domains.Native(
                    (selfAddr, argArrayAddr, x, env, store, pad, ks, tr) -> {
                        return null; // TODO
                    }
            ),
            FHashMap.build(
                    "length", Domains.Num.inject(Domains.Num.alpha(1.0)),
                    "prototype", Domains.AddressSpace.Address.inject(Init.String_prototype_Addr),
                    "fromCharCode", Domains.AddressSpace.Address.inject(Init.String_fromCharCode_Addr)
            ),
            FHashMap.empty(),
            JSClass.CString_Obj
    );

    public static final Domains.Object String_fromCharCode_Obj = InitUtils.unimplemented("String.fromCharCode");

    public static final Domains.Object String_prototype_Obj = InitUtils.createInitObj(
            FHashMap.build(
                    "charAt", Domains.AddressSpace.Address.inject(Init.String_prototype_charAt_Addr), // TODO
                    "charCodeAt", Domains.AddressSpace.Address.inject(Init.String_prototype_charCodeAt_Addr), // TODO
                    "concat", Domains.AddressSpace.Address.inject(Init.String_prototype_concat_Addr), // TODO
                    "indexOf", Domains.AddressSpace.Address.inject(Init.String_prototype_indexOf_Addr), // TODO
                    "lastIndexOf", Domains.AddressSpace.Address.inject(Init.String_prototype_lastIndexOf_Addr), // TODO
                    "localeCompare", Domains.AddressSpace.Address.inject(Init.String_prototype_localeCompare_Addr), // TODO
                    "match", Domains.AddressSpace.Address.inject(Init.String_prototype_match_Addr),
                    "replace", Domains.AddressSpace.Address.inject(Init.String_prototype_replace_Addr), // TODO
                    "search", Domains.AddressSpace.Address.inject(Init.String_prototype_search_Addr), // TODO
                    "slice", Domains.AddressSpace.Address.inject(Init.String_prototype_slice_Addr), // TODO
                    "split", Domains.AddressSpace.Address.inject(Init.String_prototype_split_Addr), // TODO
                    "substr", Domains.AddressSpace.Address.inject(Init.String_prototype_substr_Addr), // TODO
                    "substring", Domains.AddressSpace.Address.inject(Init.String_prototype_substring_Addr), // TODO
                    "toLocaleLowerCase", Domains.AddressSpace.Address.inject(Init.String_prototype_toLocaleLowerCase_Addr), // TODO
                    "toLocaleUpperCase", Domains.AddressSpace.Address.inject(Init.String_prototype_toLocaleUpperCase_Addr), // TODO
                    "toLowerCase", Domains.AddressSpace.Address.inject(Init.String_prototype_toLowerCase_Addr), // TODO
                    "toString", Domains.AddressSpace.Address.inject(Init.String_prototype_toString_Addr),
                    "toUpperCase", Domains.AddressSpace.Address.inject(Init.String_prototype_toUpperCase_Addr), // TODO
                    "trim", Domains.AddressSpace.Address.inject(Init.String_prototype_trim_Addr), // TODO
                    "valueOf", Domains.AddressSpace.Address.inject(Init.String_prototype_valueOf_Addr)
            ),
            FHashMap.build(
                    Utils.Fields.classname, JSClass.CString_prototype_Obj,
                    Utils.Fields.value, Domains.Str.inject(Domains.Str.alpha(""))
            )
    );

    public static final Domains.Object String_prototype_toString_Obj = InitUtils.unimplemented("String.prototype.toString");
    public static final Domains.Object String_prototype_valueOf_Obj = InitUtils.unimplemented("String.prototype.valueOf");
    public static final Domains.Object String_prototype_charAt_Obj = InitUtils.unimplemented("String.prototype.charAt");
    public static final Domains.Object String_prototype_charCodeAt_Obj = InitUtils.unimplemented("String.prototype.charCodeAt");
    public static final Domains.Object String_prototype_concat_Obj = InitUtils.unimplemented("String.prototype.concat");
    public static final Domains.Object String_prototype_indexOf_Obj = InitUtils.unimplemented("String.prototype.indexOf");
    public static final Domains.Object String_prototype_lastIndexOf_Obj = InitUtils.unimplemented("String.prototype.lastIndexOf");
    public static final Domains.Object String_prototype_localeCompare_Obj = InitUtils.unimplemented("String.prototype.localeCompare");
    public static final Domains.Object String_prototype_match_Obj = InitUtils.unimplemented("String.prototype.match");
    public static final Domains.Object String_prototype_replace_Obj = InitUtils.unimplemented("String.prototype.replace");
    public static final Domains.Object String_prototype_search_Obj = InitUtils.unimplemented("String.prototype.search");
    public static final Domains.Object String_prototype_slice_Obj = InitUtils.unimplemented("String.prototype.slice");
    public static final Domains.Object String_prototype_split_Obj = InitUtils.unimplemented("String.prototype.split");
    public static final Domains.Object String_prototype_substr_Obj = InitUtils.unimplemented("String.prototype.substr");
    public static final Domains.Object String_prototype_substring_Obj = InitUtils.unimplemented("String.prototype.substring");
    public static final Domains.Object String_prototype_toLowerCase_Obj = InitUtils.unimplemented("String.prototype.toLowerCase");
    public static final Domains.Object String_prototype_toLocaleLowerCase_Obj = InitUtils.unimplemented("String.prototype.toLocaleLowerCase");
    public static final Domains.Object String_prototype_toUpperCase_Obj = InitUtils.unimplemented("String.prototype.toUpperCase");
    public static final Domains.Object String_prototype_toLocaleUpperCase_Obj = InitUtils.unimplemented("String.prototype.toLocaleUpperCase");
    public static final Domains.Object String_prototype_trim_Obj = InitUtils.unimplemented("String.prototype.trim");
}
