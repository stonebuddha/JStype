package analysis;

import fj.*;
import fj.data.Option;
import fj.data.Set;
import fj.data.*;
import ir.*;
import analysis.init.Init;
import sun.text.resources.cldr.en.FormatData_en_IE;

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
        assert bv2as.defAddr() && bv3.defAddr() && bv3.as.size() == 1;
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

    public static P3<Domains.BValue, Domains.Store, Set<Domains.Domain>> toObjBody(Domains.BValue bv, Domains.Store store, Trace trace, Domains.AddressSpace.Address a) {
        Set<Domains.Domain> sorts = bv.sorts.intersect(Set.set(Ord.<Domains.Domain>hashEqualsOrd(), Domains.DAddr, Domains.DNum, Domains.DBool, Domains.DStr));

        Domains.BValue bv1 = Domains.BValue.Bot;
        Domains.Store store1 = store;
        for (Domains.Domain sort : sorts) {
            if (sort == Domains.DAddr) {
                bv1 = bv1.merge(Domains.AddressSpace.Addresses.inject(bv.as));
            }
            else if (sort == Domains.DNum) {
                P2<Domains.Store, Domains.BValue> res = allocObj(Domains.AddressSpace.Address.inject(Init.Number_Addr), a, store1, trace);
                Domains.Store store2 = res._1();
                Domains.BValue bv2 = res._2();
                assert bv2.as.size() == 1;
                Domains.Object o = store.getObj(bv.as.iterator().next());
                Domains.Object o1 = new Domains.Object(o.extern, o.intern.set(Fields.value, bv.onlyNum()), o.present);
                bv1 = bv1.merge(bv2);
                store1 = store2.putObj(bv2.as.iterator().next(), o1);
            }
            else if (sort == Domains.DBool) {
                P2<Domains.Store, Domains.BValue> res = allocObj(Domains.AddressSpace.Address.inject(Init.Number_Addr), a, store1, trace);
                Domains.Store store2 = res._1();
                Domains.BValue bv2 = res._2();
                assert bv2.as.size() == 1;
                Domains.Object o = store.getObj(bv.as.iterator().next());
                Domains.Object o1 = new Domains.Object(o.extern, o.intern.set(Fields.value, bv.onlyNum()), o.present);
                bv1 = bv1.merge(bv2);
                store1 = store2.putObj(bv2.as.iterator().next(), o1);
            }
            else if (sort == Domains.DStr) {
                P2<Domains.Store, Domains.BValue> res = allocObj(Domains.AddressSpace.Address.inject(Init.Number_Addr), a, store1, trace);
                Domains.Store store2 = res._1();
                Domains.BValue bv2 = res._2();
                assert bv2.as.size() == 1;
                Domains.Object o = store.getObj(bv.as.iterator().next());
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
                    extern = o.extern.weakUpdate(Fields.length, Domains.Num.inject(Domains.NReal)).
                            weakUpdate(Domains.SNum, Domains.Str.inject(Domains.Str.SingleChar));
                }
                TreeMap<Domains.Str, Object> intern1 = o.intern.set(Fields.value, bv.onlyStr());
                Domains.Object o1 = new Domains.Object(extern, intern1, o.present.insert(Fields.length));
                bv1 = bv1.merge(bv2);
                store1 = store2.putObj(bv2.as.iterator().next(), o1);
            }
            else if (sort == Domains.DUndef || sort == Domains.DNull) {
                throw new RuntimeException("suppresses compiler warning; this case can't happen");
            }
        }
        
        return P.p(bv1, store1, sorts);
    }

    public static P2<Option<P3<Domains.BValue, Domains.Store, Domains.Scratchpad>>, Option<Domains.EValue>> toObj(Domains.BValue bv, IRVar x, Domains.Env env, Domains.Store store, Domains.Scratchpad pad, Trace trace) {
        // TODO
        return null;
    }

    public static P2<Option<P2<Domains.BValue, Domains.Store>>, Option<Domains.EValue>> updateObj(Domains.BValue bv1, Domains.BValue bv2, Domains.BValue bv3, Domains.Store store) {
        Domains.Str str = bv2.str;
        Boolean maybeLength = Fields.length.partialLessEqual(str);
        Boolean isStrong = bv1.as.size() == 1 && store.isStrong(bv1.as.iterator().next());
        Domains.BValue bv3num = bv3.toNum();
        Boolean maybeArray = bv1.as.filter(a -> store.getObj(a).getJSClass() == JSClass.CArray).size() > 0;
        Boolean rhsMaybeU32 = Domains.Num.maybeU32(bv3num);
        Boolean propertyMaybeU32 = Domains.Num.maybeU32(Domains.Num.inject(str.toNum()));

        Option<P2<Domains.BValue, Domains.Store>> noexc;
        if (isStrong) {
            Domains.Object o = store.getObj(bv1.as.iterator().next());
            Domains.Object o1;
            if (!maybeArray) {
                o1 = o.strongUpdate(str, bv3);
            }
            else if (!maybeLength) {
                o1 = o.strongUpdate(str, bv3).strongUpdate(Fields.length, Domains.Num.inject(Domains.Num.U32));
            }
            else if (str != Fields.length) {
                o1 = o.weakDelete(Domains.Str.U32).strongUpdate(str, bv3).strongUpdate(Fields.length, Domains.Num.inject(Domains.Num.U32));
            }
            else {
                o1 = o.weakDelete(Domains.Str.U32).strongUpdate(Fields.length, Domains.Num.inject(Domains.Num.U32));
            }
            Domains.Store store1 = store.putObjStrong(bv1.as.iterator().next(), o1);
            noexc = Option.some(P.p(bv3, store1));
        }
        else if (bv1.as.size() > 0 ) {
            Domains.Store store1 = store;
            for (Domains.AddressSpace.Address a : bv1.as) {
                Domains.Object o = store1.getObj(a);
                if (o.getJSClass() == JSClass.CArray) {
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
        if (bv1.nil == Domains.Null.Top || bv1.undef == Domains.Undef.Top) {
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
            Set<Domains.AddressSpace.Address> as = env.apply(x).orSome(Set.empty(Ord.<Domains.AddressSpace.Address>hashEqualsOrd()));
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
        else if (e instanceof IRBinop && ((IRBinop) e).op == Bop.Access) {
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
        if (bv.as.size() == 1 && store.isStrong(bv.as.iterator().next()) && Domains.Str.isExact(str)) {
            Domains.Object obj = store.getObj(bv.as.iterator().next());
            if (obj.defField(str)) {
                return Option.some(P.p(bv.as.iterator().next(), obj));
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
