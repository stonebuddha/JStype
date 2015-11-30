package concrete.init;

import concrete.Domains;
import concrete.Utils;
import immutable.FHashMap;

/**
 * Created by Hwhitetooth on 15/11/11.
 */
public class InitGlobal {
    public static Domains.Object window_Obj = InitUtils.createObj(FHashMap.map(
            new Domains.Str("window"), Init.window_Addr,
            new Domains.Str("Infinity"), new Domains.Num(Double.POSITIVE_INFINITY),
            new Domains.Str("NaN"), new Domains.Num(Double.NaN),
            new Domains.Str("undefined"), Domains.Undef,
            new Domains.Str("decodeURIComponent"), Init.decodeURIComponent_Addr,
            new Domains.Str("encodeURI"), Init.encodeURI_Addr,
            new Domains.Str("encodeURIComponent"), Init.encodeURIComponent_Addr,
            new Domains.Str("escape"), Init.escape_Addr,
            new Domains.Str("isFinite"), Init.isFinite_Addr,
            new Domains.Str("isNaN"), Init.isNaN_Addr,
            new Domains.Str("parseFloat"), Init.parseFloat_Addr,
            new Domains.Str("parseInt"), Init.parseInt_Addr,
            new Domains.Str("unescape"), Init.unescape_Addr,
            new Domains.Str("Array"), Init.Array_Addr,
            new Domains.Str("decodeURI"), Init.decodeURI_Addr,
            new Domains.Str("decodeURI"), Init.decodeURI_Addr,
            new Domains.Str("Boolean"), Init.Boolean_Addr,
            new Domains.Str("Date"), Init.Date_Addr,
            new Domains.Str("Error"), Init.Error_Addr,
            new Domains.Str("EvalError"), Init.EvalError_Addr,
            new Domains.Str("RangeError"), Init.RangeError_Addr,
            new Domains.Str("ReferenceError"), Init.ReferenceError_Addr,
            new Domains.Str("TypeError"), Init.TypeError_Addr,
            new Domains.Str("URIError"), Init.URIError_Addr,
            new Domains.Str("Function"), Init.Function_Addr,
            new Domains.Str("JSON"), Init.JSON_Addr,
            new Domains.Str("Math"), Init.Math_Addr,
            new Domains.Str("Number"), Init.Number_Addr,
            new Domains.Str("Object"), Init.Object_Addr,
            new Domains.Str("RegExp"), Init.RegExp_Addr,
            new Domains.Str("String"), Init.String_Addr,
            new Domains.Str("Arguments"), Init.Arguments_Addr,
            new Domains.Str("dummyAddress"), Init.Dummy_Addr));

    public static Domains.Object decodeURI_Obj = InitUtils.approx_str;

    public static Domains.Object decodeURIComponent_Obj = InitUtils.approx_str;

    public static Domains.Object encodeURI_Obj = InitUtils.approx_str;

    public static Domains.Object encodeURIComponent_Obj = InitUtils.approx_str;

    public static Domains.Object escape_Obj = InitUtils.approx_str;

    public static Domains.Object unescape_Obj = InitUtils.approx_str;

    public static Domains.Object isFinite_Obj = InitUtils.makeNativeValue((selfAddr, argArrayAddr, store) -> {
        Domains.Object args = store.getObj(argArrayAddr);
        Domains.Num input = InitUtils.ToNumber(args.apply(new Domains.Str("0")).orSome(Domains.Undef), store);
        return Domains.Bool.apply(input.equals(new Domains.Num(Double.POSITIVE_INFINITY)) || input.equals(new Domains.Num(Double.NEGATIVE_INFINITY)) || input.equals(new Domains.Num(Double.NaN)));
    }, FHashMap.map(Utils.Fields.length, new Domains.Num(1.0)));

    public static Domains.Object isNaN_Obj = InitUtils.makeNativeValue((selfAddr, argArrayAddr, store) -> {
        Domains.Object args = store.getObj(argArrayAddr);
        Domains.Num input = InitUtils.ToNumber(args.apply(new Domains.Str("0")).orSome(Domains.Undef), store);
        return Domains.Bool.apply(Double.isNaN(input.n));
    }, FHashMap.map(Utils.Fields.length, new Domains.Num(1.0)));

    public static Domains.Object parseFloat_Obj = InitUtils.approx_num;

    public static Domains.Object parseInt_Obj = InitUtils.approx_num;
}
