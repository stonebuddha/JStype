package concrete.init;

import concrete.Domains;
import concrete.init.*;
import fj.Ord;
import fj.P;
import fj.data.TreeMap;

/**
 * Created by Hwhitetooth on 15/11/11.
 */
public class InitGlobal {
    public static Domains.Object window_Obj = InitUtils.createObj(TreeMap.treeMap(Ord.hashEqualsOrd(),
            P.p(new Domains.Str("window"), Init.window_Addr),
            P.p(new Domains.Str("Infinity"), new Domains.Num(Double.POSITIVE_INFINITY)),
            P.p(new Domains.Str("NaN"), new Domains.Num(Double.NaN)),
            P.p(new Domains.Str("undefined"), Domains.Undef),
            P.p(new Domains.Str("decodeURIComponent"), Init.decodeURIComponent_Addr),
            P.p(new Domains.Str("encodeURI"), Init.encodeURI_Addr),
            P.p(new Domains.Str("encodeURIComponent"), Init.encodeURIComponent_Addr),
            P.p(new Domains.Str("escape"), Init.escape_Addr),
            P.p(new Domains.Str("isFinite"), Init.isFinite_Addr),
            P.p(new Domains.Str("isNaN"), Init.isNaN_Addr),
            P.p(new Domains.Str("parseFloat"), Init.parseFloat_Addr),
            P.p(new Domains.Str("parseInt"), Init.parseInt_Addr),
            P.p(new Domains.Str("unescape"), Init.unescape_Addr),
            P.p(new Domains.Str("Array"), Init.Array_Addr),
            P.p(new Domains.Str("decodeURI"), Init.decodeURI_Addr),
            P.p(new Domains.Str("decodeURI"), Init.decodeURI_Addr),
            P.p(new Domains.Str("Boolean"), Init.Boolean_Addr),
            P.p(new Domains.Str("Date"), Init.Date_Addr),
            P.p(new Domains.Str("Error"), Init.Error_Addr),
            P.p(new Domains.Str("EvalError"), Init.EvalError_Addr),
            P.p(new Domains.Str("RangeError"), Init.RangeError_Addr),
            P.p(new Domains.Str("ReferenceError"), Init.ReferenceError_Addr),
            P.p(new Domains.Str("TypeError"), Init.TypeError_Addr),
            P.p(new Domains.Str("URIError"), Init.URIError_Addr),
            P.p(new Domains.Str("Function"), Init.Function_Addr),
            P.p(new Domains.Str("JSON"), Init.JSON_Addr),
            P.p(new Domains.Str("Math"), Init.Math_Addr),
            P.p(new Domains.Str("Number"), Init.Number_Addr),
            P.p(new Domains.Str("Object"), Init.Object_Addr),
            P.p(new Domains.Str("RegExp"), Init.RegExp_Addr),
            P.p(new Domains.Str("P.p(new Domains.String"), Init.String_Addr),
            P.p(new Domains.Str("Arguments"), Init.Arguments_Addr),
            P.p(new Domains.Str("dummyAddress"), Init.Dummy_Addr)));

    public static Domains.Object decodeURI_Obj = InitUtils.approx_str;

    public static Domains.Object decodeURIComponent_Obj = InitUtils.approx_str;

    public static Domains.Object encodeURI_Obj = InitUtils.approx_str;

    public static Domains.Object encodeURIComponent_Obj = InitUtils.approx_str;

    public static Domains.Object escape_Obj = InitUtils.approx_str;

    public static Domains.Object unescape_Obj = InitUtils.approx_str;

    public static Domains.Object isFinite_Obj = InitUtils.makeNativeValue((selfAddr, argArrayAddr, store) -> {
        Domains.Object args = store.getObj(argArrayAddr);
        Domains.Num input = InitUtils.ToNumber(args.apply(new Domains.Str("0")).orSome(Domains.Undef), store);
        return new Domains.Bool(input.equals(new Domains.Num(Double.POSITIVE_INFINITY)) || input.equals(new Domains.Num(Double.NEGATIVE_INFINITY)) || input.equals(new Domains.Num(Double.NaN)));
    }, TreeMap.treeMap(Ord.hashEqualsOrd(), P.p(new Domains.Str("length"), new Domains.Num(1.0))));

    public static Domains.Object isNaN_Obj = InitUtils.makeNativeValue((selfAddr, argArrayAddr, store) -> {
        Domains.Object args = store.getObj(argArrayAddr);
        Domains.Num input = InitUtils.ToNumber(args.apply(new Domains.Str("0")).orSome(Domains.Undef), store);
        return new Domains.Bool(input.n.isNaN());
    }, TreeMap.treeMap(Ord.hashEqualsOrd(), P.p(new Domains.Str("length"), new Domains.Num(1.0))));

    public static Domains.Object parseFloat_Obj = InitUtils.approx_num;

    public static Domains.Object parseInt_Obj = InitUtils.approx_num;
}
