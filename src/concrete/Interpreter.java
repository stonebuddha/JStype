package concrete;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import concrete.init.Init;
import ir.*;

import java.util.*;
import java.util.function.Predicate;

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
            State state = Init.initState(ir);
            while (!state.fin()) {
                state = state.next();
            }
            return Mutable.outputMap;
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
                        return new State(new Domains.StmtTerm(s), env1, sa.getKey(), pad, ks);
                    }

                    @Override
                    public Object forSDecl(IRSDecl irSDecl) {
                        return new State(new Domains.StmtTerm(irSDecl.s), env, store, Domains.Scratchpad.apply(irSDecl.num), ks);
                    }

                    @Override
                    public Object forSeq(IRSeq irSeq) {
                        IRStmt s = irSeq.ss.get(0);
                        ImmutableList<IRStmt> ss = irSeq.ss.subList(1, irSeq.ss.size());
                        return new State(new Domains.StmtTerm(s), env, store, pad, ks.push(new Domains.SeqKont(ss)));
                    }

                    @Override
                    public Object forIf(IRIf irIf) {
                        Domains.Bool pred = (Domains.Bool)eval(irIf.e);
                        IRStmt s = (pred.equals(Domains.Bool.True) ? irIf.s1 : irIf.s2);
                        return new State(new Domains.StmtTerm(s), env, store, pad, ks);
                    }

                    @Override
                    public Object forAssign(IRAssign irAssign) {
                        IRVar x = irAssign.x;
                        Domains.BValue bv = eval(irAssign.e);
                        if (x instanceof IRPVar) {
                            return new State(
                                    new Domains.ValueTerm(bv),
                                    env,
                                    store.extend(new AbstractMap.SimpleImmutableEntry<>(env.apply((IRPVar)x), bv)),
                                    pad,
                                    ks
                            );
                        } else {
                            return new State(
                                    new Domains.ValueTerm(bv),
                                    env,
                                    store,
                                    pad.update((IRScratch)x, bv),
                                    ks
                            );
                        }
                    }

                    @Override
                    public Object forWhile(IRWhile irWhile) {
                        IRExp e = irWhile.e;
                        IRStmt s = irWhile.s;
                        Domains.Bool pred = (Domains.Bool)eval(e);
                        if (pred.equals(Domains.Bool.True)) {
                            return new State(new Domains.StmtTerm(s), env, store, pad, ks.push(new Domains.WhileKont(e, s)));
                        } else {
                            return new State(new Domains.ValueTerm(new Domains.Undef()), env, store, pad, ks);
                        }
                    }

                    @Override
                    public Object forNewfun(IRNewfun irNewfun) {
                        return null;
                    }

                    @Override
                    public Object forNew(IRNew irNew) {
                        Domains.Address af = (Domains.Address)eval(irNew.e1);
                        Domains.Address aa = (Domains.Address)eval(irNew.e2);
                        IRVar x = irNew.x;
                        Map.Entry<Domains.Store, Domains.Address> sa = Utils.allocObj(af, store);
                        Domains.Store store1 = sa.getKey();
                        Domains.Address a1 = sa.getValue();
                        Map.Entry<Domains.Store, Domains.Scratchpad> ss;
                        if (x instanceof IRPVar) {
                            ss = new AbstractMap.SimpleImmutableEntry<>(
                                    store1.extend(new AbstractMap.SimpleImmutableEntry<>(env.apply((IRPVar)x), a1)),
                                    pad
                            );
                        } else {
                            ss = new AbstractMap.SimpleImmutableEntry<>(
                                    store1,
                                    pad.update((IRScratch)x, a1)
                            );
                        }
                        Domains.Store store2 = ss.getKey();
                        Domains.Scratchpad pad1 = ss.getValue();
                        Domains.Store store3 = Utils.setConstr(store2, aa);
                        return Utils.applyClo(af, a1, aa, x, env, store3, pad1, ks);
                    }

                    @Override
                    public Object forToObj(IRToObj irToObj) {
                        Map.Entry<Domains.Value, Map.Entry<Domains.Store, Domains.Scratchpad>> obj = Utils.toObj(eval(irToObj.e), irToObj.x, env, store, pad);
                        Domains.Value v = obj.getKey();
                        Domains.Store store1 = obj.getValue().getKey();
                        Domains.Scratchpad pad1 = obj.getValue().getValue();
                        return new State(new Domains.ValueTerm(v), env, store1, pad1, ks);
                    }

                    @Override
                    public Object forUpdate(IRUpdate irUpdate) {
                        Map.Entry<Domains.Value, Domains.Store> obj = Utils.updateObj(eval(irUpdate.e1), eval(irUpdate.e2), eval(irUpdate.e3), store);
                        Domains.Value v = obj.getKey();
                        Domains.Store store1 = obj.getValue();
                        return new State(new Domains.ValueTerm(v), env, store1, pad, ks);
                    }

                    @Override
                    public Object forDel(IRDel irDel) {
                        Map.Entry<Domains.Value, Map.Entry<Domains.Store, Domains.Scratchpad>> sa = Utils.delete(eval(irDel.e1), eval(irDel.e2), irDel.x, env, store, pad);
                        Domains.Value v = sa.getKey();
                        Domains.Store store1 = sa.getValue().getKey();
                        Domains.Scratchpad pad1 = sa.getValue().getValue();
                        return new State(new Domains.ValueTerm(v), env, store1, pad1, ks);
                    }

                    @Override
                    public Object forTry(IRTry irTry) {
                        return new State(new Domains.StmtTerm(irTry.s1), env, store, pad, ks.push(new Domains.TryKont(irTry.x, irTry.s2, irTry.s3)));
                    }

                    @Override
                    public Object forThrow(IRThrow irThrow) {
                        return new State(new Domains.ValueTerm(new Domains.EValue(eval(irThrow.e))), env, store, pad, ks);
                    }

                    @Override
                    public Object forJump(IRJump irJump) {
                        return new State(new Domains.ValueTerm(new Domains.JValue(irJump.lbl, eval(irJump.e))), env, store, pad, ks);
                    }

                    @Override
                    public Object forLbl(IRLbl irLbl) {
                        return new State(new Domains.StmtTerm(irLbl.s), env, store, pad, ks.push(new Domains.LblKont(irLbl.lbl)));
                    }

                    @Override
                    public Object forCall(IRCall irCall) {
                        return Utils.applyClo(eval(irCall.e1), eval(irCall.e2), eval(irCall.e3), irCall.x, env, store, pad, ks);
                    }

                    @Override
                    public Object forFor(IRFor irFor) {
                        ImmutableList<Domains.Str> allKeys = Utils.objAllKeys(eval(irFor.e), store);
                        if (allKeys.size() > 0) {
                            Domains.Str str = allKeys.get(0);
                            ImmutableList<Domains.Str> strs = allKeys.subList(1, allKeys.size());
                            if (irFor.x instanceof IRPVar) {
                                return new State(new Domains.StmtTerm(irFor.s),
                                        env,
                                        store.extend(new AbstractMap.SimpleImmutableEntry<>(env.apply((IRPVar)irFor.x), str)),
                                        pad,
                                        ks.push(new Domains.ForKont(strs, irFor.x, irFor.s)));

                            }
                            else {
                                return new State(new Domains.StmtTerm(irFor.s),
                                        env,
                                        store,
                                        pad.update((IRScratch)irFor.x, str),
                                        ks.push(new Domains.ForKont(strs, irFor.x, irFor.s)));
                            }
                        }
                        else {
                            return new State(new Domains.ValueTerm(new Domains.Undef()), env, store, pad, ks);
                        }
                    }

                    @Override
                    public Object forMerge(IRMerge irMerge) {
                        return new State(new Domains.ValueTerm(new Domains.Undef()), env, store, pad, ks);
                    }
                };

                return (State)((Domains.StmtTerm) t).s.accept(stmtV);
            } else {
                Domains.Value v = ((Domains.ValueTerm)t).v;
                if (v instanceof Domains.BValue) {
                    Domains.BValue bv = (Domains.BValue)v;
                    if (ks.top() instanceof Domains.SeqKont) {
                        Domains.SeqKont sk = (Domains.SeqKont) ks.top();
                        if (sk.ss.size() > 0) {
                            return new State(new Domains.StmtTerm(sk.ss.get(0)),
                                    env,
                                    store,
                                    pad,
                                    ks.repl(new Domains.SeqKont(sk.ss.subList(1, sk.ss.size()))));
                        }
                        else {
                            return new State(new Domains.ValueTerm(new Domains.Undef()), env, store, pad, ks);
                        }
                    }
                    else if (ks.top() instanceof Domains.WhileKont){
                        Domains.WhileKont wk = (Domains.WhileKont) ks.top();
                        Domains.Bool pred = (Domains.Bool)eval(wk.e);
                        if (pred.equals(Domains.Bool.True)) {
                            return new State(new Domains.StmtTerm(wk.s), env, store, pad, ks);
                        }
                        else {
                            return new State(new Domains.ValueTerm(new Domains.Undef()), env, store, pad, ks);
                        }
                    }
                    else if (ks.top() instanceof Domains.ForKont) {
                        Domains.ForKont fk = (Domains.ForKont) ks.top();
                        if (fk.strs.size() > 0) {
                            if (fk.x instanceof IRPVar) {
                                return new State(new Domains.StmtTerm(fk.s),
                                        env,
                                        store.extend(new AbstractMap.SimpleImmutableEntry<>(env.apply((IRPVar)fk.x), fk.strs.get(0))),
                                        pad,
                                        ks.repl(new Domains.ForKont(fk.strs.subList(1, fk.strs.size()), fk.x, fk.s)));
                            }
                            else {
                                return new State(new Domains.StmtTerm(fk.s),
                                        env,
                                        store,
                                        pad.update((IRScratch)fk.x, fk.strs.get(0)),
                                        ks.repl(new Domains.ForKont(fk.strs.subList(1, fk.strs.size()), fk.x, fk.s)));
                            }
                        }
                        else {
                            return new State(new Domains.ValueTerm(new Domains.Undef()), env, store, pad, ks);
                        }
                    }
                    else if (ks.top() instanceof Domains.RetKont) {
                        Domains.RetKont rk = (Domains.RetKont) ks.top();
                        if (rk.isctor && !(bv instanceof Domains.Address)) {
                            if (rk.x instanceof IRPVar) {
                                return new State(new Domains.ValueTerm(Eval.eval(rk.x, rk.env, store, rk.pad)), rk.env, store, rk.pad, ks.pop());
                            }
                            else {
                                return new State(new Domains.ValueTerm(rk.pad.apply((IRScratch)rk.x)), rk.env, store, rk.pad, ks.pop());
                            }
                        }
                        else {
                            if (rk.x instanceof IRPVar) {
                                return new State(new Domains.ValueTerm(bv),
                                        rk.env,
                                        store.extend(new AbstractMap.SimpleImmutableEntry<>(rk.env.apply((IRPVar)rk.x), bv)),
                                        rk.pad,
                                        ks.pop());
                            }
                            else {
                                return new State(new Domains.ValueTerm(bv),
                                        rk.env,
                                        store,
                                        rk.pad.update((IRScratch)rk.x, bv),
                                        ks.pop());
                            }
                        }
                    }
                    else if (ks.top() instanceof Domains.TryKont) {
                        Domains.TryKont tk = (Domains.TryKont) ks.top();
                        return new State(new Domains.StmtTerm(tk.sf), env, store, pad, ks.repl(new Domains.FinKont(new Domains.Undef())));
                    }
                    else if (ks.top() instanceof Domains.CatchKont) {
                        Domains.CatchKont ck = (Domains.CatchKont) ks.top();
                        return new State(new Domains.StmtTerm(ck.sf), env, store, pad, ks.repl(new Domains.FinKont(new Domains.Undef())));
                    }
                    else if (ks.top() instanceof Domains.FinKont) {
                        Domains.FinKont fk = (Domains.FinKont) ks.top();
                        if (fk.v instanceof Domains.BValue) {
                            return new State(new Domains.ValueTerm(bv), env, store, pad, ks.pop());
                        }
                        else {
                            return new State(new Domains.ValueTerm(fk.v), env, store, pad, ks.pop());
                        }
                    }
                    else if (ks.top() instanceof Domains.LblKont) {
                        return new State(new Domains.ValueTerm(bv), env, store, pad, ks.pop());
                    }
                    else if (ks.top() instanceof Domains.HaltKont) {
                        throw new RuntimeException("trying to transition from final state");
                    }
                }
                else if (v instanceof Domains.EValue) {
                    Domains.EValue ev = (Domains.EValue) v;
                    if (ks.top() instanceof Domains.RetKont) {
                        Domains.RetKont rk = (Domains.RetKont) ks.top();
                        return new State(new Domains.ValueTerm(ev), rk.env, store, rk.pad, ks.pop());
                    }
                    else if (ks.top() instanceof Domains.TryKont) {
                        Domains.TryKont tk = (Domains.TryKont) ks.top();
                        return new State(new Domains.StmtTerm(tk.sc),
                                env,
                                store.extend(new AbstractMap.SimpleImmutableEntry<>(env.apply(tk.x), ev.bv)),
                                pad,
                                ks.repl(new Domains.CatchKont(tk.sf)));
                    }
                    else if (ks.top() instanceof Domains.CatchKont) {
                        Domains.CatchKont ck = (Domains.CatchKont) ks.top();
                        return new State(new Domains.StmtTerm(ck.sf), env, store, pad, ks.repl(new Domains.FinKont(ev)));
                    }
                    else {
                        Predicate<Domains.Kont> f = new Predicate<Domains.Kont>() {
                            @Override
                            public boolean test(Domains.Kont kont) {
                                if (kont instanceof Domains.RetKont || kont instanceof Domains.TryKont || kont instanceof Domains.CatchKont || kont instanceof Domains.HaltKont) {
                                    return false;
                                }
                                return true;
                            }
                        };
                        Domains.KontStack ks1 = ks.dropWhile(f);
                        return new State(new Domains.ValueTerm(ev), env, store, pad, ks1);
                    }
                } else {
                    Domains.JValue jv = (Domains.JValue) v;
                    if (ks.top() instanceof Domains.TryKont) {
                        Domains.TryKont tk = (Domains.TryKont) ks.top();
                        return new State(new Domains.StmtTerm(tk.sf), env, store, pad, ks.repl(new Domains.FinKont(jv)));
                    }
                    else if (ks.top() instanceof Domains.CatchKont) {
                        Domains.CatchKont ck = (Domains.CatchKont) ks.top();
                        return new State(new Domains.StmtTerm(ck.sf), env, store, pad, ks.repl(new Domains.FinKont(jv)));
                    }
                    else if (ks.top() instanceof Domains.LblKont && jv.lbl == ((Domains.LblKont)ks.top()).lbl) {
                        return new State(new Domains.ValueTerm(jv.bv), env, store, pad, ks.pop());
                    }
                    else {
                        Predicate<Domains.Kont> f = new Predicate<Domains.Kont>() {
                            @Override
                            public boolean test(Domains.Kont kont) {
                                if (kont instanceof Domains.TryKont || kont instanceof Domains.CatchKont || kont instanceof Domains.HaltKont) {
                                    return false;
                                }
                                if (kont instanceof Domains.LblKont && (((Domains.LblKont) kont).lbl) == jv.lbl) {
                                    return false;
                                }
                                return true;
                            }
                        };
                        Domains.KontStack ks1 = ks.dropWhile(f);
                        return new State(new Domains.ValueTerm(jv), env, store, pad, ks1);
                    }
                }
                return null;
            }
        }
    }

    public static IRStmt readIR(String file) {
        // TODO
        return null;
    }
}
