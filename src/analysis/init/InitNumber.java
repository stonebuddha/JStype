package analysis.init;

import analysis.Domains;
import analysis.Traces;
import analysis.Utils;
import analysis.Interpreter;
import fj.F7;
import fj.P;
import fj.P2;
import immutable.FHashMap;
import immutable.FHashSet;
import fj.data.List;
import ir.IRVar;
import ir.JSClass;

import javax.rmi.CORBA.Util;

/**
 * Created by wayne on 15/12/7.
 */
public class InitNumber {
    public static final F7<List<Domains.BValue>, IRVar, Domains.Env, Domains.Store, Domains.Scratchpad, Domains.KontStack, Traces.Trace, FHashSet<Interpreter.State>> Internal_Number_constructor_afterToNumber = InitUtils.valueObjConstructor("Number", arg_value-> {
        assert arg_value.defNum() : "Number constructor: type conversion ensures argument is a number";
    });

    public static final F7<List<Domains.BValue>, IRVar, Domains.Env, Domains.Store, Domains.Scratchpad, Domains.KontStack, Traces.Trace, FHashSet<Interpreter.State>> Internal_Number_normal_afterToNumber =
            (bvs, x, env, store, pad, ks, tr)-> {
                assert bvs.length() == 1 : "Number function: should have 1 argument by this point";
                Domains.BValue arg_value = bvs.index(0);
                assert arg_value.defNum() : "Number function: type conversion ensures argument is a number";
                return InitUtils.makeState(arg_value, x, env, store, pad, ks, tr);
            };

    public static final Domains.Object Number_Obj = InitUtils.createInitFunctionObj(
            new Domains.Native(
                    (selfAddr, argArrayAddr, x, env, store, pad, ks, tr) -> {
                        assert argArrayAddr.defAddr() : "Number: Arguments array refers to non-addresses";
                        assert argArrayAddr.as.size() == 1 : "Number: Arguments array refers to multiple addresses";
                        Domains.Object argsObj = store.getObj(argArrayAddr.as.head());
                        P2<Domains.BValue, InitUtils.ConversionHint> input = P.p(argsObj.apply(Domains.Str.alpha("0")).orSome(Domains.Num.inject(Domains.Num.alpha(0.0))), InitUtils.NumberHint);
                        Boolean calledAsConstr = (Boolean)argsObj.intern.get(Utils.Fields.constructor).orSome(false);
                        List<P2<Domains.BValue, InitUtils.ConversionHint>> convList;
                        F7<List<Domains.BValue>, IRVar, Domains.Env, Domains.Store, Domains.Scratchpad, Domains.KontStack, Traces.Trace, FHashSet<Interpreter.State>> postConvF;
                        if (calledAsConstr) {
                            convList = List.list(P.p(selfAddr, InitUtils.NoConversion), input);
                            postConvF = Internal_Number_constructor_afterToNumber;
                        } else {
                            convList = List.list(input);
                            postConvF = Internal_Number_normal_afterToNumber;
                        }
                        return InitUtils.Convert(convList, postConvF, x, env, store, pad, ks, tr);
                    }
            ),
            FHashMap.build(
                    "prototype", Domains.AddressSpace.Address.inject(Init.Number_prototype_Addr),
                    "length", Domains.Num.inject(Domains.Num.alpha(1.0)),
                    "MAX_VALUE", Domains.Num.inject(Domains.Num.NReal),
                    "MIN_VALUE", Domains.Num.inject(Domains.Num.NReal),
                    "NEGATIVE_INFINITY", Domains.Num.inject(Domains.Num.NInf),
                    "POSITIVE_INFINITY", Domains.Num.inject(Domains.Num.Inf),
                    "NaN", Domains.Num.inject(Domains.Num.NaN)
            ),
            FHashMap.empty(),
            JSClass.CNumber_Obj
    );

    public static final Domains.Object Number_prototype_Obj = InitUtils.createInitObj(
            FHashMap.build(
                    "constructor", Domains.AddressSpace.Address.inject(Init.Number_Addr),
                    "toString", Domains.AddressSpace.Address.inject(Init.Number_prototype_toString_Addr),
                    "valueOf", Domains.AddressSpace.Address.inject(Init.Number_prototype_valueOf_Addr),
                    "toLocaleString", Domains.AddressSpace.Address.inject(Init.Number_prototype_toLocaleString_Addr),
                    "toFixed", Domains.AddressSpace.Address.inject(Init.Number_prototype_toFixed_Addr),
                    "toExponential", Domains.AddressSpace.Address.inject(Init.Number_prototype_toExponential_Addr),
                    "toPrecision", Domains.AddressSpace.Address.inject(Init.Number_prototype_toPrecision_Addr)
            ),
            FHashMap.build(
                    Utils.Fields.classname, JSClass.CNumber_prototype_Obj,
                    Utils.Fields.value, Domains.Num.inject(Domains.Num.Zero)
            )
    );

    public static final F7<List<Domains.BValue>, IRVar, Domains.Env, Domains.Store, Domains.Scratchpad, Domains.KontStack, Traces.Trace, FHashSet<Interpreter.State>> Internal_Number_prototype_toString_afterToNumber =
            (bvs, x, env, store, pad, ks, tr)-> {
                assert bvs.length() == 2 : "Number.prototype.toString: should have self value plus radix by this point";
                Domains.BValue selfNum = bvs.index(0), radix = bvs.index(1);
                assert selfNum.defNum() : "Number.prototype.toString: self value should be a number by this point";
                assert radix.defNum() : "Number.prototype.toString: radix should be a number by this point";
                FHashSet<Domains.Value> result;
                if (radix.n.equals(Domains.Num.NBot)) {
                    result = FHashSet.empty();
                } else if (radix.n instanceof Domains.NConst) {
                    Double d = ((Domains.NConst)radix.n).d;
                    if (d.equals(1)) {
                        result = FHashSet.build(Domains.Str.inject(selfNum.n.toStr()));
                    } else if (d >= 2 && d <= 36) {
                        result = FHashSet.build(Domains.Str.inject(Domains.Str.STop));
                    } else {
                        result = FHashSet.build(Utils.Errors.rangeError);
                    }
                } else {
                    result = FHashSet.build(Domains.Str.inject(Domains.Str.STop), Utils.Errors.rangeError);
                }
                return result.map(v -> InitUtils.makeState(v, x, env, store, pad, ks, tr)).foldLeft((acc, e) -> acc.union(e), FHashSet.<Interpreter.State>empty());
            };

    public static final Domains.Object Number_prototype_toString_Obj = InitUtils.createInitFunctionObj(
            new Domains.Native(
                    (selfAddr, argArrayAddr, x, env, store, pad, ks, tr)-> {
                        assert argArrayAddr.defAddr() : "Number.prototype.toString: Arguments array refers to non-addresses";
                        assert argArrayAddr.as.size() == 1 : "Number.prototype.toString: Arguments array refers to multiple addresses";
                        Domains.Object argsObj = store.getObj(argArrayAddr.as.head());
                        P2<FHashSet<Domains.Num>, FHashSet<Domains.EValue>> tmp = InitUtils.toPrimeHelper(selfAddr, store, any-> InitUtils.isNumberClass(any), any-> any.n, Domains.Num.Bot, p-> p._1().merge(p._2()));
                        FHashSet<Domains.Num> selfNums = tmp._1();
                        FHashSet<Domains.EValue> errs = tmp._2();
                        Domains.BValue radix = argsObj.apply(Domains.Str.alpha("0")).orSome(Domains.Undef.BV);
                        Domains.BValue radix_undef = Domains.Num.inject(radix.undef.equals(Domains.Undef.MaybeUndef) ? Domains.Num.alpha(10.0) : Domains.Num.NBot);
                        Domains.BValue radix_bv = radix_undef.merge(radix);
                        Domains.Num selfNum = selfNums.foldLeft((acc, e)-> acc.merge(e), Domains.Num.Bot);
                        FHashSet<Interpreter.State> num, err;
                        if (!selfNum.equals(Domains.Num.Bot)) {
                            num = InitUtils.Convert(List.list(P.p(Domains.Num.inject(selfNum), InitUtils.NoConversion), P.p(radix_bv, InitUtils.NumberHint)), Internal_Number_prototype_toString_afterToNumber, x, env, store, pad, ks, tr);
                        } else {
                            num = FHashSet.empty();
                        }
                        err = errs.map(v -> InitUtils.makeState(v, x, env, store, pad, ks, tr)).foldLeft((acc, e) -> acc.union(e), FHashSet.<Interpreter.State>empty());
                        return num.union(err);
                    }
            ), FHashMap.build("length", Domains.Num.inject(Domains.Num.alpha(1.0)))
    );
    public static final Domains.Object Number_prototype_valueOf_Obj = InitUtils.usualToPrim(any-> InitUtils.isNumberClass(any), any-> any.n, Domains.Num.Bot, any-> Domains.Num.inject(any), p-> p._1().merge(p._2()));
    public static final Domains.Object Number_prototype_toLocaleString_Obj = InitUtils.unimplemented("Number.prototype.toLocaleString");
    public static final Domains.Object Number_prototype_toFixed_Obj = InitUtils.unimplemented("Number.prototype.toFixed");
    public static final Domains.Object Number_prototype_toExponential_Obj = InitUtils.unimplemented("Number.prototype.toExponential");
    public static final Domains.Object Number_prototype_toPrecision_Obj = InitUtils.unimplemented("Number.prototype.toPrecision");
}
