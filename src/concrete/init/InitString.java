package concrete.init;

import concrete.Utils;
import concrete.Domains;
import fj.Ord;
import fj.P;
import fj.data.List;
import fj.data.Option;
import fj.data.TreeMap;
import ir.JSClass;

/**
 * Created by BenZ on 15/11/11.
 */
public class InitString {

    public static Domains.Object String_Obj = InitUtils.makeNativeValueStore(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object argsObj = store.getObj(argArrayAddr);
                Double arglen;
                Option<Domains.BValue> tmp = argsObj.apply(new Domains.Str("length"));
                if (tmp.isSome() && tmp.some() instanceof Domains.Num) {
                    arglen = ((Domains.Num) tmp.some()).n;
                } else {
                    throw new RuntimeException("inconceivable: args without length");
                }
                Boolean calledAsConstr = argsObj.calledAsCtor();
                Domains.Str pvalue;
                if (arglen == 0) {
                    pvalue = new Domains.Str("");
                } else {
                    pvalue = InitUtils.ToString(argsObj.apply(new Domains.Str("0")).orSome(Domains.Undef), store);
                }
                if (calledAsConstr) {
                    TreeMap<Domains.Str, Domains.BValue> strmap = List.range(0, pvalue.str.length()).foldLeft(
                            (acc, e) -> acc.set(new Domains.Str(e.toString()), new Domains.Str(pvalue.str.substring(e, e + 1).toString())),
                            TreeMap.treeMap(Ord.<Domains.Str>hashEqualsOrd(), P.p(Utils.Fields.length, new Domains.Num((double) pvalue.str.length())))
                    );
                    Domains.Object newObj = InitUtils.createObj(strmap,
                            TreeMap.treeMap(Ord.hashEqualsOrd(),
                                    P.p(Utils.Fields.proto, Init.String_prototype_Addr),
                                    P.p(Utils.Fields.classname, JSClass.CString),
                                    P.p(Utils.Fields.value, pvalue)));
                    Domains.Store newStore = store.putObj(selfAddr, newObj);
                    return P.p(selfAddr, newStore);
                } else {
                    return P.p(pvalue, store);
                }
            }, TreeMap.treeMap(Ord.<Domains.Str>hashEqualsOrd(),
                    P.p(new Domains.Str("prototype"), Init.String_prototype_Addr),
                    P.p(new Domains.Str("length"), new Domains.Num(1.0)),
                    P.p(new Domains.Str("fromCharCode"), Init.String_fromCharCode_Addr)),
            JSClass.CString_Obj
    );

    public static Domains.Object String_fromCharCode_Obj = InitUtils.makeNativeValue(
            (selfAddr, argArrayAddr, store) -> {
                final Domains.Object args = store.getObj(argArrayAddr);
                Double arglen;
                if (args.apply(new Domains.Str("length")).isSome() && args.apply(new Domains.Str("length")).some() instanceof Domains.Num) {
                    arglen = ((Domains.Num) args.apply(new Domains.Str("length")).some()).n;
                } else {
                    throw new RuntimeException("inconceivable: arguments without numeric length");
                }
                String result = List.range(0, arglen.intValue()).foldLeft(
                        (acc, k) -> acc + InitUtils.ToNumber(args.apply(new Domains.Str(k.toString())).orSome(Domains.Undef), store).n.toString(),
                        ""
                );
                return new Domains.Str(result);
            }, TreeMap.treeMap(Ord.<Domains.Str>hashEqualsOrd(),
                    P.p(new Domains.Str("length"), new Domains.Num(1.0)))
    );

    public static Domains.Object String_prototype_Obj = InitUtils.createObj(
            TreeMap.treeMap(Ord.<Domains.Str>hashEqualsOrd(),
                    P.p(new Domains.Str("constructor"), Init.String_Addr),
                    P.p(new Domains.Str("charAt"), Init.String_prototype_charAt_Addr),
                    P.p(new Domains.Str("charCodeAt"), Init.String_prototype_charCodeAt_Addr),
                    P.p(new Domains.Str("concat"), Init.String_prototype_concat_Addr),
                    P.p(new Domains.Str("indexOf"), Init.String_prototype_indexOf_Addr),
                    P.p(new Domains.Str("lastIndexOf"), Init.String_prototype_lastIndexOf_Addr),
                    P.p(new Domains.Str("localeCompare"), Init.String_prototype_localeCompare_Addr),
                    P.p(new Domains.Str("match"), Init.String_prototype_match_Addr),
                    P.p(new Domains.Str("replace"), Init.String_prototype_replace_Addr),
                    P.p(new Domains.Str("search"), Init.String_prototype_search_Addr),
                    P.p(new Domains.Str("slice"), Init.String_prototype_slice_Addr),
                    P.p(new Domains.Str("split"), Init.String_prototype_split_Addr),
                    P.p(new Domains.Str("substr"), Init.String_prototype_substr_Addr),
                    P.p(new Domains.Str("substring"), Init.String_prototype_substring_Addr),
                    P.p(new Domains.Str("toLocaleLowerCase"), Init.String_prototype_toLocaleLowerCase_Addr),
                    P.p(new Domains.Str("toLocaleUpperCase"), Init.String_prototype_toLocaleUpperCase_Addr),
                    P.p(new Domains.Str("toLowerCase"), Init.String_prototype_toLowerCase_Addr),
                    P.p(new Domains.Str("toInit.String"), Init.String_prototype_toString_Addr),
                    P.p(new Domains.Str("toUpperCase"), Init.String_prototype_toUpperCase_Addr),
                    P.p(new Domains.Str("trim"), Init.String_prototype_trim_Addr),
                    P.p(new Domains.Str("valueOf"), Init.String_prototype_valueOf_Addr)),
            TreeMap.treeMap(Ord.<Domains.Str>hashEqualsOrd(), P.p(Utils.Fields.classname, JSClass.CString_Obj))
    );

    public static Domains.Object String_prototype_charAt_Obj = InitUtils.makeNativeValue(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object args = store.getObj(argArrayAddr);

                String selfStr = InitUtils.ToString(selfAddr, store).str;
                int pos;
                if (args.apply(new Domains.Str("0")).isSome()) {
                    pos = InitUtils.ToNumber(args.apply(new Domains.Str("0")).some(), store).n.intValue();
                }
                else {
                    pos = 0;
                }

                String charAt;
                if (pos >= 0 && pos < selfStr.length()) {
                    charAt = String.valueOf(selfStr.charAt(pos));
                }
                else {
                    charAt = "";
                }

                return new Domains.Str(charAt);
            }, TreeMap.treeMap(Ord.<Domains.Str>hashEqualsOrd(),
                    P.p(new Domains.Str("length"), new Domains.Num(1.0)))
    );

    public static Domains.Object String_prototype_charCodeAt_Obj = InitUtils.makeNativeValue(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object args = store.getObj(argArrayAddr);

                String selfStr = InitUtils.ToString(selfAddr, store).str;
                int pos;
                if (args.apply(new Domains.Str("0")).isSome()) {
                    pos = InitUtils.ToNumber(args.apply(new Domains.Str("0")).some(), store).n.intValue();
                }
                else {
                    pos = 0;
                }

                Domains.Num charCodeAt;
                if (pos >= 0 && pos < selfStr.length()) {
                    charCodeAt = new Domains.Num(new Double(selfStr.charAt(pos)));  //?
                }
                else {
                    charCodeAt = new Domains.Num(new Double(Double.NaN));
                }

                return charCodeAt;
            }, TreeMap.treeMap(Ord.<Domains.Str>hashEqualsOrd(),
                    P.p(new Domains.Str("length"), new Domains.Num(1.0)))
    );

    public static Domains.Object String_prototype_concat_Obj = InitUtils.makeNativeValue(
            (selfAddr, argArrayAddr, final store) -> {
                final Domains.Object args = store.getObj(argArrayAddr);

                Double arglen;
                if (args.apply(new Domains.Str("length")).isSome() && args.apply(new Domains.Str("length")).some() instanceof Domains.Num) {
                    arglen = ((Domains.Num) args.apply(new Domains.Str("length")).some()).n;
                } else {
                    throw new RuntimeException("inconceivable: arguments without numeric length");
                }

                return List.range(0, arglen.intValue()).foldLeft(
                        (s, i) -> {
                            return s.strConcat(InitUtils.ToString(args.apply(new Domains.Str(String.valueOf(i))).orSome(Domains.Undef), store));
                        },
                        InitUtils.ToString(selfAddr, store)
                );

            }, TreeMap.treeMap(Ord.<Domains.Str>hashEqualsOrd(),
                    P.p(new Domains.Str("length"), new Domains.Num(1.0)))
    );

    public static Domains.Object String_prototype_indexOf_Obj = InitUtils.approx_num;
    public static Domains.Object String_prototype_lastIndexOf_Obj = InitUtils.approx_num;
    public static Domains.Object String_prototype_localeCompare_Obj = InitUtils.unimplemented;
    public static Domains.Object String_prototype_match_Obj = InitUtils.approx_array;
    public static Domains.Object String_prototype_replace_Obj = InitUtils.approx_str;
    public static Domains.Object String_prototype_search_Obj = InitUtils.approx_num;
    public static Domains.Object String_prototype_slice_Obj = InitUtils.approx_str;
    public static Domains.Object String_prototype_split_Obj = InitUtils.approx_array;
    public static Domains.Object String_prototype_substr_Obj = InitUtils.approx_str;
    public static Domains.Object String_prototype_substring_Obj = InitUtils.approx_str;
    public static Domains.Object String_prototype_toLocaleLowerCase_Obj = InitUtils.unimplemented;
    public static Domains.Object String_prototype_toLocaleUpperCase_Obj = InitUtils.unimplemented;
    public static Domains.Object String_prototype_toLowerCase_Obj = InitUtils.unimplemented;

    public static Domains.Object String_prototype_toString_Obj = InitUtils.makeNativeValue(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object self = store.getObj(selfAddr);
                if (self.getJSClass() == JSClass.CString || self.getJSClass() == JSClass.CString_prototype_Obj) {
                    if (self.getValue().isSome() && self.getValue().some() instanceof Domains.Str) {
                        return self.getValue().some();
                    }
                    else {
                        throw new RuntimeException("inconceivable: String without a string value");
                    }
                }
                else {
                    return Utils.Errors.typeError;
                }
            }, TreeMap.treeMap(Ord.<Domains.Str>hashEqualsOrd(),
                    P.p(new Domains.Str("length"), new Domains.Num(0.0)))
    );

    public static Domains.Object String_prototype_toUpperCase_Obj = InitUtils.unimplemented;
    public static Domains.Object String_prototype_trim_Obj = InitUtils.unimplemented;

    public static Domains.Object String_prototype_valueOf_Obj = InitUtils.makeNativeValue(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object self = store.getObj(selfAddr);
                if (self.getJSClass() == JSClass.CString || self.getJSClass() == JSClass.CString_prototype_Obj) {
                    if (self.getValue().isSome() && self.getValue().some() instanceof Domains.Str) {
                        return self.getValue().some();
                    }
                    else {
                        throw new RuntimeException("inconceivable: String without a string value");
                    }
                }
                else {
                    return Utils.Errors.typeError;
                }
            }, TreeMap.treeMap(Ord.<Domains.Str>hashEqualsOrd(),
                    P.p(new Domains.Str("length"), new Domains.Num(0.0)))
    );
}