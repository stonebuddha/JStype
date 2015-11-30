package concrete.init;

import concrete.Utils;
import concrete.Domains;
import fj.Ord;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.Option;
import fj.data.TreeMap;
import ir.JSClass;

/**
 * Created by Hwhitetooth on 15/11/11.
 */
public class InitFunction {
    public static Domains.Object Function_Obj = InitUtils.createFunctionObject(
            new concrete.Domains.Native((selfAddr, argArrayAddr, x, env, store, pad, ks) -> {
                throw new RuntimeException("Won't implement: Function() is eval()");
            }), TreeMap.treeMap(Utils.StrOrd,
                    P.p(new concrete.Domains.Str("prototype"), Init.Function_prototype_Addr),
                    P.p(new concrete.Domains.Str("length"), new concrete.Domains.Num(1.0))),
            JSClass.CFunction_Obj);

    public static Domains.Object Function_prototype_Obj = new Domains.Object(
            TreeMap.treeMap(Utils.StrOrd,
                    P.p(Utils.Fields.constructor, Init.Function_Addr),
                    P.p(new Domains.Str("apply"), Init.Function_prototype_apply_Addr),
                    P.p(new Domains.Str("call"), Init.Function_prototype_call_Addr),
                    P.p(new Domains.Str("toString"), Init.Function_prototype_toString_Addr),
                    P.p(Utils.Fields.length, new Domains.Num(0.0))),
            TreeMap.treeMap(Utils.StrOrd,
                    P.p(Utils.Fields.proto, Init.Object_prototype_Addr),
                    P.p(Utils.Fields.classname, JSClass.CFunction_prototype_Obj),
                    P.p(Utils.Fields.code, new Domains.Native((selfAddr, argArrayAddr, x, env, store, pad, ks) -> {
                        return InitUtils.makeState(Domains.Undef, x, env, store, pad, ks);
                    }))));

    public static Domains.Object Function_prototype_apply_Obj = InitUtils.createFunctionObject(
            new Domains.Native((selfAddr, argArrayAddr, x, env, store, pad, ks) -> {
                Domains.Object argsObj = store.getObj(argArrayAddr);
                Option<TreeMap<Domains.Str, Domains.BValue>> external;
                Option<Domains.BValue> tmp = argsObj.apply(new Domains.Str("1"));
                if (tmp.isNone() || tmp.some().equals(Domains.Undef)|| tmp.some().equals(Domains.Null)) {
                    external = Option.fromNull(TreeMap.treeMap(Utils.StrOrd, P.p(Utils.Fields.length, new Domains.Num(0.0))));
                } else if (tmp.some() instanceof Domains.Address) {
                    Domains.Address a = (Domains.Address)tmp.some();
                    Domains.Object passedArgsObj = store.getObj(a);
                    if (passedArgsObj.getJSClass().equals(JSClass.CArray) || passedArgsObj.getJSClass().equals(JSClass.CArguments)) {
                        double arglen;
                        Option<Domains.BValue> tmp2 = passedArgsObj.apply(Utils.Fields.length);
                        if (tmp2.isSome() && tmp2.some() instanceof Domains.Num) {
                            arglen = ((Domains.Num)tmp2.some()).n;
                        } else {
                            throw new RuntimeException("implementation error: inconceivable: array or arguments object with non-numeric length");
                        }
                        external = Option.fromNull(List.range(0, (int)arglen).foldLeft(
                                (m, i) -> m.set(new Domains.Str(i.toString()), passedArgsObj.apply(new Domains.Str(i.toString())).some()),
                                TreeMap.treeMap(Utils.StrOrd, P.p(Utils.Fields.length, new Domains.Num(arglen)))
                        ));
                    } else {
                        external = Option.none();
                    }
                } else {
                    external = Option.none();
                }
                if (external.isNone()) {
                    return InitUtils.makeState(Utils.Errors.typeError, x, env, store, pad, ks);
                } else {
                    TreeMap<Domains.Str, Domains.BValue> extm = external.some();
                    Domains.Address newArgsAddr = Domains.Address.generate();
                    Domains.Object newObj = InitUtils.createObj(extm,
                            TreeMap.treeMap(Utils.StrOrd,
                                    P.p(Utils.Fields.proto, Init.Object_prototype_Addr),
                                    P.p(Utils.Fields.classname, JSClass.CArguments)));
                    Domains.Value newThisAddress;
                    Domains.Store store1;
                    Option<Domains.BValue> tmp2 = argsObj.apply(new Domains.Str("0"));
                    if (tmp2.isNone() || tmp2.some().equals(Domains.Undef) || tmp2.some().equals(Domains.Null)) {
                        newThisAddress = Init.window_Addr;
                        store1 = store;
                    } else {
                        Domains.BValue v = tmp2.some();
                        P2<Domains.Value, Domains.Store> tmp3 = InitUtils.ToObject(v, store);
                        newThisAddress = tmp3._1();
                        store1 = tmp3._2();
                    }
                    assert(newThisAddress instanceof Domains.Address);
                    Domains.Store store2 = store1.putObj(newArgsAddr, newObj);
                    return Utils.applyClo(selfAddr, (Domains.Address)newThisAddress, newArgsAddr, x, env, store2, pad, ks);
                }
            }), TreeMap.treeMap(Utils.StrOrd, P.p(Utils.Fields.length, new Domains.Num(2.0)))
    );

    public static Domains.Object Function_prototype_call_Obj = InitUtils.createFunctionObject(
            new Domains.Native((selfAddr, argArrayAddr, x, env, store, pad, ks) -> {
                Domains.Object argsObj = store.getObj(argArrayAddr);
                double arglen;
                Option<Domains.BValue> tmp = argsObj.apply(Utils.Fields.length);
                if (tmp.isSome() && tmp.some() instanceof Domains.Num) {
                    arglen = ((Domains.Num)tmp.some()).n;
                } else {
                    throw new RuntimeException("implementation error: inconceivable: arguments object with non-numeric length");
                }
                Domains.Value newThisAddress;
                Domains.Store store1;
                Option<Domains.BValue> tmp2 = argsObj.apply(new Domains.Str("0"));
                if (tmp2.isNone() || tmp2.some().equals(Domains.Undef) || tmp2.some().equals(Domains.Null)) {
                    newThisAddress = Init.window_Addr;
                    store1 = store;
                } else {
                    Domains.BValue v = tmp2.some();
                    P2<Domains.Value, Domains.Store> tmp3 = InitUtils.ToObject(v, store);
                    newThisAddress = tmp3._1();
                    store1 = tmp3._2();
                }
                assert(newThisAddress instanceof Domains.Address);

                TreeMap<Domains.Str, Domains.BValue> external = List.range(1, (int)arglen).foldLeft(
                        (m, i) -> {
                            int j = i - 1;
                            return m.set(new Domains.Str(String.valueOf(j)), argsObj.apply(new Domains.Str(i.toString())).some());
                        },
                        TreeMap.treeMap(Utils.StrOrd, P.p(Utils.Fields.length, new Domains.Num(arglen - 1)))
                );
                Domains.Address newArgsAddr = Domains.Address.generate();
                Domains.Object newObj = InitUtils.createObj(external,
                        TreeMap.treeMap(Utils.StrOrd,
                                P.p(Utils.Fields.proto, Init.Object_prototype_Addr),
                                P.p(Utils.Fields.classname, JSClass.CArguments)));
                Domains.Store store2 = store1.putObj(newArgsAddr, newObj);
                return Utils.applyClo(selfAddr, (Domains.Address)newThisAddress, newArgsAddr, x, env, store2, pad, ks);
            }), TreeMap.treeMap(Utils.StrOrd, P.p(Utils.Fields.length, new Domains.Num(1.0)))
    );

    public static Domains.Object Function_prototype_toString_Obj = InitUtils.unimplemented;
}