package concrete.init;

import concrete.Domains;
import concrete.Utils;
import fj.Ord;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.Option;
import fj.data.TreeMap;
import fj.function.Doubles;
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
                Option<Domains.BValue> _arglen = args.apply(new Domains.Str("length"));
                Double arglen;
                if (_arglen.isSome() && _arglen.some() instanceof Domains.Num) {
                    arglen = ((Domains.Num) _arglen.some()).n;
                } else {
                    throw new RuntimeException("implementation error: arguments without numeric length");
                }
                Domains.Store store1 = tmp1._1();
                Domains.Address arrayAddr = tmp1._2();
                TreeMap<Domains.Str, Object> internal = store1.getObj(argArrayAddr).intern;
                if (arglen == 0 || arglen >= 2) {
                    List<Integer> range = List.range(0, arglen.intValue());
                    TreeMap<Domains.Str, Domains.BValue> initial = TreeMap.treeMap(Ord.hashEqualsOrd(), P.p(new Domains.Str("length"), new Domains.Num(arglen)));
                    TreeMap<Domains.Str, Domains.BValue> external =
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
                        Double n = ((Domains.Num) len).n;
                        if (n.intValue() != n || n < 0) {
                            return P.p(Utils.Errors.rangeError, store);
                        } else {
                            Domains.Object newObj = InitUtils.createObj(TreeMap.treeMap(Ord.hashEqualsOrd(), P.p(new Domains.Str("length"), new Domains.Num(n))), internal);
                            Domains.Store newStore = store1.putObj(arrayAddr, newObj);
                            return P.p(arrayAddr, newStore);
                        }
                    } else {
                        Domains.Object newObj = InitUtils.createObj(TreeMap.treeMap(Ord.hashEqualsOrd(), P.p(new Domains.Str("length"), new Domains.Num(1.0)), P.p(new Domains.Str("0"), len)), internal);
                        Domains.Store newStore = store1.putObj(arrayAddr, newObj);
                        return P.p(arrayAddr, newStore);
                    }
                }
            },
            TreeMap.treeMap(Ord.hashEqualsOrd(),
                    P.p(new Domains.Str("prototype"), Init.Array_prototype_Addr),
                    P.p(new Domains.Str("isArray"), Init.Array_isArray_Addr),
                    P.p(new Domains.Str("length"), new Domains.Num(1.0))),
            JSClass.CArray_Obj
    );

    public static Domains.Object Array_prototype_Obj = InitUtils.createObj(
            TreeMap.treeMap(Ord.hashEqualsOrd(),
                    P.p(new Domains.Str("constructor"), Init.Array_Addr),
                    P.p(new Domains.Str("concat"), Init.Array_prototype_concat_Addr),
                    P.p(new Domains.Str("every"), Init.Array_prototype_every_Addr),
                    P.p(new Domains.Str("filter"), Init.Array_prototype_filter_Addr),
                    P.p(new Domains.Str("forEach"), Init.Array_prototype_forEach_Addr),
                    P.p(new Domains.Str("indexOf"), Init.Array_prototype_indexOf_Addr),
                    P.p(new Domains.Str("join"), Init.Array_prototype_join_Addr),
                    P.p(new Domains.Str("lastIndexOf"), Init.Array_prototype_lastIndexOf_Addr),
                    P.p(new Domains.Str("map"), Init.Array_prototype_map_Addr),
                    P.p(new Domains.Str("pop"), Init.Array_prototype_pop_Addr),
                    P.p(new Domains.Str("push"), Init.Array_prototype_push_Addr),
                    P.p(new Domains.Str("reduce"), Init.Array_prototype_reduce_Addr),
                    P.p(new Domains.Str("reduceRight"), Init.Array_prototype_reduceRight_Addr),
                    P.p(new Domains.Str("reverse"), Init.Array_prototype_reverse_Addr),
                    P.p(new Domains.Str("shift"), Init.Array_prototype_shift_Addr),
                    P.p(new Domains.Str("slice"), Init.Array_prototype_slice_Addr),
                    P.p(new Domains.Str("some"), Init.Array_prototype_some_Addr),
                    P.p(new Domains.Str("sort"), Init.Array_prototype_sort_Addr),
                    P.p(new Domains.Str("splice"), Init.Array_prototype_splice_Addr),
                    P.p(new Domains.Str("toLocaleString"), Init.Array_prototype_toLocaleString_Addr),
                    P.p(new Domains.Str("toString"), Init.Array_prototype_toString_Addr),
                    P.p(new Domains.Str("unshift"), Init.Array_prototype_unshift_Addr)
            ),
            TreeMap.treeMap(Ord.hashEqualsOrd(),
                    P.p(Utils.Fields.classname, JSClass.CArray_prototype_Obj))
    );

    public static Domains.Object Array_isArray_Obj = InitUtils.makeNativeValue(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object args = store.getObj(argArrayAddr);
                Domains.BValue input = args.apply(new Domains.Str("0")).orSome(Domains.Undef);
                if (input instanceof Domains.Address) {
                    return new Domains.Bool(store.getObj((Domains.Address)input).getJSClass().equals(JSClass.CArray));
                } else {
                    return new Domains.Bool(false);
                }
            },
            TreeMap.treeMap(Ord.hashEqualsOrd(), P.p(new Domains.Str("length"), new Domains.Num(1.0)))
    );

    public static Domains.Object Array_prototype_concat_Obj = InitUtils.makeNativeValueStore(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object selfObj = store.getObj(selfAddr);
                Domains.Object argObj = store.getObj(argArrayAddr);

                Domains.Address newArrayAddr = Domains.Address.generate();
                Domains.BValue tmp = Utils.lookup(selfObj, new Domains.Str("length"), store);
                Double selflen;
                if (tmp instanceof Domains.Num) {
                    selflen = ((Domains.Num) tmp).n;
                } else {
                    throw new RuntimeException("not implemented: Array.concat on array of non-numeric length");
                }
                Double arglen;
                tmp = Utils.lookup(argObj, new Domains.Str("length"), store);
                if (tmp instanceof Domains.Num) {
                    arglen = ((Domains.Num) tmp).n;
                } else {
                    throw new RuntimeException("not implemented: Array.concat on array of non-numeric length");
                }
                List<Domains.BValue> selfElems = List.range(0, selflen.intValue()).foldLeft((acc, cur) -> {
                    return acc.snoc(Utils.lookup(selfObj, new Domains.Str(cur.toString()), store));
                }, List.list());
                List<Domains.BValue> newElems = List.range(0, arglen.intValue()).foldLeft((acc, cur) -> {
                    Domains.BValue _tmp = Utils.lookup(argObj, new Domains.Str(cur.toString()), store);
                    if (_tmp instanceof Domains.Address) {
                        Domains.Object elemObj = store.getObj((Domains.Address)_tmp);
                        if (elemObj.getJSClass().equals(JSClass.CArray)) {
                            Domains.BValue len = Utils.lookup(elemObj, new Domains.Str("length"), store);
                            Double elemlen;
                            if (len instanceof Domains.Num) {
                                elemlen = ((Domains.Num)len).n;
                            } else {
                                throw new RuntimeException("not implemented: array with non-numeric length");
                            }
                            return List.range(0, elemlen.intValue()).foldLeft((acc1, cur1) -> {
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
                TreeMap<Domains.Str, Domains.BValue> external = TreeMap.treeMap(Ord.hashEqualsOrd(), lst).set(new Domains.Str("length"), new Domains.Num(1.0 * newElems.length()));
                TreeMap<Domains.Str, Object> internal = TreeMap.treeMap(Ord.hashEqualsOrd(), P.p(Utils.Fields.proto, Init.Array_prototype_Addr), P.p(Utils.Fields.classname, JSClass.CArray));
                Domains.Object newArrayObj = InitUtils.createObj(external, internal);
                Domains.Store store1 = store.putObj(newArrayAddr, newArrayObj);
                return P.p(newArrayAddr, store1);
            },
            TreeMap.treeMap(Ord.hashEqualsOrd(), P.p(new Domains.Str("length"), new Domains.Num(1.0)))
    );

    public static Domains.Object Array_prototype_every_Obj = InitUtils.unimplemented;

    public static Domains.Object Array_prototype_filter_Obj = InitUtils.unimplemented;

    public static Domains.Object Array_prototype_forEach_Obj = InitUtils.unimplemented;

    public static Domains.Object Array_prototype_indexOf_Obj = InitUtils.unimplemented;
}
