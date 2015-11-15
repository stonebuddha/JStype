package analysis;

import fj.*;
import fj.data.*;
import ir.*;
import analysis.init.*;
import fj.F;

/**
 * Created by BenZ on 15/11/5.
 */
public class Interpreter {

    public static class Mutable {

        public static Boolean lightGC = false;
        public static Boolean fullGC = false;
        public static Boolean pruneStore = false;
        public static Boolean dangle = false;
        public static Boolean testing = false;
        public static Boolean print = false;
        public static Boolean catchExc = false;
        public static Boolean inPostFixpoint = false;
        public static Boolean splitStates = false;
        public static HashMap<Trace, P3<Trace, IRVar, Domains.AddressSpace.Addresses>> prunedInfo = HashMap.hashMap();

        public static void clear() {
            Mutable.lightGC = false;
            Mutable.fullGC = false;
            Mutable.pruneStore = false;
            Mutable.dangle = false;
            Mutable.testing = false;
            Mutable.print = false;
            Mutable.catchExc = false;
            Mutable.inPostFixpoint = false;
            Mutable.splitStates = false;
            Mutable.prunedInfo.clear();
        }
    }

    public static HashMap<Integer, Set<Domains.BValue>> runner(String[] args) {
        Mutable.clear();
        PruneScratch.clear();
        PruneStoreToo.clear();
        // TODO
        return null;
    }

    public static Trace optionToTrace(String str) {
        // TODO
        return null;
    }

    public static Boolean shouldSplitStates(String str) {
        // TODO
        return null;
    }

    public static IRStmt readAST(String file) {
        // TODO
        return null;
    }

    public static Set<State> process(State initSigma) {
        // TODO
        return null;
    }

    public static class PruneScratch {
        public static HashMap<Trace, Domains.Scratchpad> pruned = HashMap.hashMap();

        public static void clear() {
            pruned.clear();
        }

        public static Domains.Scratchpad apply(Trace trace) {
            if (pruned.get(trace).isNone())
                throw new RuntimeException("Analysis,Interpreter,PruneScratch.apply Null error");
            return pruned.get(trace).some();
        }

        public static void update(Trace trace, Domains.Scratchpad pad) {
            Option<Domains.Scratchpad> value = pruned.get(trace);
            if (value.isSome()) {
                Domains.Scratchpad newValue = value.some().merge(pad);
                pruned.delete(trace);
                pruned.set(trace, newValue);
            }
            else {
                pruned.set(trace, pad);
            }
        }
    }

    public static class PruneStoreToo {
        public static HashMap<Trace, P2<Domains.Store, Domains.Scratchpad>> pruned = HashMap.hashMap();

        public static void clear() {
            pruned.clear();
        }

        public static P2<Domains.Store, Domains.Scratchpad> apply(Trace trace) {
            if (pruned.get(trace).isNone())
                throw new RuntimeException("Analysis,Interpreter,PruneStoreToo.apply Null error");
            return pruned.get(trace).some();
        }

        public static void update(Trace trace, P2<Domains.Store, Domains.Scratchpad> storePad) {
            Option<P2<Domains.Store, Domains.Scratchpad>> value = pruned.get(trace);
            if (value.isSome()) {
                P2<Domains.Store, Domains.Scratchpad> newValue = P.p(value.some()._1().merge(storePad._1()), value.some()._2().merge(storePad._2()));
                pruned.delete(trace);
                pruned.set(trace, newValue);
            }
            else {
                pruned.set(trace, storePad);
            }
        }

        public static boolean contains(Trace trace) {
            return pruned.contains(trace);
        }
    }


    public static class State {
        public Domains.Term t;
        public Domains.Env env;
        public Domains.Store store;
        public Domains.Scratchpad pad;
        public Domains.KontStack ks;
        public Trace trace;

        public State(Domains.Term t, Domains.Env env, Domains.Store store, Domains.Scratchpad pad, Domains.KontStack ks, Trace trace) {
            this.t = t;
            this.env = env;
            this.store = store;
            this.pad = pad;
            this.ks = ks;
            this.trace = trace;
        }

        public State merge(State sigma) {
            // assert( t == ς.t && τ == ς.τ )
            return new State(t, env.merge(sigma.env), store.merge(sigma.store), pad.merge(sigma.pad), ks.merge(sigma.ks), trace);
        }

        public Boolean merge() {
            if (t instanceof Domains.StmtTerm && ((Domains.StmtTerm) t).s instanceof IRMerge) {
                return true;
            }
            return false;
        }

        public Integer order() {
            if (t instanceof Domains.StmtTerm && ((Domains.StmtTerm) t).s instanceof IRMerge) {
                return ((IRMerge) ((Domains.StmtTerm) t).s).order();
            }
            else {
                throw new RuntimeException("inconceivable");
            }
        }

        public Domains.BValue eval(IRExp e) {
            return Eval.eval(e, env, store, pad);
        }

        public Set<State> next() {
            Set<State> ret = Set.empty(Ord.<State>hashEqualsOrd());

            if (t instanceof Domains.StmtTerm) {
                IRStmt stmt = ((Domains.StmtTerm) t).s;

                if (stmt instanceof IRDecl) {
                    IRDecl irDecl = (IRDecl) stmt;
                    List<P2<IRPVar, IRExp>> bind = irDecl.bind;
                    IRStmt s = irDecl.s;

                    List<IRPVar> xs = List.nil();
                    List<Domains.BValue> bvs = List.nil();
                    for (P2<IRPVar, IRExp> aBind : bind) {
                        xs.cons(aBind._1());
                        bvs.cons(eval(aBind._2()));
                    }
                    List<Domains.AddressSpace.Address> as = trace.makeAddrs(xs);///
                    Domains.Store store1 = Utils.alloc(store, as, bvs);

                    List<P2<IRPVar, Domains.AddressSpace.Address>> envBind = List.nil();
                    for (int i = 0; i < xs.length(); ++i) {
                        envBind.cons(P.p(xs.index(i), as.index(i)));
                    }
                    Domains.Env env1 = env.extendAll(envBind);
                    ret.insert(new State(new Domains.StmtTerm(s), env1, store1, pad, ks, trace.update(s)));
                } else if (stmt instanceof IRSDecl) {
                    IRSDecl irSDecl = (IRSDecl) stmt;
                    Integer num = irSDecl.num;
                    IRStmt s = irSDecl.s;
                    ret.insert(new State(new Domains.StmtTerm(s), env, store, Domains.Scratchpad.apply(num), ks, trace.update(s)));
                }
                else if (stmt instanceof IRSeq) {
                    IRSeq irSeq = (IRSeq) stmt;
                    IRStmt s = irSeq.ss.index(0);
                    List<IRStmt> ss = irSeq.ss.tail();
                    ret.insert(new State(new Domains.StmtTerm(s), env, store, pad, ks.push(new Domains.SeqKont(ss)), trace.update(s)));
                }
                else if (stmt instanceof IRIf) {
                    IRIf irIf = (IRIf) stmt;
                    IRExp e = irIf.e;
                    IRStmt s1 = irIf.s1;
                    IRStmt s2 = irIf.s2;
                    Domains.Bool b = eval(e).b;
                    if (b == Domains.Bool.True) {
                        ret.insert(new State(new Domains.StmtTerm(s1), env, store, pad, ks, trace.update(s1)));
                    }
                    else if (b == Domains.Bool.False) {
                        ret.insert(new State(new Domains.StmtTerm(s2), env, store, pad, ks, trace.update(s2)));
                    }
                    else if (b == Domains.Bool.Top) {
                        ret.insert(new State(new Domains.StmtTerm(s1), env, store, pad, ks, trace.update(s1)));
                        ret.insert(new State(new Domains.StmtTerm(s2), env, store, pad, ks, trace.update(s2)));
                    }
                }
                else if (stmt instanceof IRAssign) {
                    IRAssign irAssign = (IRAssign) stmt;
                    IRVar x = irAssign.x;
                    IRExp e = irAssign.e;
                    Domains.BValue bv = eval(e);
                    if (x instanceof IRPVar) {
                        ret.union(advanceBV(bv, store.extend(env.apply(((IRPVar) x)).some(), bv), pad, ks));
                    }
                    else if (x instanceof IRScratch) {
                        ret.union(advanceBV(bv, store, pad.update(((IRScratch) x), bv), ks));
                    }

                }
                else if (stmt instanceof IRWhile) {
                    IRWhile irWhile = (IRWhile) stmt;
                    IRExp e = irWhile.e;
                    IRStmt s = irWhile.s;
                    Domains.Bool b = eval(e).b;
                    if (b == Domains.Bool.True) {
                        ret.insert(new State(new Domains.StmtTerm(s),
                                            env,
                                            store,
                                            pad,
                                            ks.push(new Domains.WhileKont(e, s)),
                                            trace.update(s)));
                    }
                    else if (b == Domains.Bool.False) {
                        ret.union(advanceBV(Domains.Undef.BV, store, pad, ks));
                    }
                    else if (b == Domains.Bool.Top) {
                        ret.union(advanceBV(Domains.Undef.BV, store, pad, ks));
                        ret.insert(new State(new Domains.StmtTerm(s),
                                env,
                                store,
                                pad,
                                ks.push(new Domains.WhileKont(e, s)),
                                trace.update(s)));
                    }
                }
                else if (stmt instanceof IRNewfun) {
                    IRNewfun irNewfun = (IRNewfun) stmt;
                    IRVar x = irNewfun.x;
                    IRMethod m = irNewfun.m;
                    IRNum n = irNewfun.n;
                    Domains.AddressSpace.Address a1 = trace.makeAddr(x);
                    Domains.Env env1 = env.filter((lamX) -> {
                        return m.freeVars.member(lamX);
                    });
                    Domains.Store store1 = Utils.allocFun(new Domains.Clo(env1, m), eval(n), a1, store);
                    Domains.BValue bv1 = Domains.AddressSpace.Address.inject(a1);
                    if (x instanceof IRPVar) {
                        ret.union(advanceBV(bv1, store1.extend(env.apply(((IRPVar) x)).some(), bv1), pad, ks));
                    }
                    else if (x instanceof IRScratch) {
                        ret.union(advanceBV(bv1, store1, pad.update(((IRScratch) x), bv1), ks));
                    }
                }
                else if (stmt instanceof IRNew) {
                    IRNew irNew = (IRNew) stmt;
                    IRVar x = irNew.x;
                    IRExp e1 = irNew.e1;
                    IRExp e2 = irNew.e2;
                    Domains.AddressSpace.Address a1 = trace.makeAddr(x);
                    Domains.BValue bv1 = eval(e1), bv2 = eval(e2);
                    P2<Domains.Store, Domains.BValue> sa = Utils.allocObj(bv1, a1, store, trace);
                    Domains.Store store1 = sa._1();
                    Domains.BValue bv = sa._2();
                    P2<Domains.Store, Domains.Scratchpad> ss;
                    if (x instanceof IRPVar) {
                        ss = P.p(store.extend(env.apply(((IRPVar) x)).some(), bv), pad);
                    }
                    else {
                        ss = P.p(store1, pad.update(((IRScratch) x), bv));
                    }
                    Domains.Store store2 = ss._1();
                    Domains.Scratchpad pad1 = ss._2();
                    Domains.Store store3 = Utils.setConstr(store2, bv2);
                    if (!bv1.defAddr()) {
                        ret.insert(new State(new Domains.ValueTerm(Utils.Errors.typeError), env, store, pad, ks, trace));
                    }
                    P2<Domains.Store, Domains.Scratchpad> sr = Utils.refineExc(e1, store3, env, pad1, Utils.Filters.IsFunc);
                    Domains.Store _store3 = sr._1();
                    Domains.Scratchpad _pad1 = sr._2();
                    ret.union(Utils.applyClo(bv1, bv, bv2, x, env, _store3, _pad1, ks, trace));

                }
                else if (stmt instanceof IRToObj) {
                    IRToObj irToObj = (IRToObj) stmt;
                    IRVar x = irToObj.x;
                    IRExp e = irToObj.e;
                    P2<Option<P3<Domains.BValue, Domains.Store, Domains.Scratchpad>>, Option<Domains.EValue>> st = Utils.toObj(eval(e), x, env, store, pad, trace);
                    Option<P3<Domains.BValue, Domains.Store, Domains.Scratchpad>> noexc = st._1();
                    Option<Domains.EValue> exc = st._2();
                    if (noexc.isSome()) {
                        Domains.BValue bv = noexc.some()._1();
                        Domains.Store store1 = noexc.some()._2();
                        Domains.Scratchpad pad1 = noexc.some()._3();
                        P2<Domains.Store, Domains.Scratchpad> sr = Utils.refineExc(e, store1, env, pad1, Utils.Filters.IsUndefNull);
                        Domains.Store _store1 = sr._1();
                        Domains.Scratchpad _pad1 = sr._2();
                        ret.union(advanceBV(bv, _store1, _pad1, ks));
                    }
                    if (exc.isSome()) {
                        Domains.EValue ev = exc.some();
                        ret.union(advanceEV(ev, env, store, pad, ks, trace));
                    }
                }
                else if (stmt instanceof IRUpdate) {
                    IRUpdate irUpdate = (IRUpdate) stmt;
                    IRExp e1 = irUpdate.e1;
                    IRExp e2 = irUpdate.e2;
                    IRExp e3 = irUpdate.e3;
                    P2<Option<P2<Domains.BValue, Domains.Store>>, Option<Domains.EValue>> su = Utils.updateObj(eval(e1), eval(e2), eval(e3), store);
                    Option<P2<Domains.BValue, Domains.Store>> noexc = su._1();
                    Option<Domains.EValue> exc = su._2();
                    if (noexc.isSome()) {
                        Domains.BValue bv = noexc.some()._1();
                        Domains.Store store1 = noexc.some()._2();
                        P2<Domains.Store, Domains.Scratchpad> sr = Utils.refineExc(e1, store1, env, pad, Utils.Filters.IsUndefNull);
                        Domains.Store _store1 = sr._1();
                        Domains.Scratchpad _pad = sr._2();
                        ret.union(advanceBV(bv, _store1, _pad, ks));
                    }
                    if (exc.isSome()) {
                        Domains.EValue ev = exc.some();
                        ret.union(advanceEV(ev, env, store, pad, ks, trace));
                    }
                }
                else if (stmt instanceof IRDel) {
                    IRDel irDel = (IRDel) stmt;
                    IRScratch x = irDel.x;
                    IRExp e1 = irDel.e1;
                    IRExp e2 = irDel.e2;
                    P2<Option<P2<Domains.Store, Domains.Scratchpad>>, Option<Domains.EValue>> sd = Utils.delete(eval(e1), eval(e2), x, env, store, pad);
                    Option<P2<Domains.Store, Domains.Scratchpad>> noexc = sd._1();
                    Option<Domains.EValue> exc = sd._2();
                    if (noexc.isSome()) {
                        Domains.Store store1 = noexc.some()._1();
                        Domains.Scratchpad pad1 = noexc.some()._2();
                        P2<Domains.Store, Domains.Scratchpad> sr = Utils.refineExc(e1, store1, env, pad, Utils.Filters.IsUndefNull);
                        Domains.Store _store1 = sr._1();
                        Domains.Scratchpad _pad1 = sr._2();
                        ret.union(advanceBV(Domains.Undef.BV, _store1, _pad1, ks));
                    }
                    if (exc.isSome()) {
                        Domains.EValue ev = exc.some();
                        ret.union(advanceEV(ev, env, store, pad, ks, trace));
                    }
                }
                else if (stmt instanceof IRTry) {
                    IRTry irTry = (IRTry) stmt;
                    IRStmt s1 = irTry.s1, s2 = irTry.s2, s3 = irTry.s3;
                    IRPVar x = irTry.x;
                    ret.insert(new State(new Domains.StmtTerm(s1),
                            env,
                            store,
                            pad,
                            ks.push(new Domains.TryKont(x, s2, s3)),
                            trace.update(s1)));
                }
                else if (stmt instanceof IRThrow) {
                    IRThrow irThrow = (IRThrow) stmt;
                    IRExp e = irThrow.e;
                    ret.union(advanceEV(new Domains.EValue(eval(e)), env, store, pad, ks, trace));
                }
                else if (stmt instanceof IRJump) {
                    IRJump irJump = (IRJump) stmt;
                    String lbl = irJump.lbl;
                    IRExp e = irJump.e;
                    ret.union(advanceJV(new Domains.JValue(lbl, eval(e)), store, pad, ks));
                }
                else if (stmt instanceof IRLbl) {
                    IRLbl irLbl = (IRLbl) stmt;
                    String lbl = irLbl.lbl;
                    IRStmt s = irLbl.s;
                    ret.insert(new State(new Domains.StmtTerm(s),
                            env,
                            store,
                            pad,
                            ks.push(new Domains.LblKont(lbl)),
                            trace.update(s)));
                }
                else if (stmt instanceof IRCall) {
                    IRCall irCall = (IRCall) stmt;
                    IRExp e1 = irCall.e1, e2 = irCall.e2, e3 = irCall.e3;
                    IRVar x = irCall.x;
                    Domains.BValue bv1 = eval(e1);
                    if (!bv1.defAddr()) {
                        ret.insert(new State(new Domains.ValueTerm(Utils.Errors.typeError), env, store, pad, ks, trace));
                    }
                    P2<Domains.Store, Domains.Scratchpad> sr = Utils.refineExc(e1, store, env, pad, Utils.Filters.IsFunc);
                    Domains.Store _store = sr._1();
                    Domains.Scratchpad _pad = sr._2();
                    ret.union(Utils.applyClo(bv1, eval(e2), eval(e3), x, env, _store, _pad, ks, trace));
                }
                else if (stmt instanceof IRFor) {
                    IRFor irFor = (IRFor) stmt;
                    IRExp e = irFor.e;
                    IRVar x = irFor.x;
                    IRStmt s = irFor.s;
                    Set<Domains.Str> keys = Utils.objAllKeys(eval(e), store);
                    if (keys.size() > 0) {
                        Domains.Str acc = Domains.Str.Bot;
                        for (Domains.Str key : keys) {
                            acc = acc.merge(key);
                        }
                        Domains.BValue uber = Domains.Str.inject(acc);
                        if (x instanceof IRPVar) {
                            ret.insert(new State(new Domains.StmtTerm(s), env, store.extend(env.apply(((IRPVar) x)).some(), uber), pad, ks.push(new Domains.ForKont(uber, x, s)), trace.update(s)));
                        }
                        else if (x instanceof IRScratch) {
                            ret.insert(new State(new Domains.StmtTerm(s), env, store, pad.update(((IRScratch) x), uber), ks.push(new Domains.ForKont(uber, x, s)), trace.update(s)));
                        }
                    }
                }
                else if (stmt instanceof IRMerge) {
                    IRMerge irMerge = (IRMerge) stmt;
                    ret.union(advanceBV(Domains.Undef.BV, store, pad, ks));
                }
                else {
                    throw new RuntimeException("malformed program");
                }
            }
            else {
                Domains.Value v = ((Domains.ValueTerm)t).v;
                if (v instanceof Domains.BValue) {
                    Domains.BValue bv = (Domains.BValue)v;
                    ret.union(advanceBV(bv, store, pad, ks));
                }
                else if (v instanceof Domains.EValue) {
                    Domains.EValue ev = (Domains.EValue) v;
                    ret.union(advanceEV(ev, env, store, pad, ks, trace));
                }
                else if (v instanceof Domains.JValue) {
                    Domains.JValue jv = (Domains.JValue) v;
                    ret.union(advanceJV(jv, store, pad, ks));
                }
            }

            return ret;
        }

        public Set<State> advanceBV(Domains.BValue bv, Domains.Store store1, Domains.Scratchpad pad1, Domains.KontStack ks1){
            Set<State> ret = Set.<State>empty(Ord.<State>hashEqualsOrd());
            if (ks1.top() instanceof Domains.SeqKont) {
                Domains.SeqKont sk = (Domains.SeqKont) ks1.top();
                if (sk.ss.isNotEmpty()) {
                    IRStmt s = sk.ss.index(0);
                    List<IRStmt> ss = sk.ss.tail();
                    ret.insert(new State(new Domains.StmtTerm(s), env, store1, pad1, ks1.repl(new Domains.SeqKont(ss)), trace.update(s)));
                }
                else {
                    ret.union(advanceBV(Domains.Undef.BV, store1, pad1, ks1.pop()));
                }
            }
            else if (ks1.top() instanceof Domains.WhileKont){
                Domains.WhileKont wk = (Domains.WhileKont) ks1.top();
                IRExp e = wk.e;
                IRStmt s = wk.s;
                Domains.Bool b = Eval.eval(e, env, store1, pad1).b;
                if (b == Domains.Bool.True) {
                    ret.insert(new State(new Domains.StmtTerm(s), env, store1, pad1, ks1, trace.update(s)));
                }
                else if (b == Domains.Bool.False) {
                    ret.union(advanceBV(Domains.Undef.BV, store1, pad1, ks1.pop()));
                }
                else if (b == Domains.Bool.Top) {
                    ret.insert(new State(new Domains.StmtTerm(s), env, store1, pad1, ks1, trace.update(s)));
                    ret.union(advanceBV(Domains.Undef.BV, store1, pad1, ks1.pop()));
                }
            }
            else if (ks1.top() instanceof Domains.ForKont) {
                Domains.ForKont fk = (Domains.ForKont) ks1.top();
                Domains.BValue bv1 = fk.bv;
                IRVar x = fk.x;
                IRStmt s = fk.s;
                ret.union(advanceBV(Domains.Undef.BV, store1, pad1, ks1.pop()));
                if (x instanceof IRPVar) {
                    ret.insert(new State(new Domains.StmtTerm(s), env, store1.extend(env.apply(((IRPVar) x)).some(), bv1), pad1, ks1, trace.update(s)));
                }
                else if (x instanceof IRScratch) {
                    ret.insert(new State(new Domains.StmtTerm(s), env, store1, pad1.update(((IRScratch) x), bv1), ks1, trace.update(s)));
                }
            }
            else if (ks1.top() instanceof Domains.AddrKont) {
                Domains.AddrKont ak = (Domains.AddrKont) ks1.top();
                Domains.AddressSpace.Address a = ak.a;
                IRMethod m = ak.m;
                Domains.Store store2;
                if (Mutable.lightGC) {
                    store2 = store1.lightGC(m.cannotEscape);
                }
                else {
                    store2 = store1;
                }
                Domains.Store store3 = store2.weaken(m.canEscapeVar, m.canEscapeObj);
                Set<Domains.KontStack> konts = store3.getKont(a);
                for (Domains.KontStack tmpKS : konts) {
                    ret.union(advanceBV(bv, store3, pad1, tmpKS));
                }
            }
            else if (ks1.top() instanceof Domains.RetKont) {
                Domains.RetKont rk = (Domains.RetKont) ks1.top();
                IRVar x = rk.x;
                Domains.Env envc = rk.env;
                Boolean isctor = rk.isctor;
                Trace tracec = rk.trace;

                Domains.Store store2;
                Domains.Scratchpad pad2;
                if (Mutable.pruneStore) {
                    P2<Domains.Store, Domains.Scratchpad> valuePruned = PruneStoreToo.apply(tracec);
                    store2 = store1.merge(valuePruned._1());
                    pad2 = valuePruned._2();
                }
                else {
                    store2 = store1;
                    pad2 = PruneScratch.apply(tracec);
                }

                Domains.Store store3;
                if (Mutable.fullGC) {
                    Set<Domains.AddressSpace.Address> vroots = envc.addrs();
                    Set<Domains.AddressSpace.Address> oroots = bv.as.union(pad2.addrs()).union(Init.keepInStore);
                    Set<Domains.AddressSpace.Address> kroots;
                    if (ks1.last() instanceof Domains.AddrKont) {
                        kroots = Domains.AddressSpace.Addresses.apply(((Domains.AddrKont) ks1.last()).a);
                    }
                    else {
                        kroots = Domains.AddressSpace.Addresses.apply();
                    }
                    store3 = store2.fullGC(vroots, oroots, kroots);
                }
                else {
                    store3 = store2;
                }

                Set<State> call = Set.<State>empty(Ord.<State>hashEqualsOrd());
                if (!isctor || (bv.as.size() > 0)) {
                    Domains.BValue bv1;
                    if (!isctor) {
                        bv1 = bv;
                    }
                    else {
                        bv1 = Domains.AddressSpace.Addresses.inject(bv.as);
                    }
                    if (x instanceof IRPVar) {
                        call.insert(new State(new Domains.ValueTerm(bv1),
                                envc,
                                store3.extend(envc.apply(((IRPVar) x)).some(), bv1),
                                pad2,
                                ks1.pop(),
                                trace.update(tracec)));
                    }
                    else if (x instanceof IRScratch ){
                        call.insert(new State(new Domains.ValueTerm(bv1),
                                envc,
                                store3,
                                pad2.update(((IRScratch) x), bv1),
                                ks1.pop(),
                                trace.update(tracec)));
                    }
                }

                Set<State> ctor = Set.<State>empty(Ord.<State>hashEqualsOrd());
                if (!isctor && !bv.defAddr()) {
                    if (x instanceof IRPVar) {
                        Domains.BValue t1 = Eval.eval(x, envc, store3, pad2);
                        ctor.insert(new State(new Domains.ValueTerm(t1), envc, store3, pad2, ks1.pop(), trace.update(tracec)));
                    }
                    else if (x instanceof IRScratch) {
                        ctor.insert(new State(new Domains.ValueTerm(pad2.apply((IRScratch) x)), envc, store3, pad2, ks1.pop(), trace.update(tracec)));
                    }
                }

                ret.union(call);
                ret.union(ctor);
            }
            else if (ks1.top() instanceof Domains.TryKont) {
                Domains.TryKont tk = (Domains.TryKont) ks1.top();
                IRStmt s3 = tk.sf;
                ret.insert(new State(new Domains.StmtTerm(s3), env, store1, pad1, ks1.repl(new Domains.FinKont(Set.set(Ord.<Domains.Value>hashEqualsOrd(), Domains.Undef.BV))), trace.update(s3)));
            }
            else if (ks1.top() instanceof Domains.CatchKont) {
                Domains.CatchKont ck = (Domains.CatchKont) ks1.top();
                IRStmt s3 = ck.sf;
                ret.insert(new State(new Domains.StmtTerm(s3), env, store1, pad1, ks1.repl(new Domains.FinKont(Set.set(Ord.<Domains.Value>hashEqualsOrd(), Domains.Undef.BV))), trace.update(s3)));
            }
            else if (ks1.top() instanceof Domains.FinKont) {
                Domains.FinKont fk = (Domains.FinKont) ks1.top();
                Set<Domains.Value> vs = fk.vs;
                for (Domains.Value value : vs) {
                    if (value instanceof Domains.BValue) {
                        ret.union(advanceBV(bv, store1, pad1, ks1.pop()));
                    }
                    else if (value instanceof Domains.EValue) {
                        ret.union(advanceEV((Domains.EValue)value, env, store1, pad1, ks1.pop(), trace));
                    }
                    else if (value instanceof Domains.JValue) {
                        ret.union(advanceJV((Domains.JValue)value, store1, pad1, ks1.pop()));
                    }
                }
            }
            else if (ks1.top() instanceof Domains.LblKont) {
                ret.union(advanceBV(bv, store1, pad1, ks1.pop()));
            }
            return ret;
        }

        public Set<State> advanceEV(Domains.EValue ev, Domains.Env env1, Domains.Store store1, Domains.Scratchpad pad1, Domains.KontStack ks1, Trace trace1) {
            Set<State> ret = Set.<State>empty(Ord.<State>hashEqualsOrd());
            HashSet<Domains.AddressSpace.Address> addrsSeen = new HashSet<Domains.AddressSpace.Address>(Equal.<Domains.AddressSpace.Address>anyEqual(), Hash.<Domains.AddressSpace.Address>anyHash());
            if (ks1.exc.isNotEmpty()) {
                ret.union(innerAdvance(ev, env1, store1, pad1, ks1, trace1, addrsSeen));
            }
            return ret;
        }

        private Set<State> innerAdvance(Domains.EValue ev, Domains.Env env1, Domains.Store store1, Domains.Scratchpad pad1, Domains.KontStack ks1, Trace trace1, HashSet<Domains.AddressSpace.Address> addrsSeen) {
            Set<State> ret = Set.<State>empty(Ord.<State>hashEqualsOrd());
            if (ks1.exc.head() == 1) {
                Domains.AddressSpace.Address a = ((Domains.AddrKont)ks1.last()).a;
                IRMethod m = ((Domains.AddrKont)ks1.last()).m;
                if (!addrsSeen.contains(a)) {
                    addrsSeen.set(a);

                    Domains.Store store2;
                    if (!Mutable.lightGC) {
                        store2 = store1.lightGC(m.cannotEscape);
                    }
                    else {
                        store2 = store1;
                    }

                    Domains.Store store3 = store2.weaken(m.canEscapeVar, m.canEscapeObj);

                    Set<Domains.KontStack> konts = store3.getKont(a);
                    for (Domains.KontStack ks2 : konts) {
                        Domains.Env envc = ((Domains.RetKont)ks.top()).env;
                        Trace tracec = ((Domains.RetKont)ks.top()).trace;
                        Domains.Store store4;
                        Domains.Scratchpad pad2;
                        if (Mutable.pruneStore) {
                            P2<Domains.Store, Domains.Scratchpad> pruned = PruneStoreToo.apply(tracec);
                            store4 = store3.merge(pruned._1());
                            pad2 = pruned._2();
                        }
                        else {
                            store4 = store3;
                            pad2 = PruneScratch.apply(tracec);
                        }

                        Domains.Store store5;
                        if (Mutable.fullGC) {
                            Set<Domains.AddressSpace.Address> vroots = envc.addrs();
                            Set<Domains.AddressSpace.Address> oroots = ev.bv.as.union(pad2.addrs()).union(Init.keepInStore);
                            Set<Domains.AddressSpace.Address> kroots;
                            if (ks2.last() instanceof Domains.AddrKont) {
                                kroots = Domains.AddressSpace.Addresses.apply(((Domains.AddrKont) ks2.last()).a);
                            }
                            else {
                                kroots = Domains.AddressSpace.Addresses.apply();
                            }
                            store5 = store4.fullGC(vroots, oroots, kroots);
                        }
                        else {
                            store5 = store4;
                        }

                        ret.union(innerAdvance(ev, envc, store5, pad2, ks2.pop(), trace1.update(tracec), addrsSeen));
                    }
                }
            }
            else if (ks1.exc.head() == 2) {
                Domains.KontStack ks2 = ks1.toHandler();
                if (ks2.top() instanceof Domains.TryKont) {
                    IRPVar x = ((Domains.TryKont) ks2.top()).x;
                    IRStmt s2 = ((Domains.TryKont) ks2.top()).sc;
                    IRStmt s3 = ((Domains.TryKont) ks2.top()).sf;
                    ret.insert(new State(new Domains.StmtTerm(s2), env1, store1.extend(env1.apply(x).some(), ev.bv), pad1, ks2.repl(new Domains.CatchKont(s3)), trace1));
                }
                else if (ks2.top() instanceof Domains.CatchKont) {
                    IRStmt s3 = ((Domains.CatchKont) ks2.top()).sf;
                    ret.insert(new State(new Domains.StmtTerm(s3), env1, store1, pad1, ks2.repl(new Domains.FinKont(Set.set(Ord.<Domains.Value>hashEqualsOrd(), ev))), trace1));
                }
                else {
                    throw new RuntimeException("inconceivable");
                }
            }
            return ret;
        }


        public Set<State> advanceJV(Domains.JValue jv, Domains.Store store1, Domains.Scratchpad pad1, Domains.KontStack ks1) {
            Set<State> ret = Set.<State>empty(Ord.<State>hashEqualsOrd());
            if (ks1.top() instanceof Domains.TryKont) {
                Domains.TryKont tk = (Domains.TryKont) ks1.top();
                IRStmt s3 = tk.sf;
                ret.insert(new State(new Domains.StmtTerm(s3), env, store1, pad1, ks1.repl(new Domains.FinKont(Set.set(Ord.<Domains.Value>hashEqualsOrd(), jv))), trace));
            }
            else if (ks1.top() instanceof Domains.CatchKont) {
                Domains.CatchKont ck = (Domains.CatchKont) ks1.top();
                IRStmt s3 = ck.sf;
                ret.insert(new State(new Domains.StmtTerm(s3), env, store1, pad1, ks1.repl(new Domains.FinKont(Set.set(Ord.<Domains.Value>hashEqualsOrd(), jv))), trace));
            }
            else if (ks1.top() instanceof Domains.LblKont && ((Domains.LblKont)ks1.top()).lbl == jv.lbl) {
                ret.union(advanceBV(jv.bv, store1, pad1, ks1.pop()));
            }
            else if (ks1.top() != Domains.HaltKont) {
                Domains.KontStack ks2 = ks1.toSpecial(jv.lbl);
                ret.union(advanceJV(jv, store1, pad1, ks2));
            }
            return ret;
        }

        // TODO
    }
}
