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
            if (t instanceof Domains.StmtTerm) {
                IRStmt s = ((Domains.StmtTerm) t).s;
                if (s instanceof IRDecl) {
                    List<P2<IRPVar, IRExp>> bind = ((IRDecl) s).bind;
                    IRStmt s1 = ((IRDecl) s).s;
                    // TODO
                } else if (s instanceof IRSDecl) {
                    Integer num = ((IRSDecl) s).num;
                    IRStmt s1 = ((IRSDecl) s).s;
                    return Set.set(Ord.hashEqualsOrd(), new State(new Domains.StmtTerm(s1), env, store, Domains.Scratchpad.apply(num), ks, trace.update(s1)));
                }
                // TODO
            }
            return null;
        }

        // TODO
    }
}
