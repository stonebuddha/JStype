package analysis;

import fj.Ord;
import fj.P2;
import fj.data.HashMap;
import fj.data.Set;
import fj.data.List;
import ir.*;

/**
 * Created by BenZ on 15/11/5.
 */
public class Interpreter {

    public static class Mutable {

        public static Boolean lightGC = false;
        // TODO

        public static void clear() {
            // TODO
        }
    }

    public static HashMap<Integer, Set<Domains.BValue>> runner(String[] args) {
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
            // TODO
            return null;
        }

        public Integer order() {
            // TODO
            return null;
        }

        public Domains.BValue eval(IRExp e) {
            return Eval.eval(e, env, store, pad);
        }

        public Set<State> next() {
            Set<State> ret = Set.empty(Ord.hashEqualsOrd());

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
                    // TODO
                }
                else if (stmt instanceof IRAssign) {
                    IRAssign irAssign = (IRAssign) stmt;
                    IRVar x = irAssign.x;
                    IRExp e = irAssign.e;
                    // TODO
                }
                else if (stmt instanceof IRWhile) {
                    IRWhile irWhile = (IRWhile) stmt;
                    IRExp e = irWhile.e;
                    IRStmt s = irWhile.s;
                    // TODO
                }
                else if (stmt instanceof IRNewfun) {
                    IRNewfun irNewfun = (IRNewfun) stmt;
                    IRVar x = irNewfun.x;
                    IRMethod m = irNewfun.m;
                    IRNum n = irNewfun.n;
                    // TODO
                }
                else if (stmt instanceof IRNew) {
                    IRNew irNew = (IRNew) stmt;
                    IRVar x = irNew.x;
                    IRExp e1 = irNew.e1;
                    IRExp e2 = irNew.e2;
                    // TODO
                }
                else if (stmt instanceof IRToObj) {
                    IRToObj irToObj = (IRToObj) stmt;
                    IRVar x = irToObj.x;
                    IRExp e = irToObj.e;
                    // TODO
                }
                else if (stmt instanceof IRUpdate) {
                    IRUpdate irUpdate = (IRUpdate) stmt;
                    IRExp e1 = irUpdate.e1;
                    IRExp e2 = irUpdate.e2;
                    IRExp e3 = irUpdate.e3;
                    // TODO
                }
                else if (stmt instanceof IRDel) {
                    IRDel irDel = (IRDel) stmt;
                    IRScratch x = irDel.x;
                    IRExp e1 = irDel.e1;
                    IRExp e2 = irDel.e2;
                    // TODO
                }
                else if (stmt instanceof IRTry) {
                    IRTry irTry = (IRTry) stmt;
                    IRStmt s1 = irTry.s1, s2 = irTry.s2, s3 = irTry.s3;
                    IRPVar x = irTry.x;
                    // TODO
                }
                else if (stmt instanceof IRThrow) {
                    IRThrow irThrow = (IRThrow) stmt;
                    IRExp e = irThrow.e;
                    // TODO
                }
                else if (stmt instanceof IRJump) {
                    IRJump irJump = (IRJump) stmt;
                    String lbl = irJump.lbl;
                    IRExp e = irJump.e;
                    // TODO
                }
                else if (stmt instanceof IRLbl) {
                    IRLbl irLbl = (IRLbl) stmt;
                    String lbl = irLbl.lbl;
                    IRStmt s = irLbl.s;
                    // TODO
                }
                else if (stmt instanceof IRCall) {
                    IRCall irCall = (IRCall) stmt;
                    IRExp e1 = irCall.e1, e2 = irCall.e2, e3 = irCall.e3;
                    IRVar x = irCall.x;
                    // TODO
                }
                else if (stmt instanceof IRFor) {
                    IRFor irFor = (IRFor) stmt;
                    IRExp e = irFor.e;
                    IRVar x = irFor.x;
                    IRStmt s = irFor.s;
                    // TODO
                }
                else if (stmt instanceof IRMerge) {
                    IRMerge irMerge = (IRMerge) stmt;
                    // TODO
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
        
        // TODO
    }
}
