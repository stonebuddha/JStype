package concrete;

import com.google.common.collect.ImmutableSet;
import ir.IRExp;
import ir.IRStmt;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wayne on 15/10/29.
 */
public class Interpreter {

    public static class Mutable {

        public static Boolean catchExc = false;
        public static Map<Integer, ImmutableSet<Domains.BValue>> outputMap = new HashMap<>();

        public static void clear() {
            catchExc = false;
            outputMap.clear();
        }
    }

    public static Map<Integer, ImmutableSet<Domains.BValue>> runner(String[] args) {
        Mutable.clear();
        IRStmt ir = readIR(args[0]);
        try {

        } catch (Exception e) {
            if (Mutable.catchExc) {
                return Mutable.outputMap;
            } else {
                return Mutable.outputMap;
            }
        }
    }

    public static class State {
        public Domains.Term t;
        public Domains.Env env;
        public Domains.Store store;
        public Domains.Scratchpad pad;
        public Domains.KontStack ks;

        public State(Domains.Term t, Domains.Env env, Domains.Store store, Domains.Scratchpad pad, Domains.KontStack ks) {
            this.t = t;
            this.env = env;
            this.store = store;
            this.pad = pad;
            this.ks = ks;
        }

        public Boolean fin() {
            return (t instanceof Domains.ValueTerm && ks.top().equals(new Domains.HaltKont()));
        }

        public Domains.BValue eval(IRExp e) {
            return Eval.eval(e, env, store, pad);
        }

        public State next() {

        }
    }

    public static IRStmt readIR(String file) {
        // TODO
        return null;
    }
}
