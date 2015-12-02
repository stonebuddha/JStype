package analysis.init;

import analysis.Domains;
import immutable.FHashMap;

/**
 * Created by BenZ on 15/11/24.
 */
public class InitGlobal {

    // TODO
    public static final Domains.Object window_Obj = InitUtils.createInitObj(
            FHashMap.build(
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
                    "NaN", Domains.Num.inject(Domains.Num.alpha(Double.NaN))
                    )
    );

    public static final Domains.Object uriMethodObj = null;
    public static final Domains.Object decodeURI_Obj = uriMethodObj;
    public static final Domains.Object decodeURIComponent_Obj = uriMethodObj;
    public static final Domains.Object encodeURI_Obj = uriMethodObj;
    public static final Domains.Object encodeURIComponent_Obj = uriMethodObj;
    public static final Domains.Object compatabilityURIMethodObj = null;
    public static final Domains.Object escape_Obj = compatabilityURIMethodObj;
    public static final Domains.Object unescape_Obj = compatabilityURIMethodObj;
    public static final Domains.Object isFinite_Obj = null;
    public static final Domains.Object isNaN_Obj = null;
    public static final Domains.Object parseFloat_Obj = null;
    public static final Domains.Object parseInt_Obj = null;
}
