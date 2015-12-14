package analysis.init;

import analysis.Domains;
import analysis.Interpreter;
import analysis.Utils;
import fj.P2;
import fj.data.List;
import fj.data.Option;
import immutable.FHashMap;
import immutable.FHashSet;
import ir.JSClass;

/**
 * Created by wayne on 15/12/7.
 */
public class InitArray {
    public static final Domains.Object Array_Obj = InitUtils.createInitFunctionObj(
            new Domains.Native(
                    (selfAddr, argArrayAddr, x, env, store, pad, ks, tr) -> {
                        assert argArrayAddr.defAddr() : "Arguments array refers to non-addresses";
                        assert argArrayAddr.as.size() == 1 : "Arguments array refers to multiple addresses";

                        Domains.Object argsObj = store.getObj(argArrayAddr.as.head());
                        Domains.Num argLength = argsObj.apply(Utils.Fields.length).orSome(Domains.BValue.Bot).n;
                        assert !argLength.equals(Domains.Num.Bot) : "When constructing an array, arguments length should be provided";

                        Boolean calledAsConstr = (Boolean)argsObj.intern.get(Utils.Fields.constructor).orSome(false);
                        Domains.Store store1 = store;
                        FHashSet<Domains.AddressSpace.Address> arrayAddrs;
                        if (calledAsConstr) {
                            arrayAddrs = selfAddr.as.filter(a -> store.getObj(a).getJSClass().equals(JSClass.CArray));
                        } else {
                            P2<Domains.Store, Domains.BValue> tmp = Utils.allocObj(Domains.AddressSpace.Address.inject(Init.Array_Addr), tr.toAddr(), store, tr);
                            store1 = tmp._1();
                            arrayAddrs = tmp._2().as;
                        }
                        assert arrayAddrs.size() == 1 : "We should have allocated one and only one address for arrays";
                        Domains.AddressSpace.Address arrayAddr = arrayAddrs.head();
                        Domains.Object oldArrayObj = store1.getObj(arrayAddr);

                        boolean argLenMaybe1, argLenMaybeNot1;
                        if (argLength instanceof Domains.NConst) {
                            Double d = ((Domains.NConst) argLength).d;
                            if (d.intValue() == 1) {
                                argLenMaybe1 = true;
                                argLenMaybeNot1 = false;
                            } else {
                                argLenMaybe1 = false;
                                argLenMaybeNot1 = true;
                            }
                        } else {
                            argLenMaybe1 = true;
                            argLenMaybeNot1 = true;
                        }
                        Option<Integer> extractedArgLength;
                        if (argLength instanceof Domains.NConst) {
                            extractedArgLength = Option.some(((Domains.NConst) argLength).d.intValue());
                        } else {
                            extractedArgLength = Option.none();
                        }
                        Domains.BValue arg0 = argsObj.apply(Domains.Str.alpha("0")).orSome(Domains.Undef.BV);
                        boolean arg0MaybeNumeric = arg0.sorts.member(Domains.DNum);
                        boolean arg0MaybeNotNumeric = !arg0.defNum();

                        FHashSet<Interpreter.State> ones;
                        if (argLenMaybe1 && arg0MaybeNumeric) {
                            P2<Option<P2<Domains.BValue, Domains.Store>>, Option<Domains.EValue>> tmp =
                                    Utils.updateObj(Domains.AddressSpace.Address.inject(arrayAddr), Domains.Str.inject(Utils.Fields.length), arg0.onlyNum(), store1);
                            Option<P2<Domains.BValue, Domains.Store>> noexc = tmp._1();
                            Option<Domains.EValue> exc = tmp._2();
                            FHashSet<Interpreter.State> s1, s2;
                            if (noexc.isSome()) {
                                Domains.BValue bv = noexc.some()._1();
                                Domains.Store store2 = noexc.some()._2();
                                s1 = InitUtils.makeState(Domains.AddressSpace.Address.inject(arrayAddr), x, env, store2, pad, ks, tr);
                            } else {
                                s1 = FHashSet.empty();
                            }
                            if (exc.isSome()) {
                                Domains.EValue ev = exc.some();
                                s2 = InitUtils.makeState(ev, x, env, store1, pad, ks, tr);
                            } else {
                                s2 = FHashSet.empty();
                            }
                            ones = s1.union(s2);
                        } else {
                            ones = FHashSet.empty();
                        }

                        FHashSet<Interpreter.State> notones;
                        if (argLenMaybeNot1 || arg0MaybeNotNumeric) {
                            Domains.Object updatedArrObj;
                            if (extractedArgLength.isSome()) {
                                int knownArgLength = extractedArgLength.some();
                                updatedArrObj = StringHelpers.newArray(Domains.Num.alpha(knownArgLength * 1.0), List.range(0, knownArgLength).map(n -> argsObj.apply(Domains.Str.alpha(n.toString())).orSome(Domains.Undef.BV)), Option.none(), oldArrayObj, false);
                            } else {
                                updatedArrObj = StringHelpers.newArray(argLength, List.list(), argsObj.extern.num, oldArrayObj, false);
                            }
                            notones = InitUtils.makeState(Domains.AddressSpace.Address.inject(arrayAddr), x, env, store1.putObj(arrayAddr, updatedArrObj), pad, ks, tr);
                        } else {
                            notones = FHashSet.empty();
                        }

                        return ones.union(notones);
                    }
            ),
            FHashMap.build(
                    "prototype", Domains.AddressSpace.Address.inject(Init.Array_prototype_Addr),
                    "length", Domains.Num.inject(Domains.Num.alpha(1.0)),
                    "isArray", Domains.AddressSpace.Address.inject(Init.Array_isArray_Addr)),
            FHashMap.empty(),
            JSClass.CArray_Obj
    );

    public static final Domains.Object Array_prototype_Obj = InitUtils.createInitObj(
            FHashMap.build(
                    "constructor",Domains.AddressSpace.Address.inject(Init.Array_Addr),
                    "length", Domains.Num.inject(Domains.Num.alpha(0.0)),
                    "concat",Domains.AddressSpace.Address.inject(Init.Array_prototype_concat_Addr),
                    "indexOf",Domains.AddressSpace.Address.inject(Init.Array_prototype_indexOf_Addr),
                    "join",Domains.AddressSpace.Address.inject(Init.Array_prototype_join_Addr),
                    "lastIndexOf",Domains.AddressSpace.Address.inject(Init.Array_prototype_lastIndexOf_Addr),
                    "pop",Domains.AddressSpace.Address.inject(Init.Array_prototype_pop_Addr),
                    "push",Domains.AddressSpace.Address.inject(Init.Array_prototype_push_Addr),
                    "reverse",Domains.AddressSpace.Address.inject(Init.Array_prototype_reverse_Addr), // TODO
                    "shift",Domains.AddressSpace.Address.inject(Init.Array_prototype_shift_Addr), // TODO
                    "sort",Domains.AddressSpace.Address.inject(Init.Array_prototype_sort_Addr),
                    "splice",Domains.AddressSpace.Address.inject(Init.Array_prototype_splice_Addr),
                    "toString",Domains.AddressSpace.Address.inject(Init.Array_prototype_toString_Addr), // TODO
                    "every",Domains.AddressSpace.Address.inject(Init.Array_prototype_every_Addr), // TODO
                    "filter",Domains.AddressSpace.Address.inject(Init.Array_prototype_filter_Addr), // TODO
                    "forEach",Domains.AddressSpace.Address.inject(Init.Array_prototype_forEach_Addr), // TODO
                    "map",Domains.AddressSpace.Address.inject(Init.Array_prototype_map_Addr), // TODO
                    "reduce",Domains.AddressSpace.Address.inject(Init.Array_prototype_reduce_Addr), // TODO
                    "reduceRight",Domains.AddressSpace.Address.inject(Init.Array_prototype_reduceRight_Addr), // TODO
                    "slice",Domains.AddressSpace.Address.inject(Init.Array_prototype_slice_Addr), // TODO
                    "some",Domains.AddressSpace.Address.inject(Init.Array_prototype_some_Addr), // TODO
                    "toLocaleString",Domains.AddressSpace.Address.inject(Init.Array_prototype_toLocaleString_Addr), // TODO
                    "unshift",Domains.AddressSpace.Address.inject(Init.Array_prototype_unshift_Addr) // TODO
            ),
            FHashMap.build(Utils.Fields.classname, JSClass.CArray_prototype_Obj)
    );

    public static final Domains.Object Array_prototype_join_Obj = InitUtils.createInitFunctionObj(
            new Domains.Native(
                    (selfAddr, argArrayAddr, x, env, store, pad, ks, tr) -> {
                        assert argArrayAddr.defAddr() : "Arguments array refers to non-addresses";
                        assert argArrayAddr.as.size() == 1 : "Arguments array refers to multiple addresses";
                        Domains.BValue lenVal = Utils.lookup(selfAddr.as, Utils.Fields.length, store);
                        Domains.Object argsObj = store.getObj(argArrayAddr.as.head());
                        Domains.BValue separator = argsObj.apply(Domains.Str.alpha("0")).orSome(Domains.Str.inject(Domains.Str.alpha(",")));
                        if (lenVal.equals(Domains.Num.inject(Domains.Num.alpha(0.0))))
                            return InitUtils.makeState(Domains.Str.inject(Domains.Str.alpha("")), x, env, store, pad, ks, tr);
                        else {
                            Domains.BValue summaryVal = Utils.lookup(selfAddr.as, Domains.Str.SNum, store);
                            return InitUtils.ToString(
                                    List.list(separator, summaryVal),
                                    (l, x1, env1, store1, pad1, ks1, tr1) -> {
                                        return InitUtils.makeState(Domains.Str.inject(Domains.Str.STop), x1, env1, store1, pad1, ks1, tr1);
                                    },
                                    x, env, store, pad, ks, tr);
                        }
                    }
            ),
            FHashMap.build("length", Domains.Num.inject(Domains.Num.alpha(1.0)))
    );
    public static final Domains.Object Array_prototype_pop_Obj = InitUtils.unimplemented("Array.prototype.pop");
    public static final Domains.Object Array_prototype_push_Obj = InitUtils.createInitFunctionObj(
            new Domains.Native(
                    (selfAddr, argArrayAddr, x, env, store, pad, ks, tr) -> {
                        assert argArrayAddr.defAddr() : "Arguments array refers to non-addresses";
                        assert argArrayAddr.as.size() == 1 : "Arguments array refers to multiple addresses";
                        FHashSet<Domains.AddressSpace.Address> arrayAddrs = selfAddr.as.filter(a -> store.getObj(a).getJSClass().equals(JSClass.CArray));
                        FHashSet<Interpreter.State> errState;
                        if (arrayAddrs.size() != selfAddr.as.size()) {
                            errState = InitUtils.makeState(Utils.Errors.typeError, x, env, store, pad, ks, tr);
                        } else {
                            errState = FHashSet.empty();
                        }

                        boolean isStrong = arrayAddrs.size() == 1 && store.isStrong(arrayAddrs.head());
                        Domains.Store store1 = arrayAddrs.foldLeft((acc, cur) -> {
                            Domains.Object oldArrayObj = acc.getObj(cur);
                            Domains.BValue summaryVal = Utils.lookup(FHashSet.build(cur), Domains.Str.SNum, acc).merge(
                                    Utils.lookup(argArrayAddr.as, Domains.Str.SNum, acc));
                            Domains.Object updatedArrObj = StringHelpers.newArray(Domains.Num.NReal, List.list(), Option.some(summaryVal), oldArrayObj, true);

                            if (isStrong) {
                                return acc.putObjStrong(cur, updatedArrObj);
                            } else {
                                return acc.putObjWeak(cur, updatedArrObj);
                            }
                        }, store);
                        FHashSet<Interpreter.State> pushedState = InitUtils.makeState(Domains.Num.inject(Domains.Num.NReal), x, env, store1, pad, ks, tr);

                        return pushedState.union(errState);
                    }
            ),
            FHashMap.build("length", Domains.Num.inject(Domains.Num.alpha(1.0)))
    );
    public static final Domains.Object Array_prototype_indexOf_Obj = InitUtils.unimplemented("Array.prototype.indexOf");
    public static final Domains.Object Array_prototype_lastIndexOf_Obj = InitUtils.unimplemented("Array.prototype.lastIndexOf");
    public static final Domains.Object Array_prototype_concat_Obj = InitUtils.unimplemented("Array.prototype.concat");
    public static final Domains.Object Array_prototype_sort_Obj = InitUtils.unimplemented("Array.prototype.sort");
    public static final Domains.Object Array_prototype_splice_Obj = InitUtils.unimplemented("Array.prototype.splice");
    public static final Domains.Object Array_prototype_every_Obj = InitUtils.unimplemented("Array.prototype.every");
    public static final Domains.Object Array_prototype_filter_Obj = InitUtils.unimplemented("Array.prototype.filter");
    public static final Domains.Object Array_prototype_forEach_Obj = InitUtils.unimplemented("Array.prototype.forEach");
    public static final Domains.Object Array_prototype_map_Obj = InitUtils.unimplemented("Array.prototype.map");
    public static final Domains.Object Array_prototype_reduce_Obj = InitUtils.unimplemented("Array.prototype.reduce");
    public static final Domains.Object Array_prototype_reduceRight_Obj = InitUtils.unimplemented("Array.prototype.reduceRight");
    public static final Domains.Object Array_prototype_reverse_Obj = InitUtils.unimplemented("Array.prototype.reverse");
    public static final Domains.Object Array_prototype_shift_Obj = InitUtils.unimplemented("Array.prototype.shift");
    public static final Domains.Object Array_prototype_slice_Obj = InitUtils.unimplemented("Array.prototype.slice");
    public static final Domains.Object Array_prototype_some_Obj = InitUtils.unimplemented("Array.prototype.some");
    public static final Domains.Object Array_prototype_toLocaleString_Obj = InitUtils.unimplemented("Array.prototype.toLocaleString");
    public static final Domains.Object Array_prototype_toString_Obj = InitUtils.unimplemented("Array.prototype.toString");
    public static final Domains.Object Array_prototype_unshift_Obj = InitUtils.unimplemented("Array.prototype.unshift");

}
