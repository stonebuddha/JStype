package analysis.init;

import analysis.Domains;
import analysis.Interpreter;
import analysis.Traces;
import analysis.Utils;
import fj.F7;
import fj.P;
import fj.P2;
import immutable.FHashMap;
import immutable.FHashSet;
import ir.IRVar;
import ir.JSClass;
import fj.data.List;

/**
 * Created by wayne on 15/12/7.
 */
public class InitString {
    public static InitUtils.Sig strSig(List<InitUtils.ConversionHint> argHints, Integer length) {
        return new InitUtils.Sig(InitUtils.StringHint, argHints, length);
    }

    //WARNING: Might be unsuitable !!!
    public static InitUtils.Sig ezStrSig(InitUtils.ConversionHint ...argHints) {
        return InitUtils.ezSig(InitUtils.StringHint, List.range(0, argHints.length).map(i-> argHints[i]));
    }

    public static final F7<List<Domains.BValue>, IRVar, Domains.Env, Domains.Store, Domains.Scratchpad, Domains.KontStack, Traces.Trace, FHashSet<Interpreter.State>> Internal_String_constructor_afterToString = InitUtils.valueObjConstructor("String",
            arg_value-> {
                assert arg_value.defStr() : "String constructor: type conversion ensures argument is a string";
            });

    public static final F7<List<Domains.BValue>, IRVar, Domains.Env, Domains.Store, Domains.Scratchpad, Domains.KontStack, Traces.Trace, FHashSet<Interpreter.State>> Internal_String_normal_afterToString =
            (bvs, x, env, store, pad, ks, tr)-> {
                assert bvs.length() == 1 : "String function: should have 1 argument by this point";
                Domains.BValue arg_value = bvs.index(0);
                assert arg_value.defStr() : "String function: type conversion ensures argument is a string";
                return InitUtils.makeState(arg_value, x, env, store, pad, ks, tr);
            };

    public static final Domains.Object String_Obj = InitUtils.createInitFunctionObj(
            new Domains.Native(
                    (selfAddr, argArrayAddr, x, env, store, pad, ks, tr) -> {
                        assert argArrayAddr.defAddr() : "String: argument array must be an address set";
                        assert argArrayAddr.as.size() == 1 : "String: argument array address set size must be 1";
                        Domains.Object argsArray = store.getObj(argArrayAddr.as.head());
                        P2<Domains.BValue,InitUtils.ConversionHint> arg_preconv = P.p(argsArray.apply(Domains.Str.alpha("0")).orSome(Domains.Str.inject(Domains.Str.alpha(""))), InitUtils.StringHint);
                        Boolean calledAsConstr = (Boolean)argsArray.intern.get(Utils.Fields.constructor).orSome(false);
                        List<P2<Domains.BValue, InitUtils.ConversionHint>> argList;
                        F7<List<Domains.BValue>, IRVar, Domains.Env, Domains.Store, Domains.Scratchpad, Domains.KontStack, Traces.Trace, FHashSet<Interpreter.State>> postconvF;
                        if (calledAsConstr) {
                            argList = List.list(P.p(selfAddr, InitUtils.NoConversion), arg_preconv);
                            postconvF = Internal_String_constructor_afterToString;
                        } else {
                            argList = List.list(arg_preconv);
                            postconvF = Internal_String_normal_afterToString;
                        }
                        return InitUtils.Convert(argList, postconvF, x, env, store, pad, ks, tr);
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

    public static final Domains.Object String_fromCharCode_Obj = InitUtils.constFunctionObj(new InitUtils.VarSig(InitUtils.NoConversion, InitUtils.NumberHint, 1), Domains.Str.inject(Domains.Str.STop));

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

    public static final Domains.Object String_prototype_toString_Obj = InitUtils.usualToPrim(any-> InitUtils.isStringClass(any), any-> any.str, Domains.Str.Bot, any-> Domains.Str.inject(any), p-> p._1().merge(p._2()));
    public static final Domains.Object String_prototype_valueOf_Obj = String_prototype_toString_Obj;
    public static final Domains.Object String_prototype_charAt_Obj = InitUtils.constFunctionObj(ezStrSig(InitUtils.NumberHint), Domains.Str.inject(Domains.Str.SingleChar.merge(Domains.Str.Empty)));
    public static final Domains.Object String_prototype_charCodeAt_Obj = InitUtils.constFunctionObj(ezStrSig(InitUtils.NumberHint), Domains.Num.inject(Domains.Num.NTop));
    public static final Domains.Object String_prototype_concat_Obj = InitUtils.constFunctionObj(new InitUtils.VarSig(InitUtils.StringHint, InitUtils.StringHint, 1), Domains.Str.inject(Domains.Str.STop));
    public static final Domains.Object String_prototype_indexOf_Obj = InitUtils.constFunctionObj(strSig(List.list(InitUtils.StringHint, InitUtils.NumberHint), 1), Domains.Num.inject(Domains.Num.NReal));
    public static final Domains.Object String_prototype_lastIndexOf_Obj = InitUtils.constFunctionObj(strSig(List.list(InitUtils.StringHint, InitUtils.NumberHint), 1), Domains.Num.inject(Domains.Num.NReal));
    public static final Domains.Object String_prototype_localeCompare_Obj = InitUtils.constFunctionObj(ezStrSig(InitUtils.StringHint), Domains.Num.inject(Domains.Num.NReal));
    public static final Domains.Object String_prototype_match_Obj = InitUtils.unimplemented("String.prototype.match");//TODO
    public static final Domains.Object String_prototype_replace_Obj = InitUtils.unimplemented("String.prototype.replace");//TODO
    public static final Domains.Object String_prototype_search_Obj = InitUtils.constFunctionObj(ezStrSig(InitUtils.StringHint), Domains.Num.inject(Domains.Num.NReal));
    public static final Domains.Object String_prototype_slice_Obj = InitUtils.constFunctionObj(ezStrSig(InitUtils.NumberHint, InitUtils.NumberHint), Domains.Str.inject(Domains.Str.STop));
    public static final Domains.Object String_prototype_split_Obj = InitUtils.unimplemented("String.prototype.split");//TODO
    public static final Domains.Object String_prototype_substring_Obj = InitUtils.constFunctionObj(ezStrSig(InitUtils.NumberHint, InitUtils.NumberHint), Domains.Str.inject(Domains.Str.STop));
    public static final Domains.Object String_prototype_substr_Obj = String_prototype_substring_Obj;
    public static final Domains.Object String_prototype_toLowerCase_Obj = InitUtils.constFunctionObj(ezStrSig(), Domains.Str.inject(Domains.Str.STop));
    public static final Domains.Object String_prototype_toLocaleLowerCase_Obj = InitUtils.constFunctionObj(ezStrSig(), Domains.Str.inject(Domains.Str.STop));
    public static final Domains.Object String_prototype_toUpperCase_Obj = InitUtils.constFunctionObj(ezStrSig(), Domains.Str.inject(Domains.Str.STop));
    public static final Domains.Object String_prototype_toLocaleUpperCase_Obj = InitUtils.constFunctionObj(ezStrSig(), Domains.Str.inject(Domains.Str.STop));
    public static final Domains.Object String_prototype_trim_Obj = InitUtils.constFunctionObj(ezStrSig(), Domains.Str.inject(Domains.Str.STop));
}
