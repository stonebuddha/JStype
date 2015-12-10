package analysis.init;

import analysis.Domains;
import analysis.Utils;
import analysis.Interpreter;
import fj.P;
import fj.P2;
import fj.P3;
import fj.data.List;
import fj.data.Option;
import immutable.FHashMap;
import immutable.FHashSet;
import ir.JSClass;

/**
 * Created by wayne on 15/12/7.
 */
public class InitFunction {
    public static final Domains.Object Function_prototype_Obj = InitUtils.createInitObj(
            FHashMap.build(
                    "length", Domains.Num.inject(Domains.Num.alpha(0.0)),
                    "apply", Domains.AddressSpace.Address.inject(Init.Function_prototype_apply_Addr),
                    "call", Domains.AddressSpace.Address.inject(Init.Function_prototype_call_Addr),
                    "toString", Domains.AddressSpace.Address.inject(Init.Function_prototype_toString_Addr),
                    "constructor", Domains.AddressSpace.Address.inject(Init.Function_Addr)
            ),
            FHashMap.build(
                    Utils.Fields.proto, Domains.AddressSpace.Address.inject(Init.Object_prototype_Addr),
                    Utils.Fields.classname, JSClass.CFunction_prototype_Obj,
                    Utils.Fields.code, FHashSet.build(new Domains.Native(
                            (selfAddr, argArrayAddr, x, env, store, pad, ks, tr) -> {
                                return InitUtils.makeState(Domains.Undef.BV, x, env, store, pad, ks, tr);
                            }
                    ))
            )
    );

    public static final Domains.Object Function_prototype_toString_Obj = /*InitUtils.unimplemented("Function.prototype.toString");*/ InitUtils.constFunctionObj(InitUtils.ezSig(InitUtils.NoConversion, List.list()), Domains.Str.inject(Domains.Str.FunctionStr));
    public static final Domains.Object Function_prototype_apply_Obj = /*InitUtils.unimplemented("Function.prototype.apply");*/ InitUtils.createInitFunctionObj(
            new Domains.Native((selfAddr, argArrayAddr, x, env, store, pad, ks, tr)-> {
                assert argArrayAddr.defAddr() && argArrayAddr.as.size() == 1 : "Arguments array refers to non-addresses or multiple addresses";
                Domains.Object argsObj = store.getObj(argArrayAddr.as.head());
                Domains.Num argLength = argsObj.apply(Domains.Str.alpha("length")).orSome(Domains.BValue.Bot).n;
                Domains.BValue input = argsObj.apply(Domains.Str.alpha("0")).orSome(Domains.Undef.BV);
                Domains.AddressSpace.Address traceAddr = tr.toAddr();
                P3<Domains.BValue, Domains.Store, FHashSet<Domains.Domain>> tmp1 = Utils.toObjBody(input, store, tr, traceAddr);
                Domains.BValue bv1 = tmp1._1(), bv2;
                Domains.Store store1 = tmp1._2(), store2;
                P2<Domains.Store, Domains.BValue> tmp2;
                if (input.nil.equals(Domains.Null.MaybeNull) || input.undef.equals(Domains.Undef.MaybeUndef)) {
                    tmp2 = Utils.allocObj(Domains.AddressSpace.Address.inject(Init.Object_Addr), traceAddr, store1, tr);
                } else {
                    tmp2 = P.p(store1, Domains.BValue.Bot);
                }
                store2 = tmp2._1();
                bv2 = tmp2._2();
                Domains.BValue newThisAddr = bv2.merge(bv1);
                Option<Integer> extractedArgLength;
                if (argLength instanceof Domains.NConst) {
                    Double d = ((Domains.NConst)argLength).d;
                    extractedArgLength = Option.some(d.intValue());
                } else {
                    extractedArgLength = Option.none();
                }
                FHashMap<Domains.Str, java.lang.Object> intern = FHashMap.build(
                        Utils.Fields.proto, Domains.AddressSpace.Address.inject(Init.Object_prototype_Addr),
                        Utils.Fields.classname, JSClass.CArguments
                );
                Domains.Object tmpArgsObj = new Domains.Object(new Domains.ExternMap(), intern, FHashSet.empty());
                if (extractedArgLength.isSome() && extractedArgLength.some().equals(2)) {
                    Domains.BValue newArgHolderAs_ = argsObj.apply(Domains.Str.alpha("1")).some();
                    Boolean mightError = !newArgHolderAs_.defAddr() || !selfAddr.defAddr();
                    FHashSet<Domains.AddressSpace.Address> newArgHolderAs = newArgHolderAs_.as;
                    Domains.Num newArgLength = Utils.lookup(newArgHolderAs, Domains.Str.alpha("length"), store2).n;
                    if (!newArgLength.equals(Domains.Num.Bot)) {
                        Option<Integer> newExtractedArgLength;
                        if (newArgLength instanceof Domains.NConst) {
                            Double d = ((Domains.NConst)newArgLength).d;
                            newExtractedArgLength = Option.some(d.intValue());
                        } else {
                            newExtractedArgLength = Option.none();
                        }
                        Domains.AddressSpace.Address newArgsAddr = tr.modAddr(traceAddr, JSClass.CArguments);
                        Domains.Object newArgsObj;
                        if (newExtractedArgLength.isSome()) {
                            Integer newlen = newExtractedArgLength.some();
                            newArgsObj = StringHelpers.newArray(Domains.Num.alpha(newlen.doubleValue()), List.range(0, newlen).map(n -> Utils.lookup(newArgHolderAs, Domains.Str.alpha(n.toString()), store2)), Option.none(), tmpArgsObj, false);
                        } else {
                            newArgsObj = StringHelpers.newArray(newArgLength, List.list(), Option.some(Utils.lookup(newArgHolderAs, Domains.Str.SNum, store2)), tmpArgsObj, false);
                        }
                        Domains.Store store3 = store2.alloc(newArgsAddr, newArgsObj);
                        FHashSet<Interpreter.State> tmp;
                        if (mightError) {
                            tmp = InitUtils.makeState(Utils.Errors.typeError, x, env, store, pad, ks, tr);
                        } else {
                            tmp = FHashSet.empty();
                        }
                        return Utils.applyClo(selfAddr, newThisAddr, Domains.AddressSpace.Address.inject(newArgsAddr), x, env, store3, pad, ks, tr).union(tmp);
                    } else {
                        return InitUtils.makeState(Utils.Errors.typeError, x, env, store, pad, ks, tr);
                    }
                } else if (extractedArgLength.isSome() && extractedArgLength.some().equals(1)) {
                    Domains.AddressSpace.Address newArgsAddr = tr.modAddr(traceAddr, JSClass.CArguments);
                    Domains.Object newArgsObj = StringHelpers.newArray(Domains.Num.Zero, List.list(), Option.none(), tmpArgsObj, false);
                    Domains.Store store3 = store2.alloc(newArgsAddr, newArgsObj);
                    return Utils.applyClo(selfAddr, newThisAddr, Domains.AddressSpace.Address.inject(newArgsAddr), x, env, store3, pad, ks, tr);
                } else {
                    throw new RuntimeException("!! Not Implemented: .apply with arguments length = " + extractedArgLength);
                }
            }),
            FHashMap.build("length", Domains.Num.inject(Domains.Num.alpha(2.0)))
    );
    public static final Domains.Object Function_prototype_call_Obj = /*InitUtils.unimplemented("Function.prototype.call");*/ InitUtils.createInitFunctionObj(
            new Domains.Native((selfAddr, argArrayAddr, x, env, store, pad, ks, tr)-> {
                assert argArrayAddr.defAddr() && argArrayAddr.as.size() == 1 : "Arguments array refers to non-addresses or multiple addresses";
                Domains.Object argsObj = store.getObj(argArrayAddr.as.head());
                Domains.Num argLength = argsObj.apply(Domains.Str.alpha("length")).orSome(Domains.BValue.Bot).n;
                Domains.BValue input = argsObj.apply(Domains.Str.alpha("0")).orSome(Domains.Undef.BV);
                Domains.AddressSpace.Address traceAddr = tr.toAddr();
                P3<Domains.BValue, Domains.Store, FHashSet<Domains.Domain>> tmp1 = Utils.toObjBody(input, store, tr, traceAddr);
                Domains.BValue bv1 = tmp1._1(), bv2;
                Domains.Store store1 = tmp1._2(), store2;
                P2<Domains.Store, Domains.BValue> tmp2;
                if (input.nil.equals(Domains.Null.MaybeNull) || input.undef.equals(Domains.Undef.MaybeUndef)) {
                    tmp2 = Utils.allocObj(Domains.AddressSpace.Address.inject(Init.Object_Addr), traceAddr, store1, tr);
                } else {
                    tmp2 = P.p(store1, Domains.BValue.Bot);
                }
                store2 = tmp2._1();
                bv2 = tmp2._2();
                Domains.BValue newThisAddr = bv2.merge(bv1);
                Option<Integer> extractedArgLength;
                if (argLength instanceof Domains.NConst) {
                    Double d = ((Domains.NConst)argLength).d;
                    extractedArgLength = Option.some(d.intValue());
                } else {
                    extractedArgLength = Option.none();
                }
                Domains.AddressSpace.Address newArgsAddr = tr.modAddr(traceAddr, JSClass.CArguments);
                FHashMap<Domains.Str, java.lang.Object> intern = FHashMap.build(
                        Utils.Fields.proto, Domains.AddressSpace.Address.inject(Init.Object_prototype_Addr),
                        Utils.Fields.classname, JSClass.CArguments
                );
                Domains.Object tmpArgsObj = new Domains.Object(new Domains.ExternMap(), intern, FHashSet.empty());

                //TODO length 0

                Domains.Object newArgsObj;
                if (extractedArgLength.isSome()) {
                    Integer newlen = extractedArgLength.some();
                    newArgsObj = StringHelpers.newArray(Domains.Num.alpha(newlen.doubleValue() - 1.0), List.range(1, newlen).map(n -> argsObj.apply(Domains.Str.alpha(n.toString())).some()), Option.none(), tmpArgsObj, false);
                } else {
                    newArgsObj = StringHelpers.newArray(argLength, List.list(), argsObj.apply(Domains.Str.SNum), tmpArgsObj, false);
                }
                Domains.Store store3 = store2.alloc(newArgsAddr, newArgsObj);
                FHashSet<Interpreter.State> tmp;
                if (!selfAddr.defAddr()) {
                    tmp = InitUtils.makeState(Utils.Errors.typeError, x, env, store, pad, ks, tr);
                } else {
                    tmp = FHashSet.empty();
                }
                return Utils.applyClo(selfAddr, newThisAddr, Domains.AddressSpace.Address.inject(newArgsAddr), x, env, store3, pad, ks, tr).union(tmp);
            }),
            FHashMap.build("length", Domains.Num.inject(Domains.Num.alpha(1.0)))
    );
}