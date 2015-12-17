package analysis.init;

import analysis.Domains;
import analysis.Interpreter;
import analysis.Traces;
import analysis.Utils;
import fj.F7;
import fj.P;
import fj.P2;
import fj.P3;
import fj.data.List;
import fj.data.Option;
import immutable.FHashMap;
import immutable.FHashSet;
import ir.IRVar;
import ir.JSClass;

/**
 * Created by wayne on 15/12/7.
 */
public class InitRegExp {
    public static P3<Domains.BValue, Domains.Store, Option<Domains.EValue>> allocUnknownStringArray(Domains.Store store, Traces.Trace trace) {
        P2<Domains.Store, Domains.BValue> tmp = Utils.allocObj(Domains.AddressSpace.Address.inject(Init.Array_Addr), trace.toAddr(), store, trace);
        Domains.Store store1 = tmp._1();
        Domains.BValue arr_bv = tmp._2();
        assert arr_bv.as.size() == 1 : "freshly-allocated address set should have size 1";
        Domains.BValue arrayAddr_bv = Domains.AddressSpace.Address.inject(arr_bv.as.head());
        P2<Option<P2<Domains.BValue, Domains.Store>>, Option<Domains.EValue>> tmp2 =
                Utils.updateObj(arrayAddr_bv, Domains.Str.inject(Utils.Fields.length), Domains.Num.inject(Domains.Num.NReal), store1);
        if (tmp2._1().isSome()) {
            Domains.Store store2 = tmp2._1().some()._2();
            P2<Option<P2<Domains.BValue, Domains.Store>>, Option<Domains.EValue>> tmp3 =
                    Utils.updateObj(arrayAddr_bv, Domains.Str.inject(Domains.Str.SNum), Domains.Str.inject(Domains.Str.STop).merge(Domains.Undef.BV), store2);
            if (tmp3._1().isSome()) {
                return P.p(arrayAddr_bv, tmp3._1().some()._2(), tmp2._2());
            } else {
                throw new RuntimeException("error in making new array's length unknown");
            }
        } else {
            throw new RuntimeException("error in making new array's length unknown");
        }
    }

    public static Domains.Store mutateLastIndex(FHashSet<Domains.AddressSpace.Address> addrs, Domains.Store store) {
        return addrs.foldLeft((acc, cur) -> {
            return acc.merge(store.putObj(cur, store.getObj(cur).weakUpdate(Domains.Str.alpha("lastIndex"), Domains.Num.inject(Domains.Num.NReal))));
        }, store);
    }

    public static FHashSet<P2<Domains.Value, Domains.Store>> matchBody(FHashSet<Domains.AddressSpace.Address> regexp_as, Domains.Store store, Traces.Trace trace) {
        P3<Domains.BValue, Domains.Store, Option<Domains.EValue>> tmp = allocUnknownStringArray(store, trace);
        Domains.BValue arr_a_bv = tmp._1();
        Domains.Store store_ = tmp._2();
        Option<Domains.EValue> err = tmp._3();
        FHashSet<P2<Domains.Value, Domains.Store>> res = FHashSet.build(P.p(arr_a_bv.merge(Domains.Null.BV), mutateLastIndex(regexp_as, store_)));
        if (err.isSome()) {
            res = res.insert(P.p(err.some().bv, store_));
        }
        return res;
    }

    public static final F7<List<Domains.BValue>, IRVar, Domains.Env, Domains.Store, Domains.Scratchpad, Domains.KontStack, Traces.Trace, FHashSet<Interpreter.State>> Internal_RegExp_afterToString =
            (bvs, x, env, store, pad, ks, tr) -> {
                Domains.BValue re_addr_bv = bvs.head();
                assert re_addr_bv.as.size() == 1 : "RegExp: address set of fresh RegExp object should be of size 1";
                Domains.AddressSpace.Address re_addr = re_addr_bv.as.head();
                Domains.Object old_obj = store.getObj(re_addr);
                Domains.Object new_obj = new Domains.Object(old_obj.extern, old_obj.intern.union(
                        FHashMap.build(
                                Domains.Str.alpha("source"), Domains.Str.inject(Domains.Str.STop),
                                Domains.Str.alpha("global"), Domains.Bool.inject(Domains.Bool.BTop),
                                Domains.Str.alpha("ignoreCase"), Domains.Bool.inject(Domains.Bool.BTop),
                                Domains.Str.alpha("multiline"), Domains.Bool.inject(Domains.Bool.BTop),
                                Domains.Str.alpha("lastIndex"), Domains.Num.inject(Domains.Num.alpha(0.0))
                                )
                ), old_obj.present);
                return InitUtils.makeState(re_addr_bv, x, env, store.putObj(re_addr, new_obj), pad, ks, tr);
            };

    public static final Domains.Object RegExp_Obj = InitUtils.createInitFunctionObj(
            new Domains.Native(
                    (selfAddr, argArrayAddr, x, env, store, pad, ks, tr) -> {
                        assert argArrayAddr.defAddr() : "RegExp: Arguments array address set should refer to addresses";
                        assert argArrayAddr.as.size() == 1 : "RegExp: Arguments array address set should contain a single address";
                        Domains.Object argsArray = store.getObj(argArrayAddr.as.head());
                        List<P2<Domains.BValue, InitUtils.ConversionHint>> argList = List.range(0, 2).map(i -> {
                            return P.p(argsArray.apply(Domains.Str.alpha(Integer.toString(i))).orSome(Domains.Undef.BV), InitUtils.StringHint);
                        });
                        Boolean calledAsConstr = (Boolean)argsArray.intern.get(Utils.Fields.constructor).orSome(false);
                        Domains.Store store_;
                        Domains.BValue re_addr_bv;
                        if (calledAsConstr) {
                            P2<Domains.Store, Domains.BValue> tmp = Utils.allocObj(Domains.AddressSpace.Address.inject(Init.RegExp_Addr), tr.toAddr(), store, tr);
                            store_ = tmp._1();
                            re_addr_bv = tmp._2();
                        } else {
                            store_ = store;
                            re_addr_bv = selfAddr;
                        }
                        FHashSet<Interpreter.State> states_success =
                                InitUtils.Convert(argList.cons(P.p(re_addr_bv, InitUtils.NoConversion)), Internal_RegExp_afterToString, x, env, store_, pad, ks, tr);
                        FHashSet<Interpreter.State> states_error =
                                InitUtils.makeState(new Domains.EValue(Utils.Errors.typeError.bv.merge(Utils.Errors.syntaxError.bv)), x, env, store, pad, ks, tr);
                        return states_success.union(states_error);
                    }
            ),
            FHashMap.build(
                    "length", Domains.Num.inject(Domains.Num.alpha(2.0)),
                    "prototype", Domains.AddressSpace.Address.inject(Init.RegExp_prototype_Addr)
            )
    );

    public static final Domains.Object RegExp_prototype_Obj = InitUtils.createInitObj(
            FHashMap.build(
                    "constructor", Domains.AddressSpace.Address.inject(Init.RegExp_Addr),
                    "exec", Domains.AddressSpace.Address.inject(Init.RegExp_prototype_exec_Addr),
                    "test", Domains.AddressSpace.Address.inject(Init.RegExp_prototype_test_Addr),
                    "toString", Domains.AddressSpace.Address.inject(Init.RegExp_prototype_toString_Addr),
                    "source", Domains.Str.inject(Domains.Str.alpha("")),
                    "global", Domains.Bool.inject(Domains.Bool.False),
                    "ignoreCase", Domains.Bool.inject(Domains.Bool.False),
                    "multiline", Domains.Bool.inject(Domains.Bool.False),
                    "lastIndex", Domains.Num.inject(Domains.Num.alpha(0.0))
            ),
            FHashMap.build(
                    Utils.Fields.classname, JSClass.CRegexp
            )
    );

    public static final Domains.Object RegExp_prototype_exec_Obj = InitUtils.usualFunctionObj(
            InitUtils.ezSig(InitUtils.NoConversion, List.list(InitUtils.StringHint)),
            (bvs, store, trace) -> {
                if (bvs.length() == 2) {
                    Domains.BValue self = bvs.head();
                    return matchBody(self.as, store, trace);
                } else {
                    throw new RuntimeException("RegExp.prototype.exec: arguments nonconformant to signature");
                }
            });
    public static final Domains.Object RegExp_prototype_test_Obj = InitUtils.unimplemented("RegExp.prototype.test");
    public static final Domains.Object RegExp_prototype_toString_Obj = InitUtils.unimplemented("RegExp.prototype.toString");
}
