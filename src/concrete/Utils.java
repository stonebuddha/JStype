package concrete;

import com.google.common.collect.ImmutableList;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.sun.java.browser.plugin2.DOM;
import com.sun.tools.corba.se.idl.constExpr.Negative;
import concrete.init.Init;
import ir.*;

import java.util.*;

/**
 * Created by wayne on 15/10/28.
 */
public class Utils {

    public static class Recursive<I> {
        public I func;
    }

    public static class Errors {
        public static final Domains.EValue typeError = new Domains.EValue(new Domains.Str("TypeError"));
        public static final Domains.EValue rangeError = new Domains.EValue(new Domains.Str("RangeError"));
    }

    public static class Fields {
        public static final Domains.Str proto = new Domains.Str("proto");
        public static final Domains.Str classname = new Domains.Str("class");
        public static final Domains.Str code = new Domains.Str("code");
        public static final Domains.Str prototype = new Domains.Str("prototype");
        public static final Domains.Str length = new Domains.Str("length");
        public static final Domains.Str value = new Domains.Str("value");
        public static final Domains.Str message = new Domains.Str("message");
        public static final Domains.Str constructor = new Domains.Str("constructor");
    }

    public static Map.Entry<Domains.Store, ArrayList<Domains.Address>> alloc(Domains.Store store, ArrayList<Domains.BValue> bvs) {
        ArrayList<Domains.Address> as = new ArrayList<Domains.Address>();
        for (int i = 0; i < bvs.size(); ++i) {
            as.add(Domains.Address.generate());
        }
        ArrayList<Map.Entry<Domains.Address, Domains.BValue>> avs = new ArrayList<>();
        for (int i = 0; i < as.size(); ++i) {
            avs.add(new AbstractMap.SimpleImmutableEntry<Domains.Address, Domains.BValue>(as.get(i), bvs.get(i)));
        }
        Domains.Store store1 = store.extendAll(
                ImmutableList.<Map.Entry<Domains.Address, Domains.BValue>>builder().addAll(avs).build()
        );
        return new AbstractMap.SimpleImmutableEntry<Domains.Store, ArrayList<Domains.Address>>(store1, as);
    }

    public static Map.Entry<Domains.Store, Domains.Address> allocFun(Domains.Closure clo, Domains.BValue n, Domains.Store store) {
        Domains.Address a1 = Domains.Address.generate();
        ImmutableMap.Builder<Domains.Str, Object> internBuilder = ImmutableMap.builder();
        internBuilder.put(Fields.proto, Init.Function_prototype_Addr);
        internBuilder.put(Fields.classname, JSClass.CFunction);
        internBuilder.put(Fields.code, clo);
        ImmutableMap<Domains.Str, Object> intern = internBuilder.build();
        ImmutableMap.Builder<Domains.Str, Domains.BValue> externBuilder = ImmutableMap.builder();
        externBuilder.put(Fields.length, n);
        ImmutableMap<Domains.Str, Domains.BValue> extern = externBuilder.build();
        return new AbstractMap.SimpleEntry<Domains.Store, Domains.Address>(store.putObj(a1, new Domains.Object(extern, intern)), a1);
    }

    public static Map.Entry<Domains.Store, Domains.Address> allocObj(Domains.Address a, Domains.Store store) {
        JSClass c = Init.classFromAddress(a);
        Domains.Address a1 = Domains.Address.generate();
        Domains.Address a2;
        Domains.BValue tmp = store.getObj(a).apply(Fields.prototype);
        if (tmp == null) {
            a2 = Init.Object_prototype_Addr;
        }
        else {
            a2 = (Domains.Address)tmp;
        }
        ImmutableMap.Builder<Domains.Str, java.lang.Object> internBuilder = ImmutableMap.builder();
        internBuilder.put(Fields.proto, a2);
        internBuilder.put(Fields.classname, c);
        ImmutableMap<Domains.Str, java.lang.Object> intern = internBuilder.build();
        ImmutableMap.Builder<Domains.Str, Domains.BValue> tmpBuilder = ImmutableMap.builder();
        Domains.Store store1 = store.putObj(a1, new Domains.Object(tmpBuilder.build(), intern));
        return new AbstractMap.SimpleEntry<Domains.Store, Domains.Address>(store1, a1);
    }

    public static Interpreter.State applyClo(Domains.BValue bv1, Domains.BValue bv2, Domains.BValue bv3, IRVar x, Domains.Env env, Domains.Store store, Domains.Scratchpad pad, Domains.KontStack ks) {
        if (bv1 instanceof Domains.Address && bv2 instanceof Domains.Address && bv3 instanceof Domains.Address) {
            Domains.Address a1 = (Domains.Address)bv1;
            Domains.Address a2 = (Domains.Address)bv2;
            Domains.Address a3 = (Domains.Address)bv3;
            Domains.Object o = store.getObj(a1);
            Boolean isCtor = store.getObj(a3).calledAsCtor();

            Domains.Closure tmp = o.getCode();
            if (tmp == null) {
                return new Interpreter.State(new Domains.ValueTerm(Errors.typeError), env, store, pad, ks);
            }
            else if (tmp instanceof Domains.Clo) {
                Domains.Clo tmp2 = (Domains.Clo)tmp;
                Domains.Env envc = tmp2.env;
                IRMethod m = tmp2.m;
                IRPVar self = m.self;
                IRPVar args = m.args;
                IRStmt s = m.s;
                ArrayList<Domains.BValue> tmpList = new ArrayList<>();
                tmpList.add(a2);
                tmpList.add(a3);
                Map.Entry<Domains.Store, ArrayList<Domains.Address>> tmp3 = alloc(store, tmpList);
                Domains.Store store1 = tmp3.getKey();
                ArrayList<Domains.Address> as = tmp3.getValue();
//                ArrayList<IRPVar> tmp4 = new ArrayList<IRPVar>();
//                tmp4.add(self);
//                tmp4.add(args);
                ImmutableList.Builder<Map.Entry<IRPVar, Domains.Address>> listBuilder = ImmutableList.builder();
                listBuilder.add(new AbstractMap.SimpleImmutableEntry<IRPVar, Domains.Address>(self, as.get(0)));
                listBuilder.add(new AbstractMap.SimpleImmutableEntry<IRPVar, Domains.Address>(args, as.get(1)));
                ImmutableList<Map.Entry<IRPVar, Domains.Address>> list = listBuilder.build();
                Domains.Env envc1 = env.extendAll(list);
                return new Interpreter.State(new Domains.StmtTerm(s), envc1, store1, Domains.Scratchpad.apply(0), ks.push(new Domains.RetKont(x, env, isCtor, pad)));
            }
            else {
                //TODO: how to shatter high ordered functions
                return null;
            }
        }
        else {
            return new Interpreter.State(new Domains.ValueTerm(Errors.typeError), env, store, pad, ks);
        }
    }

    public static Map.Entry<Domains.Value, Map.Entry<Domains.Store, Domains.Scratchpad>> delete(Domains.BValue bv1, Domains.BValue bv2, IRScratch x, Domains.Env env, Domains.Store store, Domains.Scratchpad pad) {
        if (bv1 instanceof Domains.Null || bv1 instanceof Domains.Undef) {
            return new AbstractMap.SimpleImmutableEntry<Domains.Value, AbstractMap.SimpleImmutableEntry<Domains.Store, Domains.Scratchpad>>(Errors.typeError, new AbstractMap.SimpleEntry<Domains.Store, Domains.Scratchpad>(store, pad));
        }
        else if (bv1 instanceof Domains.Address && bv2 instanceof Domains.Str) {
            Domains.Address a = (Domains.Address)bv1;
            Domains.Str str = (Domains.Str)bv2;
            Map.Entry<Domains.Object, Boolean> tmp = store.getObj(a).delete(str);
            Domains.Object o1 = tmp.getKey();
            Boolean del = tmp.getValue();
            if (del) {
                store.putObj(a, o1);
                pad.update(x, Domains.Bool.True);
                return new AbstractMap.SimpleImmutableEntry<Domains.Value, AbstractMap.SimpleImmutableEntry<Domains.Store, Domains.Scratchpad>>(new Domains.Undef(), new AbstractMap.SimpleImmutableEntry<Domains.Store, Domains.Scratchpad>(store, pad));
            }
            else {
                pad.update(x, Domains.Bool.False);
                return new AbstractMap.SimpleImmutableEntry<Domains.Value, AbstractMap.SimpleImmutableEntry<Domains.Store, Domains.Scratchpad>>(new Domains.Undef(), new AbstractMap.SimpleEntry<Domains.Store, Domains.Scratchpad>(store, pad));
            }
        }
        else {
            pad.update(x, Domains.Bool.True);
            return new AbstractMap.SimpleImmutableEntry<Domains.Value, AbstractMap.SimpleImmutableEntry<Domains.Store, Domains.Scratchpad>>(new Domains.Undef(), new AbstractMap.SimpleEntry<Domains.Store, Domains.Scratchpad>(store, pad));
        }
    }

    public static Domains.BValue lookup(Domains.Object o, Domains.Str str, Domains.Store store) {
        while (true) {
            Domains.BValue tmp = o.apply(str);
            if (tmp == null) {
                tmp = o.getProto();
                if (tmp instanceof Domains.Address) {
                    Domains.Address a = (Domains.Address)tmp;
                    o = store.getObj(a);
                }
                else {
                    return new Domains.Undef();
                }
            }
            else {
                return tmp;
            }
        }
    }

    public static ImmutableList<Domains.Str> objAllKeys(Domains.BValue bv, Domains.Store store) {
        ImmutableList.Builder<Domains.Str> listBuilder = ImmutableList.builder();
        ImmutableList<Domains.Str> list = listBuilder.build();
        if (bv instanceof Domains.Address) {
            Domains.Address a = (Domains.Address)bv;
            Domains.Object o;
            ImmutableSet<Domains.Str> flds, pflds;
            while (true) {
                o = store.getObj(a);
                flds = o.fields();
                for (Domains.Str str : flds) {
                    list.add(str);
                }
                java.lang.Object tmpObject = o.intern.get(Fields.proto);
                if (tmpObject instanceof Domains.Address) {
                    a = (Domains.Address)tmpObject;
                }
                else {
                    break;
                }
            }
        }
        return list;
    }

    public static Domains.Store setConstr(Domains.Store store, Domains.Address a) {
        Domains.Object o = store.getObj(a);
        ImmutableMap.Builder<Domains.Str, java.lang.Object> tmpBuilder = ImmutableMap.builder();
        tmpBuilder.putAll(o.intern);
        tmpBuilder.put(Fields.constructor, true);
        ImmutableMap<Domains.Str, java.lang.Object> tmpIntern = tmpBuilder.build();
        store.putObj(a, new Domains.Object(o.extern, tmpIntern));
        return store;
    }

    public static Map.Entry<Domains.Value, Map.Entry<Domains.Store, Domains.Scratchpad>> toObj(Domains.BValue bv, IRVar x, Domains.Env env, Domains.Store store, Domains.Scratchpad pad) {
        if (bv instanceof Domains.Null || bv instanceof Domains.Undef) {
            return new AbstractMap.SimpleImmutableEntry<Domains.Value, AbstractMap.SimpleImmutableEntry<Domains.Store, Domains.Scratchpad>>(Errors.typeError, new AbstractMap.SimpleImmutableEntry<Domains.Store, Domains.Scratchpad>(store, pad));
        }
        else if (bv instanceof Domains.Address) {
            if (x instanceof IRPVar) {
                IRPVar pv = (IRPVar)x;
                store = store.extend(new AbstractMap.SimpleImmutableEntry<Domains.Address, Domains.BValue>(env.apply(pv), bv));
                return new AbstractMap.SimpleImmutableEntry<Domains.Value, AbstractMap.SimpleImmutableEntry<Domains.Store, Domains.Scratchpad>>(bv, new AbstractMap.SimpleImmutableEntry<Domains.Store, Domains.Scratchpad>(store, pad));
            }
        }
        else {

        }
    }

    public static Map.Entry<Domains.Value, Domains.Store> updateObj(Domains.BValue bv1, Domains.BValue bv2, Domains.BValue bv3, Domains.Store store) {
        // TODO
        return null;
    }
}
