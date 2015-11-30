package concrete.init;

import concrete.Domains;
import concrete.Utils;
import fj.*;
import fj.data.List;
import fj.data.TreeMap;
import ir.IRPVar;
import ir.IRScratch;
import ir.IRVar;
import ir.JSClass;

/**
 * Created by wayne on 15/11/9.
 */
public class InitUtils {
    public static Domains.Object unimplemented = createFunctionObject(new Domains.Native((selfAddr, argArrayAddr, x, env, store, pad, ks) -> {
        throw new RuntimeException("not implemented");
    }), TreeMap.treeMap(Utils.StrOrd, P.p(Utils.Fields.length, new Domains.Num(0.0))));

    public static Domains.Object approx_str = makeNativeValue((selfAddr, argArrayAddr, store) -> {
        System.out.println("warning: use of approximated concrete function");
        return new Domains.Str("UNIMPLEMENTED");
    }, TreeMap.treeMap(Utils.StrOrd, P.p(Utils.Fields.length, new Domains.Num(1.0))));

    public static Domains.Object approx_num = makeNativeValue((selfAddr, argArrayAddr, store) -> {
        System.out.println("warning: use of approximated concrete function");
        return new Domains.Num(0.0);
    }, TreeMap.treeMap(Utils.StrOrd, P.p(Utils.Fields.length, new Domains.Num(1.0))));

    public static Domains.Object approx_array = makeNativeValueStore((selfAddr, argArrayAddr, store) -> {
        System.out.println("warning: use of approximated concrete function");
        P2<Domains.Store, Domains.Address> tmp = concrete.Utils.allocObj(Init.Array_Addr, store);
        Domains.Store store1 = tmp._1();
        Domains.Address arrayAddr = tmp._2();
        TreeMap<Domains.Str, java.lang.Object> internal = store1.getObj(arrayAddr).intern;
        Domains.Object newObj = createObj(TreeMap.treeMap(Utils.StrOrd,
                P.p(Utils.Fields.length, new Domains.Num(1.0)),
                P.p(new Domains.Str("0"), new Domains.Str("UNIMPLEMENTED ARRAY"))),
                internal);
        Domains.Store newStore = store1.putObj(arrayAddr, newObj);
        return P.p(arrayAddr, newStore);
    }, TreeMap.treeMap(Utils.StrOrd, P.p(Utils.Fields.length, new Domains.Num(1.0))));

    public static String JSClassToString(JSClass j) {
        if (j.equals(JSClass.CObject)) {
            return new String("Object");
        } else if (j.equals(JSClass.CFunction)) {
            return new String("Function");
        } else if (j.equals(JSClass.CArray)) {
            return new String("Array");
        } else if (j.equals(JSClass.CString)) {
            return new String("String");
        } else if (j.equals(JSClass.CBoolean)) {
            return new String("Boolean");
        } else if (j.equals(JSClass.CNumber)) {
            return new String("Number");
        } else if (j.equals(JSClass.CDate)) {
            return new String("Date");
        } else if (j.equals(JSClass.CError)) {
            return new String("Error");
        } else if (j.equals(JSClass.CRegexp)) {
            return new String("Regexp");
        } else if (j.equals(JSClass.CArguments)) {
            return new String("Arguments");
        } else if (j.equals(JSClass.CObject_Obj)) {
            return new String("Function");
        } else if (j.equals(JSClass.CObject_prototype_Obj)) {
            return new String("Object");
        } else if (j.equals(JSClass.CArray_prototype_Obj)) {
            return new String("Array");
        } else if (j.equals(JSClass.CArray_Obj)) {
            return new String("Function");
        } else if (j.equals(JSClass.CFunction_Obj)) {
            return new String("Function");
        } else if (j.equals(JSClass.CFunction_prototype_Obj)) {
            return new String("Function");
        } else if (j.equals(JSClass.CMath_Obj)) {
            return new String("Math");
        } else if (j.equals(JSClass.CNumber_Obj)) {
            return new String("Function");
        } else if (j.equals(JSClass.CNumber_prototype_Obj)) {
            return new String("Number");
        } else if (j.equals(JSClass.CString_Obj)) {
            return new String("Function");
        } else if (j.equals(JSClass.CString_prototype_Obj)) {
            return new String("String");
        } else {
            return null;
        }
    }

    public static Domains.Str Object_toString_helper(Domains.Object o) {
        return new Domains.Str("[object " + JSClassToString(o.getJSClass()) + "]");
    }

    public static Domains.Num ToNumber(Domains.BValue v, Domains.Store store) {
        if (v instanceof Domains.Address) {
            Domains.Address a = (Domains.Address) v;
            Domains.Object o = store.getObj(a);
            Domains.BValue valueOf = concrete.Utils.lookup(o, new Domains.Str("valueOf"), store);
            Domains.BValue value;
            if (valueOf.equals(Init.Number_prototype_valueOf_Addr) || valueOf.equals(Init.String_prototype_valueOf_Addr) || valueOf.equals(Init.Boolean_prototype_valueOf_Addr)) {
                value = o.getValue().some();
            } else {
                throw new RuntimeException("implementation error: Your valueOf is not saned.");
            }
            return ToNumber(value, store);
        } else {
            return v.toNum();
        }
    }

    public static Domains.Str ToString(Domains.BValue v, Domains.Store store) {
        if (v instanceof Domains.Address) {
            Domains.Address a = (Domains.Address)v;
            Domains.Object o = store.getObj(a);
            Domains.BValue toString = concrete.Utils.lookup(o, new Domains.Str("toString"), store);
            Domains.BValue string;
            if (toString.equals(Init.Number_prototype_toString_Addr) || toString.equals(Init.Boolean_prototype_toString_Addr)) {
                string = o.getValue().some().toStr();
            } else if (toString.equals(Init.String_prototype_toString_Addr)) {
                string = o.getValue().some();
            } else if (toString.equals(Init.Object_prototype_toString_Addr)) {
                string = Object_toString_helper(o);
            } else if (toString.equals(Init.Array_prototype_toString_Addr)) {
                string = ArrayToString(a, store);
            } else {
                throw new RuntimeException("implementation error");
            }
            return ToString(string, store);
        } else {
            return v.toStr();
        }
    }

    public static Domains.Str ArrayToString(Domains.Address a, Domains.Store store) {
        Domains.Object arrayObj = store.getObj(a);
        Domains.Str separator = new Domains.Str(",");
        Domains.BValue tmp = concrete.Utils.lookup(arrayObj, Utils.Fields.length, store);
        int len;
        if (tmp instanceof Domains.Num) {
            double n = ((Domains.Num)tmp).n;
            len = (int)n;
        } else {
            throw new RuntimeException("implementation error: Non-numeric array length not handled");
        }
        if (len == 0) {
            return new Domains.Str("");
        } else {
            Domains.Str start;
            Domains.BValue v = concrete.Utils.lookup(arrayObj, new Domains.Str("0"), store);
            if (v.equals(Domains.Null) || v.equals(Domains.Undef)) {
                start = new Domains.Str("");
            } else {
                start = ToString(v, store);
            }
            return List.range(1, len).foldLeft((s, i) -> {
                Domains.BValue tmp2 = concrete.Utils.lookup(arrayObj, new Domains.Str(i.toString()), store);
                if (tmp2.equals(Domains.Null) || tmp2.equals(Domains.Undef)) {
                    return s.strConcat(separator.strConcat(new Domains.Str("")));
                } else {
                    return s.strConcat(separator.strConcat(ToString(tmp2, store)));
                }
            }, start);
        }
    }

    public static P2<Domains.Value, Domains.Store> ToObject(Domains.BValue v, Domains.Store store) {
        if (v instanceof Domains.Address) {
            Domains.Address a = (Domains.Address)v;
            return P.p(a, store);
        } else if (v.equals(Domains.Null) || v.equals(Domains.Undef)) {
            return P.p(concrete.Utils.Errors.typeError, store);
        } else {
            Domains.Address proto;
            JSClass classname;
            if (v instanceof Domains.Str) {
                proto = Init.String_prototype_Addr;
                classname = JSClass.CString;
            } else if (v instanceof Domains.Bool) {
                proto = Init.Boolean_prototype_Addr;
                classname = JSClass.CBoolean;
            } else if (v instanceof Domains.Num) {
                proto = Init.Number_prototype_Addr;
                classname = JSClass.CNumber;
            } else {
                throw new RuntimeException("implementation error: inconceivable");
            }
            Domains.Address newAddr = Domains.Address.generate();
            Domains.Object newObj = createObj(TreeMap.empty(Utils.StrOrd),
                    TreeMap.treeMap(Utils.StrOrd,
                            P.p(concrete.Utils.Fields.proto, proto),
                            P.p(concrete.Utils.Fields.classname, classname),
                            P.p(concrete.Utils.Fields.value, v)));
            Domains.Store newStore = store.putObj(newAddr, newObj);
            return P.p(newAddr, newStore);
        }
    }

    public static Domains.Object createObj(TreeMap<Domains.Str, Domains.BValue> external) {
        return createObj(external, TreeMap.empty(Utils.StrOrd));
    }

    public static Domains.Object createObj(TreeMap<Domains.Str, Domains.BValue> external, TreeMap<Domains.Str, Object> internal) {
        TreeMap<Domains.Str, Object> intern;
        if (!internal.contains(concrete.Utils.Fields.proto)) {
            intern = internal.set(concrete.Utils.Fields.proto, Init.Object_prototype_Addr);
        } else {
            intern = internal;
        }
        TreeMap<Domains.Str, Object> intern1;
        if (!internal.contains(concrete.Utils.Fields.classname)) {
            intern1 = intern.set(concrete.Utils.Fields.classname, JSClass.CObject);
        } else {
            intern1 = intern;
        }
        return new Domains.Object(external, intern1);
    }

    public static Domains.Object createFunctionObject(Domains.Native clo, TreeMap<Domains.Str, Domains.BValue> external) {
        TreeMap<Domains.Str, java.lang.Object> internal = TreeMap.empty(Utils.StrOrd);
        JSClass myclass = JSClass.CFunction;
        assert(external.contains(Utils.Fields.length)) : "Native function with no length.";
        TreeMap<Domains.Str, java.lang.Object> internalFieldMap = internal.union(TreeMap.treeMap(Utils.StrOrd,
                P.p(concrete.Utils.Fields.proto, Init.Function_prototype_Addr),
                P.p(concrete.Utils.Fields.code, clo),
                P.p(concrete.Utils.Fields.classname, myclass)));
        return new Domains.Object(external, internalFieldMap);
    }
    public static Domains.Object createFunctionObject(Domains.Native clo, TreeMap<Domains.Str, Domains.BValue> external, JSClass myclass) {
        TreeMap<Domains.Str, java.lang.Object> internal = TreeMap.empty(Utils.StrOrd);
        assert(external.contains(Utils.Fields.length)) : "Native function with no length.";
        TreeMap<Domains.Str, java.lang.Object> internalFieldMap = internal.union(TreeMap.treeMap(Utils.StrOrd,
                P.p(concrete.Utils.Fields.proto, Init.Function_prototype_Addr),
                P.p(concrete.Utils.Fields.code, clo),
                P.p(concrete.Utils.Fields.classname, myclass)));
        return new Domains.Object(external, internalFieldMap);
    }
    public static Domains.Object createFunctionObject(Domains.Native clo, TreeMap<Domains.Str, Domains.BValue> external, TreeMap<Domains.Str, java.lang.Object> internal, JSClass myclass) {
        assert(external.contains(Utils.Fields.length)) : "Native function with no length.";
        TreeMap<Domains.Str, java.lang.Object> internalFieldMap = internal.union(TreeMap.treeMap(Utils.StrOrd,
                P.p(concrete.Utils.Fields.proto, Init.Function_prototype_Addr),
                P.p(concrete.Utils.Fields.code, clo),
                P.p(concrete.Utils.Fields.classname, myclass)));
        return new Domains.Object(external, internalFieldMap);
    }

    public static Domains.Object makeNativeValueStore(F3<Domains.Address, Domains.Address, Domains.Store, P2<Domains.Value, Domains.Store>> f, TreeMap<Domains.Str, Domains.BValue> external) {
        JSClass myclass = JSClass.CFunction;
        return createFunctionObject(new Domains.Native((selfAddr, argArrayAddr, x, env, store, pad, ks) -> {
            P2<Domains.Value, Domains.Store> tmp = f.f(selfAddr, argArrayAddr, store);
            Domains.Value rv = tmp._1();
            Domains.Store newStore = tmp._2();
            return makeState(rv, x, env, newStore, pad, ks);
        }), external, myclass);
    }
    public static Domains.Object makeNativeValueStore(F3<Domains.Address, Domains.Address, Domains.Store, P2<Domains.Value, Domains.Store>> f, TreeMap<Domains.Str, Domains.BValue> external, JSClass myclass) {
        return createFunctionObject(new Domains.Native((selfAddr, argArrayAddr, x, env, store, pad, ks) -> {
            P2<Domains.Value, Domains.Store> tmp = f.f(selfAddr, argArrayAddr, store);
            Domains.Value rv = tmp._1();
            Domains.Store newStore = tmp._2();
            return makeState(rv, x, env, newStore, pad, ks);
        }), external, myclass);
    }

    public static Domains.Object makeNativeValue(F3<Domains.Address, Domains.Address, Domains.Store, Domains.Value> f, TreeMap<Domains.Str, Domains.BValue> external) {
        return createFunctionObject(new Domains.Native((selfAddr, argArrayAddr, x, env, store, pad, ks) -> {
            Domains.Value rv = f.f(selfAddr, argArrayAddr, store);
            return makeState(rv, x, env, store, pad, ks);
        }), external);
    }

    public static concrete.Interpreter.State makeState(Domains.Value v, IRVar x, Domains.Env env, Domains.Store store, Domains.Scratchpad pad, Domains.KontStack ks) {
        if (v instanceof Domains.BValue) {
            Domains.BValue bv = (Domains.BValue)v;
            if (x instanceof IRPVar) {
                IRPVar pv = (IRPVar)x;
                return new concrete.Interpreter.State(new Domains.ValueTerm(bv), env, store.extend(P.p(env.apply(pv), bv)), pad, ks);
            } else {
                IRScratch sc = (IRScratch)x;
                return new concrete.Interpreter.State(new Domains.ValueTerm(bv), env, store, pad.update(sc, bv), ks);
            }
        } else {
            return new concrete.Interpreter.State(new Domains.ValueTerm(v), env, store, pad, ks);
        }
    }

    public static Domains.Object makeMath(F<Double, Double> f) {
        return makeNativeValue((selfAddr, argArrayAddr, store) -> {
            Domains.Object argsObj = store.getObj(argArrayAddr);
            double inp = ToNumber(argsObj.apply(new Domains.Str("0")).orSome(Domains.Undef), store).n;
            if (inp == Double.NaN) {
                return new Domains.Num(Double.NaN);
            } else {
                return new Domains.Num(f.f(inp));
            }
        }, TreeMap.treeMap(Utils.StrOrd, P.p(Utils.Fields.length, new Domains.Num(1.0))));
    }
}
