package analysis.init;

import analysis.Domains;
import analysis.Utils;
import fj.data.List;
import immutable.FHashMap;
import immutable.FHashSet;

import java.util.DoubleSummaryStatistics;

/**
 * Created by BenZ on 15/11/24.
 */
public class InitGlobal {

    public static final Domains.Object window_Obj = InitUtils.createInitObj(
            FHashMap.<String, Domains.BValue>build(
                    "window", Domains.AddressSpace.Address.inject(Init.window_Addr),
                    "Arguments", Domains.AddressSpace.Address.inject(Init.Arguments_Addr),
                    "undefined", Domains.Undef.BV,
                    "decodeURI", Domains.AddressSpace.Address.inject(Init.decodeURI_Addr),
                    "decodeURIComponent", Domains.AddressSpace.Address.inject(Init.decodeURIComponent_Addr),
                    "encodeURI", Domains.AddressSpace.Address.inject(Init.encodeURI_Addr),
                    "encodeURIComponent", Domains.AddressSpace.Address.inject(Init.encodeURIComponent_Addr),
                    "escape", Domains.AddressSpace.Address.inject(Init.escape_Addr),
                    "isFinite", Domains.AddressSpace.Address.inject(Init.isFinite_Addr),
                    "isNaN", Domains.AddressSpace.Address.inject(Init.isNaN_Addr),
                    "parseFloat", Domains.AddressSpace.Address.inject(Init.parseFloat_Addr),
                    "parseInt", Domains.AddressSpace.Address.inject(Init.parseInt_Addr),
                    "unescape", Domains.AddressSpace.Address.inject(Init.unescape_Addr),
                    "Array", Domains.AddressSpace.Address.inject(Init.Array_Addr),
                    "Boolean", Domains.AddressSpace.Address.inject(Init.Boolean_Addr),
                    "Number", Domains.AddressSpace.Address.inject(Init.Number_Addr),
                    "Object", Domains.AddressSpace.Address.inject(Init.Object_Addr),
                    "String", Domains.AddressSpace.Address.inject(Init.String_Addr),
                    "Date", Domains.AddressSpace.Address.inject(Init.Date_Addr), // TODO
                    "JSON", Domains.AddressSpace.Address.inject(Init.JSON_Addr), // TODO
                    "Math", Domains.AddressSpace.Address.inject(Init.Math_Addr),
                    "RegExp", Domains.AddressSpace.Address.inject(Init.RegExp_Addr),
                    "ArrayBuffer", Domains.AddressSpace.Address.inject(Init.ArrayBuffer_Addr),
                    "Int8Array", Domains.AddressSpace.Address.inject(Init.Int8Array_Addr),
                    "Uint8Array", Domains.AddressSpace.Address.inject(Init.Uint8Array_Addr),
                    "Int16Array", Domains.AddressSpace.Address.inject(Init.Int16Array_Addr),
                    "Uint16Array", Domains.AddressSpace.Address.inject(Init.Uint16Array_Addr),
                    "Int32Array", Domains.AddressSpace.Address.inject(Init.Int32Array_Addr),
                    "Uint32Array", Domains.AddressSpace.Address.inject(Init.Uint32Array_Addr),
                    "Float32Array", Domains.AddressSpace.Address.inject(Init.Float32Array_Addr),
                    "Float64Array", Domains.AddressSpace.Address.inject(Init.Float64Array_Addr),
                    "dummyAddress", Domains.AddressSpace.Address.inject(Init.Dummy_Addr),
                    "Infinity", Domains.Num.inject(Domains.Num.alpha(Double.POSITIVE_INFINITY)),
                    "NaN", Domains.Num.inject(Domains.Num.alpha(Double.NaN))).union(InitUtils.dangleMap(FHashMap.build(
                    "Error", Domains.AddressSpace.Address.inject(Init.Error_Addr),
                    "EvalError", Domains.AddressSpace.Address.inject(Init.EvalError_Addr),
                    "RangeError", Domains.AddressSpace.Address.inject(Init.RangeError_Addr),
                    "ReferenceError", Domains.AddressSpace.Address.inject(Init.ReferenceError_Addr),
                    "TypeError", Domains.AddressSpace.Address.inject(Init.TypeError_Addr),
                    "URIError", Domains.AddressSpace.Address.inject(Init.URIError_Addr),
                    "Function", Domains.AddressSpace.Address.inject(Init.Function_Addr)
            )))
    );

    public static final Domains.Object uriMethodObj = /* InitUtils.unimplemented("uriMethod"); */ InitUtils.pureFunctionObj(InitUtils.ezSig(InitUtils.NoConversion, List.list(InitUtils.StringHint)),
           any-> FHashSet.build(Domains.Str.inject(Domains.Str.STop), Utils.Errors.uriError));
    public static final Domains.Object decodeURI_Obj = uriMethodObj;
    public static final Domains.Object decodeURIComponent_Obj = uriMethodObj;
    public static final Domains.Object encodeURI_Obj = uriMethodObj;
    public static final Domains.Object encodeURIComponent_Obj = uriMethodObj;
    public static final Domains.Object compatabilityURIMethodObj = /*InitUtils.unimplemented("compatabilityURIMethod");*/ InitUtils.constFunctionObj(InitUtils.ezSig(InitUtils.NoConversion, List.list(InitUtils.NumberHint)),  Domains.Str.inject(Domains.Str.STop));
    public static final Domains.Object escape_Obj = compatabilityURIMethodObj;
    public static final Domains.Object unescape_Obj = compatabilityURIMethodObj;
    public static final Domains.Object isFinite_Obj = /*InitUtils.unimplemented("isFinite");*/ InitUtils.pureFunctionObj(InitUtils.ezSig(InitUtils.NoConversion, List.list(InitUtils.NumberHint)),
            (list)-> {
                if (list.length() == 2) {
                    Domains.BValue bv = list.index(1);
                    assert bv.defNum() : "isFinite: conversion should guarante argument must be a number";
                    Domains.Bool tmp;
                    if (bv.n.equals(Domains.Num.Bot)) {
                        tmp = Domains.Bool.Bot;
                    } else if (bv.n.equals(Domains.Num.Top)) {
                        tmp = Domains.Bool.Top;
                    } else if (bv.n.equals(Domains.Num.NReal)) {
                        tmp = Domains.Bool.True;
                    } else {
                        Double d = ((Domains.NConst)bv.n).d;
                        if (d.equals(Double.POSITIVE_INFINITY) || d.equals(Double.NEGATIVE_INFINITY) || d.isNaN()) {
                            tmp = Domains.Bool.False;
                        } else {
                            tmp = Domains.Bool.True;
                        }
                    }
                    return FHashSet.build(Domains.Bool.inject(tmp));
                } else {
                    throw new RuntimeException("isFinite: signature conformance error");
                }
            });
    public static final Domains.Object isNaN_Obj = /*InitUtils.unimplemented("isNaN");*/ InitUtils.pureFunctionObj(InitUtils.ezSig(InitUtils.NoConversion, List.list(InitUtils.NumberHint)),
            (list)-> {
                if (list.length() == 2) {
                    Domains.BValue bv = list.index(1);
                    assert bv.defNum() : "isNaN: conversion should guarantee argument must be a number";
                    Domains.Bool tmp;
                    if (bv.n.equals(Domains.Num.Bot)) {
                        tmp = Domains.Bool.Bot;
                    } else if (bv.n.equals(Domains.Num.Top)) {
                        tmp = Domains.Bool.Top;
                    } else if (bv.n.equals(Domains.Num.NReal)) {
                        tmp = Domains.Bool.False;
                    } else {
                        Double d = ((Domains.NConst)bv.n).d;
                        if (d.isNaN()) {
                            tmp = Domains.Bool.True;
                        } else {
                            tmp = Domains.Bool.False;
                        }
                    }
                    return FHashSet.build(Domains.Bool.inject(tmp));
                } else {
                    throw new RuntimeException("isNaN: signature conformance error");
                }
            });
    public static final Domains.Object parseFloat_Obj = /*InitUtils.unimplemented("parseFloat");*/ InitUtils.constFunctionObj(InitUtils.ezSig(InitUtils.NoConversion, List.list(InitUtils.StringHint)), Domains.Num.inject(Domains.Num.Top));
    public static final Domains.Object parseInt_Obj = /*InitUtils.unimplemented("parseInt");*/ InitUtils.constFunctionObj(InitUtils.ezSig(InitUtils.NoConversion, List.list(InitUtils.StringHint, InitUtils.NumberHint)), Domains.Num.inject(Domains.Num.Top));

    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}
