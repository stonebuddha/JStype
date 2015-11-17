package analysis;

import fj.Ord;
import fj.P;
import fj.data.Option;
import fj.data.Set;
import fj.P2;
import fj.P3;
import fj.data.*;
import ir.*;
import analysis.init.Init;

/**
 * Created by wayne on 15/11/2.
 */
public class Utils {

    public static class Recursive<I> {
        public I func;
    }

    public static class Errors {
        public static final Domains.EValue typeError = new Domains.EValue(Domains.Str.inject(Domains.Str.alpha("TypeError")));
        public static final Domains.EValue rangeError = new Domains.EValue(Domains.Str.inject(Domains.Str.alpha("RangeError")));
        public static final Domains.EValue uriError = new Domains.EValue(Domains.Str.inject(Domains.Str.alpha("URIError")));
        public static final Domains.EValue syntaxError = new Domains.EValue(Domains.Str.inject(Domains.Str.alpha("SyntaxError")));
    }

    public static class Fields {
        public static final Domains.Str proto = Domains.Str.alpha("proto");
        public static final Domains.Str classname = Domains.Str.alpha("class");
        public static final Domains.Str code = Domains.Str.alpha("code");
        public static final Domains.Str prototype = Domains.Str.alpha("prototype");
        public static final Domains.Str length = Domains.Str.alpha("length");
        public static final Domains.Str value = Domains.Str.alpha("value");
        public static final Domains.Str message = Domains.Str.alpha("message");
        public static final Domains.Str constructor = Domains.Str.alpha("constructor");
    }

    public static class Filters {
        public static class BVFilter {}
        public static final BVFilter IsFunc = new BVFilter();
        public static final BVFilter IsUndefNull = new BVFilter();
    }

    public static Domains.Store alloc(Domains.Store store, List<Domains.AddressSpace.Address> as, List<Domains.BValue> bvs) {
        return store.alloc(as.zip(bvs));
    }

    public static Domains.Store alloc(Domains.Store store, Domains.AddressSpace.Address a, Domains.KontStack ks) {
        return store.alloc(a, ks);
    }

    public static Domains.Store allocFun(Domains.Closure clo, Domains.BValue n, Domains.AddressSpace.Address a, Domains.Store store) {
        TreeMap<Domains.Str, java.lang.Object> intern = TreeMap.treeMap(Ord.hashEqualsOrd(),
                P.p(Fields.proto, Domains.AddressSpace.Address.inject(Init.Function_prototype_Addr)),
                P.p(Fields.classname, JSClass.CFunction),
                P.p(Fields.code, Set.single(Ord.hashEqualsOrd(), clo))
        );
        Domains.ExternMap extern = new Domains.ExternMap().strongUpdate(Fields.length, n);
        return store.alloc(a, new Domains.Object(extern, intern, Set.single(Ord.<Domains.Str>hashEqualsOrd(), Fields.length)));
    }

    public static P2<Domains.Store, Domains.BValue> allocObj(Domains.BValue bv, Domains.AddressSpace.Address a, Domains.Store store, Trace trace) {
        TreeMap<JSClass, Set<Domains.AddressSpace.Address>> class1 = TreeMap.empty(Ord.<JSClass>hashEqualsOrd());
        for (Domains.AddressSpace.Address add : bv.as) {
            JSClass addClass = Init.classFromAddress.get(add).orSome(JSClass.CObject);
            if (class1.contains(addClass)) {
                class1.set(addClass, class1.get(addClass).some().insert(add));
            }
            else {
                class1.set(addClass, Set.single(Ord.<Domains.AddressSpace.Address>hashEqualsOrd(), add));
            }
        }

        TreeMap<JSClass, Set<Domains.AddressSpace.Address>> classes;
        if (bv.defAddr()) {
            classes = class1;
        }
        else {
            classes = class1;
            if (classes.get(JSClass.CObject).isSome()) {
                classes.set(JSClass.CObject, classes.get(JSClass.CObject).some().insert(Init.Object_prototype_Addr));
            }
            else {
                classes.set(JSClass.CObject, Set.single(Ord.<Domains.AddressSpace.Address>hashEqualsOrd(), Init.Object_prototype_Addr));
            }
        }

        TreeMap<JSClass, Domains.AddressSpace.Address> addrs = TreeMap.empty(Ord.<JSClass>hashEqualsOrd());
        for (JSClass key : classes.keys()) {
            addrs.set(key, trace.modAddr(a, key));
        }

        TreeMap<JSClass, Domains.BValue> pas = classes.map(
                as -> {
                    Set<Domains.AddressSpace.Address> acc = Domains.AddressSpace.Addresses.apply();
                    for (Domains.AddressSpace.Address add : as) {
                        Option<Domains.BValue> proto = store.getObj(add).apply(Fields.prototype);
                        if (proto.isSome()) {
                            if (proto.some().defAddr()) {
                                acc = acc.union(proto.some().as);
                            }
                            else {
                                acc = acc.union(proto.some().as).insert(Init.Object_prototype_Addr);
                            }
                        }
                        else {
                            acc = acc.insert(Init.Object_prototype_Addr);
                        }
                    }
                    return Domains.AddressSpace.Addresses.inject(acc);
                }
        );

        TreeMap<JSClass, TreeMap<Domains.Str, java.lang.Object>> intern = TreeMap.empty(Ord.<JSClass>hashEqualsOrd());
        for (JSClass key : classes.keys()) {
            intern.set(key, TreeMap.treeMap(Ord.<Domains.Str>hashEqualsOrd(),
                    P.p(Fields.proto, pas.get(key).some()),
                    P.p(Fields.classname, key)));
        }

        Domains.Store store1 = store;
        for (JSClass key : classes.keys()) {
            Domains.Object o = new Domains.Object(new Domains.ExternMap(), intern.get(key).some(), Set.empty(Ord.<Domains.Str>hashEqualsOrd()));
            store1 = store1.alloc(addrs.get(key).some(), o);
        }

        Domains.BValue bv1 = Domains.AddressSpace.Addresses.inject(Set.set(Ord.<Domains.AddressSpace.Address>hashEqualsOrd(), addrs.values()));

        return P.p(store1, bv1);
    }

    public static Set<Interpreter.State> applyClo(Domains.BValue bv1, Domains.BValue bv2, Domains.BValue bv3, IRVar x, Domains.Env env, Domains.Store store, Domains.Scratchpad pad, Domains.KontStack ks, Trace trace) {
        if (Interpreter.Mutable.splitStates) {
            return applyCloWithSplits(bv1, bv2, bv3, x, env, store, pad, ks, trace);
        }
        Domains.BValue bv2as = Domains.AddressSpace.Addresses.inject(bv2.as);
        // assert( bv2as.defAddr && bv3.defAddr && bv3.as.size == 1 )
        Boolean isctor = store.getObj(bv3.as.iterator().next()).calledAsCtor();

        Set<Domains.AddressSpace.Address> oas;
        if (Interpreter.Mutable.pruneStore) {
            oas = bv2.as.union(bv3.as).union(Init.keepInStore);
        }
        else {
            oas = Domains.AddressSpace.Addresses.apply();
        }

        TreeMap<Domains.AddressSpace.Addresses, Domains.Store> memo = TreeMap.empty(Ord.<Domains.AddressSpace.Addresses>hashEqualsOrd());

        Set<Interpreter.State> sigmas = Set.empty(Ord.<Interpreter.State>hashEqualsOrd());
        Boolean nonfun = false;
        for (Domains.AddressSpace.Address a : bv1.as) {
            Set<Domains.Closure> clos = store.getObj(a).getCode();
            if (!clos.isEmpty()) {
                clos.map(Ord.<Interpreter.State>hashEqualsOrd(),
                        closure -> {
                            //TODO
                           return null;
                        });
            }
            else {
                nonfun = true;
            }
        }
        // TODO
        return null;
    }

    public static Set<Interpreter.State> applyCloWithSplits(Domains.BValue bv1, Domains.BValue bv2, Domains.BValue bv3, IRVar x, Domains.Env env, Domains.Store store, Domains.Scratchpad pad, Domains.KontStack ks, Trace trace) {
        // TODO
        return null;
    }

    public static P2<Option<P2<Domains.Store, Domains.Scratchpad>>, Option<Domains.EValue>> delete(Domains.BValue bv1, Domains.BValue bv2, IRScratch x, Domains.Env env, Domains.Store store, Domains.Scratchpad pad) {
        // TODO
        return null;
    }

    public static Domains.BValue lookup(Set<Domains.AddressSpace.Address> as, Domains.Str str, Domains.Store store) {
        return null; // TODO
    }

    public static Set<Domains.Str> objAllKeys(Domains.BValue bv, Domains.Store store) {
        // TODO
        return null;
    }

    public static Domains.Store setConstr(Domains.Store store, Domains.BValue bv) {
        // TODO
        return null;
    }

    public static P2<Option<P3<Domains.BValue, Domains.Store, Domains.Scratchpad>>, Option<Domains.EValue>> toObj(Domains.BValue bv, IRVar x, Domains.Env env, Domains.Store store, Domains.Scratchpad pad, Trace trace) {
        // TODO
        return null;
    }

    public static P2<Option<P2<Domains.BValue, Domains.Store>>, Option<Domains.EValue>> updateObj(Domains.BValue bv1, Domains.BValue bv2, Domains.BValue bv3, Domains.Store store) {
        // TODO
        return null;
    }

        public static P2<Domains.Store, Domains.Scratchpad> refineExc(IRExp e, Domains.Store store, Domains.Env env, Domains.Scratchpad pad, Filters.BVFilter bvf) {
        // TODO
        return null;
    }
}
