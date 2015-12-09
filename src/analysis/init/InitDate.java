package analysis.init;

import analysis.Domains;
import analysis.Traces;
import analysis.Utils;
import analysis.Interpreter;
import fj.F7;
import fj.P;
import fj.P2;
import fj.data.List;
import immutable.FHashMap;
import immutable.FHashSet;
import ir.IRVar;
import ir.JSClass;

/**
 * Created by wayne on 15/12/7.
 */
public class InitDate {
    public static final F7<List<Domains.BValue>, IRVar, Domains.Env, Domains.Store, Domains.Scratchpad, Domains.KontStack, Traces.Trace, FHashSet<Interpreter.State>> Internal_Date_constructor_afterToNumber = InitUtils.genValueObjConstructor("Date", any-> Domains.Num.inject(Domains.Num.NTop));
    public static final Domains.Object Date_Obj = InitUtils.createInitFunctionObj(
            new Domains.Native(
                    (selfAddr, argArrayAddr, x, env, store, pad, ks, tr) -> {
                        assert argArrayAddr.defAddr() : "Date: Arguments array should refer to addresses";
                        assert argArrayAddr.as.size() == 1 : "Date: Arguments array should refer to a single address";
                        Domains.Object argsArray = store.getObj(argArrayAddr.as.head());
                        List<P2<Domains.BValue, InitUtils.ConversionHint>> argList = List.range(0, 7).map(i-> P.p(argsArray.apply(Domains.Str.alpha(i.toString())).orSome(Domains.Undef.BV), InitUtils.NumberHint));
                        Boolean calledAsConstr = (Boolean)argsArray.intern.get(Utils.Fields.constructor).orSome(false);
                        if (calledAsConstr) {
                            return InitUtils.Convert(argList.cons(P.p(selfAddr, InitUtils.NoConversion)), Internal_Date_constructor_afterToNumber, x, env, store, pad, ks, tr);
                        }
                        else {
                            return InitUtils.makeState(Domains.Str.inject(Domains.Str.DateStr), x, env, store, pad, ks, tr);
                        }
                    }
            ),
            FHashMap.build(
                    "length", Domains.Num.inject(Domains.Num.alpha(7.0)),
                    "prototype", Domains.AddressSpace.Address.inject(Init.Date_prototype_Addr),
                    "now", Domains.AddressSpace.Address.inject(Init.Date_now_Addr),
                    "parse", Domains.AddressSpace.Address.inject(Init.Date_parse_Addr)
            )
    );

    public static final Domains.Object Date_now_Obj = InitUtils.unimplemented("Date.now"); /*InitUtils.constFunctionObj(InitUtils.ezSig(InitUtils.NoConversion, List.list()), Domains.Num.inject(Domains.Num.NTop));*/
    public static final Domains.Object Date_parse_Obj = InitUtils.unimplemented("Date.parse"); /*InitUtils.constFunctionObj(InitUtils.ezSig(InitUtils.NoConversion, List.list(InitUtils.StringHint)), Domains.Num.inject(Domains.Num.NTop));*/

    public static final Domains.Object Date_prototype_Obj = InitUtils.createInitObj(
            FHashMap.build(
                    "toString", Domains.AddressSpace.Address.inject(Init.Date_prototype_toString_Addr),
                    "valueOf", Domains.AddressSpace.Address.inject(Init.Date_prototype_valueOf_Addr),
                    "toLocaleString", Domains.AddressSpace.Address.inject(Init.Date_prototype_toLocaleString_Addr)
            ),
            FHashMap.build(
                    Utils.Fields.value, Domains.Num.inject(Domains.Num.NaN),
                    Utils.Fields.classname, JSClass.CDate
            )
    );

    public static final Domains.Object Date_prototype_toString_Obj = InitUtils.unimplemented("Date.prototype.toString"); /*InitUtils.constFunctionObj(InitUtils.ezSig(InitUtils.NoConversion, List.list()), Domains.Str.inject(Domains.Str.DateStr));*/
    public static final Domains.Object Date_prototype_valueOf_Obj = InitUtils.unimplemented("Date.prototype.valueOf");/*InitUtils.constFunctionObj(InitUtils.ezSig(InitUtils.NoConversion, List.list()), Domains.Num.inject(Domains.Num.NTop));*/
    public static final Domains.Object Date_prototype_toLocaleString_Obj = InitUtils.unimplemented("Date.prototype.toLocaleString");/*InitUtils.constFunctionObj(InitUtils.ezSig(InitUtils.NoConversion, List.list()), Domains.Str.inject(Domains.Str.DateStr));*/
}
