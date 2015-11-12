package analysis;

import fj.*;
import fj.data.*;
import ir.*;

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
        public static Boolean inPostFixpoin = false;
        public static Boolean splitStates = false;
        public static HashMap<Trace, P3<Trace, IRVar, Domains.AddressSpace.Addresses>> prunedInfo = HashMap.hashMap();
        // TODO

        public static void clear() {
            // TODO
        }
    }

    public static HashMap<Integer, Set<Domains.BValue>> runner(String[] args) {
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
                    // TODO
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
                    // TODO
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
                    ret.union(Utils.applyClo(bv1,
                                            eval(e2),
                                            eval(e3),
                                            x,
                                            env,
                                            _store,
                                            _pad,
                                            ks,
                                            trace));
                }
                else if (stmt instanceof IRFor) {
                    IRFor irFor = (IRFor) stmt;
                    IRExp e = irFor.e;
                    IRVar x = irFor.x;
                    IRStmt s = irFor.s;
                    Set<State> keys = Utils.objAllKeys(eval(e), store);
                    if (keys.size() > 0) {

                    }
                    // TODO
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
                    // TODO
                }
                else if (v instanceof Domains.EValue) {
                    Domains.EValue ev = (Domains.EValue) v;
                    // TODO
                }
                else if (v instanceof Domains.JValue) {
                    Domains.JValue jv = (Domains.JValue) v;
                    // TODO
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
                if (Mutable.pruneStore) {
                }
                // TODO
            }
            else if (ks1.top() instanceof Domains.TryKont) {
                Domains.TryKont tk = (Domains.TryKont) ks1.top();
                IRStmt s3 = tk.sf;
                // TODO
            }
            else if (ks1.top() instanceof Domains.CatchKont) {
                Domains.CatchKont ck = (Domains.CatchKont) ks1.top();
                // TODO
            }
            else if (ks1.top() instanceof Domains.FinKont) {
                Domains.FinKont fk = (Domains.FinKont) ks1.top();
                Set<Domains.Value> vs = fk.vs;
                // TODO
            }
            else if (ks1.top() instanceof Domains.LblKont) {
                ret.union(advanceBV(bv, store1, pad1, ks1.pop()));
            }
            return ret;
        }

        public Set<State> advanceEV(Domains.EValue ev, Domains.Env env1, Domains.Store store1, Domains.Scratchpad pad1, Domains.KontStack ks1, Trace trace) {
            Set<State> ret = Set.<State>empty(Ord.<State>hashEqualsOrd());
            HashSet<Domains.AddressSpace.Address> addrsSeen = new HashSet<Domains.AddressSpace.Address>(Equal.<Domains.AddressSpace.Address>anyEqual(), Hash.<Domains.AddressSpace.Address>anyHash());
            // TODO
            return null;
        }

        public Set<State> advanceJV(Domains.JValue jv, Domains.Store store1, Domains.Scratchpad pad1, Domains.KontStack ks1) {
            // TODO
            return null;
        }

        // TODO
    }
}
