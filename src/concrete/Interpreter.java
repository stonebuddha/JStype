package concrete;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import ir.*;

import java.util.AbstractMap;
import java.util.ArrayList;
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
            if (t instanceof Domains.StmtTerm) {
                IRStmtVisitor stmtV = new IRStmtVisitor() {
                    @Override
                    public Object forDecl(IRDecl irDecl) {
                        ImmutableList<Map.Entry<IRPVar, IRExp>> bind = irDecl.bind;
                        IRStmt s = irDecl.s;
                        ArrayList<IRPVar> xs = new ArrayList<>();
                        ArrayList<Domains.BValue> bvs = new ArrayList<>();
                        for (Map.Entry<IRPVar, IRExp> entry : bind) {
                            xs.add(entry.getKey());
                            bvs.add(eval(entry.getValue()));
                        }
                        Map.Entry<Domains.Store, ArrayList<Domains.Address>> sa = Utils.alloc(store, bvs);
                        ArrayList<Domains.Address> as = sa.getValue();
                        ArrayList<Map.Entry<IRPVar, Domains.Address>> xas = new ArrayList<>();
                        for (int i = 0; i < xs.size(); i += 1) {
                            xas.add(new AbstractMap.SimpleImmutableEntry<>(xs.get(i), as.get(i)));
                        }
                        Domains.Env env1 = env.extendAll(
                                ImmutableList.<Map.Entry<IRPVar, Domains.Address>>builder().addAll(xas).build()
                        );
                        return new State(Domains.Term.fromStmt(s), env1, sa.getKey(), pad, ks);
                    }

                    @Override
                    public Object forSDecl(IRSDecl irSDecl) {
                        return null;
                    }

                    @Override
                    public Object forSeq(IRSeq irSeq) {
                        return null;
                    }

                    @Override
                    public Object forIf(IRIf irIf) {
                        return null;
                    }

                    @Override
                    public Object forWhile(IRWhile irWhile) {
                        return null;
                    }

                    @Override
                    public Object forAssign(IRAssign irAssign) {
                        return null;
                    }

                    @Override
                    public Object forCall(IRCall irCall) {
                        return null;
                    }

                    @Override
                    public Object forNew(IRNew irNew) {
                        return null;
                    }

                    @Override
                    public Object forNewfun(IRNewfun irNewfun) {
                        return null;
                    }

                    @Override
                    public Object forToObj(IRToObj irToObj) {
                        return null;
                    }

                    @Override
                    public Object forDel(IRDel irDel) {
                        return null;
                    }

                    @Override
                    public Object forUpdate(IRUpdate irUpdate) {
                        return null;
                    }

                    @Override
                    public Object forThrow(IRThrow irThrow) {
                        return null;
                    }

                    @Override
                    public Object forTry(IRTry irTry) {
                        return null;
                    }

                    @Override
                    public Object forLbl(IRLbl irLbl) {
                        return null;
                    }

                    @Override
                    public Object forJump(IRJump irJump) {
                        return null;
                    }

                    @Override
                    public Object forFor(IRFor irFor) {
                        return null;
                    }

                    @Override
                    public Object forMerge(IRMerge irMerge) {
                        return null;
                    }
                };

                return (State)((Domains.StmtTerm) t).s.accept(stmtV);
            } else {
                Domains.Value v = ((Domains.ValueTerm)t).v;
                if (v instanceof Domains.BValue) {
                    Domains.BValue bv = (Domains.BValue)v;
                } else if (v instanceof Domains.EValue) {
                    Domains.EValue ev = (Domains.EValue)v;
                } else {
                    Domains.JValue jv = (Domains.JValue)v;
                }
            }
        }
    }

    public static IRStmt readIR(String file) {
        // TODO
        return null;
    }
}
