package concrete;

import ast.Program;
import concrete.init.Init;
import fj.*;
import fj.data.HashMap;
import fj.data.List;
import fj.data.Set;
import ir.*;
import translator.*;

import java.io.*;

/**
 * Created by wayne on 15/10/29.
 */
public class Interpreter {

    public static void main(String[] args) {
        try {
            runner(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static class Mutable {

        public static HashMap<Integer, Set<Domains.BValue>> outputMap = new HashMap<>(Equal.anyEqual(), Hash.anyHash());

        public static void clear() {
            outputMap.clear();
        }
    }

    public static HashMap<Integer, Set<Domains.BValue>> runner(String[] args) throws IOException {
        Mutable.clear();
        IRStmt ir = readIR(args[0]);
        try {
            State state = Init.initState(ir);
            while (!state.fin()) {
                /*if (state.t instanceof Domains.StmtTerm) {
                    System.out.println(((Domains.StmtTerm) state.t).s);
                    System.out.println("----");
                }*/
                state = state.next();
            }
            return Mutable.outputMap;
        } catch (Exception e) {
            System.out.println("Exception occurred: "+ e.getMessage() + "\n");
            for (StackTraceElement element : e.getStackTrace()) {
                System.out.println(element);
            }
            return Mutable.outputMap;
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

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof State && t.equals(((State) obj).t) && env.equals(((State) obj).env) && store.equals(((State) obj).store) && pad.equals(((State) obj).pad) && ks.equals(((State) obj).ks));
        }

        @Override
        public int hashCode() {
            return P.p(t, env, store, pad, ks).hashCode();
        }

        public Boolean fin() {
            return (t instanceof Domains.ValueTerm && ks.top().equals(Domains.HaltKont));
        }

        public Domains.BValue eval(IRExp e) {
            return Eval.eval(e, env, store, pad);
        }

        public State next() {
            if (t instanceof Domains.StmtTerm) {
                IRStmtVisitor stmtV = new IRStmtVisitor() {
                    @Override
                    public Object forDecl(IRDecl irDecl) {
                        List<P2<IRPVar, IRExp>> bind = irDecl.bind;
                        IRStmt s = irDecl.s;
                        P2<List<IRPVar>, List<IRExp>> tmp = List.unzip(bind);
                        List<IRPVar> xs = tmp._1();
                        List<IRExp> es = tmp._2();
                        P2<Domains.Store, List<Domains.Address>> tmp1 = Utils.alloc(store, es.map(e -> eval(e)));
                        Domains.Env env1 = env.extendAll(xs.zip(tmp1._2()));
                        return new State(new Domains.StmtTerm(s), env1, tmp1._1(), pad, ks);
                    }

                    @Override
                    public Object forSDecl(IRSDecl irSDecl) {
                        return new State(new Domains.StmtTerm(irSDecl.s), env, store, Domains.Scratchpad.apply(irSDecl.num), ks);
                    }

                    @Override
                    public Object forSeq(IRSeq irSeq) {
                        IRStmt s = irSeq.ss.head();
                        List<IRStmt> ss = irSeq.ss.tail();
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
                                    store.extend(P.p(env.apply((IRPVar)x), bv)),
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
                            return new State(new Domains.ValueTerm(Domains.Undef), env, store, pad, ks);
                        }
                    }

                    @Override
                    public Object forNewfun(IRNewfun irNewfun) {
                        IRVar x = irNewfun.x;
                        IRMethod m = irNewfun.m;
                        IRNum n = irNewfun.n;
                        Domains.Env env1 = env.filter(v -> m.freeVars.member(v));
                        P2<Domains.Store, Domains.Address> tmp = Utils.allocFun(new Domains.Clo(env1, m), eval(n), store);
                        Domains.Store store1 = tmp._1();
                        Domains.Address a1 = tmp._2();
                        if (x instanceof IRPVar) {
                            return new State(new Domains.ValueTerm(a1), env, store1.extend(P.p(env.apply((IRPVar)x), a1)), pad, ks);
                        } else {
                            return new State(new Domains.ValueTerm(a1), env, store1, pad.update((IRScratch)x, a1), ks);
                        }
                    }

                    @Override
                    public Object forNew(IRNew irNew) {
                        Domains.Address af = (Domains.Address)eval(irNew.e1);
                        Domains.Address aa = (Domains.Address)eval(irNew.e2);
                        IRVar x = irNew.x;
                        P2<Domains.Store, Domains.Address> sa = Utils.allocObj(af, store);
                        Domains.Store store1 = sa._1();
                        Domains.Address a1 = sa._2();
                        P2<Domains.Store, Domains.Scratchpad> ss;
                        if (x instanceof IRPVar) {
                            ss = P.p(store1.extend(P.p(env.apply((IRPVar)x), a1)), pad);
                        } else {
                            ss = P.p(store1, pad.update((IRScratch)x, a1));
                        }
                        Domains.Store store2 = ss._1();
                        Domains.Scratchpad pad1 = ss._2();
                        Domains.Store store3 = Utils.setConstr(store2, aa);
                        return Utils.applyClo(af, a1, aa, x, env, store3, pad1, ks);
                    }

                    @Override
                    public Object forToObj(IRToObj irToObj) {
                        IRExp e = irToObj.e;
                        IRVar x = irToObj.x;
                        P3<Domains.Value, Domains.Store, Domains.Scratchpad> obj = Utils.toObj(eval(e), x, env, store, pad);
                        Domains.Value v = obj._1();
                        Domains.Store store1 = obj._2();
                        Domains.Scratchpad pad1 = obj._3();
                        return new State(new Domains.ValueTerm(v), env, store1, pad1, ks);
                    }

                    @Override
                    public Object forUpdate(IRUpdate irUpdate) {
                        IRExp e1 = irUpdate.e1, e2 = irUpdate.e2, e3 = irUpdate.e3;
                        P2<Domains.Value, Domains.Store> obj = Utils.updateObj(eval(e1), eval(e2), eval(e3), store);
                        Domains.Value v = obj._1();
                        Domains.Store store1 = obj._2();
                        return new State(new Domains.ValueTerm(v), env, store1, pad, ks);
                    }

                    @Override
                    public Object forDel(IRDel irDel) {
                        IRExp e1 = irDel.e1, e2 = irDel.e2;
                        IRScratch x = irDel.x;
                        P3<Domains.Value, Domains.Store, Domains.Scratchpad> sa = Utils.delete(eval(e1), eval(e2), x, env, store, pad);
                        Domains.Value v = sa._1();
                        Domains.Store store1 = sa._2();
                        Domains.Scratchpad pad1 = sa._3();
                        return new State(new Domains.ValueTerm(v), env, store1, pad1, ks);
                    }

                    @Override
                    public Object forTry(IRTry irTry) {
                        IRStmt s1 = irTry.s1, s2 = irTry.s2, s3 = irTry.s3;
                        IRPVar x = irTry.x;
                        return new State(new Domains.StmtTerm(s1), env, store, pad, ks.push(new Domains.TryKont(x, s2, s3)));
                    }

                    @Override
                    public Object forThrow(IRThrow irThrow) {
                        IRExp e = irThrow.e;
                        return new State(new Domains.ValueTerm(new Domains.EValue(eval(e))), env, store, pad, ks);
                    }

                    @Override
                    public Object forJump(IRJump irJump) {
                        String lbl = irJump.lbl;
                        IRExp e = irJump.e;
                        return new State(new Domains.ValueTerm(new Domains.JValue(lbl, eval(e))), env, store, pad, ks);
                    }

                    @Override
                    public Object forLbl(IRLbl irLbl) {
                        String lbl = irLbl.lbl;
                        IRStmt s = irLbl.s;
                        return new State(new Domains.StmtTerm(s), env, store, pad, ks.push(new Domains.LblKont(lbl)));
                    }

                    @Override
                    public Object forCall(IRCall irCall) {
                        IRExp e1 = irCall.e1, e2 = irCall.e2, e3 = irCall.e3;
                        IRVar x = irCall.x;
                        return Utils.applyClo(eval(e1), eval(e2), eval(e3), x, env, store, pad, ks);
                    }

                    @Override
                    public Object forFor(IRFor irFor) {
                        IRExp e = irFor.e;
                        IRVar x = irFor.x;
                        IRStmt s = irFor.s;
                        List<Domains.Str> allKeys = Utils.objAllKeys(eval(e), store);
                        if (allKeys.length() > 0) {
                            Domains.Str str = allKeys.head();
                            List<Domains.Str> strs = allKeys.tail();
                            if (x instanceof IRPVar) {
                                return new State(
                                        new Domains.StmtTerm(s),
                                        env,
                                        store.extend(P.p(env.apply((IRPVar)x), str)),
                                        pad,
                                        ks.push(new Domains.ForKont(strs, x, s))
                                );
                            } else {
                                return new State(
                                        new Domains.StmtTerm(s),
                                        env,
                                        store,
                                        pad.update((IRScratch)x, str),
                                        ks.push(new Domains.ForKont(strs, x, s))
                                );
                            }
                        } else {
                            return new State(new Domains.ValueTerm(Domains.Undef), env, store, pad, ks);
                        }
                    }

                    @Override
                    public Object forMerge(IRMerge irMerge) {
                        return new State(new Domains.ValueTerm(Domains.Undef), env, store, pad, ks);
                    }

                    @Override
                    public Object forPrint(IRPrint irPrint) {
                        Domains.BValue bv = eval(irPrint.e);
                        System.out.println(bv);
                        return new State(new Domains.ValueTerm(Domains.Undef), env, store, pad, ks);
                    }
                };

                return (State)((Domains.StmtTerm) t).s.accept(stmtV);
            } else {
                Domains.Value v = ((Domains.ValueTerm)t).v;
                if (v instanceof Domains.BValue) {
                    Domains.BValue bv = (Domains.BValue)v;
                    if (ks.top() instanceof Domains.SeqKont) {
                        Domains.SeqKont sk = (Domains.SeqKont) ks.top();
                        if (sk.ss.length() > 0) {
                            return new State(new Domains.StmtTerm(sk.ss.head()),
                                    env,
                                    store,
                                    pad,
                                    ks.repl(new Domains.SeqKont(sk.ss.tail())));
                        }
                        else {
                            return new State(new Domains.ValueTerm(Domains.Undef), env, store, pad, ks.pop());
                        }
                    }
                    else if (ks.top() instanceof Domains.WhileKont){
                        Domains.WhileKont wk = (Domains.WhileKont) ks.top();
                        Domains.Bool pred = (Domains.Bool)eval(wk.e);
                        if (pred.equals(Domains.Bool.True)) {
                            return new State(new Domains.StmtTerm(wk.s), env, store, pad, ks);
                        }
                        else {
                            return new State(new Domains.ValueTerm(Domains.Undef), env, store, pad, ks.pop());
                        }
                    }
                    else if (ks.top() instanceof Domains.ForKont) {
                        Domains.ForKont fk = (Domains.ForKont) ks.top();
                        if (fk.strs.length() > 0) {
                            if (fk.x instanceof IRPVar) {
                                return new State(new Domains.StmtTerm(fk.s),
                                        env,
                                        store.extend(P.p(env.apply((IRPVar)fk.x), fk.strs.head())),
                                        pad,
                                        ks.repl(new Domains.ForKont(fk.strs.tail(), fk.x, fk.s)));
                            }
                            else {
                                return new State(new Domains.StmtTerm(fk.s),
                                        env,
                                        store,
                                        pad.update((IRScratch)fk.x, fk.strs.head()),
                                        ks.repl(new Domains.ForKont(fk.strs.tail(), fk.x, fk.s)));
                            }
                        }
                        else {
                            return new State(new Domains.ValueTerm(Domains.Undef), env, store, pad, ks.pop());
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
                                        store.extend(P.p(rk.env.apply((IRPVar)rk.x), bv)),
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
                        return new State(new Domains.StmtTerm(tk.sf), env, store, pad, ks.repl(new Domains.FinKont(Domains.Undef)));
                    }
                    else if (ks.top() instanceof Domains.CatchKont) {
                        Domains.CatchKont ck = (Domains.CatchKont) ks.top();
                        return new State(new Domains.StmtTerm(ck.sf), env, store, pad, ks.repl(new Domains.FinKont(Domains.Undef)));
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
                    else {
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
                                store.extend(P.p(env.apply(tk.x), ev.bv)),
                                pad,
                                ks.repl(new Domains.CatchKont(tk.sf)));
                    }
                    else if (ks.top() instanceof Domains.CatchKont) {
                        Domains.CatchKont ck = (Domains.CatchKont) ks.top();
                        return new State(new Domains.StmtTerm(ck.sf), env, store, pad, ks.repl(new Domains.FinKont(ev)));
                    }
                    else {
                        Domains.KontStack ks1 = ks.dropWhile(k -> !(k instanceof Domains.RetKont || k instanceof Domains.TryKont || k instanceof Domains.CatchKont || k.equals(Domains.HaltKont)));
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
                    else if (ks.top() instanceof Domains.LblKont && jv.lbl.equals(((Domains.LblKont)ks.top()).lbl)) {
                        return new State(new Domains.ValueTerm(jv.bv), env, store, pad, ks.pop());
                    }
                    else {
                        Domains.KontStack ks1 = ks.dropWhile(k -> {
                            if (k instanceof Domains.TryKont || k instanceof Domains.CatchKont || k.equals(Domains.HaltKont)) {
                                return false;
                            } else if (k instanceof Domains.LblKont && ((Domains.LblKont)k).lbl.equals(jv.lbl)) {
                                return false;
                            } else {
                                return true;
                            }
                        });
                        return new State(new Domains.ValueTerm(jv), env, store, pad, ks1);
                    }
                }
            }
        }
    }

    public static IRStmt readIR(String file) throws IOException {
        File f = new File(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        String data;
        StringBuilder builder = new StringBuilder();
        while ((data = br.readLine()) != null) {
            builder.append(data + "\n");
        }
        Parser.init();
        Program program = Parser.parse(builder.toString(), f.getCanonicalPath());
        //System.err.println(program.accept(PrettyPrinter.formatProgram));
        program = AST2AST.transform(program);
        System.err.println(program.accept(PrettyPrinter.formatProgram));
        IRStmt stmt = AST2IR.transform(program);
        //System.err.println(stmt);
        stmt = IR2IR.transform(stmt);
        System.err.println(stmt);
        return stmt;
    }
}
