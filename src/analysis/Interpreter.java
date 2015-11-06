package analysis;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import analysis.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    public static Map<Integer, ImmutableSet<Domains.BValue>> runner(String[] args) {
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
        
        // TODO
    }
}
