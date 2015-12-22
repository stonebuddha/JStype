package analysis;

import fj.*;
import fj.data.Option;
import fj.data.List;
import immutable.FHashMap;
import immutable.FHashSet;
import ir.*;
import analysis.init.Init;
import analysis.Traces.Trace;

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
        public static final Domains.EValue warningError = new Domains.EValue(Domains.Str.inject(Domains.Str.alpha("WarningError")));
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
        FHashMap<Domains.Str, Object> intern = FHashMap.build(
                Fields.proto, Domains.AddressSpace.Address.inject(Init.Function_prototype_Addr),
                Fields.classname, JSClass.CFunction,
                Fields.code, FHashSet.build(clo)
        );
        Domains.ExternMap extern = new Domains.ExternMap().strongUpdate(Fields.length, n);
        return store.alloc(a, new Domains.Object(extern, intern, FHashSet.build(Fields.length)));
    }

    public static P2<Domains.Store, Domains.BValue> allocObj(Domains.BValue bv, Domains.AddressSpace.Address a, Domains.Store store, Trace trace) {
        FHashMap<JSClass, FHashSet<Domains.AddressSpace.Address>> class1 = FHashMap.empty();
        for (Domains.AddressSpace.Address add : bv.as) {
            JSClass addClass = Init.classFromAddress.get(add).orSome(JSClass.CObject);
            if (class1.contains(addClass)) {
                class1 = class1.set(addClass, class1.get(addClass).some().insert(add));
            }
            else {
                class1 = class1.set(addClass, FHashSet.build(add));
            }
        }

        FHashMap<JSClass, FHashSet<Domains.AddressSpace.Address>> classes;
        if (bv.defAddr()) {
            classes = class1;
        }
        else {
            classes = class1;
            if (classes.get(JSClass.CObject).isSome()) {
                classes = classes.set(JSClass.CObject, classes.get(JSClass.CObject).some().insert(Init.Object_prototype_Addr));
            }
            else {
                classes = classes.set(JSClass.CObject, FHashSet.build(Init.Object_prototype_Addr));
            }
        }

        FHashMap<JSClass, Domains.AddressSpace.Address> addrs = FHashMap.empty();
        for (JSClass key : classes.keys()) {
            addrs = addrs.set(key, trace.modAddr(a, key));
        }

        FHashMap<JSClass, Domains.BValue> pas = classes.map(
                as -> {
                    FHashSet<Domains.AddressSpace.Address> acc = Domains.AddressSpace.Addresses.apply();
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

        FHashMap<JSClass, FHashMap<Domains.Str, Object>> intern = FHashMap.empty();
        for (JSClass key : classes.keys()) {
            intern = intern.set(key, FHashMap.build(
                    Fields.proto, pas.get(key).some(),
                    Fields.classname, key));
        }

        Domains.Store store1 = store;
        for (JSClass key : classes.keys()) {
            Domains.Object o = new Domains.Object(new Domains.ExternMap(), intern.get(key).some(), FHashSet.empty());
            store1 = store1.alloc(addrs.get(key).some(), o);
        }

        Domains.BValue bv1 = Domains.AddressSpace.Addresses.inject(FHashSet.build(addrs.values()));

        return P.p(store1, bv1);
    }

    public static FHashSet<Interpreter.State> applyClo(Domains.BValue bv1, Domains.BValue bv2, Domains.BValue bv3, IRVar x, Domains.Env env, Domains.Store store, Domains.Scratchpad pad, Domains.KontStack ks, Trace trace) {
        if (Interpreter.Mutable.splitStates) {
            return applyCloWithSplits(bv1, bv2, bv3, x, env, store, pad, ks, trace);
        }
        Domains.BValue bv2as = Domains.AddressSpace.Addresses.inject(bv2.as);
        assert bv2as.defAddr() && bv3.defAddr() && bv3.as.size() == 1;
        Boolean isctor = store.getObj(bv3.as.head()).calledAsCtor();

        FHashSet<Domains.AddressSpace.Address> oas;
        if (Interpreter.Mutable.pruneStore) {
            oas = bv2.as.union(bv3.as).union(Init.keepInStore);
        }
        else {
            oas = Domains.AddressSpace.Addresses.apply();
        }

        FHashMap<FHashSet<Domains.AddressSpace.Address>, Domains.Store> memo = FHashMap.empty();

        FHashSet<Interpreter.State> sigmas = FHashSet.empty();
        Boolean nonfun = false;

        for (Domains.AddressSpace.Address a : bv1.as) {
            FHashSet<Domains.Closure> clos = store.getObj(a).getCode();
            if (clos.size() > 0) {
                for (Domains.Closure clo : clos) {
                    if (clo instanceof Domains.Clo) {
                        Domains.Env envc = ((Domains.Clo) clo).env;
                        IRMethod m = ((Domains.Clo) clo).m;
                        IRPVar self = ((Domains.Clo) clo).m.self;
                        IRPVar args = ((Domains.Clo) clo).m.args;
                        IRStmt s = ((Domains.Clo) clo).m.s;

                        if (Interpreter.Mutable.pruneStore) {
                            FHashSet<Domains.AddressSpace.Address> vas = envc.addrs();
                            Domains.Store reach_store;
                            Option<Domains.Store> storeOption = memo.get(vas);
                            if (storeOption.isNone()) {
                                P2<Domains.Store, Domains.Store> pruneStore = store.prune(vas, oas);
                                Interpreter.PruneStoreToo.update(trace, P.p(pruneStore._2(), pad));
                                memo = memo.set(vas, pruneStore._1());
                                reach_store = pruneStore._1();
                            }
                            else {
                                reach_store = storeOption.some();
                            }
                            for (Domains.AddressSpace.Address selfAddr : bv2.as) {
                                Domains.BValue selfBV = Domains.AddressSpace.Address.inject(selfAddr);
                                Trace trace1 = trace.update(envc, store, selfBV, bv3, s);
                                Domains.AddressSpace.Address ka = trace1.toAddr();
                                List<Domains.AddressSpace.Address> as = List.list(trace1.makeAddr(self), trace1.makeAddr(args));
                                Domains.Store rstore1 = alloc(reach_store, ka, ks.push(new Domains.RetKont(x, env, isctor, trace)));
                                Domains.Store rstore2 = alloc(rstore1, as, List.list(selfBV, bv3));
                                Domains.Env envc1 = envc.extendAll(List.list(self, args).zip(as));
                                Integer exc = ks.exc.head() != 0 ? 1 : 0;
                                sigmas = sigmas.insert(new Interpreter.State(new Domains.StmtTerm(s), envc1, rstore2, Domains.Scratchpad.apply(0), new Domains.KontStack(List.list(new Domains.AddrKont(ka, m)), List.list(exc)), trace1));
                            }
                        }
                        else {
                            for (Domains.AddressSpace.Address selfAddr : bv2.as) {
                                Domains.BValue selfBV = Domains.AddressSpace.Address.inject(selfAddr);
                                Trace trace1 = trace.update(envc, store, selfBV, bv3, s);
                                Domains.AddressSpace.Address ka = trace1.toAddr();
                                List<Domains.AddressSpace.Address> as = List.list(trace1.makeAddr(self), trace1.makeAddr(args));
                                Domains.Store store1 = alloc(store, ka, ks.push(new Domains.RetKont(x, env, isctor, trace)));
                                Domains.Store store2 = alloc(store1, as, List.list(selfBV, bv3));
                                Domains.Env envc1 = envc.extendAll(List.list(self, args).zip(as));
                                Integer exc = ks.exc.head() != 0 ? 1 : 0;
                                Interpreter.PruneScratch.update(trace, pad);
                                sigmas = sigmas.insert(new Interpreter.State(new Domains.StmtTerm(s), envc1, store2, Domains.Scratchpad.apply(0), new Domains.KontStack(List.list(new Domains.AddrKont(ka, m)), List.list(exc)), trace1));
                            }
                        }
                    }
                    else if (clo instanceof Domains.Native) {
                        for (Domains.AddressSpace.Address selfAddr : bv2.as) {
                            sigmas = sigmas.union(((Domains.Native) clo).f.f(Domains.AddressSpace.Address.inject(selfAddr), bv3, x, env, store, pad, ks, trace));
                        }
                    }
                }
            }
            else {
                nonfun = true;
            }
        }

        if (Interpreter.Mutable.pruneStore && Interpreter.PruneStoreToo.contains(trace)) {
            Trace merge_trace;
            if (ks.top() instanceof Domains.SeqKont && ((Domains.SeqKont) ks.top()).ss.isNotEmpty()) {
                merge_trace = trace.update(((Domains.SeqKont) ks.top()).ss.head());
            }
            else {
                throw new RuntimeException("translator reneged");
            }

            FHashSet<Domains.AddressSpace.Address> as;
            if (x instanceof IRPVar) {
                as = env.apply((IRPVar) x).some();
            }
            else {
                as = Domains.AddressSpace.Addresses.apply();
            }

            Interpreter.Mutable.prunedInfo.set(merge_trace, P.p(trace, x, as));
        }

        for (Interpreter.State s : sigmas) {
            if (s.t instanceof Domains.ValueTerm && ((Domains.ValueTerm) s.t).v.equals(Errors.typeError)) {
                Interpreter.Mutable.except(x.id, Errors.typeError, x.loc, "");
            }
        }

        if (!bv1.defAddr() || nonfun) {
            Interpreter.Mutable.except(x.id, Errors.typeError, x.loc, "call a non-function");
        }

        if (nonfun) {
            sigmas = sigmas.insert(new Interpreter.State(new Domains.ValueTerm(Errors.typeError), env, store, pad, ks, trace));
        }

        return sigmas;
    }

    public static FHashSet<Interpreter.State> applyCloWithSplits(Domains.BValue bv1, Domains.BValue bv2, Domains.BValue bv3, IRVar x, Domains.Env env, Domains.Store store, Domains.Scratchpad pad, Domains.KontStack ks, Trace trace) {
        Domains.BValue bv2as = Domains.AddressSpace.Addresses.inject(bv2.as);
        assert bv2as.defAddr() && bv3.defAddr() && bv3.as.size() == 1;
        boolean isctor = store.getObj(bv3.as.head()).calledAsCtor();

        FHashSet<Domains.AddressSpace.Address> oas;
        if (Interpreter.Mutable.pruneStore) {
            oas = bv2.as.union(bv3.as).union(Init.keepInStore);
        }
        else {
            oas = Domains.AddressSpace.Addresses.apply();
        }

        FHashMap<FHashSet<Domains.AddressSpace.Address>, Domains.Store> memo = FHashMap.empty();

        FHashSet<Interpreter.State> sigmas = FHashSet.empty();
        boolean nonfun = false;

        for (Domains.AddressSpace.Address a : bv1.as) {
            FHashSet<Domains.Closure> clos = store.getObj(a).getCode();
            if (clos.size() > 0) {
                for (Domains.Closure clo : clos) {
                    if (clo instanceof Domains.Clo) {
                        Domains.Env envc = ((Domains.Clo) clo).env;
                        IRMethod m = ((Domains.Clo) clo).m;
                        IRPVar self = ((Domains.Clo) clo).m.self;
                        IRPVar args = ((Domains.Clo) clo).m.args;
                        IRStmt s = ((Domains.Clo) clo).m.s;
                        if (Interpreter.Mutable.pruneStore) {
                            FHashSet<Domains.AddressSpace.Address> vas = envc.addrs();
                            Domains.Store reach_store;
                            Option<Domains.Store> storeOption = memo.get(vas);
                            if (storeOption.isNone()) {
                                P2<Domains.Store, Domains.Store> pruneStore = store.prune(vas, oas);
                                Interpreter.PruneStoreToo.update(trace, P.p(pruneStore._2(), pad));
                                memo = memo.set(vas, pruneStore._1());
                                reach_store = pruneStore._1();
                            }
                            else {
                                reach_store = storeOption.some();
                            }
                            sigmas = sigmas.union(FHashSet.build(bv2.as.map(
                                    selfAddr -> {
                                        Domains.BValue selfBV = Domains.AddressSpace.Address.inject(selfAddr);
                                        Trace trace1 = trace.update(envc, store, selfBV, bv3, s);
                                        Domains.AddressSpace.Address ka = trace1.toAddr();
                                        List<Domains.AddressSpace.Address> as = List.list(trace1.makeAddr(self), trace1.makeAddr(args));
                                        Domains.Store rstore1 = alloc(reach_store, ka, ks.push(new Domains.RetKont(x, env, isctor, trace)));
                                        Domains.Store rstore2 = alloc(rstore1, as, List.list(selfBV, bv3));
                                        Domains.Env envc1 = envc.extendAll(List.list(self, args).zip(as));
                                        Integer exc = ks.exc.head() != 0 ? 1 : 0;
                                        return new Interpreter.State(new Domains.StmtTerm(s), envc1, rstore2, Domains.Scratchpad.apply(0), new Domains.KontStack(List.list(new Domains.AddrKont(ka, m)), List.list(exc)), trace1);
                                    })));
                        }
                        else {
                            sigmas = sigmas.union(FHashSet.build(bv2.as.map(
                                    selfAddr -> {
                                        Domains.BValue selfBV = Domains.AddressSpace.Address.inject(selfAddr);
                                        Trace trace1 = trace.update(envc, store, selfBV, bv3, s);
                                        Domains.AddressSpace.Address ka = trace1.toAddr();
                                        List<Domains.AddressSpace.Address> as = List.list(trace1.makeAddr(self), trace1.makeAddr(args));
                                        Domains.Store store1 = alloc(store, ka, ks.push(new Domains.RetKont(x, env, isctor, trace)));
                                        Domains.Store store2 = alloc(store1, as, List.list(selfBV, bv3));
                                        Domains.Env envc1 = envc.extendAll(List.list(self, args).zip(as));
                                        Integer exc = ks.exc.head() != 0 ? 1 : 0;
                                        Interpreter.PruneScratch.update(trace, pad);
                                        return new Interpreter.State(new Domains.StmtTerm(s), envc1, store2, Domains.Scratchpad.apply(0), new Domains.KontStack(List.list(new Domains.AddrKont(ka, m)), List.list(exc)), trace1);
                                    })));
                        }
                    }
                    else if (clo instanceof Domains.Native) {
                        sigmas = sigmas.union(bv2.as.bind(
                                selfAddr -> ((Domains.Native) clo).f.f(Domains.AddressSpace.Address.inject(selfAddr), bv3, x, env, store, pad, ks, trace)
                                ));
                    }
                }
            }
            else {
                nonfun = true;
            }
        }

        if (Interpreter.Mutable.pruneStore && Interpreter.PruneStoreToo.contains(trace)) {
            Trace merge_trace;
            if (ks.top() instanceof Domains.SeqKont && ((Domains.SeqKont) ks.top()).ss.isNotEmpty()) {
                merge_trace = trace.update(((Domains.SeqKont) ks.top()).ss.head());
            }
            else {
                throw new RuntimeException("translator reneged");
            }

            FHashSet<Domains.AddressSpace.Address> as;
            if (x instanceof IRPVar) {
                as = env.apply((IRPVar) x).some();
            }
            else {
                as = Domains.AddressSpace.Addresses.apply();
            }

            Interpreter.Mutable.prunedInfo.set(merge_trace, P.p(trace, x, as));
        }

        if (!bv1.defAddr() || nonfun) {
            sigmas = sigmas.insert(new Interpreter.State(new Domains.ValueTerm(Errors.typeError), env, store, pad, ks, trace));
        }

        return sigmas;

    }

    public static P2<Option<P2<Domains.Store, Domains.Scratchpad>>, Option<Domains.EValue>> delete(Domains.BValue bv1, Domains.BValue bv2, IRScratch x, Domains.Env env, Domains.Store store, Domains.Scratchpad pad) {
        Boolean isStrong = bv1.as.size() == 1 & store.isStrong(bv1.as.head());

        Boolean defPresent = true, defAbsent = true;
        for (Domains.AddressSpace.Address a : bv1.as) {
            Domains.Object o = store.getObj(a);
            Boolean dp, da;
            if (defPresent) {
                dp = o.defField(bv2.str) && !(Init.nodelete.get(o.getJSClass()).orSome(FHashSet.empty()).member(bv2.str));
            }
            else {
                dp = false;
            }
            if (defAbsent && !dp) {
                da = o.defNotField(bv2.str) || (Init.nodelete.get(o.getJSClass()).orSome(FHashSet.empty()).member(bv2.str));
            }
            else {
                da = false;
            }

            defPresent = dp;
            defAbsent = da;
        }

        Option<P2<Domains.Store, Domains.Scratchpad>> noexc;
        if (bv1.as.isEmpty()) {
            noexc = Option.none();
        }
        else if (defAbsent) {
            noexc = Option.some(P.p(store, pad.update(x, Domains.Bool.FalseBV)));
        }
        else if (defPresent) {
            if (isStrong) {
                Domains.AddressSpace.Address a = bv1.as.head();
                Domains.Store store1 = store.putObjStrong(a, store.getObj(a).strongDelete(bv2.str));
                noexc = Option.some(P.p(store1, pad.update(x, Domains.Bool.TrueBV)));
            }
            else {
                Domains.Store store1 = store;
                for (Domains.AddressSpace.Address a : bv1.as) {
                    store1 = store1.putObjWeak(a, store1.getObj(a).weakDelete(bv2.str));
                }
                noexc = Option.some(P.p(store1, pad.update(x, Domains.Bool.TrueBV)));
            }
        }
        else {
            Domains.Store store1 = store;
            for (Domains.AddressSpace.Address a : bv1.as) {
                store1 = store1.putObjWeak(a, store1.getObj(a).weakDelete(bv2.str));
            }
            noexc = Option.some(P.p(store1, pad.update(x, Domains.Bool.TopBV)));
        }

        Option<Domains.EValue> exc;
        if (bv1.nil.equals(Domains.Null.Top) || bv1.undef.equals(Domains.Undef.Top)) {
            exc = Option.some(Errors.typeError);
        }
        else {
            exc = Option.none();
        }

        return P.p(noexc, exc);
    }

    public static Domains.BValue lookup(FHashSet<Domains.AddressSpace.Address> as, Domains.Str str, Domains.Store store) {
        Domains.BValue bv = Domains.BValue.Bot;
        for (Domains.AddressSpace.Address a : as) {
            bv = bv.merge(look(store.getObj(a), str, store));
        }
        return bv;
    }

    private static Domains.BValue look(Domains.Object o, Domains.Str str, Domains.Store store) {
        Option<Domains.BValue> localBV = o.apply(str);
        FHashSet<Domains.BValue> local, chain, fin;
        if (localBV.isSome()) {
            local = FHashSet.build(localBV.some());
        }
        else {
            local = FHashSet.empty();
        }

        if (!o.defField(str)) {
            chain = o.getProto().as.map(a -> look(store.getObj(a), str, store));
        }
        else {
            chain = FHashSet.empty();
        }

        if (!o.defField(str) && o.getProto().nil.equals(Domains.Null.Top)) {
            fin = FHashSet.build(Domains.Undef.BV);
        }
        else {
            fin = FHashSet.empty();
        }
        Domains.BValue bv = Domains.BValue.Bot;
        for (Domains.BValue v : local.union(chain).union(fin)) {
            bv = bv.merge(v);
        }
        return bv;
    }

    public static FHashSet<Domains.Str> objAllKeys(Domains.BValue bv, Domains.Store store) {
        FHashSet<Domains.Str> keys = FHashSet.empty();
        for (Domains.AddressSpace.Address a : bv.as) {
            keys = keys.union(recur(a, store));
        }
        return keys;
    }

    private static FHashSet<Domains.Str> recur(Domains.AddressSpace.Address addr, Domains.Store store) {
        Domains.Object o = store.getObj(addr);
        FHashSet<Domains.Str> ret = FHashSet.empty();
        for (Domains.AddressSpace.Address a : o.getProto().as) {
            ret = ret.union(recur(a, store));
        }
        return ret;
    }

    public static Domains.Store setConstr(Domains.Store store, Domains.BValue bv) {
        assert bv.as.size() == 1;
        Domains.Object o = store.getObj(bv.as.head());
        Domains.Object o1 = new Domains.Object(o.extern, o.intern.set(Fields.constructor, true), o.present);
        return store.putObjStrong(bv.as.head(), o1);
    }

    public static P3<Domains.BValue, Domains.Store, FHashSet<Domains.Domain>> toObjBody(Domains.BValue bv, Domains.Store store, Trace trace, Domains.AddressSpace.Address a) {
        FHashSet<Domains.Domain> sorts = bv.sorts.intersect(FHashSet.build(Domains.DAddr, Domains.DNum, Domains.DBool, Domains.DStr));

        Domains.BValue bv1 = Domains.BValue.Bot;
        Domains.Store store1 = store;
        for (Domains.Domain sort : sorts) {
            if (sort.equals(Domains.DAddr)) {
                bv1 = bv1.merge(Domains.AddressSpace.Addresses.inject(bv.as));
            }
            else if (sort.equals(Domains.DNum)) {
                P2<Domains.Store, Domains.BValue> res = allocObj(Domains.AddressSpace.Address.inject(Init.Number_Addr), a, store1, trace);
                Domains.Store store2 = res._1();
                Domains.BValue bv2 = res._2();
                assert bv2.as.size() == 1;
                Domains.Object o = store2.getObj(bv2.as.head());
                Domains.Object o1 = new Domains.Object(o.extern, o.intern.set(Fields.value, bv.onlyNum()), o.present);
                bv1 = bv1.merge(bv2);
                store1 = store2.putObj(bv2.as.head(), o1);
            }
            else if (sort.equals(Domains.DBool)) {
                P2<Domains.Store, Domains.BValue> res = allocObj(Domains.AddressSpace.Address.inject(Init.Boolean_Addr), a, store1, trace);
                Domains.Store store2 = res._1();
                Domains.BValue bv2 = res._2();
                assert bv2.as.size() == 1;
                Domains.Object o = store2.getObj(bv2.as.head());
                Domains.Object o1 = new Domains.Object(o.extern, o.intern.set(Fields.value, bv.onlyNum()), o.present);
                bv1 = bv1.merge(bv2);
                store1 = store2.putObj(bv2.as.head(), o1);
            }
            else if (sort.equals(Domains.DStr)) {
                P2<Domains.Store, Domains.BValue> res = allocObj(Domains.AddressSpace.Address.inject(Init.String_Addr), a, store1, trace);
                Domains.Store store2 = res._1();
                Domains.BValue bv2 = res._2();
                assert bv2.as.size() == 1;
                Domains.Object o = store2.getObj(bv2.as.head());
                Option<String> exactStr = Domains.Str.getExact(bv.str);
                Domains.ExternMap extern;
                if (exactStr.isSome()) {
                    String s = exactStr.some();
                    extern = List.range(0, s.length()).foldLeft(
                            (acc, e) -> acc.strongUpdate(Domains.Str.alpha(e.toString()), Domains.Str.inject(Domains.Str.alpha(String.valueOf(s.charAt(e))))),
                            o.extern.strongUpdate(Fields.length, Domains.Num.inject(Domains.Num.alpha(new Double(s.length()))))
                    );
                }
                else {
                    extern = o.extern.weakUpdate(Fields.length, Domains.Num.inject(Domains.Num.NReal)).
                            weakUpdate(Domains.Str.SNum, Domains.Str.inject(Domains.Str.SingleChar));
                }
                FHashMap<Domains.Str, Object> intern1 = o.intern.set(Fields.value, bv.onlyStr());
                Domains.Object o1 = new Domains.Object(extern, intern1, o.present.insert(Fields.length));
                bv1 = bv1.merge(bv2);
                store1 = store2.putObj(bv2.as.head(), o1);
            }
            else if (sort.equals(Domains.DUndef) || sort.equals(Domains.DNull)) {
                throw new RuntimeException("suppresses compiler warning; this case can't happen");
            }
        }
        
        return P.p(bv1, store1, sorts);
    }

    public static P2<Option<P3<Domains.BValue, Domains.Store, Domains.Scratchpad>>, Option<Domains.EValue>> toObj(Domains.BValue bv, IRVar x, Domains.Env env, Domains.Store store, Domains.Scratchpad pad, Trace trace) {
        P3<Domains.BValue, Domains.Store, FHashSet<Domains.Domain>> objBody = toObjBody(bv, store, trace, trace.makeAddr(x));
        Domains.BValue bv1 = objBody._1();
        Domains.Store store1 = objBody._2();
        FHashSet<Domains.Domain> sorts = objBody._3();

        Option<P3<Domains.BValue, Domains.Store, Domains.Scratchpad>> noexc;
        if (sorts.size() > 0) {
            if (x instanceof IRPVar) {
                noexc = Option.some(P.p(bv1, store1.extend(env.apply(((IRPVar) x)).some(), bv1), pad));
            }
            else {
                noexc = Option.some(P.p(bv1, store1, pad.update(((IRScratch) x), bv1)));
            }
        }
        else {
            noexc = Option.none();
        }

        Option<Domains.EValue> exc;
        if (bv.nil.equals(Domains.Null.Top) || bv.undef.equals(Domains.Undef.Top)) {
            exc = Option.some(Errors.typeError);
        }
        else {
            exc = Option.none();
        }
        return P.p(noexc, exc);
    }

    public static P2<Option<P2<Domains.BValue, Domains.Store>>, Option<Domains.EValue>> updateObj(Domains.BValue bv1, Domains.BValue bv2, Domains.BValue bv3, Domains.Store store) {
        Domains.Str str = bv2.str;
        Boolean maybeLength = Fields.length.partialLessEqual(str);
        Boolean isStrong = bv1.as.size() == 1 && store.isStrong(bv1.as.head());
        Domains.BValue bv3num = bv3.toNum();
        Boolean maybeArray = bv1.as.filter(a -> store.getObj(a).getJSClass().equals(JSClass.CArray)).size() > 0;
        Boolean rhsMaybeU32 = Domains.Num.maybeU32(bv3num);
        Boolean propertyMaybeU32 = Domains.Num.maybeU32(Domains.Num.inject(str.toNum()));

        Option<P2<Domains.BValue, Domains.Store>> noexc;
        if (isStrong) {
            Domains.Object o = store.getObj(bv1.as.head());
            Domains.Object o1;
            if (!maybeArray) {
                o1 = o.strongUpdate(str, bv3);
            }
            else if (!maybeLength) {
                o1 = o.strongUpdate(str, bv3).strongUpdate(Fields.length, Domains.Num.inject(Domains.Num.U32));
            }
            else if (!str.equals(Fields.length)) {
                o1 = o.weakDelete(Domains.Str.U32).strongUpdate(str, bv3).strongUpdate(Fields.length, Domains.Num.inject(Domains.Num.U32));
            }
            else {
                o1 = o.weakDelete(Domains.Str.U32).strongUpdate(Fields.length, Domains.Num.inject(Domains.Num.U32));
            }
            Domains.Store store1 = store.putObjStrong(bv1.as.head(), o1);
            noexc = Option.some(P.p(bv3, store1));
        }
        else if (bv1.as.size() > 0 ) {
            Domains.Store store1 = store;
            for (Domains.AddressSpace.Address a : bv1.as) {
                Domains.Object o = store1.getObj(a);
                if (o.getJSClass().equals(JSClass.CArray)) {
                    Domains.Object o1;
                    if (maybeLength && rhsMaybeU32) {
                        o1 = o.weakDelete(Domains.Str.U32).weakUpdate(str, bv3);
                    }
                    else {
                        o1 = o.weakUpdate(str, bv3);
                    }
                    Domains.Object o2;
                    if (propertyMaybeU32) {
                        o2 = o1.weakUpdate(Fields.length, Domains.Num.inject(Domains.Num.U32));
                    }
                    else {
                        o2 = o1;
                    }
                    store1 = store1.putObjWeak(a, o2);
                }
                else {
                    store1 = store1.putObjWeak(a, o.weakUpdate(str, bv3));
                }
            }
            noexc = Option.some(P.p(bv3, store1));
        }
        else {
            noexc = Option.none();
        }

        Option<Domains.EValue> exc;
        if (bv1.nil.equals(Domains.Null.Top) || bv1.undef.equals(Domains.Undef.Top)) {
            exc = Option.some(Errors.typeError);
        }
        else if (maybeArray && maybeLength && Domains.Num.maybeNotU32(bv3)) {
            exc = Option.some(Errors.rangeError);
        }
        else {
            exc = Option.none();
        }
        return P.p(noexc, exc);
    }

    public static P2<Domains.Store, Domains.Scratchpad> refineExc(IRExp e, Domains.Store store, Domains.Env env, Domains.Scratchpad pad, Filters.BVFilter bvf) {
        P4<Domains.Store, Domains.Scratchpad, Domains.Store, Domains.Scratchpad> res = refine(bvf, e, store, env, pad);
        return P.p(res._1(), res._2());
    }

    public static P4<Domains.Store, Domains.Scratchpad, Domains.Store, Domains.Scratchpad> refine(Filters.BVFilter bvf, IRExp e, Domains.Store store, Domains.Env env, Domains.Scratchpad pad) {
        if (e instanceof IRPVar) {
            IRPVar x = ((IRPVar) e);
            FHashSet<Domains.AddressSpace.Address> as = env.apply(x).some();
            if (as.size() == 1) {
                P2<Domains.BValue, Domains.BValue> newBVP = store.applyAll(as).filterBy(bvf, store);
                Domains.BValue newBVT = newBVP._1(), newBVF = newBVP._2();
                return P.p(store.extend(as, newBVT), pad, store.extend(as, newBVF), pad);
            }
            else {
                return P.p(store, pad, store, pad);
            }
        }
        else if (e instanceof IRScratch) {
            IRScratch x = ((IRScratch) e);
            P2<Domains.BValue, Domains.BValue> newBVP = pad.apply(x).filterBy(bvf, store);
            Domains.BValue newBVT = newBVP._1(), newBVF = newBVP._2();
            return P.p(store, pad.update(x, newBVT), store, pad.update(x, newBVF));
        }
        else if (e instanceof IRBinop && ((IRBinop) e).op.equals(Bop.Access)) {
            IRExp el = ((IRBinop) e).e1, er = ((IRBinop) e).e2;
            Domains.BValue objbv = Eval.eval(el, env, store, pad);
            Domains.BValue strbv = Eval.eval(er, env, store, pad);
            Option<P2<Domains.AddressSpace.Address, Domains.Object>> refineable = refineableAddrObj(objbv, strbv.str, store);
            if (refineable.isSome()) {
                Domains.AddressSpace.Address addr = refineable.some()._1();
                Domains.Object o = refineable.some()._2();
                Option<Domains.BValue> optBV = o.apply(strbv.str);
                if (optBV.isNone()) {
                    throw new RuntimeException("refineableAddrObj returned bad object");
                }
                Domains.BValue oldBV = optBV.some();

                P2<Domains.BValue, Domains.BValue> newBVP = oldBV.filterBy(bvf, store);
                Domains.BValue newBVT = newBVP._1(), newBVF = newBVP._2();
                Domains.Object oT = new Domains.Object(o.extern.strongUpdate(strbv.str, newBVT), o.intern, o.present);
                Domains.Store storeT = store.putObjStrong(addr, oT);
                Domains.Object oF = new Domains.Object(o.extern.strongUpdate(strbv.str, newBVF), o.intern, o.present);
                Domains.Store storeF = store.putObjStrong(addr, oF);
                return P.p(storeT, pad, storeF, pad);
            }
            else {
                return P.p(store, pad, store, pad);
            }
        }
        else {
            return P.p(store, pad, store, pad);
        }
    }

    public static Option<P2<Domains.AddressSpace.Address, Domains.Object>> refineableAddrObj(Domains.BValue bv, Domains.Str str, Domains.Store store) {
        if (bv.as.size() == 1 && store.isStrong(bv.as.head()) && Domains.Str.isExact(str)) {
            Domains.Object obj = store.getObj(bv.as.head());
            if (obj.defField(str)) {
                return Option.some(P.p(bv.as.head(), obj));
            }
            else if (obj.defNotField(str)) {
                return refineableAddrObj(obj.getProto(), str, store);
            }
            else {
                return Option.none();
            }
        }
        else {
            return Option.none();
        }
    }
}
