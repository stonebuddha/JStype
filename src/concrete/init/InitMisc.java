package concrete.init;

import concrete.Domains;
import concrete.Utils;
import fj.Ord;
import fj.P;
import fj.data.Option;
import fj.data.TreeMap;
import ir.JSClass;

/**
 * Created by Hwhitetooth on 15/11/12.
 */
public class InitMisc {
    public static Domains.Object Boolean_Obj = InitUtils.makeNativeValueStore(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object argsObj = store.getObj(argArrayAddr);
                Boolean calledAsConstr = argsObj.calledAsCtor();
                Domains.BValue boolValue;
                Option<Domains.BValue> tmp = argsObj.apply(new Domains.Str("0"));
                if (tmp.isSome()) {
                    Domains.BValue v = tmp.some();
                    boolValue = v.toBool();
                } else {
                    boolValue = Domains.Bool.False;
                }
                if (calledAsConstr) {
                    Domains.Address newAddr = new Domains.Address();
                    Domains.Object newObj = InitUtils.createObj(TreeMap.empty(Ord.hashEqualsOrd()),
                            TreeMap.treeMap(Ord.hashEqualsOrd(),
                                    P.p(Utils.Fields.proto, Init.Boolean_prototype_Addr),
                                    P.p(Utils.Fields.classname, JSClass.CBoolean),
                                    P.p(Utils.Fields.value, boolValue)));
                    Domains.Store newStore = store.putObj(newAddr, newObj);
                    return P.p(newAddr, newStore);
                } else {
                    return P.p(boolValue, store);
                }
            }, TreeMap.treeMap(Ord.hashEqualsOrd(),
                    P.p(new Domains.Str("prototype"), Init.Boolean_prototype_Addr),
                    P.p(new Domains.Str("length"), new Domains.Num(1.0))
            )
    );

    public static Domains.Object Boolean_prototype_Obj = InitUtils.createObj(
            TreeMap.treeMap(Ord.hashEqualsOrd(),
                    P.p(new Domains.Str("toString"), Init.Boolean_prototype_toString_Addr),
                    P.p(new Domains.Str("valueOd"), Init.Boolean_prototype_valueOf_Addr))
    );

    public static Domains.Object Boolean_prototype_toString_Obj = InitUtils.makeNativeValue(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object selfObj = store.getObj(selfAddr);
                if (selfObj.getJSClass().equals(JSClass.CBoolean)) {
                    Option<Domains.BValue> tmp = selfObj.getValue();
                    if (tmp.isSome() && tmp.some().equals(Domains.Bool.True)) {
                        return new Domains.Str("True");
                    } else if (tmp.isSome() && tmp.some().equals(Domains.Bool.False)) {
                        return new Domains.Str("False");
                    } else {
                        //sys.error("We should not have a non-boolean as a Boolean's internal value")
                        return null;
                    }
                } else {
                    return Utils.Errors.typeError;
                }
            }, TreeMap.treeMap(Ord.hashEqualsOrd(), P.p(new Domains.Str("length"), new Domains.Num(0.0)))
    );

    public static Domains.Object Boolean_prototype_valueOf_Obj = InitUtils.makeNativeValue(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object selfObj = store.getObj(selfAddr);
                if (selfObj.getJSClass().equals(JSClass.CBoolean)) {
                    Option<Domains.BValue> tmp = selfObj.getValue();
                    if (tmp.isSome() && tmp.some() instanceof Domains.Bool) {
                        return tmp.some();
                    } else {
                        //sys.error("We should not have a non-boolean as a Boolean's internal value")
                        return null;
                    }
                } else {
                    return Utils.Errors.typeError;
                }
            }, TreeMap.treeMap(Ord.hashEqualsOrd(), P.p(new Domains.Str("length"), new Domains.Num(0.0)))
    );

    //TODO
}