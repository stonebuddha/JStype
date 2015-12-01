package concrete.init;

import concrete.Domains;
import concrete.Utils;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.Option;
import immutable.FHashMap;
import ir.JSClass;

/**
 * Created by wayne on 15/11/28.
 */
public class InitArray {
    public static Domains.Object Array_Obj = InitUtils.makeNativeValueStore(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object args = store.getObj(argArrayAddr);
                Boolean calledAsConstr = args.intern.get(Utils.Fields.constructor).map(o -> (Boolean)o).orSome(false);
                P2<Domains.Store, Domains.Address> tmp1;
                if (calledAsConstr) {
                    tmp1 = P.p(store, selfAddr);
                } else {
                    tmp1 = Utils.allocObj(Init.Array_Addr, store);
                }
                Option<Domains.BValue> _arglen = args.apply(Utils.Fields.length);
                double arglen;
                if (_arglen.isSome() && _arglen.some() instanceof Domains.Num) {
                    arglen = ((Domains.Num) _arglen.some()).n;
                } else {
                    throw new RuntimeException("implementation error: arguments without numeric length");
                }
                Domains.Store store1 = tmp1._1();
                Domains.Address arrayAddr = tmp1._2();
                FHashMap<Domains.Str, Object> internal = store1.getObj(arrayAddr).intern;
                if (arglen == 0 || arglen >= 2) {
                    List<Integer> range = List.range(0, (int)arglen);
                    FHashMap<Domains.Str, Domains.BValue> initial = FHashMap.build(Utils.Fields.length, new Domains.Num(arglen));
                    FHashMap<Domains.Str, Domains.BValue> external =
                            range.foldLeft((acc, cur) -> {
                                Option<Domains.BValue> bv = args.apply(new Domains.Str(cur.toString()));
                                return acc.set(new Domains.Str(cur.toString()), bv.orSome(Domains.Undef));
                            }, initial);
                    Domains.Object newObj = InitUtils.createObj(external, internal);
                    Domains.Store newStore = store1.putObj(arrayAddr, newObj);
                    return P.p(arrayAddr, newStore);
                } else {
                    Domains.BValue len = args.apply(new Domains.Str("0")).orSome(Domains.Undef);
                    if (len instanceof Domains.Num) {
                        double n = ((Domains.Num) len).n;
                        if ((int)n != n || n < 0) {
                            return P.p(Utils.Errors.rangeError, store);
                        } else {
                            Domains.Object newObj = InitUtils.createObj(FHashMap.build(Utils.Fields.length, new Domains.Num(n)), internal);
                            Domains.Store newStore = store1.putObj(arrayAddr, newObj);
                            return P.p(arrayAddr, newStore);
                        }
                    } else {
                        Domains.Object newObj = InitUtils.createObj(FHashMap.build(Utils.Fields.length, new Domains.Num(1.0), new Domains.Str("0"), len), internal);
                        Domains.Store newStore = store1.putObj(arrayAddr, newObj);
                        return P.p(arrayAddr, newStore);
                    }
                }
            },
            FHashMap.build(
                    Utils.Fields.prototype, Init.Array_prototype_Addr,
                    new Domains.Str("isArray"), Init.Array_isArray_Addr,
                    Utils.Fields.length, new Domains.Num(1.0)),
            JSClass.CArray_Obj
    );

    public static Domains.Object Array_prototype_Obj = InitUtils.createObj(
            FHashMap.build(
                    Utils.Fields.constructor, Init.Array_Addr,
                    new Domains.Str("concat"), Init.Array_prototype_concat_Addr,
                    new Domains.Str("every"), Init.Array_prototype_every_Addr,
                    new Domains.Str("filter"), Init.Array_prototype_filter_Addr,
                    new Domains.Str("forEach"), Init.Array_prototype_forEach_Addr,
                    new Domains.Str("indexOf"), Init.Array_prototype_indexOf_Addr,
                    new Domains.Str("join"), Init.Array_prototype_join_Addr,
                    new Domains.Str("lastIndexOf"), Init.Array_prototype_lastIndexOf_Addr,
                    new Domains.Str("map"), Init.Array_prototype_map_Addr,
                    new Domains.Str("pop"), Init.Array_prototype_pop_Addr,
                    new Domains.Str("push"), Init.Array_prototype_push_Addr,
                    new Domains.Str("reduce"), Init.Array_prototype_reduce_Addr,
                    new Domains.Str("reduceRight"), Init.Array_prototype_reduceRight_Addr,
                    new Domains.Str("reverse"), Init.Array_prototype_reverse_Addr,
                    new Domains.Str("shift"), Init.Array_prototype_shift_Addr,
                    new Domains.Str("slice"), Init.Array_prototype_slice_Addr,
                    new Domains.Str("some"), Init.Array_prototype_some_Addr,
                    new Domains.Str("sort"), Init.Array_prototype_sort_Addr,
                    new Domains.Str("splice"), Init.Array_prototype_splice_Addr,
                    new Domains.Str("toLocaleString"), Init.Array_prototype_toLocaleString_Addr,
                    new Domains.Str("toString"), Init.Array_prototype_toString_Addr,
                    new Domains.Str("unshift"), Init.Array_prototype_unshift_Addr
            ),
            FHashMap.build(
                    Utils.Fields.classname, JSClass.CArray_prototype_Obj)
    );

    public static Domains.Object Array_isArray_Obj = InitUtils.makeNativeValue(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object args = store.getObj(argArrayAddr);
                Domains.BValue input = args.apply(new Domains.Str("0")).orSome(Domains.Undef);
                if (input instanceof Domains.Address) {
                    return Domains.Bool.apply(store.getObj((Domains.Address)input).getJSClass().equals(JSClass.CArray));
                } else {
                    return Domains.Bool.apply(false);
                }
            },
            FHashMap.build(Utils.Fields.length, new Domains.Num(1.0))
    );

    public static Domains.Object Array_prototype_concat_Obj = InitUtils.makeNativeValueStore(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object selfObj = store.getObj(selfAddr);
                Domains.Object argObj = store.getObj(argArrayAddr);

                Domains.Address newArrayAddr = Domains.Address.generate();
                Domains.BValue tmp = Utils.lookup(selfObj, Utils.Fields.length, store);
                double selflen;
                if (tmp instanceof Domains.Num) {
                    selflen = ((Domains.Num) tmp).n;
                } else {
                    throw new RuntimeException("not implemented: Array.concat on array of non-numeric length");
                }
                double arglen;
                tmp = Utils.lookup(argObj, Utils.Fields.length, store);
                if (tmp instanceof Domains.Num) {
                    arglen = ((Domains.Num) tmp).n;
                } else {
                    throw new RuntimeException("not implemented: Array.concat on array of non-numeric length");
                }
                List<Domains.BValue> selfElems = List.range(0, (int)selflen).foldLeft((acc, cur) -> {
                    return acc.snoc(Utils.lookup(selfObj, new Domains.Str(cur.toString()), store));
                }, List.list());
                List<Domains.BValue> newElems = List.range(0, (int)arglen).foldLeft((acc, cur) -> {
                    Domains.BValue _tmp = Utils.lookup(argObj, new Domains.Str(cur.toString()), store);
                    if (_tmp instanceof Domains.Address) {
                        Domains.Object elemObj = store.getObj((Domains.Address)_tmp);
                        if (elemObj.getJSClass().equals(JSClass.CArray)) {
                            Domains.BValue len = Utils.lookup(elemObj, Utils.Fields.length, store);
                            double elemlen;
                            if (len instanceof Domains.Num) {
                                elemlen = ((Domains.Num)len).n;
                            } else {
                                throw new RuntimeException("not implemented: array with non-numeric length");
                            }
                            return List.range(0, (int)elemlen).foldLeft((acc1, cur1) -> {
                                return acc1.snoc(Utils.lookup(elemObj, new Domains.Str(cur1.toString()), store));
                            }, acc);
                        } else {
                            return acc.snoc(_tmp);
                        }
                    } else {
                        return acc.snoc(_tmp);
                    }
                }, selfElems);
                List<P2<Domains.Str, Domains.BValue>> lst = List.range(0, newElems.length()).map(x -> new Domains.Str(x.toString())).zip(newElems);
                FHashMap<Domains.Str, Domains.BValue> external = FHashMap.build(lst).set(Utils.Fields.length, new Domains.Num(1.0 * newElems.length()));
                FHashMap<Domains.Str, Object> internal = FHashMap.build(Utils.Fields.proto, Init.Array_prototype_Addr, Utils.Fields.classname, JSClass.CArray);
                Domains.Object newArrayObj = InitUtils.createObj(external, internal);
                Domains.Store store1 = store.putObj(newArrayAddr, newArrayObj);
                return P.p(newArrayAddr, store1);
            },
            FHashMap.build(Utils.Fields.length, new Domains.Num(1.0))
    );

    public static Domains.Object Array_prototype_every_Obj = InitUtils.unimplemented;

    public static Domains.Object Array_prototype_filter_Obj = InitUtils.unimplemented;

    public static Domains.Object Array_prototype_forEach_Obj = InitUtils.unimplemented;

    public static Domains.Object Array_prototype_indexOf_Obj = InitUtils.unimplemented;

    public static Domains.Object Array_prototype_join_Obj = InitUtils.makeNativeValue(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object selfObj = store.getObj(selfAddr);
                Domains.Object argObj = store.getObj(argArrayAddr);
                Domains.Str sep;
                Option<Domains.BValue> tmp = argObj.apply(new Domains.Str("0"));
                if (tmp.isSome()) {
                    if (tmp.some().equals(Domains.Undef)) {
                        sep = new Domains.Str(",");
                    } else {
                        sep = InitUtils.ToString(tmp.some(), store);
                    }
                } else {
                    sep = new Domains.Str(",");
                }
                double len;
                Domains.BValue tmp2 = Utils.lookup(selfObj, Utils.Fields.length, store);
                if (tmp2 instanceof Domains.Num) {
                    len = ((Domains.Num) tmp2).n;
                } else {
                    throw new RuntimeException("not implemented: Array.join on array of non-numeric length");
                }
                if (len == 0) {
                    return new Domains.Str("");
                } else {
                    Domains.BValue tmp3 = Utils.lookup(selfObj, new Domains.Str("0"), store);
                    Domains.Str begin;
                    if (tmp3.equals(Domains.Null) || tmp3.equals(Domains.Undef)) {
                        begin = new Domains.Str("");
                    } else {
                        begin = InitUtils.ToString(tmp3, store);
                    }
                    return List.range(1, (int)len).foldLeft((acc, cur) -> {
                        Domains.BValue tmp4 = Utils.lookup(selfObj, new Domains.Str(cur.toString()), store);
                        if (tmp4.equals(Domains.Null) || tmp4.equals(Domains.Undef)) {
                            return acc.strConcat(sep.strConcat(new Domains.Str("")));
                        } else {
                            return acc.strConcat(sep.strConcat(InitUtils.ToString(tmp4, store)));
                        }
                    }, begin);
                }
            },
            FHashMap.build(Utils.Fields.length, new Domains.Num(1.0))
    );

    public static Domains.Object Array_prototype_lastIndexOf_Obj = InitUtils.approx_num;

    public static Domains.Object Array_prototype_map_Obj = InitUtils.unimplemented;

    public static Domains.Object Array_prototype_pop_Obj = InitUtils.makeNativeValueStore(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object selfObj = store.getObj(selfAddr);
                Domains.BValue len = Utils.lookup(selfObj, Utils.Fields.length, store);
                if (len instanceof Domains.Num) {
                    double n = ((Domains.Num) len).n;
                    if (n == 0) {
                        return P.p(Domains.Undef, store.putObj(selfAddr, selfObj.update(Utils.Fields.length, new Domains.Num(0.0))));
                    } else {
                        Domains.Str last = new Domains.Str(String.valueOf((int)n - 1));
                        Domains.BValue res = Utils.lookup(selfObj, last, store);
                        Domains.Object o = selfObj.delete(last)._1();
                        return P.p(res, store.putObj(selfAddr, o.update(Utils.Fields.length, new Domains.Num(n - 1))));
                    }
                } else {
                    throw new RuntimeException("not implemented: non-numeric array length");
                }
            },
            FHashMap.build(Utils.Fields.length, new Domains.Num(0.0))
    );

    public static Domains.Object Array_prototype_push_Obj = InitUtils.makeNativeValueStore(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object selfObj = store.getObj(selfAddr);
                Domains.BValue len = Utils.lookup(selfObj, Utils.Fields.length, store);
                if (len instanceof Domains.Num) {
                    double n = ((Domains.Num) len).n;
                    Domains.Object argObj = store.getObj(argArrayAddr);
                    Option<Domains.BValue> tmp = argObj.apply(Utils.Fields.length);
                    double arglen;
                    if (tmp.isSome() && tmp.some() instanceof Domains.Num) {
                        arglen = ((Domains.Num) tmp.some()).n;
                    } else {
                        throw new RuntimeException("implementation error: args without length");
                    }
                    Domains.Store store1 = List.range(0, (int)arglen).foldLeft((acc, cur) -> {
                        return acc.putObj(selfAddr, acc.getObj(selfAddr).update(new Domains.Str(String.valueOf((int)n + cur)), argObj.apply(new Domains.Str(cur.toString())).some()));
                    }, store);
                    Domains.Store store2 = store1.putObj(selfAddr, store1.getObj(selfAddr).update(Utils.Fields.length, new Domains.Num(arglen + n)));
                    return P.p(new Domains.Num(arglen + n), store2);
                } else {
                    throw new RuntimeException("not implemented: non-numeric array length");
                }
            },
            FHashMap.build(Utils.Fields.length, new Domains.Num(1.0))
    );

    public static Domains.Object Array_prototype_reduce_Obj = InitUtils.unimplemented;

    public static Domains.Object Array_prototype_reduceRight_Obj = InitUtils.unimplemented;

    public static Domains.Object Array_prototype_reverse_Obj = InitUtils.makeNativeValueStore(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object selfObj = store.getObj(selfAddr);
                Domains.BValue len = Utils.lookup(selfObj, Utils.Fields.length, store);
                if (len instanceof Domains.Num) {
                    double n = ((Domains.Num) len).n;
                    List<Domains.BValue> lst = List.range(0, (int)n).map(i -> Utils.lookup(selfObj, new Domains.Str(i.toString()), store));
                    List<Domains.BValue> lst1 = lst.reverse();
                    Domains.Store store1 = List.range(0, (int)n).zip(lst1).foldLeft((acc, cur) -> {
                        return acc.putObj(selfAddr, acc.getObj(selfAddr).update(new Domains.Str(cur._1().toString()), cur._2()));
                    }, store);
                    return P.p(selfAddr, store1);
                } else {
                    throw new RuntimeException("not implemented: non-numeric array length");
                }
            },
            FHashMap.build(Utils.Fields.length, new Domains.Num(0.0))
    );

    public static Domains.Object Array_prototype_shift_Obj = InitUtils.makeNativeValueStore(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object selfObj = store.getObj(selfAddr);
                Domains.BValue len = Utils.lookup(selfObj, Utils.Fields.length, store);
                if (len instanceof Domains.Num) {
                    double n = ((Domains.Num) len).n;
                    if (n == 0) {
                        return P.p(Domains.Undef, store);
                    } else {
                        Domains.BValue first = Utils.lookup(selfObj, new Domains.Str("0"), store);
                        Domains.Str last = new Domains.Str(String.valueOf((int)n - 1));
                        List<Domains.BValue> newList = List.range(1, (int)n).map(i -> Utils.lookup(selfObj, new Domains.Str(i.toString()), store));
                        Domains.Store store1 = List.range(0, (int)n - 1).zip(newList).foldLeft((acc, cur) -> {
                            return acc.putObj(selfAddr, acc.getObj(selfAddr).update(new Domains.Str(cur._1().toString()), cur._2()));
                        }, store);
                        Domains.Object o = store1.getObj(selfAddr).delete(last)._1();
                        Domains.Store store2 = store1.putObj(selfAddr, o.update(Utils.Fields.length, new Domains.Num(n - 1)));
                        return P.p(first, store2);
                    }
                } else {
                    throw new RuntimeException("not implemented: non-numeric array length");
                }
            },
            FHashMap.build(Utils.Fields.length, new Domains.Num(0.0))
    );
    public static Domains.Object Array_prototype_slice_Obj = InitUtils.unimplemented;
    public static Domains.Object Array_prototype_some_Obj = InitUtils.unimplemented;
    public static Domains.Object Array_prototype_sort_Obj = InitUtils.unimplemented;
    public static Domains.Object Array_prototype_splice_Obj = InitUtils.unimplemented;
    public static Domains.Object Array_prototype_toLocaleString_Obj = InitUtils.unimplemented;

    public static Domains.Object Array_prototype_toString_Obj = InitUtils.makeNativeValue(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object selfObj = store.getObj(selfAddr);
                JSClass klass = selfObj.getJSClass();
                if (klass.equals(JSClass.CArray)) {
                    Domains.Str sep = new Domains.Str(",");
                    Domains.BValue len = Utils.lookup(selfObj, Utils.Fields.length, store);
                    if (len instanceof Domains.Num) {
                        double n = ((Domains.Num) len).n;
                        if (n == 0) {
                            return new Domains.Str("");
                        } else {
                            Domains.BValue tmp = Utils.lookup(selfObj, new Domains.Str("0"), store);
                            Domains.Str start;
                            if (tmp.equals(Domains.Null) || tmp.equals(Domains.Undef)) {
                                start = new Domains.Str("");
                            } else {
                                start = InitUtils.ToString(tmp, store);
                            }
                            return List.range(1, (int)n).foldLeft((acc, cur) -> {
                                Domains.BValue _tmp = Utils.lookup(selfObj, new Domains.Str(cur.toString()), store);
                                if (_tmp.equals(Domains.Null) || _tmp.equals(Domains.Undef)) {
                                    return acc.strConcat(sep.strConcat(new Domains.Str("")));
                                } else {
                                    return acc.strConcat(sep.strConcat(InitUtils.ToString(_tmp, store)));
                                }
                            }, start);
                        }
                    } else {
                        throw new RuntimeException("not implemented");
                    }
                } else {
                    return Utils.Errors.typeError;
                }
            },
            FHashMap.build(Utils.Fields.length, new Domains.Num(0.0))
    );

    public static Domains.Object Array_prototype_unshift_Obj = InitUtils.unimplemented;
}
