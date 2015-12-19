package analysis;

import analysis.init.Init;
import analysis.Traces.Trace;
import com.sun.jndi.cosnaming.IiopUrl;
import fj.*;
import fj.data.*;
import fj.data.HashSet;
import fj.data.List;
import immutable.FHashMap;
import immutable.FHashSet;
import immutable.FVector;
import ir.*;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by wayne on 15/10/29.
 */
public class Domains {

    public static abstract class Term {}

    public static class StmtTerm extends Term {
        public IRStmt s;
        int recordHash;
        boolean calced;

        public StmtTerm(IRStmt s) {
            this.s = s;
            this.calced = false;
        }

        @Override
        public String toString() {
            return s.toString();
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof StmtTerm && s.equals(((StmtTerm) obj).s));
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = s.hashCode();
                calced = true;
                return recordHash;
            }
        }
    }

    public static class ValueTerm extends Term {
        public Value v;
        int recordHash;
        boolean calced;

        public ValueTerm(Value v) {
            this.v = v;
            this.calced = false;
        }

        @Override
        public String toString() {
            return v.toString();
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof ValueTerm && v.equals(((ValueTerm) obj).v));
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = v.hashCode();
                calced = true;
                return recordHash;
            }
        }
    }

    public static class Env {
        public FHashMap<IRPVar, FHashSet<AddressSpace.Address>> env;
        int recordHash;
        boolean calced;

        public Env(FHashMap<IRPVar, FHashSet<AddressSpace.Address>> env) {
            this.env = env;
            this.calced = false;
        }

        @Override
        public String toString() {
            return "Env(" + env.toString() + ")";
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Env && env.equals(((Env) obj).env));
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = env.hashCode();
                calced = true;
                return recordHash;
            }
        }

        public Env merge(Env rho) {
            if (this.equals(rho)) {
                return this;
            } else {
                ArrayList<P2<IRPVar, FHashSet<AddressSpace.Address>>> list = new ArrayList<>();
                assert env.keys().equals(rho.env.keys());
                for (Map.Entry<IRPVar, FHashSet<AddressSpace.Address>> entry : env) {
                    list.add(P.p(entry.getKey(), entry.getValue().union(rho.env.get(entry.getKey()).some())));
                }
                return new Env(FHashMap.build(list));
            }
        }

        public Option<FHashSet<AddressSpace.Address>> apply(IRPVar x) {
            return env.get(x);
        }

        public Env extendAll(List<P2<IRPVar, AddressSpace.Address>> bind) {
            List<P2<IRPVar, FHashSet<AddressSpace.Address>>> list = bind.map(p -> P.p(p._1(), AddressSpace.Addresses.apply(p._2())));
            return new Env(env.union(list));
        }

        public Env filter(F<IRPVar, Boolean> f) {
            return new Env(FHashMap.build(env.keys().filter(f).map(k -> P.p(k, env.get(k).some()))));
        }

        public FHashSet<AddressSpace.Address> addrs() {
            return env.values().foldLeft((acc, cur) -> acc.union(cur), FHashSet.empty());
        }
    }

    public static class Store {
        public FHashMap<AddressSpace.Address, BValue> toValue;
        public FHashMap<AddressSpace.Address, Object> toObject;
        public FHashMap<AddressSpace.Address, FHashSet<KontStack>> toKonts;
        public FHashSet<AddressSpace.Address> weak;
        int recordHash;
        boolean calced;
        static final Hash<P4<FHashMap<AddressSpace.Address, BValue>, FHashMap<AddressSpace.Address, Object>, FHashMap<AddressSpace.Address, FHashSet<KontStack>>, FHashSet<AddressSpace.Address>>> hash = Hash.p4Hash(Hash.anyHash(), Hash.anyHash(), Hash.anyHash(), Hash.anyHash());

        public Store(FHashMap<AddressSpace.Address, BValue> toValue, FHashMap<AddressSpace.Address, Object> toObject, FHashMap<AddressSpace.Address, FHashSet<KontStack>> toKonts, FHashSet<AddressSpace.Address> weak) {
            this.toValue = toValue;
            this.toObject = toObject;
            this.toKonts = toKonts;
            this.weak = weak;
            this.calced = false;
        }

        @Override
        public String toString() {
            return "Store(" + toValue.toString() + "; " + toObject.toString() + ")";
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            if (obj instanceof Store) {
                return (toValue.equals(((Store) obj).toValue) && toObject.equals(((Store) obj).toObject) && toKonts.equals(((Store) obj).toKonts) && weak.equals(((Store) obj).weak));
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = hash.hash(P.p(toValue, toObject, toKonts, weak));
                calced = true;
                return recordHash;
            }
        }

        public Store merge(Store sigma) {
            FHashMap<AddressSpace.Address, BValue> _toValue;
            if (toValue.equals(sigma.toValue)) {
                _toValue = toValue;
            } else {
                _toValue = sigma.toValue.union(
                        toValue.keys().map(a -> {
                            Option<BValue> bv = sigma.toValue.get(a);
                            if (bv.isSome()) {
                                return P.p(a, toValue.get(a).some().merge(bv.some()));
                            } else {
                                return P.p(a, toValue.get(a).some());
                            }
                        })
                );
            }
            FHashMap<AddressSpace.Address, Object> _toObject;
            if (toObject.equals(sigma.toObject)) {
                _toObject = toObject;
            } else {
                _toObject = sigma.toObject.union(
                        toObject.keys().map(a -> {
                            Option<Object> o = sigma.toObject.get(a);
                            if (o.isSome()) {
                                return P.p(a, toObject.get(a).some().merge(o.some()));
                            } else {
                                return P.p(a, toObject.get(a).some());
                            }
                        })
                );
            }
            FHashMap<AddressSpace.Address, FHashSet<KontStack>> _toKonts;
            if (toKonts.equals(sigma.toKonts)) {
                _toKonts = toKonts;
            } else {
                _toKonts = sigma.toKonts.union(
                        toKonts.keys().map(a -> {
                            Option<FHashSet<KontStack>> ks = sigma.toKonts.get(a);
                            if (ks.isSome()) {
                                return P.p(a, toKonts.get(a).some().union(ks.some()));
                            } else {
                                return P.p(a, toKonts.get(a).some());
                            }
                        })
                );
            }
            return new Store(_toValue, _toObject, _toKonts, weak.union(sigma.weak));
        }

        public Store alloc(List<P2<AddressSpace.Address, BValue>> bind) {
            assert bind.isNotEmpty();
            P2<List<P2<AddressSpace.Address, BValue>>, List<P2<AddressSpace.Address, BValue>>> par = bind.partition(p -> toValue.contains(p._1()));
            List<P2<AddressSpace.Address, BValue>> bindw = par._1();
            List<P2<AddressSpace.Address, BValue>> bindn = par._2();
            List<P2<AddressSpace.Address, BValue>> wToValue = bindw.map(p -> P.p(p._1(), toValue.get(p._1()).some().merge(p._2())));
            return new Store(toValue.union(bindn).union(wToValue), toObject, toKonts, weak.union(FHashSet.build(List.unzip(bindw)._1())));
        }

        public Store alloc(AddressSpace.Address a, Object o) {
            Option<Object> oo = toObject.get(a);
            Object cod;
            FHashSet<AddressSpace.Address> wk;
            if (oo.isSome()) {
                cod = oo.some().merge(o);
                wk = weak.insert(a);
            } else {
                cod = o;
                wk = weak;
            }
            return new Store(toValue, toObject.set(a, cod), toKonts, wk);
        }

        public Store alloc(AddressSpace.Address a, KontStack ks) {
            Option<FHashSet<KontStack>> kss = toKonts.get(a);
            FHashSet<KontStack> cod;
            if (kss.isSome()) {
                cod = kss.some().insert(ks);
            } else {
                cod = FHashSet.build(ks);
            }
            return new Store(toValue, toObject, toKonts.set(a, cod), weak);
        }

        public BValue applyAll(FHashSet<AddressSpace.Address> as) {
            assert !as.isEmpty();
            if (as.size() == 1) {
                return apply(as.toList().head());
            } else {
                return as.toList().map(this::apply).foldLeft1(BValue::merge);
            }
        }

        public BValue apply(AddressSpace.Address a) {
            Option<BValue> obv = toValue.get(a);
            if (obv.isSome()) {
                return obv.some();
            } else {
                throw new RuntimeException("ccc: address not found");
            }
        }

        public Object getObj(AddressSpace.Address a) {
            Option<Object> oo = toObject.get(a);
            if (oo.isSome()) {
                return oo.some();
            } else {
                throw new RuntimeException("ccc: address not found");
            }
        }

        public FHashSet<KontStack> getKont(AddressSpace.Address a) {
            return toKonts.get(a).some();
        }

        public Store extend(FHashSet<AddressSpace.Address> as, BValue bv) {
            FHashMap<AddressSpace.Address, BValue> _toValue;
            if (as.size() == 1 && !weak.member(as.toList().head())) {
                _toValue = toValue.set(as.toList().head(), bv);
            } else {
                _toValue = as.toList().foldLeft((acc, a) -> acc.set(a, bv.merge(toValue.get(a).some())), toValue);
            }
            return new Store(_toValue, toObject, toKonts, weak);
        }

        public Store putObj(AddressSpace.Address a, Object o) {
            if (weak.member(a)) {
                return putObjWeak(a, o);
            } else {
                return putObjStrong(a, o);
            }
        }

        public Store putObjStrong(AddressSpace.Address a, Object o) {
            return new Store(toValue, toObject.set(a, o), toKonts, weak);
        }

        public Store putObjWeak(AddressSpace.Address a, Object o) {
            return new Store(toValue, toObject.set(a, o.merge(toObject.get(a).some())), toKonts, weak);
        }

        public Boolean isStrong(AddressSpace.Address a) {
            return !(weak.member(a));
        }

        public Boolean toValueContains(FHashSet<AddressSpace.Address> as) {
            return !as.filter(a -> toValue.contains(a)).isEmpty();
        }

        public Store weaken(FHashSet<Integer> valIDs, FHashSet<Integer> objIDs) {
            FHashSet<AddressSpace.Address> wkn = FHashSet.build(
                    toValue.keys().filter(a -> valIDs.member(Trace.getBase(a))).append(
                    toObject.keys().filter(a -> objIDs.member(Trace.getBase(a)))));
            return new Store(toValue, toObject, toKonts, weak.union(wkn));
        }

        public Store lightGC(FHashSet<Integer> ids) {
            FHashMap<AddressSpace.Address, BValue> toKeep = FHashMap.build(
                    toValue.keys().filter(a -> !ids.member(Trace.getBase(a)) || weak.member(a)).map(a -> P.p(a, toValue.get(a).some())));
            return new Store(toKeep, toObject, toKonts, weak);
        }

        public Store fullGC(FHashSet<AddressSpace.Address> vRoots, FHashSet<AddressSpace.Address> oRoots, FHashSet<AddressSpace.Address> kRoots) {
            HashSet<AddressSpace.Address> todoV = new HashSet<AddressSpace.Address>(Equal.anyEqual(), Hash.anyHash());
            for (AddressSpace.Address a : vRoots) {
                todoV.set(a);
            }
            HashSet<AddressSpace.Address> doneV = new HashSet<AddressSpace.Address>(Equal.anyEqual(), Hash.anyHash());
            HashSet<AddressSpace.Address> todoO = new HashSet<AddressSpace.Address>(Equal.anyEqual(), Hash.anyHash());
            for (AddressSpace.Address a : oRoots) {
                todoO.set(a);
            }
            HashSet<AddressSpace.Address> doneO = new HashSet<AddressSpace.Address>(Equal.anyEqual(), Hash.anyHash());
            HashSet<AddressSpace.Address> todoK = new HashSet<AddressSpace.Address>(Equal.anyEqual(), Hash.anyHash());
            for (AddressSpace.Address a : kRoots) {
                todoK.set(a);
            }
            HashSet<AddressSpace.Address> doneK = new HashSet<AddressSpace.Address>(Equal.anyEqual(), Hash.anyHash());
            FHashSet<AddressSpace.Address> empty = FHashSet.empty();
            while (!todoK.isEmpty()) {
                AddressSpace.Address a = todoK.iterator().next();
                todoK.delete(a);
                doneK.set(a);
                FHashSet<AddressSpace.Address> vas, oas, kas;
                Option<FHashSet<KontStack>> tmp = toKonts.get(a);
                if (tmp.isSome()) {
                    FHashSet<KontStack> kss = tmp.some();
                    P3<FHashSet<AddressSpace.Address>, FHashSet<AddressSpace.Address>, FHashSet<AddressSpace.Address>> p3;
                    p3 = kss.foldLeft(
                            (acc, ks)-> ks.ks.foldLeft(
                                    (P3<FHashSet<AddressSpace.Address>, FHashSet<AddressSpace.Address>, FHashSet<AddressSpace.Address>> nacc, Kont kont) -> {
                                        if (kont instanceof AddrKont) {
                                            AddressSpace.Address na = ((AddrKont)kont).a;
                                            return P.p(nacc._1(), nacc._2(), nacc._3().insert(na));
                                        } else if (kont instanceof RetKont) {
                                            Domains.Env nenv = ((RetKont)kont).env;
                                            Traces.Trace ntr = ((RetKont)kont).trace;
                                            FHashSet<AddressSpace.Address> noas;
                                            if (Interpreter.Mutable.pruneStore) {
                                                noas = Interpreter.PruneStoreToo.apply(ntr)._2().addrs();
                                            } else {
                                                noas = Interpreter.PruneScratch.apply(ntr).addrs();
                                            }
                                            return P.p(nacc._1().union(nenv.addrs()), nacc._2().union(noas), nacc._3());
                                        } else if (kont instanceof FinKont) {
                                            FHashSet<Value> vs = ((FinKont)kont).vs;
                                            return vs.foldLeft(
                                                    (P3<FHashSet<AddressSpace.Address>, FHashSet<AddressSpace.Address>, FHashSet<AddressSpace.Address>> nnacc, Value v)-> {
                                                        BValue bv;
                                                        if (v instanceof BValue) {
                                                            bv = (BValue)v;
                                                        } else if (v instanceof EValue) {
                                                            bv = ((EValue)v).bv;
                                                        } else {
                                                            bv = ((JValue)v).bv;
                                                        }
                                                        return P.p(nnacc._1(), nnacc._2().union(bv.as), nnacc._3());
                                                    }, nacc);
                                        } else {
                                            return nacc;
                                        }
                                    },  acc),
                            P.p(empty, empty, empty));
                    vas = p3._1();
                    oas = p3._2();
                    kas = p3._3();
                } else {
                    if (Interpreter.Mutable.pruneStore) {
                        vas = oas = kas = empty;
                    } else {
                        throw new RuntimeException("dangling address in store");
                    }
                }
                for (AddressSpace.Address addr : vas) {
                    todoV.set(addr);
                }
                for (AddressSpace.Address addr : oas) {
                    todoO.set(addr);
                }
                for (AddressSpace.Address addr : kas) {
                    if (!doneK.contains(addr)) {
                        todoK.set(addr);
                    }
                }
            }
            FHashMap<AddressSpace.Address, FHashSet<KontStack>> _toKonts = FHashMap.empty();
            for (AddressSpace.Address a : toKonts.keys()) {
                if (doneK.contains(a)) {
                    _toKonts = _toKonts.set(a, toKonts.get(a).some());
                }
            }
            while (!todoV.isEmpty() || !todoO.isEmpty()) {
                if (Interpreter.Mutable.pruneStore) {
                    for (AddressSpace.Address addr : todoV) {
                        if (!toValue.contains(addr)) {
                            todoV.delete(addr);
                        }
                    }
                }
//                todoO = todoO.union(todoV.foldLeft((acc, a)-> acc.union(toValue.get(a).some().as), empty)).minus(doneO);
                for (AddressSpace.Address addr : todoV) {
                    for (AddressSpace.Address i : toValue.get(addr).some().as) {
                        if (!doneO.contains(i)) {
                            todoO.set(i);
                        }
                    }
                }
/*
                for (AddressSpace.Address addr : todoO) {
                    if (doneO.contains(addr)) {
                        todoO.delete(addr);
                    }
                }
*/
                for (AddressSpace.Address addr : todoV) {
                    doneV.set(addr);
                }
                todoV.clear();
                while (!todoO.isEmpty()) {
                    AddressSpace.Address a = todoO.iterator().next();
                    todoO.delete(a);
                    doneO.set(a);
                    if (toObject.get(a).isSome()) {
                        Object o = toObject.get(a).some();
                        FHashSet<BValue> bvs = o.getAllValues();
                        for (BValue bv : bvs) {
                            for (AddressSpace.Address addr : bv.as) {
                                if (!doneO.contains(addr)) {
                                    todoO.set(addr);
                                }
                            }
                        }
/*
                        todoV = todoV.union(o.getCode().foldLeft(
                                (acc, clo)-> {
                                    if (clo instanceof Clo) {
                                        Env env = ((Clo)clo).env;
                                        return acc.union(env.addrs());
                                    } else {
                                        return acc;
                                    }
                                }, empty
                        ).minus(doneV));
*/
                        for (Closure clo : o.getCode()) {
                            if (clo instanceof  Clo) {
                                Env env = ((Clo)clo).env;
                                for (AddressSpace.Address addr : env.addrs()) {
                                    if (!doneV.contains(addr)) {
                                        todoV.set(addr);
                                    }
                                }
                            }
                        }
/*
                        for (AddressSpace.Address addr : todoV) {
                            if (doneV.contains(addr)) {
                                todoV.delete(addr);
                            }
                        }
*/
                    } else {
                        if (!Interpreter.Mutable.pruneStore) {
                            throw new RuntimeException("dangling address in store");
                        }
                    }
                }
            }
            FHashMap<AddressSpace.Address, BValue> _toValue = FHashMap.empty();
            for (AddressSpace.Address a : toValue.keys()) {
                if (doneV.contains(a)) {
                    _toValue = _toValue.set(a, toValue.get(a).some());
                }
            }
            FHashMap<AddressSpace.Address, Object> _toObject = FHashMap.empty();
            for (AddressSpace.Address a : toObject.keys()) {
                if (doneO.contains(a)) {
                    _toObject = _toObject.set(a, toObject.get(a).some());
                }
            }
            FHashSet<AddressSpace.Address> done = FHashSet.empty();
            for (AddressSpace.Address addr : doneV) {
                done.insert(addr);
            }
            for (AddressSpace.Address addr : doneO) {
                done.insert(addr);
            }
            return new Store(_toValue, _toObject, _toKonts, weak.intersect(done));
        }

        public P2<Store, Store> prune(FHashSet<AddressSpace.Address> vRoots, FHashSet<AddressSpace.Address> oRoots) {
            HashSet<AddressSpace.Address> todoV = new HashSet<AddressSpace.Address>(Equal.anyEqual(), Hash.anyHash());
            for (AddressSpace.Address a : vRoots) {
                todoV.set(a);
            }
            HashSet<AddressSpace.Address> doneV = new HashSet<AddressSpace.Address>(Equal.anyEqual(), Hash.anyHash());
            HashSet<AddressSpace.Address> todoO = new HashSet<AddressSpace.Address>(Equal.anyEqual(), Hash.anyHash());
            for (AddressSpace.Address a : oRoots) {
                todoO.set(a);
            }
            HashSet<AddressSpace.Address> doneO = new HashSet<AddressSpace.Address>(Equal.anyEqual(), Hash.anyHash());
            FHashSet<AddressSpace.Address> empty = FHashSet.empty();

            while (!todoV.isEmpty() || !todoO.isEmpty()) {
                for (AddressSpace.Address a : todoV) {
                    for (AddressSpace.Address a1 : apply(a).as) {
                        if (!doneO.contains(a1)) {
                            todoO.set(a1);
                        }
                    }
                }
                for (AddressSpace.Address a : todoV) {
                    doneV.set(a);
                }
                todoV.clear();
                while (!todoO.isEmpty()) {
                    AddressSpace.Address a = todoO.iterator().next();
                    todoO.delete(a);
                    doneO.set(a);
                    Object o = getObj(a);
                    FHashSet<BValue> bvs = o.getAllValues();
                    for (BValue bv : bvs) {
                        for (AddressSpace.Address na : bv.as) {
                            if (!doneO.contains(na)) {
                                todoO.set(na);
                            }
                        }
                    }
                    for (Closure clo : o.getCode()) {
                        if (clo instanceof Clo) {
                            Env env = ((Clo)clo).env;
                            for (AddressSpace.Address na : env.addrs()) {
                                if (!doneV.contains(na)) {
                                    todoV.set(na);
                                }
                            }
                        }
                    }
                }
            }
            FHashMap<AddressSpace.Address, BValue> reachToValue = FHashMap.empty(), unreachToValue = FHashMap.empty();
            for (AddressSpace.Address a : toValue.keys()) {
                if (doneV.contains(a)) {
                    reachToValue = reachToValue.set(a, toValue.get(a).some());
                } else {
                    unreachToValue = unreachToValue.set(a, toValue.get(a).some());
                }
            }
            FHashMap<AddressSpace.Address, Object> reachToObject = FHashMap.empty(), unreachToObject = FHashMap.empty();
            for (AddressSpace.Address a : toObject.keys()) {
                if (doneO.contains(a)) {
                    reachToObject = reachToObject.set(a, toObject.get(a).some());
                } else {
                    unreachToObject= unreachToObject.set(a, toObject.get(a).some());
                }
            }
            FHashMap<AddressSpace.Address, FHashSet<KontStack>> reachToKonts = FHashMap.empty(), unreachToKonts = toKonts;
            FHashSet<AddressSpace.Address> reachWeak = FHashSet.empty(), unreachWeak = FHashSet.empty();
            for (AddressSpace.Address a : weak) {
                if (doneV.contains(a) || doneO.contains(a)) {
                    reachWeak = reachWeak.insert(a);
                } else {
                    unreachWeak = unreachWeak.insert(a);
                }
            }
            return P.p(new Store(reachToValue, reachToObject, reachToKonts, reachWeak), new Store(unreachToValue, unreachToObject, unreachToKonts, unreachWeak));
        }
    }

    public static class Scratchpad {
        public FVector<BValue> mem;
        int recordHash;
        boolean calced;

        public Scratchpad(FVector<BValue> mem) {
            this.mem = mem;
            this.calced = false;
        }

        @Override
        public String toString() {
            return "Pad(" + mem + ")";
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Scratchpad && mem.equals(((Scratchpad) obj).mem));
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = mem.hashCode();
                calced = true;
                return recordHash;
            }
        }

        public Scratchpad merge(Scratchpad pad) {
            assert mem.length() == pad.mem.length();
            if (this.equals(pad)) {
                return this;
            } else {
                ArrayList<BValue> bvs = new ArrayList<>();
                for (int i = 0; i < mem.length(); i += 1) {
                    bvs.add(mem.index(i).merge(pad.mem.index(i)));
                }
                return new Scratchpad(FVector.build(bvs));
            }
        }

        public BValue apply(IRScratch x) {
            return mem.index(x.n);
        }

        public Scratchpad update(IRScratch x, BValue bv) {
            return new Scratchpad(mem.update(x.n, bv));
        }

        public FHashSet<AddressSpace.Address> addrs() {
            return mem.foldLeft((sa, bv) -> sa.union(bv.as), FHashSet.empty());
        }

        public static Scratchpad apply(Integer len) {
            return new Scratchpad(FVector.build(len, Undef.BV));
        }
    }

    public static class Domain {}
    public static final Domain DNum = new Domain();
    public static final Domain DBool = new Domain();
    public static final Domain DStr = new Domain();
    public static final Domain DAddr = new Domain();
    public static final Domain DNull = new Domain();
    public static final Domain DUndef = new Domain();

    public static abstract class Value {}

    public static class EValue extends Value {
        public BValue bv;
        int recordHash;
        boolean calced;

        public EValue(BValue bv) {
            this.bv = bv;
            this.calced = false;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof EValue && bv.equals(((EValue) obj).bv));
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = bv.hashCode();
                calced = true;
                return recordHash;
            }
        }
    }

    public static class JValue extends Value {
        public String lbl;
        public BValue bv;
        int recordHash;
        boolean calced;
        static final Hash<P2<String, BValue>> hash = Hash.p2Hash(Hash.anyHash(), Hash.anyHash());

        public JValue(String lbl, BValue bv) {
            this.lbl = lbl;
            this.bv = bv;
            this.calced = false;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof JValue && lbl.equals(((JValue) obj).lbl) && bv.equals(((JValue) obj).bv));
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = hash.hash(P.p(lbl, bv));
                calced = true;
                return recordHash;
            }
        }
    }

    public static class BValue extends Value {
        public Num n;
        public Bool b;
        public Str str;
        public FHashSet<AddressSpace.Address> as;
        public Null nil;
        public Undef undef;
        int recordHash;
        boolean calced;
        static final Hash<P6<Num, Bool, Str, FHashSet<AddressSpace.Address>, Null, Undef>> hash = Hash.p6Hash(Hash.anyHash(), Hash.anyHash(), Hash.anyHash(), Hash.anyHash(), Hash.anyHash(), Hash.anyHash());

        FHashSet<Domain> types;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("<");
            builder.append(n);
            builder.append(",");
            builder.append(b);
            builder.append(",");
            builder.append(str);
            builder.append(",");
            builder.append(as);
            builder.append(",");
            builder.append(nil);
            builder.append(",");
            builder.append(undef);
            builder.append(">");
            return builder.toString();
        }

        public BValue(Num n, Bool b, Str str, FHashSet<AddressSpace.Address> as, Null nil, Undef undef) {
            this.n = n;
            this.b = b;
            this.str = str;
            this.as = as;
            this.nil = nil;
            this.undef = undef;
            this.calced = false;

            this.sorts = FHashSet.<Domain>empty().
                    union(n.equals(Num.Bot) ? FHashSet.empty() : FHashSet.build(DNum)).
                    union(b.equals(Bool.Bot) ? FHashSet.empty() : FHashSet.build(DBool)).
                    union(str.equals(Str.Bot) ? FHashSet.empty() : FHashSet.build(DStr)).
                    union(as.isEmpty() ? FHashSet.empty() : FHashSet.build(DAddr)).
                    union(nil.equals(Null.Bot) ? FHashSet.empty() : FHashSet.build(DNull)).
                    union(undef.equals(Undef.Bot) ? FHashSet.empty() : FHashSet.build(DUndef));

            ArrayList<Domain> doms = new ArrayList<>();
            if (!n.equals(Num.Bot)) doms.add(DNum);
            if (!b.equals(Bool.Bot)) doms.add(DBool);
            if (!str.equals(Str.Bot)) doms.add(DStr);
            if (!as.isEmpty()) doms.add(DAddr);
            if (!nil.equals(Null.Bot)) doms.add(DNull);
            if (!undef.equals(Undef.Bot)) doms.add(DUndef);
            this.types = FHashSet.build(doms);
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            if (obj instanceof BValue) {
                BValue bv = (BValue)obj;
                return (n.equals(bv.n) && b.equals(bv.b) && str.equals(bv.str) && as.equals(bv.as) && nil.equals(bv.nil) && undef.equals(bv.undef));
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = hash.hash(P.p(n, b, str, as, nil, undef));
                calced = true;
                return recordHash;
            }
        }

        public BValue merge(BValue bv) {
            return new BValue(
                    n.merge(bv.n),
                    b.merge(bv.b),
                    str.merge(bv.str),
                    as.union(bv.as),
                    nil.merge(bv.nil),
                    undef.merge(bv.undef));
        }

        public BValue plus(BValue bv) {
            return Num.inject(n.plus(bv.n));
        }

        public BValue minus(BValue bv) {
            return Num.inject(n.minus(bv.n));
        }

        public BValue times(BValue bv) {
            return Num.inject(n.times(bv.n));
        }

        public BValue divide(BValue bv) {
            return Num.inject(n.divide(bv.n));
        }

        public BValue mod(BValue bv) {
            return Num.inject(n.mod(bv.n));
        }

        public BValue shl(BValue bv) {
            return Num.inject(n.shl(bv.n));
        }

        public BValue sar(BValue bv) {
            return Num.inject(n.sar(bv.n));
        }

        public BValue shr(BValue bv) {
            return Num.inject(n.shr(bv.n));
        }

        public BValue lessThan(BValue bv) {
            return Bool.inject(n.lessThan(bv.n));
        }

        public BValue lessEqual(BValue bv) {
            return Bool.inject(n.lessEqual(bv.n));
        }

        public BValue and(BValue bv) {
            return Num.inject(n.and(bv.n));
        }

        public BValue or(BValue bv) {
            return Num.inject(n.or(bv.n));
        }

        public BValue xor(BValue bv) {
            return Num.inject(n.xor(bv.n));
        }

        public BValue logicalAnd(BValue bv) {
            return Bool.inject(b.logicalAnd(bv.b));
        }

        public BValue logicalOr(BValue bv) {
            return Bool.inject(b.logicalOr(bv.b));
        }

        public BValue strConcat(BValue bv) {
            return Str.inject(str.strConcat(bv.str));
        }

        public BValue strLessThan(BValue bv) {
            return Bool.inject(str.strLessThan(bv.str));
        }

        public BValue strLessEqual(BValue bv) {
            return Bool.inject(str.strLessEqual(bv.str));
        }

        public BValue negate() {
            return Num.inject(n.negate());
        }

        public BValue not() {
            return Num.inject(n.not());
        }

        public BValue logicalNot() {
            return Bool.inject(b.logicalNot());
        }

        public BValue isPrim() {
            Bool notaddr = Bool.alpha(!types.member(DAddr));
            Bool hasprim = Bool.alpha(!defAddr());
            return Bool.inject(notaddr.merge(hasprim));
        }

        public BValue toBool() {
            Bool res = Bool.Bot;
            for (Domain dom : types) {
                if (!res.equals(Bool.Top)) {
                    Bool b1;
                    if (dom.equals(DNum)) {
                        if (n.defNaN() || n.def0()) {
                            b1 = Bool.False;
                        } else if (n.defNotNaN() && n.defNot0()) {
                            b1 = Bool.True;
                        } else {
                            b1 = Bool.Top;
                        }
                    } else if (dom.equals(DBool)) {
                        b1 = b;
                    } else if (dom.equals(DStr)) {
                        if (str.defEmpty()) {
                            b1 = Bool.False;
                        } else if (str.defNotEmpty()) {
                            b1 = Bool.True;
                        } else {
                            b1 = Bool.Top;
                        }
                    } else if (dom.equals(DAddr)) {
                        b1 = Bool.True;
                    } else {
                        b1 = Bool.False;
                    }
                    res = res.merge(b1);
                }
            }
            return Bool.inject(res);
        }

        public BValue toStr() {
            Str res = Str.Bot;
            for (Domain dom : types) {
                if (!res.equals(Str.Top)) {
                    Str str1;
                    if (dom.equals(DNum)) {
                        str1 = n.toStr();
                    } else if (dom.equals(DBool)) {
                        str1 = b.toStr();
                    } else if (dom.equals(DStr)) {
                        str1 = str;
                    } else if (dom.equals(DNull)) {
                        str1 = Str.alpha("null");
                    } else if (dom.equals(DUndef)) {
                        str1 = Str.alpha("undefined");
                    } else {
                        str1 = Str.Top;
                    }
                    res = res.merge(str1);
                }
            }
            return Str.inject(res);
        }

        public BValue toNum() {
            Num res = Num.Bot;
            for (Domain dom : types) {
                if (!res.equals(Num.Top)) {
                    Num n1;
                    if (dom.equals(DNum)) {
                        n1 = n;
                    } else if (dom.equals(DBool)) {
                        n1 = b.toNum();
                    } else if (dom.equals(DStr)) {
                        n1 = str.toNum();
                    } else if (dom.equals(DNull)) {
                        n1 = Num.alpha(0.0);
                    } else if (dom.equals(DUndef)) {
                        n1 = Num.alpha(Double.NaN);
                    } else {
                        n1 = Num.Top;
                    }
                    res = res.merge(n1);
                }
            }
            return Num.inject(res);
        }

        public final FHashSet<Domain> sorts;

        public Boolean isBot() {
            return types.isEmpty();
        }

        public Boolean defNum() {
            return (types.size() == 1 && types.member(DNum));
        }

        public Boolean defBool() {
            return (types.size() == 1 && types.member(DBool));
        }

        public Boolean defStr() {
            return (types.size() == 1 && types.member(DStr));
        }

        public Boolean defAddr() {
            return (types.size() == 1 && types.member(DAddr));
        }

        public Boolean defNull() {
            return (types.size() == 1 && types.member(DNull));
        }

        public Boolean defUndef() {
            return (types.size() == 1 && types.member(DUndef));
        }

        public BValue onlyNum() {
            return Num.inject(n);
        }

        public BValue onlyBool() {
            return Bool.inject(b);
        }

        public BValue onlyStr() {
            return Str.inject(str);
        }

        public BValue onlyAddr() {
            return AddressSpace.Addresses.inject(as);
        }

        public BValue removeNullAndUndef() {
            return new BValue(n, b, str, as, Null.Bot, Undef.Bot);
        }

        public P2<BValue, BValue> filterBy(Utils.Filters.BVFilter bvf, Store store) {
            if (bvf.equals(Utils.Filters.IsFunc)) {
                return P.p(onlyAddr(), this);
            }
            else if (bvf.equals(Utils.Filters.IsUndefNull)) {
                return P.p(removeNullAndUndef(), this);
            }
            throw new RuntimeException("implementation error");
        }

        public static final BValue Bot = new BValue(Num.Bot, Bool.Bot, Str.Bot, FHashSet.empty(), Null.Bot, Undef.Bot);
    }

    public static abstract class Num {
        public Num merge(Num n) {
            if (this.equals(n)) {
                return this;
            } else if (this.equals(NBot)) {
                return n;
            } else if (n.equals(NBot)) {
                return this;
            } else if (this.equals(NTop) || n.equals(NTop)) {
                return Top;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return Top;
            } else if (this.equals(Inf) || n.equals(Inf)) {
                return Top;
            } else if (this.equals(NInf) || n.equals(NInf)) {
                return Top;
            } else {
                return NReal;
            }
        }

        public Bool strictEqual(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Bool.Bot;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return Bool.False;
            } else if (this.equals(NTop) || n.equals(NTop)) {
                return Bool.Top;
            } else if (this instanceof NConst && n instanceof NConst) {
                return Bool.alpha(((NConst)this).d.equals(((NConst)n).d));
            } else if (this.equals(Inf) && n.equals(NReal)) {
                return Bool.False;
            } else if (this.equals(NReal) && n.equals(Inf)) {
                return Bool.False;
            } else if (this.equals(NInf) && n.equals(NReal)) {
                return Bool.False;
            } else if (this.equals(NReal) && n.equals(NInf)) {
                return Bool.False;
            } else {
                return Bool.Top;
            }
        }

        public Num plus(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Bot;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return NaN;
            } else if (this.equals(NTop) || n.equals(NTop)) {
                return Top;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst(((NConst)this).d + ((NConst)n).d);
            } else if (this.equals(NReal) && n.equals(Inf)) {
                return Inf;
            } else if (this.equals(Inf) && n.equals(NReal)) {
                return Inf;
            } else if (this.equals(NReal) && n.equals(NInf)) {
                return NInf;
            } else if (this.equals(NInf) && n.equals(NReal)) {
                return NInf;
            } else {
                return NReal;
            }
        }

        public Num minus(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Bot;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return NaN;
            } else if (this.equals(NTop) || n.equals(NTop)) {
                return Top;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst(((NConst)this).d - ((NConst)n).d);
            } else if (this.equals(NReal) && n.equals(Inf)) {
                return NInf;
            } else if (this.equals(NInf) && n.equals(NReal)) {
                return NInf;
            } else if (this.equals(NReal) && n.equals(NInf)) {
                return Inf;
            } else if (this.equals(Inf) && n.equals(NReal)) {
                return Inf;
            } else {
                return NReal;
            }
        }

        public Num times(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Bot;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return NaN;
            } else if (this.equals(NTop) || n.equals(NTop)) {
                return Top;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst(((NConst)this).d * ((NConst)n).d);
            } else if ((this.equals(NReal) && n.equals(Inf)) || (this.equals(Inf) && n.equals(NReal))) {
                return Top;
            } else if ((this.equals(NReal) && n.equals(NInf) || (this.equals(NInf) && n.equals(NReal)))) {
                return Top;
            } else {
                return NReal;
            }
        }

        public Num divide(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Bot;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return NaN;
            } else if (this.equals(NTop) || n.equals(NTop)) {
                return Top;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst(((NConst)this).d / ((NConst)n).d);
            } else if (this.equals(NReal) && (n.equals(Inf) || n.equals(NInf))) {
                return new NConst(0.0);
            } else {
                return Top;
            }
        }

        public Num mod(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Bot;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return NaN;
            } else if (this.equals(NTop) || n.equals(NTop)) {
                return Top;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst(((NConst)this).d % ((NConst)n).d);
            } else if ((this.equals(Inf) || this.equals((NInf))) && (n.equals(NReal))) {
                return NaN;
            } else {
                return NReal;
            }
        }

        public Num shl(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Bot;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst((double)(((NConst)this).d.longValue() << ((NConst)n).d.longValue()));
            } else {
                return NReal;
            }
        }

        public Num sar(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Bot;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst((double)(((NConst)this).d.longValue() >> ((NConst)n).d.longValue()));
            } else {
                return NReal;
            }
        }

        public Num shr(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Bot;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst((double)(((NConst)this).d.longValue() >>> ((NConst)n).d.longValue()));
            } else {
                return NReal;
            }
        }

        public Bool lessThan(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Bool.Bot;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return Bool.False;
            } else if (this.equals(NTop) || n.equals(NTop)) {
                return Bool.Top;
            } else if (this instanceof NConst && n instanceof NConst) {
                return Bool.alpha(((NConst)this).d < ((NConst)n).d);
            } else if ((this.equals(NReal) && n.equals(Inf))
                    || (this.equals(NInf) && n.equals(NReal))) {
                return Bool.True;
            } else if ((this.equals(Inf) && n.equals(NReal))
                    || (this.equals(NReal) && n.equals(NInf))) {
                return Bool.False;
            } else {
                return Bool.Top;
            }
        }

        public Bool lessEqual(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Bool.Bot;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return Bool.False;
            } else if (this.equals(NTop) || n.equals(NTop)) {
                return Bool.Top;
            } else if (this instanceof NConst && n instanceof NConst) {
                return Bool.alpha(((NConst)this).d <= ((NConst)n).d);
            } else if ((this.equals(NReal) && n.equals(Inf))
                    || (this.equals(NInf) && n.equals(NReal))) {
                return Bool.True;
            } else if ((this.equals(Inf) && n.equals(NReal))
                    || (this.equals(NReal) && n.equals(NInf))) {
                return Bool.False;
            } else {
                return Bool.Top;
            }
        }

        public Num and(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Num.Bot;
            } else if (this.equals(NaN) || n.equals((NaN))) {
                return Num.Zero;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst((double)(((NConst)this).d.longValue() & ((NConst)n).d.longValue()));
            } else {
                return NReal;
            }
        }

        public Num or(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Num.Bot;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst((double)(((NConst)this).d.longValue() | ((NConst)n).d.longValue()));
            } else {
                return NReal;
            }
        }

        public Num xor(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Num.Bot;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst((double)(((NConst)this).d.longValue() ^ ((NConst)n).d.longValue()));
            } else {
                return NReal;
            }
        }

        public Num negate() {
            if (this instanceof NConst) {
                return new NConst(-((NConst)this).d);
            } else {
                return this;
            }
        }

        public Num not() {
            if (this instanceof NConst) {
                return new NConst((double)(~((NConst)this).d.longValue()));
            } else {
                return this;
            }
        }

        public Str toStr() {
            if (this instanceof NConst) {
                Double d = ((NConst)this).d;
                if (d.longValue() == d) {
                    return Str.alpha(Long.toString(d.longValue()));
                } else {
                    return Str.alpha(d.toString());
                }
            } else if (this.equals(NTop)) {
                return Str.Top;
            } else if (this.equals(NReal)) {
                return Str.NumStr;
            } else {
                return Str.Bot;
            }
        }

        public Boolean defNaN() {
            return this.equals(NaN);
        }

        public Boolean defNotNaN() {
            return (!this.equals(NaN) && !this.equals(Top));
        }

        public Boolean def0() {
            return this.equals(Zero);
        }

        public Boolean defNot0() {
            return (this.equals(Bot) || (this instanceof NConst && ((NConst)this).d != 0));
        }

        public static final Num NTop = new Num() {
            @Override
            public String toString() {
                return "Top";
            }
        };
        public static final Num NBot = new Num() {
            @Override
            public String toString() {
                return "Bot";
            }
        };
        public static final Num NReal = new Num() {
            @Override
            public String toString() {
                return "Real";
            }
        };

        public static final Num Top = NTop;
        public static final Num Bot = NBot;
        public static final Num Zero = new NConst(0.0);
        public static final Num NaN = new NConst(Double.NaN);
        public static final Num Inf = new NConst(Double.POSITIVE_INFINITY);
        public static final Num NInf = new NConst(Double.NEGATIVE_INFINITY);
        public static final Num U32 = NReal;
        public static final Long maxU32 = 4294967295L;

        public static Num alpha(Double d) {
            return new NConst(d);
        }

        public static Boolean maybeU32(BValue bv) {
            Num n = bv.n;
            if (n.equals(NTop) || n.equals(NReal)) {
                return true;
            } else if (n instanceof NConst) {
                Double d = ((NConst) n).d;
                return (d.longValue() == d && d >= 0 && d <= maxU32);
            } else {
                return false;
            }
        }

        public static Boolean maybeNotU32(BValue bv) {
            if (!bv.defNum()) {
                return true;
            } else {
                Num n = bv.n;
                if (n instanceof NConst) {
                    Double d = ((NConst) n).d;
                    return !(d.longValue() == d && d >= 0 && d <= maxU32);
                } else {
                    return true;
                }
            }
        }

        public static BValue inject(Num n) {
            return new BValue(n, Bool.Bot, Str.Bot, FHashSet.empty(), Null.Bot, Undef.Bot);
        }
    }

    public static class NConst extends Num {
        public Double d;
        int recordHash;
        boolean calced;

        public NConst(Double d) {
            this.d = d;
            this.calced = false;
        }

        @Override
        public String toString() {
            return d.toString();
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            if (obj instanceof NConst) {
                return (d.equals(((NConst) obj).d) || (d.isNaN() && ((NConst) obj).d.isNaN()));
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = d.hashCode();
                calced = true;
                return recordHash;
            }
        }
    }

    public static abstract class Bool {
        public Bool merge(Bool b) {
            if (this.equals(b)) {
                return this;
            } else if (this.equals(BTop) || b.equals(BTop) || (this.equals(BTrue) && b.equals(BFalse)) || (this.equals(BFalse) && b.equals(BTrue))) {
                return Top;
            } else if (this.equals(BBot)) {
                return b;
            } else if (b.equals(BBot)) {
                return this;
            } else {
                throw new RuntimeException("suppress false compiler warning");
            }
        }

        public Bool strictEqual(Bool b) {
            if (this.equals(BBot) || b.equals(BBot)) {
                return Bot;
            } else if (this.equals(BTop) || b.equals(BTop)) {
                return Top;
            } else if ((this.equals(BTrue) && b.equals(BTrue))
                    || (this.equals(BFalse) && b.equals(BFalse))) {
                return True;
            } else {
                return False;
            }
        }

        public Bool logicalAnd(Bool b) {
            if (this.equals(BBot) || b.equals(BBot)) {
                return Bot;
            } else if (this.equals(BFalse) || b.equals(BFalse)) {
                return False;
            } else if (this.equals(BTop) || b.equals(BTop)) {
                return Top;
            } else {
                return True;
            }
        }

        public Bool logicalOr(Bool b) {
            if (this.equals(BBot) || b.equals(BBot)) {
                return Bot;
            } else if (this.equals(BTrue) || b.equals(BTrue)) {
                return True;
            } else if (this.equals(BTop) || b.equals(BTop)) {
                return Top;
            } else {
                return False;
            }
        }

        public Bool logicalNot() {
            if (this.equals(BBot)) {
                return Bot;
            } else if (this.equals(BTrue)) {
                return False;
            } else if (this.equals(BFalse)) {
                return True;
            } else {
                return Top;
            }
        }

        public Num toNum() {
            if (this.equals(BBot)) {
                return Num.Bot;
            } else if (this.equals(BTrue)) {
                return Num.alpha(1.0);
            } else if (this.equals(BFalse)) {
                return Num.alpha(0.0);
            } else {
                return Num.Top;
            }
        }

        public Str toStr() {
            if (this.equals(BBot)) {
                return Str.Bot;
            } else if (this.equals(BTrue)) {
                return Str.alpha("true");
            } else if (this.equals(BFalse)) {
                return Str.alpha("false");
            } else {
                return Str.Top;
            }
        }

        public static final Bool BBot = new Bool() {
            @Override
            public String toString() {
                return "Bot";
            }
        };
        public static final Bool BTop = new Bool() {
            @Override
            public String toString() {
                return "Top";
            }
        };
        public static final Bool BTrue = new Bool() {
            @Override
            public String toString() {
                return "True";
            }
        };
        public static final Bool BFalse = new Bool() {
            @Override
            public String toString() {
                return "False";
            }
        };

        public static final Bool Top = BTop;
        public static final Bool Bot = BBot;
        public static final Bool True = BTrue;
        public static final Bool False = BFalse;
        public static final BValue TrueBV = inject(True);
        public static final BValue FalseBV = inject(False);
        public static final BValue TopBV = inject(Top);

        public static Bool alpha(Boolean b) {
            if (b) {
                return True;
            } else {
                return False;
            }
        }

        public static BValue inject(Bool b) {
            return new BValue(Num.Bot, b, Str.Bot, FHashSet.empty(), Null.Bot, Undef.Bot);
        }
    }

    public static abstract class Str {
        public Str merge(Str str) {
            if (this.equals(str)) {
                return this;
            } else if (this.equals(SBot)) {
                return str;
            } else if (str.equals(SBot)) {
                return this;
            } else if (this instanceof SConstNum && str instanceof SConstNum) {
                return SNum;
            } else if ((this instanceof SConstNum && str instanceof SConstNotSplNorNum)
                    || (this instanceof SConstNotSplNorNum && str instanceof SConstNum)) {
                return SNotSpl;
            } else if ((this instanceof SConstNum && str instanceof SConstSpl)
                    || (this instanceof SConstSpl && str instanceof SConstNum)) {
                return Top;
            } else if ((this instanceof SConstNum && str.equals(SNotSplNorNum))
                    || (this.equals(SNotSplNorNum) && str instanceof SConstNum)) {
                return SNotSpl;
            } else if ((this instanceof SConstNum && str.equals(SSpl))
                    || (this.equals(SSpl) && str instanceof SConstNum)) {
                return Top;
            } else if ((this instanceof SConstNum && str.equals(SNotNum))
                    || (this.equals(SNotNum) && str instanceof SConstNum)) {
                return Top;
            } else if (this instanceof SConstNotSplNorNum && str instanceof SConstNotSplNorNum) {
                return SNotSplNorNum;
            } else if ((this instanceof SConstNotSplNorNum && str instanceof SConstSpl)
                    || (this instanceof SConstSpl && str instanceof SConstNotSplNorNum)) {
                return SNotNum;
            } else if ((this instanceof SConstNotSplNorNum && str.equals(SNum))
                    || (this.equals(SNum) && str instanceof SConstNotSplNorNum)) {
                return SNotSpl;
            } else if ((this instanceof SConstNotSplNorNum && str.equals(SSpl))
                    || (this.equals(SSpl) && str instanceof SConstNotSplNorNum)) {
                return SNotNum;
            } else if (this instanceof SConstSpl && str instanceof SConstSpl) {
                return SSpl;
            } else if ((this instanceof SConstSpl && str.equals(SNum))
                    || (this.equals(SNum) && str instanceof SConstSpl)) {
                return Top;
            } else if ((this instanceof SConstSpl && str.equals(SNotSplNorNum))
                    || (this.equals(SNotSplNorNum) && str instanceof SConstSpl)) {
                return SNotNum;
            } else if ((this instanceof SConstSpl && str.equals(SNotSpl))
                    || (this.equals(SNotSpl) && str instanceof SConstSpl)) {
                return Top;
            } else if ((this.equals(SNum) && str.equals(SNotSplNorNum))
                    || (this.equals(SNotSplNorNum) && str.equals(SNum))) {
                return SNotSpl;
            } else if ((this.equals(SNum) && str.equals(SSpl))
                    || (this.equals(SSpl) && str.equals(SNum))) {
                return Top;
            } else if ((this.equals(SNum) && str.equals(SNotNum))
                    || (this.equals(SNotNum) && str.equals(SNum))) {
                return Top;
            } else if ((this.equals(SNotSplNorNum) && str.equals(SSpl))
                    || (this.equals(SNotSpl) && str.equals(SNotSplNorNum))) {
                return SNotNum;
            } else if ((this.equals(SSpl) && str.equals(SNotSpl))
                    || (this.equals(SNotSpl) && str.equals(SSpl))) {
                return Top;
            } else if ((this.equals(SNotNum) && str.equals(SNotSpl))
                    || (this.equals(SNotSpl) && str.equals(SNotNum))) {
                return Top;
            } else if (this.partialLessEqual(str)) {
                return str;
            } else if (str.partialLessEqual(this)) {
                return this;
            } else {
                throw new RuntimeException("Incorrect implementation of string lattice");
            }
        }

        public Boolean partialLessEqual(Str str) {
            if (this.equals(SBot) || str.equals(STop)) {
                return true;
            } else if (this.equals(str)) {
                return true;
            } else if ((this instanceof SConstNum && str.equals(SNum))
                    || (this instanceof SConstNum && str.equals(SNotSpl))
                    || (this instanceof SConstNotSplNorNum && str.equals(SNotSplNorNum))
                    || (this instanceof SConstNotSplNorNum && str.equals(SNotSpl))
                    || (this instanceof SConstNotSplNorNum && str.equals(SNotNum))
                    || (this instanceof SConstSpl && str.equals(SSpl))
                    || (this instanceof SConstSpl && str.equals(SNotNum))
                    || (this.equals(SNum) && str.equals(SNotSpl))
                    || (this.equals(SSpl) && str.equals(SNotNum))
                    || (this.equals(SNotSplNorNum) && str.equals(SNotNum))
                    || (this.equals(SNotSplNorNum) && str.equals(SNotSpl))) {
                return true;
            } else {
                return false;
            }
        }

        public Boolean notPartialLessEqual(Str str) {
            return !(this.partialLessEqual(str));
        }

        public Bool strictEqual(Str str) {
            if (this instanceof SConstNum && str instanceof SConstNum) {
                return Bool.alpha(((SConstNum)this).str.equals(((SConstNum)str).str));
            } else if (this instanceof SConstNotSplNorNum && str instanceof SConstNotSplNorNum) {
                return Bool.alpha(((SConstNotSplNorNum)this).str.equals(((SConstNotSplNorNum)str).str));
            } else if (this instanceof SConstSpl && str instanceof SConstSpl) {
                return Bool.alpha(((SConstSpl)this).str.equals(((SConstSpl)str).str));
            } else if ((this.equals(SNotSpl) && str.equals(SNotNum))
                    || (this.equals(SNotNum) && str.equals(SNotSpl))) {
                return Bool.Top;
            } else if (this.notPartialLessEqual(str) && str.notPartialLessEqual(this)) {
                return Bool.False;
            } else if (this.equals(SBot) || str.equals(SBot)) {
                return Bool.Bot;
            } else {
                return Bool.Top;
            }
        }

        public Str strConcat(Str str) {
            if (this.equals(SBot) || str.equals(SBot)) {
                return Bot;
            } else if (this.equals(STop) && str instanceof SConstNum) {
                return SNotSpl;
            } else if (this.equals(STop) && str instanceof SConstSpl) {
                return SNotNum;
            } else if (this.equals(STop) && str.equals(SNum)) {
                return SNotSpl;
            } else if (this.equals(STop) && str.equals(SSpl)) {
                return SNotNum;
            } else if (this.equals(STop)) {
                return Top;
            } else if (this.equals(Str.Empty)) {
                return str;
            } else if (str.equals(Str.Empty)) {
                return this;
            } else if (Str.isExact(this) && Str.isExact(str)) {
                return Str.alpha(Str.getExact(this).some() + Str.getExact(str).some());
            } else if (this instanceof SConstNum) {
                if (str.equals(SNum)) {
                    return SNotSpl;
                } else if (str.equals(SNotSplNorNum)) {
                    return SNotSpl;
                } else if (str.equals(SSpl)) {
                    return SNotSpl;
                } else if (str.equals(SNotSpl)) {
                    return SNotSpl;
                } else if (str.equals(SNotNum)) {
                    return SNotSpl;
                } else if (str.equals(STop)) {
                    return SNotSpl;
                }
            } else if (this instanceof SConstNotSplNorNum) {
                if (str.equals(SNum)) {
                    return SNotSpl;
                } else if (str.equals(SNotSplNorNum)) {
                    return SNotNum;
                } else if (str.equals(SSpl)) {
                    return SNotNum;
                } else if (str.equals(SNotSpl)) {
                    return Top;
                } else if (str.equals(SNotNum)) {
                    return SNotNum;
                } else if (str.equals(STop)) {
                    return Top;
                }
            } else if (this instanceof SConstSpl) {
                if (str.equals(SNum)) {
                    return SNotSpl;
                } else if (str.equals(SNotSplNorNum)) {
                    return SNotNum;
                } else if (str.equals(SSpl)) {
                    return SNotSplNorNum;
                } else if (str.equals(SNotSpl)) {
                    return SNotNum;
                } else if (str.equals(SNotNum)) {
                    return SNotNum;
                } else if (str.equals(STop)) {
                    return SNotNum;
                }
            } else if (this.equals(SNum)) {
                if (str instanceof SConstNum) {
                    return SNotSpl;
                } else if (str instanceof SConstNotSplNorNum) {
                    return SNotSpl;
                } else if (str instanceof SConstSpl) {
                    return SNotSplNorNum;
                } else if (str.equals(SNum)) {
                    return SNotSpl;
                } else if (str.equals(SNotSplNorNum)) {
                    return SNotSpl;
                } else if (str.equals(SSpl)) {
                    return SNotSplNorNum;
                } else if (str.equals(SNotSpl)) {
                    return SNotSpl;
                } else if (str.equals(SNotNum)) {
                    return SNotSpl;
                } else if (str.equals(STop)) {
                    return SNotSpl;
                }
            } else if (this.equals(SNotSplNorNum)) {
                if (str instanceof SConstNum) {
                    return SNotSpl;
                } else if (str instanceof SConstNotSplNorNum) {
                    return SNotNum;
                } else if (str instanceof SConstSpl) {
                    return SNotNum;
                } else if (str.equals(SNum)) {
                    return SNotSpl;
                } else if (str.equals(SNotSplNorNum)) {
                    return SNotNum;
                } else if (str.equals(SSpl)) {
                    return SNotNum;
                } else if (str.equals(SNotSpl)) {
                    return Top;
                } else if (str.equals(SNotNum)) {
                    return SNotNum;
                } else if (str.equals(STop)) {
                    return Top;
                }
            } else if (this.equals(SSpl)) {
                if (str instanceof SConstNum) {
                    return SNotSplNorNum;
                } else if (str instanceof SConstNotSplNorNum) {
                    return SNotNum;
                } else if (str instanceof SConstSpl) {
                    return SNotSplNorNum;
                } else if (str.equals(SNum)) {
                    return SNotSplNorNum;
                } else if (str.equals(SNotSplNorNum)) {
                    return SNotNum;
                } else if (str.equals(SSpl)) {
                    return SNotSplNorNum;
                } else if (str.equals(SNotSpl)) {
                    return SNotNum;
                } else if (str.equals(SNotNum)) {
                    return SNotNum;
                } else if (str.equals(STop)) {
                    return SNotNum;
                }
            } else if (this.equals(SNotSpl)) {
                if (str instanceof SConstNum) {
                    return SNotSpl;
                } else if (str instanceof SConstNotSplNorNum) {
                    return Top;
                } else if (str instanceof SConstSpl) {
                    return SNotNum;
                } else if (str.equals(SNum)) {
                    return SNotSpl;
                } else if (str.equals(SNotSplNorNum)) {
                    return Top;
                } else if (str.equals(SSpl)) {
                    return SNotNum;
                } else if (str.equals(SNotSpl)) {
                    return Top;
                } else if (str.equals(SNotNum)) {
                    return Top;
                } else if (str.equals(STop)) {
                    return Top;
                }
            } else if (this.equals(SNotNum)) {
                if (str instanceof SConstNum) {
                    return SNotSpl;
                } else if (str instanceof SConstNotSplNorNum) {
                    return SNotNum;
                } else if (str instanceof SConstSpl) {
                    return SNotNum;
                } else if (str.equals(SNum)) {
                    return SNotSpl;
                } else if (str.equals(SNotSplNorNum)) {
                    return SNotNum;
                } else if (str.equals(SSpl)) {
                    return SNotNum;
                } else if (str.equals(SNotSpl)) {
                    return Top;
                } else if (str.equals(SNotNum)) {
                    return SNotNum;
                } else if (str.equals(STop)) {
                    return Top;
                }
            }
            throw new RuntimeException("incorrect implementation of string lattice");
        }

        public Bool strLessThan(Str str) {
            if (this.equals(SBot) || str.equals(SBot)) {
                return Bool.Bot;
            } else if (Str.isExact(this) && Str.isExact(str)) {
                return Bool.alpha(Str.getExact(this).some().compareTo(Str.getExact(str).some()) < 0);
            } else if ((this instanceof SConstNum && str instanceof SConstSpl)
                    || (this instanceof SConstNum && str.equals(SSpl))
                    || (this.equals(SNum) && str instanceof SConstSpl)
                    || (this.equals(SNum) && str.equals(SSpl))) {
                return Bool.True;
            } else if ((this instanceof SConstSpl && str instanceof SConstNum)
                    || (this instanceof SConstSpl && str.equals(SNum))
                    || (this.equals(SSpl) && str instanceof SConstNum)
                    || (this.equals(SSpl) && str.equals(SNum))) {
                return Bool.False;
            } else {
                return Bool.Top;
            }
        }

        public Bool strLessEqual(Str str) {
            if (this.equals(SBot) || str.equals(SBot)) {
                return Bool.Bot;
            } else if (Str.isExact(this) && Str.isExact(str)) {
                return Bool.alpha(Str.getExact(this).some().compareTo(Str.getExact(str).some()) <= 0);
            } else if ((this instanceof SConstNum && str instanceof SConstSpl)
                    || (this instanceof SConstNum && str.equals(SSpl))
                    || (this.equals(SNum) && str instanceof SConstSpl)
                    || (this.equals(SNum) && str.equals(SSpl))) {
                return Bool.True;
            } else if ((this instanceof SConstSpl && str instanceof SConstNum)
                    || (this instanceof SConstSpl && str.equals(SNum))
                    || (this.equals(SSpl) && str instanceof SConstNum)
                    || (this.equals(SSpl) && str.equals(SNum))) {
                return Bool.False;
            } else {
                return Bool.Top;
            }
        }

        public Num toNum() {
            if (this.equals(SBot)) {
                return Num.Bot;
            } else if (this.equals(SNotNum) || this.equals(SSpl) || this.equals(SNotSplNorNum)) {
                return Num.NaN;
            } else if (this instanceof SConstNotSplNorNum || this instanceof SConstSpl) {
                return Num.NaN;
            } else if (this instanceof SConstNum) {
                return Num.alpha(Double.valueOf(((SConstNum)this).str));
            } else {
                return Num.Top;
            }
        }

        public Boolean defEmpty() {
            return this.equals(Empty);
        }

        public Boolean defNotEmpty() {
            return (this.equals(SBot) || this.equals(SNum) || this instanceof SConstNum ||
                    this instanceof SConstSpl || this.equals(SSpl) ||
                    (this instanceof SConstNotSplNorNum && !((SConstNotSplNorNum)this).str.isEmpty()));
        }

        public static final Str STop = new Str() {
            @Override
            public String toString() {
                return "Top";
            }
        };
        public static final Str SBot = new Str() {
            @Override
            public String toString() {
                return "Bot";
            }
        };
        public static final Str SNum = new Str() {
            @Override
            public String toString() {
                return "Num";
            }
        };
        public static final Str SNotNum = new Str() {
            @Override
            public String toString() {
                return "NotNum";
            }
        };
        public static final Str SSpl = new Str() {
            @Override
            public String toString() {
                return "Spl";
            }
        };
        public static final Str SNotSplNorNum = new Str() {
            @Override
            public String toString() {
                return "NotSplNorNum";
            }
        };
        public static final Str SNotSpl = new Str() {
            @Override
            public String toString() {
                return "NotSpl";
            }
        };

        public static final Str Top = STop;
        public static final Str Bot = SBot;
        public static final Str U32 = SNum;
        public static final Str NumStr = SNum;
        public static final Str Empty = new SConstNotSplNorNum("");
        public static final Str SingleChar = SNotSpl;
        public static final Str DateStr = SNotSplNorNum;
        public static final Str FunctionStr = SNotSplNorNum;
        public static final FHashSet<String> SplStrings = FHashSet.build(
                "valueOf",
                "toString",
                "length",
                "constructor",
                "toLocaleString",
                "hasOwnProperty",
                "isPrototypeOf",
                "propertyIsEnumerable",
                "concat",
                "indexOf",
                "join",
                "lastIndexOf",
                "pop",
                "push",
                "reverse",
                "shift",
                "sort",
                "splice"
        );

        public static Str alpha(String str) {
            if (isNum(str)) {
                return new SConstNum(str);
            } else if (isSpl(str)) {
                return new SConstSpl(str);
            } else {
                return new SConstNotSplNorNum(str);
            }
        }

        public static BValue inject(Str str) {
            return new BValue(Num.Bot, Bool.Bot, str, FHashSet.empty(), Null.Bot, Undef.Bot);
        }

        public static Boolean isExact(Str str) {
            return (str instanceof SConstSpl || str instanceof SConstNum || str instanceof SConstNotSplNorNum);
        }

        public static Option<String> getExact(Str str) {
            if (str instanceof SConstSpl) {
                return Option.some(((SConstSpl) str).str);
            } else if (str instanceof SConstNum) {
                return Option.some(((SConstNum) str).str);
            } else if (str instanceof SConstNotSplNorNum) {
                return Option.some(((SConstNotSplNorNum) str).str);
            } else {
                return Option.none();
            }
        }

        public static Boolean isNum(String str) {
            try {
                Double d = Double.valueOf(str);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        public static Boolean isSpl(String str) {
            return SplStrings.member(str);
        }

        public static Boolean isExactNum(Str str) {
            return (str instanceof SConstNum);
        }

        public static Boolean isExactNotNum(Str str) {
            return (str instanceof SConstSpl || str instanceof SConstNotSplNorNum);
        }

        public static FHashSet<Str> minimize(FHashSet<Str> strs) {
            assert !strs.member(Bot);
            if (strs.member(Top)) {
                return FHashSet.build(Top);
            } else {
                boolean hasSNum = strs.member(SNum);
                boolean hasSNotSpl = strs.member(SNotSpl);
                boolean hasSNotSplNorNum = strs.member(SNotSplNorNum);
                boolean hasSSpl = strs.member(SSpl);
                boolean hasSNotNum = strs.member(SNotNum);

                return strs.toList().foldLeft((acc, str) -> {
                    if (str instanceof SConstNum && (hasSNum || hasSNotSpl)) {
                        return acc;
                    } else if (str instanceof SConstNotSplNorNum && (hasSNotSplNorNum || hasSNotSpl || hasSNotNum)) {
                        return acc;
                    } else if (str instanceof SConstSpl && (hasSSpl || hasSNotNum)) {
                        return acc;
                    } else if (str.equals(SNum) && (hasSNotSpl)) {
                        return acc;
                    } else if (str.equals(SNotSplNorNum) && (hasSNotSpl || hasSNotNum)) {
                        return acc;
                    } else if (str.equals(SSpl) && (hasSNotNum)) {
                        return acc;
                    } else if (str.equals(SBot)) {
                        return acc;
                    } else {
                        return acc.insert(str);
                    }
                }, FHashSet.empty());
            }
        }
    }

    public static class SConstNum extends Str {
        public String str;
        int recordHash;
        boolean calced;

        public SConstNum(String str) {
            this.str = str;
            this.calced = false;
        }

        @Override
        public String toString() {
            return "\"" + str + "\"";
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof SConstNum && ((SConstNum) obj).str.equals(str));
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = str.hashCode();
                calced = true;
                return recordHash;
            }
        }
    }

    public static class SConstNotSplNorNum extends Str {
        public String str;
        int recordHash;
        boolean calced;

        public SConstNotSplNorNum(String str) {
            this.str = str;
            this.calced = false;
        }

        @Override
        public String toString() {
            return "\"" + str + "\"";
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof SConstNotSplNorNum && ((SConstNotSplNorNum) obj).str.equals(str));
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = str.hashCode();
                calced = true;
                return recordHash;
            }
        }
    }

    public static class SConstSpl extends Str {
        public String str;
        int recordHash;
        boolean calced;

        @Override
        public String toString() {
            return "\"" + str + "\"";
        }

        public SConstSpl(String str) {
            this.str = str;
            this.calced = false;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof SConstSpl && ((SConstSpl) obj).str.equals(str));
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = str.hashCode();
                calced = true;
                return recordHash;
            }
        }
    }

    public static class AddressSpace {

        public static class Address {
            public BigInteger loc;
            int recordHash;
            boolean calced;

            public Address(BigInteger loc) {
                this.loc = loc;
                this.calced = false;
            }

            @Override
            public String toString() {
                return "ADDR@" + loc;
            }

            @Override
            public boolean equals(java.lang.Object obj) {
                return (obj instanceof Address && loc.equals(((Address) obj).loc));
            }

            @Override
            public int hashCode() {
                if (calced) {
                    return recordHash;
                } else {
                    recordHash = loc.hashCode();
                    calced = true;
                    return recordHash;
                }
            }

            public static Address apply(Integer x) {
                return new Address(BigInteger.valueOf(x));
            }

            public static BValue inject(Address a) {
                return new BValue(Num.Bot, Bool.Bot, Str.Bot, FHashSet.build(a), Null.Bot, Undef.Bot);
            }
        }

        public static class Addresses {

            public static FHashSet<Address> apply() {
                return FHashSet.empty();
            }
            public static FHashSet<Address> apply(Address a) {
                return FHashSet.build(a);
            }

            public static BValue inject(FHashSet<Address> as) {
                return new BValue(Num.Bot, Bool.Bot, Str.Bot, as, Null.Bot, Undef.Bot);
            }
        }
    }

    public static abstract class Null {
        public Null merge(Null nil) {
            if (this.equals(MaybeNull) || nil.equals(MaybeNull)) {
                return Top;
            } else {
                return Bot;
            }
        }

        public static final Null MaybeNull = new Null() {
            @Override
            public String toString() {
                return "MaybeNull";
            }
        };
        public static final Null NotNull = new Null() {
            @Override
            public String toString() {
                return "NotNull";
            }
        };

        public static final Null Top = MaybeNull;
        public static final Null Bot = NotNull;

        public static final BValue BV = new BValue(Num.Bot, Bool.Bot, Str.Bot, FHashSet.empty(), Top, Undef.Bot);
    }

    public static abstract class Undef {
        public Undef merge(Undef undef) {
            if (this.equals(MaybeUndef) || undef.equals(MaybeUndef)) {
                return Top;
            } else {
                return Bot;
            }
        }

        public static final Undef MaybeUndef = new Undef() {
            @Override
            public String toString() {
                return "MaybeUndef";
            }
        };
        public static final Undef NotUndef = new Undef() {
            @Override
            public String toString() {
                return "NotUndef";
            }
        };

        public static final Undef Top = MaybeUndef;
        public static final Undef Bot = NotUndef;

        public static final BValue BV = new BValue(Num.Bot, Bool.Bot, Str.Bot, FHashSet.empty(), Null.Bot, Top);
    }

    public static abstract class Closure {}

    public static class Clo extends Closure {
        public Env env;
        public IRMethod m;
        int recordHash;
        boolean calced;
        static final Hash<P2<Env, IRMethod>> hash = Hash.p2Hash(Hash.anyHash(), Hash.anyHash());

        public Clo(Env env, IRMethod m) {
            this.env = env;
            this.m = m;
            this.calced = false;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Clo && env.equals(((Clo) obj).env) && m.equals(((Clo) obj).m));
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = hash.hash(P.p(env, m));
                calced = true;
                return recordHash;
            }
        }
    }

    public static class Native extends Closure {
        public F8<BValue, BValue, IRVar, Env, Store, Scratchpad, KontStack, Trace, FHashSet<Interpreter.State>> f;

        int hash;

        public Native(F8<BValue, BValue, IRVar, Env, Store, Scratchpad, KontStack, Trace, FHashSet<Interpreter.State>> f) {
            this.f = f;
            this.hash = Native.freshHash();
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Native && f.equals(((Native) obj).f));
        }

        @Override
        public int hashCode() {
            return hash;
        }

        private static int hashCounter = 0;
        public static int freshHash() {
            int retval = hashCounter;
            hashCounter += 1;
            return retval;
        }
    }

    public static class Object {
        public ExternMap extern;
        public FHashMap<Str, java.lang.Object> intern;
        public FHashSet<Str> present;
        int recordHash;
        boolean calced;
        static final Hash<P3<ExternMap, FHashMap<Str, java.lang.Object>, FHashSet<Str>>> hash = Hash.p3Hash(Hash.anyHash(), Hash.anyHash(), Hash.anyHash());

        JSClass myClass;
        BValue myProto;

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Object && extern.equals(((Object) obj).extern) && intern.equals(((Object) obj).intern) && present.equals(((Object) obj).present));
        }

        @Override
        public String toString() {
            return "Obj(" + extern + ")";
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = hash.hash(P.p(extern, intern, present));
                calced = true;
                return recordHash;
            }
        }

        public Object(ExternMap extern, FHashMap<Str, java.lang.Object> intern, FHashSet<Str> present) {
            this.extern = extern;
            this.intern = intern;
            this.present = present;
            this.calced = false;
            myClass = (JSClass)intern.get(Utils.Fields.classname).some();
            myProto = (BValue)intern.get(Utils.Fields.proto).some();
        }

        public Object merge(Object o) {
            if (this.equals(o)) {
                return this;
            } else {
                assert myClass.equals(o.myClass);

                ExternMap extern1 = extern.merge(o.extern);
                FHashSet<Str> present1 = present.intersect(o.present);

                FHashMap<Str, java.lang.Object> intern1 = FHashMap.build(o.intern.keys().map(k -> {
                    if (k.equals(Utils.Fields.code)) {
                        FHashSet<Closure> me = (FHashSet<Closure>)intern.get(Utils.Fields.code).some();
                        FHashSet<Closure> that = (FHashSet<Closure>)o.intern.get(k).some();
                        return P.p(k, me.union(that));
                    } else if (k.equals(Utils.Fields.classname)) {
                        assert o.intern.get(k).some().equals(myClass);
                        return P.p(k, myClass);
                    } else if (k.equals(Utils.Fields.constructor)) {
                        return P.p(k, true);
                    } else {
                        BValue me;
                        if (intern.contains(k)) {
                            me = (BValue)intern.get(k).some();
                        } else {
                            me = BValue.Bot;
                        }
                        BValue that = (BValue)o.intern.get(k).some();
                        return P.p(k, me.merge(that));
                    }
                }));

                FHashMap<Str, java.lang.Object> intern2;
                if (intern.contains(Utils.Fields.constructor)) {
                    intern2 = intern1.set(Utils.Fields.constructor, true);
                } else {
                    intern2 = intern1;
                }

                return new Object(extern1, intern2, present1);
            }
        }

        public Option<BValue> apply(Str str) {
            return extern.apply(str);
        }

        public Object strongUpdate(Str str, BValue bv) {
            if (Str.isExact(str)) {
                if (Init.noupdate.get(myClass).orSome(FHashSet.empty()).member(str)) {
                    return this;
                } else {
                    return new Object(extern.strongUpdate(str, bv), intern, present.insert(str));
                }
            } else {
                return new Object(extern.weakUpdate(str, bv), intern, present);
            }
        }

        public Object weakUpdate(Str str, BValue bv) {
            if (Str.isExact(str)) {
                if (Init.noupdate.get(myClass).orSome(FHashSet.empty()).member(str)) {
                    return this;
                } else {
                    return new Object(extern.weakUpdate(str, bv), intern, present);
                }
            } else {
                return new Object(extern.weakUpdate(str, bv), intern, present);
            }
        }

        public Object strongDelete(Str str) {
            assert present.member(str) && !(Init.nodelete.get(myClass).orSome(FHashSet.empty()).member(str));
            return new Object(extern.delete(str), intern, present.delete(str));
        }

        public Object weakDelete(Str str) {
            if (Str.isExact(str)) {
                if (!(Init.nodelete.get(myClass).orSome(FHashSet.empty()).member(str))) {
                    return new Object(extern, intern, present.delete(str));
                } else {
                    return this;
                }
            } else {
                FHashSet<Str> le = extern.exactLE(str).minus(Init.nodelete.get(myClass).orSome(FHashSet.empty()));
                return new Object(extern, intern, present.minus(le));
            }
        }

        public FHashSet<Str> fields() {
            return extern.reducedKeys().minus(Init.noenum.get(myClass).orSome(FHashSet.empty()));
        }

        public JSClass getJSClass() {
            return myClass;
        }

        public BValue getProto() {
            return myProto;
        }

        public BValue getValue() {
            return (BValue)intern.get(Utils.Fields.value).some();
        }

        public FHashSet<Closure> getCode() {
            Option<java.lang.Object> o = intern.get(Utils.Fields.code);
            if (o.isSome()) {
                return (FHashSet<Closure>)o.some();
            } else {
                return FHashSet.empty();
            }
        }

        public Boolean calledAsCtor() {
            return intern.contains(Utils.Fields.constructor);
        }

        public Boolean defField(Str str) {
            return present.member(str);
        }

        public Boolean defNotField(Str str) {
            return extern.notContains(str);
        }

        public FHashSet<BValue> getAllValues() {
            return extern.getAllValues().union(FHashSet.build(intern.values().filter(x -> x instanceof BValue).map(x -> (BValue)x)));
        }
    }

    public static class ExternMap {
        public Option<BValue> top;
        public Option<BValue> notnum;
        public Option<BValue> num;
        public FHashMap<Str, BValue> exactnotnum;
        public FHashMap<Str, BValue> exactnum;
        int recordHash;
        boolean calced;
        static final Hash<P5<Option<BValue>, Option<BValue>, Option<BValue>, FHashMap<Str, BValue>, FHashMap<Str, BValue>>> hash = Hash.p5Hash(Hash.anyHash(), Hash.anyHash(), Hash.anyHash(), Hash.anyHash(), Hash.anyHash());

        public ExternMap() {
            this.top = Option.none();
            this.notnum = Option.none();
            this.num = Option.none();
            this.exactnotnum = FHashMap.empty();
            this.exactnum = FHashMap.empty();
            this.calced = false;
        }

        public ExternMap(Option<BValue> top, Option<BValue> notnum, Option<BValue> num, FHashMap<Str, BValue> exactnotnum, FHashMap<Str, BValue> exactnum) {
            this.top = top;
            this.notnum = notnum;
            this.num = num;
            this.exactnotnum = exactnotnum;
            this.exactnum = exactnum;
            this.calced = false;
        }

        @Override
        public String toString() {
            return exactnotnum.toString();
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            if (obj instanceof ExternMap) {
                ExternMap em = (ExternMap)obj;
                return (top.equals(em.top) && notnum.equals(em.notnum) && num.equals(em.num) && exactnotnum.equals(em.exactnotnum) && exactnum.equals(em.exactnum));
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = hash.hash(P.p(top, notnum, num, exactnotnum, exactnum));
                calced = true;
                return recordHash;
            }
        }

        public ExternMap merge(ExternMap ext) {
            Option<BValue> top1;
            if (top.isSome() && ext.top.isSome()) {
                top1 = Option.some(top.some().merge(ext.top.some()));
            } else if (top.isSome()) {
                top1 = top;
            } else if (ext.top.isSome()) {
                top1 = ext.top;
            } else {
                top1 = Option.none();
            }

            Option<BValue> notnum1;
            if (notnum.isSome() && ext.notnum.isSome()) {
                notnum1 = Option.some(notnum.some().merge(ext.notnum.some()));
            } else if (notnum.isSome()) {
                notnum1 = notnum;
            } else if (ext.notnum.isSome()) {
                notnum1 = ext.notnum;
            } else {
                notnum1 = Option.none();
            }

            Option<BValue> num1;
            if (num.isSome() && ext.num.isSome()) {
                num1 = Option.some(num.some().merge(ext.num.some()));
            } else if (num.isSome()) {
                num1 = num;
            } else if (ext.num.isSome()) {
                num1 = ext.num;
            } else {
                num1 = Option.none();
            }

            FHashMap<Str, BValue> _exactnotnum;
            if (exactnotnum.equals(ext.exactnotnum)) {
                _exactnotnum = exactnotnum;
            } else {
                _exactnotnum = ext.exactnotnum.union(
                        exactnotnum.keys().map(k -> {
                            BValue bv = exactnotnum.get(k).some();
                            Option<BValue> bv1 = ext.exactnotnum.get(k);
                            if (bv1.isSome()) {
                                return P.p(k, bv.merge(bv1.some()));
                            } else {
                                return P.p(k, bv);
                            }
                        })
                );
            }

            FHashMap<Str, BValue> _exactnum;
            if (exactnum.equals(ext.exactnum)) {
                _exactnum = exactnum;
            } else {
                _exactnum = ext.exactnum.union(
                        exactnum.keys().map(k -> {
                            BValue bv = exactnum.get(k).some();
                            Option<BValue> bv1 = ext.exactnum.get(k);
                            if (bv1.isSome()) {
                                return P.p(k, bv.merge(bv1.some()));
                            } else {
                                return P.p(k, bv);
                            }
                        })
                );
            }

            return new ExternMap(top1, notnum1, num1, _exactnotnum, _exactnum);
        }

        public Option<BValue> apply(Str str) {
            List<BValue> splValues = exactnotnum.keys().filter(k -> Str.SplStrings.member(Str.getExact(k).some())).map(s -> exactnotnum.get(s).some());
            List<BValue> nonSplValues = exactnotnum.keys().filter(k -> !Str.SplStrings.member(Str.getExact(k).some())).map(s -> exactnotnum.get(s).some());

            List<BValue> bvs = List.nil();
            if (str instanceof SConstNotSplNorNum || str instanceof SConstSpl) {
                if (top.isSome()) bvs = bvs.snoc(top.some());
                if (notnum.isSome()) bvs = bvs.snoc(notnum.some());
                if (exactnotnum.contains(str)) bvs = bvs.snoc(exactnotnum.get(str).some());
            } else if (str instanceof SConstNum) {
                if (top.isSome()) bvs = bvs.snoc(top.some());
                if (num.isSome()) bvs = bvs.snoc(num.some());
                if (exactnum.contains(str)) bvs = bvs.snoc(exactnum.get(str).some());
            } else if (str.equals(Str.SNum)) {
                if (top.isSome()) bvs = bvs.snoc(top.some());
                if (num.isSome()) bvs = bvs.snoc(num.some());
                bvs = bvs.append(exactnum.values());
            } else if (str.equals(Str.SNotSplNorNum)) {
                if (top.isSome()) bvs = bvs.snoc(top.some());
                if (notnum.isSome()) bvs = bvs.snoc(notnum.some());
                bvs = bvs.append(nonSplValues);
            } else if (str.equals(Str.SSpl)) {
                if (top.isSome()) bvs = bvs.snoc(top.some());
                if (notnum.isSome()) bvs = bvs.snoc(notnum.some());
                bvs = bvs.append(splValues);
            } else if (str.equals(Str.SNotSpl)) {
                if (top.isSome()) bvs = bvs.snoc(top.some());
                if (num.isSome()) bvs = bvs.snoc(num.some());
                if (notnum.isSome()) bvs = bvs.snoc(notnum.some());
                bvs = bvs.append(exactnum.values());
                bvs = bvs.append(nonSplValues);
            } else if (str.equals(Str.SNotNum)) {
                if (top.isSome()) bvs = bvs.snoc(top.some());
                if (notnum.isSome()) bvs = bvs.snoc(notnum.some());
                bvs = bvs.append(exactnotnum.values());
            } else if (str.equals(Str.STop)) {
                if (top.isSome()) bvs = bvs.snoc(top.some());
                if (num.isSome()) bvs = bvs.snoc(num.some());
                if (notnum.isSome()) bvs = bvs.snoc(notnum.some());
                bvs = bvs.append(exactnotnum.values());
                bvs = bvs.append(exactnum.values());
            } else {
                throw new RuntimeException("used SBot with an object");
            }

            if (bvs.isEmpty()) {
                return Option.none();
            } else {
                return Option.some(bvs.foldLeft(BValue::merge, BValue.Bot));
            }
        }

        public ExternMap strongUpdate(Str str, BValue bv) {
            if (str instanceof SConstNotSplNorNum || str instanceof SConstSpl) {
                FHashMap<Str, BValue> exactnotnum1 = exactnotnum.set(str, bv);
                return new ExternMap(top, notnum, num, exactnotnum1, exactnum);
            } else if (str instanceof SConstNum) {
                FHashMap<Str, BValue> exactnum1 = exactnum.set(str, bv);
                return new ExternMap(top, notnum, num, exactnotnum, exactnum1);
            } else {
                throw new RuntimeException("strong updated with inexact string");
            }
        }

        public ExternMap weakUpdate(Str str, BValue bv) {
            if (str instanceof SConstNotSplNorNum || str instanceof SConstSpl) {
                Option<BValue> _bv = exactnotnum.get(str);
                BValue bv1;
                if (_bv.isSome()) {
                    bv1 = bv.merge(_bv.some());
                } else {
                    bv1 = bv;
                }
                return new ExternMap(top, notnum, num, exactnotnum.set(str, bv1), exactnum);
            } else if (str instanceof SConstNum) {
                Option<BValue> _bv = exactnum.get(str);
                BValue bv1;
                if (_bv.isSome()) {
                    bv1 = bv.merge(_bv.some());
                } else {
                    bv1 = bv;
                }
                return new ExternMap(top, notnum, num, exactnotnum, exactnum.set(str, bv1));
            } else if (str.equals(Str.SNum)) {
                Option<BValue> num1;
                if (num.isSome()) {
                    num1 = Option.some(num.some().merge(bv));
                } else {
                    num1 = Option.some(bv);
                }
                return new ExternMap(top, notnum, num1, exactnotnum, exactnum);
            } else if (str.equals(Str.SSpl) || str.equals(Str.SNotSplNorNum) || str.equals(Str.SNotNum)) {
                Option<BValue> notnum1;
                if (notnum.isSome()) {
                    notnum1 = Option.some(notnum.some().merge(bv));
                } else {
                    notnum1 = Option.some(bv);
                }
                return new ExternMap(top, notnum1, num, exactnotnum, exactnum);
            } else if (str.equals(Str.SNotSpl) || str.equals(Str.STop)) {
                Option<BValue> top1;
                if (top.isSome()) {
                    top1 = Option.some(top.some().merge(bv));
                } else {
                    top1 = Option.some(bv);
                }
                return new ExternMap(top1, notnum, num, exactnotnum, exactnum);
            } else {
                throw new RuntimeException("used SBot with an object");
            }
        }

        public ExternMap delete(Str str) {
            if (str instanceof SConstNotSplNorNum || str instanceof SConstSpl) {
                return new ExternMap(top, notnum, num, exactnotnum.delete(str), exactnum);
            } else if (str instanceof SConstNum) {
                return new ExternMap(top, notnum, num, exactnotnum, exactnum.delete(str));
            } else {
                throw new RuntimeException("tried to delete inexact string");
            }
        }

        public Boolean notContains(Str str) {
            List<BValue> splValues = exactnotnum.keys().filter(k -> Str.SplStrings.member(Str.getExact(k).some())).map(s -> exactnotnum.get(s).some());
            List<BValue> nonSplValues = exactnotnum.keys().filter(k -> !Str.SplStrings.member(Str.getExact(k).some())).map(s -> exactnotnum.get(s).some());

            if (str instanceof SConstNotSplNorNum || str instanceof SConstSpl) {
                return top.isNone() && notnum.isNone() && !(exactnotnum.contains(str));
            } else if (str instanceof SConstNum) {
                return top.isNone() && num.isNone() && !(exactnum.contains(str));
            } else if (str.equals(Str.SNotSplNorNum)) {
                return top.isNone() && notnum.isNone() && nonSplValues.isEmpty();
            } else if (str.equals(Str.SSpl)) {
                return top.isNone() && notnum.isNone() && splValues.isEmpty();
            } else if (str.equals(Str.SNum)) {
                return top.isNone() && num.isNone() && exactnum.isEmpty();
            } else if (str.equals(Str.SNotSpl)) {
                return top.isNone() && num.isNone() && notnum.isNone() && exactnum.isEmpty() && nonSplValues.isEmpty();
            } else if (str.equals(Str.SNotNum)) {
                return top.isNone() && notnum.isNone() && exactnotnum.isEmpty();
            } else if (str.equals(Str.STop)) {
                return top.isNone() && notnum.isNone() && num.isNone() && exactnotnum.isEmpty() && exactnum.isEmpty();
            } else {
                throw new RuntimeException("used SBot with an object");
            }
        }

        public FHashSet<Str> exactLE(Str str) {
            List<Str> nonSplKeys = exactnotnum.keys().filter(k -> !(Str.SplStrings.member(Str.getExact(k).some())));
            if (str instanceof SConstNotSplNorNum || str instanceof SConstSpl) {
                if (exactnotnum.contains(str)) {
                    return FHashSet.build(str);
                } else {
                    return FHashSet.empty();
                }
            } else if (str instanceof SConstNum) {
                if (exactnum.contains(str)) {
                    return FHashSet.build(str);
                } else {
                    return FHashSet.empty();
                }
            } else if (str.equals(Str.SNum)) {
                return FHashSet.build(exactnum.keys());
            } else if (str.equals(Str.SNotSplNorNum)) {
                return FHashSet.build(nonSplKeys);
            } else if (str.equals(Str.SSpl)) {
                return FHashSet.build(exactnotnum.keys().filter(k -> Str.SplStrings.member(Str.getExact(k).some())));
            } else if (str.equals(Str.SNotSpl)) {
                return FHashSet.build(exactnum.keys().append(nonSplKeys));
            } else if (str.equals(Str.SNotNum)) {
                return FHashSet.build(exactnotnum.keys());
            } else if (str.equals(Str.STop)) {
                return FHashSet.build(exactnotnum.keys().append(exactnum.keys()));
            } else {
                throw new RuntimeException("used SBot with an object");
            }
        }

        public FHashSet<Str> reducedKeys() {
            if (top.isSome()) {
                return FHashSet.build(Str.STop);
            } else {
                List<Str> l1 = notnum.isSome() ? List.list(Str.SNotNum) : exactnotnum.keys();
                List<Str> l2 = num.isSome() ? List.list(Str.SNum) : exactnum.keys();
                return FHashSet.build(l1.append(l2));
            }
        }

        public FHashSet<BValue> getAllValues() {
            List<BValue> list = List.list();
            if (top.isSome()) list = list.snoc(top.some());
            if (notnum.isSome()) list = list.snoc(notnum.some());
            if (num.isSome()) list = list.snoc(num.some());
            list = list.append(exactnotnum.values());
            list = list.append(exactnum.values());
            return FHashSet.build(list);
        }
    }

    public static abstract class Kont {}

    public static final Kont HaltKont = new Kont() {};

    public static class SeqKont extends Kont {
        public List<IRStmt> ss;
        int recordHash;
        boolean calced;

        public SeqKont(List<IRStmt> ss) {
            this.ss = ss;
            this.calced = false;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof SeqKont && ss.equals(((SeqKont) obj).ss));
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = ss.hashCode();
                calced = true;
                return recordHash;
            }
        }
    }

    public static class WhileKont extends Kont {
        public IRExp e;
        public IRStmt s;
        int recordHash;
        boolean calced;
        static final Hash<P2<IRExp, IRStmt>> hash = Hash.p2Hash(Hash.anyHash(), Hash.anyHash());

        public WhileKont(IRExp e, IRStmt s) {
            this.e = e;
            this.s = s;
            this.calced = false;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof WhileKont && e.equals(((WhileKont) obj).e) && s.equals(((WhileKont) obj).s));
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = hash.hash(P.p(e, s));
                calced = true;
                return recordHash;
            }
        }
    }

    public static class ForKont extends Kont {
        public BValue bv;
        public IRVar x;
        public IRStmt s;
        int recordHash;
        boolean calced;
        static final Hash<P3<BValue, IRVar, IRStmt>> hash = Hash.p3Hash(Hash.anyHash(), Hash.anyHash(), Hash.anyHash());

        public ForKont(BValue bv, IRVar x, IRStmt s) {
            this.bv = bv;
            this.x = x;
            this.s = s;
            this.calced = false;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof ForKont && bv.equals(((ForKont) obj).bv) && x.equals(((ForKont) obj).x) && s.equals(((ForKont) obj).s));
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = hash.hash(P.p(bv, x, s));
                calced = true;
                return recordHash;
            }
        }
    }

    public static class RetKont extends Kont {
        public IRVar x;
        public Env env;
        public Boolean isctor;
        public Trace trace;
        int recordHash;
        boolean calced;
        static final Hash<P4<IRVar, Env, Boolean, Trace>> hash = Hash.p4Hash(Hash.anyHash(), Hash.anyHash(), Hash.anyHash(), Hash.anyHash());

        public RetKont(IRVar x, Env env, Boolean isctor, Trace trace) {
            this.x = x;
            this.env = env;
            this.isctor = isctor;
            this.trace = trace;
            this.calced = false;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof RetKont && x.equals(((RetKont) obj).x) && env.equals(((RetKont) obj).env) && isctor.equals(((RetKont) obj).isctor) && trace.equals(((RetKont) obj).trace));
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = hash.hash(P.p(x, env, isctor, trace));
                calced = true;
                return recordHash;
            }
        }
    }

    public static class TryKont extends Kont {
        public IRPVar x;
        public IRStmt sc;
        public IRStmt sf;
        int recordHash;
        boolean calced;
        static final Hash<P3<IRPVar, IRStmt, IRStmt>> hash = Hash.p3Hash(Hash.anyHash(), Hash.anyHash(), Hash.anyHash());

        public TryKont(IRPVar x, IRStmt sc, IRStmt sf) {
            this.x = x;
            this.sc = sc;
            this.sf = sf;
            this.calced = false;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof TryKont && x.equals(((TryKont) obj).x) && sc.equals(((TryKont) obj).sc) && sf.equals(((TryKont) obj).sf));
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = hash.hash(P.p(x, sc, sf));
                calced = true;
                return recordHash;
            }
        }
    }

    public static class CatchKont extends Kont {
        public IRStmt sf;
        int recordHash;
        boolean calced;

        public CatchKont(IRStmt sf) {
            this.sf = sf;
            this.calced = false;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof CatchKont && sf.equals(((CatchKont) obj).sf));
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = sf.hashCode();
                calced = true;
                return recordHash;
            }
        }
    }

    public static class FinKont extends Kont {
        public FHashSet<Value> vs;
        int recordHash;
        boolean calced;

        public FinKont(FHashSet<Value> vs) {
            this.vs = vs;
            this.calced = false;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof FinKont && vs.equals(((FinKont) obj).vs));
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = vs.hashCode();
                calced = true;
                return recordHash;
            }
        }
    }

    public static class LblKont extends Kont {
        public String lbl;
        int recordHash;
        boolean calced;

        public LblKont(String lbl) {
            this.lbl = lbl;
            this.calced = false;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof LblKont && lbl.equals(((LblKont) obj).lbl));
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = lbl.hashCode();
                calced = true;
                return recordHash;
            }
        }
    }

    public static class AddrKont extends Kont {
        public AddressSpace.Address a;
        public IRMethod m;
        int recordHash;
        boolean calced;
        static final Hash<P2<AddressSpace.Address, IRMethod>> hash = Hash.p2Hash(Hash.anyHash(), Hash.anyHash());

        public AddrKont(AddressSpace.Address a, IRMethod m) {
            this.a = a;
            this.m = m;
            this.calced = false;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof AddrKont && a.equals(((AddrKont) obj).a) && m.equals(((AddrKont) obj).m));
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = hash.hash(P.p(a, m));
                calced = true;
                return recordHash;
            }
        }
    }

    public static class KontStack {
        public List<Kont> ks;
        public List<Integer> exc;
        int recordHash;
        boolean calced;
        static final Hash<P2<List<Kont>, List<Integer>>> hash = Hash.p2Hash(Hash.anyHash(), Hash.anyHash());

        public KontStack(List<Kont> ks, List<Integer> exc) {
            this.ks = ks;
            this.exc = exc;
            this.calced = false;
        }

        public KontStack(List<Kont> ks) {
            this.ks = ks;
            this.exc = List.list(0);
            this.calced = false;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof KontStack && ks.equals(((KontStack) obj).ks) && exc.equals(((KontStack) obj).exc));
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = hash.hash(P.p(ks, exc));
                calced = true;
                return recordHash;
            }
        }

        public KontStack merge(KontStack rhs) {
            assert ks.length() == rhs.ks.length();
            ArrayList<Kont> list = new ArrayList<>();
            for (int i = 0; i < ks.length(); i += 1) {
                Kont k1 = ks.index(i);
                Kont k2 = rhs.ks.index(i);
                if (k1 instanceof FinKont && k2 instanceof FinKont) {
                    FinKont fk1 = (FinKont)k1, fk2 = (FinKont)k2;
                    FHashSet<Value> bv;
                    FHashSet<Value> bvs1 = fk1.vs.filter(v -> v instanceof BValue);
                    FHashSet<Value> bvs2 = fk2.vs.filter(v -> v instanceof BValue);
                    if (bvs1.isEmpty() && bvs2.isEmpty()) {
                        bv = FHashSet.empty();
                    } else if (!bvs1.isEmpty() && !bvs2.isEmpty()) {
                        bv = FHashSet.build(((BValue)bvs1.toList().index(0)).merge((BValue)bvs2.toList().index(0)));
                    } else if (!bvs1.isEmpty()) {
                        bv = bvs1;
                    } else {
                        bv = bvs2;
                    }
                    FHashSet<Value> ev;
                    FHashSet<Value> evs1 = fk1.vs.filter(v -> v instanceof EValue);
                    FHashSet<Value> evs2 = fk2.vs.filter(v -> v instanceof EValue);
                    if (evs1.isEmpty() && evs2.isEmpty()) {
                        ev = FHashSet.empty();
                    } else if (!evs1.isEmpty() && !evs2.isEmpty()) {
                        ev = FHashSet.build(new EValue(((EValue)evs1.toList().index(0)).bv.merge(((EValue)evs2.toList().index(0)).bv)));
                    } else if (!evs1.isEmpty()) {
                        ev = evs1;
                    } else {
                        ev = evs2;
                    }
                    FHashSet<Value> jv;
                    FHashSet<Value> jvs1 = fk1.vs.filter(v -> v instanceof JValue);
                    FHashSet<Value> jvs2 = fk2.vs.filter(v -> v instanceof JValue);
                    if (jvs1.isEmpty() && jvs2.isEmpty()) {
                        jv = FHashSet.empty();
                    } else if (!jvs1.isEmpty() && !jvs2.isEmpty()) {
                        JValue jv1 = (JValue)jvs1.toList().index(0);
                        JValue jv2 = (JValue)jvs2.toList().index(0);
                        if (jv1.lbl.equals(jv2.lbl)) {
                            jv = FHashSet.build(new JValue(jv1.lbl, jv1.bv.merge(jv2.bv)));
                        } else {
                            jv = FHashSet.build(jv1, jv2);
                        }
                    } else if (!jvs1.isEmpty()) {
                        jv = jvs1;
                    } else {
                        jv = jvs2;
                    }
                    list.add(new FinKont(bv.union(ev).union(jv)));
                } else if (k1 instanceof ForKont && k2 instanceof ForKont) {
                    ForKont fk1 = (ForKont)k1, fk2 = (ForKont)k2;
                    assert fk1.s.equals(fk2.s) && fk1.x.equals(fk2.x);
                    list.add(new ForKont(fk1.bv.merge(fk2.bv), fk1.x, fk1.s));
                } else {
                    list.add(k1);
                }
            }
            List<Kont> newks = List.list(list);
            List<Integer> newexc;
            if (exc.last() < rhs.exc.last()) {
                newexc = rhs.exc;
            } else {
                newexc = exc;
            }
            return new KontStack(newks, newexc);
        }

        public KontStack push(Kont k) {
            List<Integer> newexc;
            if (k instanceof TryKont || k instanceof CatchKont) {
                newexc = exc.cons(2);
            } else {
                newexc = exc;
            }
            return new KontStack(ks.cons(k), newexc);
        }

        public KontStack pop() {
            List<Integer> newexc;
            if (ks.head() instanceof TryKont || ks.head() instanceof CatchKont) {
                newexc = exc.tail();
            } else {
                newexc = exc;
            }
            return new KontStack(ks.tail(), newexc);
        }

        public KontStack repl(Kont k) {
            List<Integer> newexc;
            if (ks.head() instanceof TryKont && k instanceof CatchKont) {
                newexc = exc;
            } else if (k instanceof TryKont) {
                newexc = exc.cons(2);
            } else if (ks.head() instanceof TryKont || ks.head() instanceof CatchKont) {
                newexc = exc.tail();
            } else {
                newexc = exc;
            }
            return new KontStack(ks.tail().cons(k), newexc);
        }

        public Kont top() {
            return ks.head();
        }

        public Kont last() {
            return ks.last();
        }

        public KontStack toHandler() {
            List<Kont> newks = ks.dropWhile(k -> !(k instanceof TryKont || k instanceof CatchKont));
            return new KontStack(newks, exc);
        }

        public KontStack toSpecial(String lbl) {
            List<Kont> newks = ks.dropWhile(k -> {
               if (k instanceof TryKont || k instanceof CatchKont || k.equals(HaltKont)) {
                   return false;
               } else if (k instanceof LblKont) {
                   return (!((LblKont)k).lbl.equals(lbl));
               } else {
                   return true;
               }
            });
            return new KontStack(newks, exc);
        }

        public static KontStack apply(Kont k) {
            return new KontStack(List.list(k));
        }

        public static KontStack apply(Kont k, Integer exc) {
            return new KontStack(List.list(k), List.list(exc));
        }
    }

    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}
