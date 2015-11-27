package concrete.init;

import concrete.Utils;
import concrete.Domains;
import fj.Ord;
import fj.P;
import fj.data.List;
import fj.data.Option;
import fj.data.TreeMap;
import ir.JSClass;

import javax.rmi.CORBA.Util;

/**
 * Created by Hwhitetooth on 15/11/11.
 */
public class InitNumber {
    public static Domains.Object Number_Obj = InitUtils.makeNativeValueStore(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object argsObj = store.getObj(argArrayAddr);
                Double arglen;
                Option<Domains.BValue> tmp = argsObj.apply(new Domains.Str("length"));
                if (tmp.isSome() && tmp.some() instanceof Domains.Num) {
                    arglen = ((Domains.Num) tmp.some()).n;
                } else {
                    //sys.error("inconceivable: args without length")
                    return null;
                }
                Boolean calledAsConstr = argsObj.calledAsCtor();
                Domains.Num pvalue;
                if (arglen == 0) {
                    pvalue = new Domains.Num(0.0);
                } else {
                    pvalue = InitUtils.ToNumber(argsObj.apply(new Domains.Str("0")).some(), store);
                }
                if (calledAsConstr) {
                    Domains.Address newAddr = Domains.Address.generate();
                    Domains.Object newObj = InitUtils.createObj(TreeMap.empty(Ord.hashEqualsOrd()),
                            TreeMap.treeMap(Ord.hashEqualsOrd(),
                                    P.p(Utils.Fields.proto, Init.Number_prototype_Addr),
                                    P.p(Utils.Fields.classname, JSClass.CNumber),
                                    P.p(Utils.Fields.value, pvalue)));
                    Domains.Store newStore = store.putObj(newAddr, newObj);
                    return P.p(newAddr, newStore);
                } else {
                    return P.p(pvalue, store);
                }
            }, TreeMap.treeMap(Ord.hashEqualsOrd(),
                    P.p(new Domains.Str("prototype"), Init.Number_prototype_Addr),
                    P.p(new Domains.Str("length"), new Domains.Num(1.0)),
                    P.p(new Domains.Str("MAX_VALUE"), new Domains.Num(Double.MAX_VALUE)),
                    P.p(new Domains.Str("MIN_VALUE"), new Domains.Num(Double.MIN_VALUE)),
                    P.p(new Domains.Str("NaN"), new Domains.Num(Double.NaN)),
                    P.p(new Domains.Str("NEGATIVE_INFINITY"), new Domains.Num(Double.POSITIVE_INFINITY)),
                    P.p(new Domains.Str("POSITIVE_INFINITY"), new Domains.Num(Double.NEGATIVE_INFINITY))),
            JSClass.CNumber_Obj
    );

    public static Domains.Object Number_prototype_Obj = InitUtils.createObj(TreeMap.treeMap(Ord.hashEqualsOrd(),
            P.p(new Domains.Str("constructor"), Init.Number_Addr),
            P.p(new Domains.Str("toString"), Init.Number_prototype_toString_Addr),
            P.p(new Domains.Str("toLocaleString"), Init.Number_prototype_toLocaleString_Addr),
            P.p(new Domains.Str("valueOf"), Init.Number_prototype_valueOf_Addr),
            P.p(new Domains.Str("toFixed"), Init.Number_prototype_toFixed_Addr),
            P.p(new Domains.Str("toExponential"), Init.Number_prototype_toExponential_Addr),
            P.p(new Domains.Str("toPrecision"), Init.Number_prototype_toPrecision_Addr)),
        TreeMap.treeMap(Ord.hashEqualsOrd(), P.p(Utils.Fields.classname, JSClass.CNumber_prototype_Obj))
    );

    public static Domains.Object Number_prototype_toString_Obj = InitUtils.makeNativeValue(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object self = store.getObj(selfAddr);
                if (self.getJSClass().equals(JSClass.CNumber)) {
                    Domains.Object args = store.getObj(argArrayAddr);
                    int arglen;
                    Option<Domains.BValue> tmp = args.apply(new Domains.Str("length"));
                    if (tmp.isSome() && tmp.some() instanceof Domains.Num) {
                        arglen = ((Domains.Num)tmp.some()).n.intValue();
                    } else {
                        //sys.error("inconceivable");
                        return null;
                    }
                    int radix;
                    if (arglen == 0) {
                        radix = 10;
                    } else {
                        Domains.BValue tmp2 = args.apply(new Domains.Str("0")).some();
                        if (tmp2 instanceof Domains.Num && ((Domains.Num)tmp2).n == 10) {
                            radix = 10;
                        } else if (tmp2.equals(Domains.Undef)) {
                            radix = 10;
                        } else if (tmp2 instanceof Domains.Num && ((Domains.Num)tmp2).n >= 2 && ((Domains.Num)tmp2).n <= 36) {
                            //sys.error("!! Not Implemented");
                            return null;
                        } else {
                            radix = -1;
                        }
                    }
                    if (radix == -1) {
                        return Utils.Errors.rangeError;
                    } else {
                        if (self.getValue().isSome() && self.getValue().some() instanceof Domains.Num) {
                            return self.getValue().some().toStr();
                        } else {
                            //sys.error("inconceivable: Number without a number value")
                            return null;
                        }
                    }
                } else {
                    return Utils.Errors.typeError;
                }
            }, TreeMap.treeMap(Ord.hashEqualsOrd(), P.p(new Domains.Str("length"), new Domains.Num(1.0)))
    );

    public static Domains.Object Number_prototype_toLocaleString_Obj = InitUtils.unimplemented;

    public static Domains.Object Number_prototype_valueOf_Obj = InitUtils.makeNativeValue(
            (selfAddr, argArrayAddr, store) -> {
                Domains.Object self = store.getObj(selfAddr);
                if (self.getJSClass().equals(JSClass.CNumber)) {
                    return self.getValue().some();
                } else {
                    return Utils.Errors.typeError;
                }
            }, TreeMap.treeMap(Ord.hashEqualsOrd(), P.p(new Domains.Str("length"), new Domains.Num(0.0)))
    );

    public static Domains.Object Number_prototype_toFixed_Obj = InitUtils.unimplemented;

    public static Domains.Object Number_prototype_toExponential_Obj = InitUtils.unimplemented;

    public static Domains.Object Number_prototype_toPrecision_Obj = InitUtils.unimplemented;
}
