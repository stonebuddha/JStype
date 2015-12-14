package analysis;

import ast.Program;
import fj.*;
import fj.data.*;
import immutable.FHashSet;
import ir.*;
import analysis.init.*;
import analysis.Traces.Trace;
import translator.*;

import java.io.*;
import java.util.Comparator;
import java.util.PriorityQueue;

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
        public static HashMap<Trace, P3<Trace, IRVar, FHashSet<Domains.AddressSpace.Address>>> prunedInfo = new HashMap<>(Equal.anyEqual(), Hash.anyHash());
        public static HashMap<Integer, FHashSet<Domains.BValue>> outputMap = new HashMap<>(Equal.anyEqual(), Hash.anyHash());


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
            Mutable.outputMap.clear();
        }
    }

    public static void main(String[] args) {
        try {
            HashMap<Integer, FHashSet<Domains.BValue>> result = runner(args);
            for (Integer p : result) {
                FHashSet<Domains.BValue> values = result.get(p).some();
                System.out.println(p + " -->");
                for (Domains.BValue bv : values) {
                    System.out.println("    " + bv);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static HashMap<Integer, FHashSet<Domains.BValue>> runner(String[] args) throws IOException {
        Mutable.clear();
        PruneScratch.clear();
        PruneStoreToo.clear();

        PriorityQueue<P2<Integer, Trace>> work = new PriorityQueue<P2<Integer, Trace>>(new Comparator<P2<Integer, Trace>>() {
            @Override
            public int compare(P2<Integer, Trace> p1, P2<Integer, Trace> p2) {
                return Integer.compare(p2._1(), p1._1());
            }
        });

        // default: flow-sensitive context-insensitive
        //Trace initTrace = new Traces.FSCI(0);
        //Trace initTrace = Traces.StackCFA.apply(2000, 1000);
        Trace initTrace = Traces.KMNS.apply(100);
        Mutable.splitStates = false;
        IRStmt ast = readIR(args[0]);
        HashMap<Trace, State> memo = new HashMap<Trace, State>(Equal.anyEqual(), Hash.anyHash());

        try {
            State initSigma = Init.initState(ast, initTrace);
            for (State sigma : process(initSigma)) {
                Option<State> memoSigma = memo.get(sigma.trace);
                if (memoSigma.isNone()) {
                    memo.set(sigma.trace, sigma);
                }
                else {
                    memo.set(sigma.trace, sigma.merge(memoSigma.some()));
                }
                work.add(P.p(sigma.order(), sigma.trace));
            }
            do {
                while (!work.isEmpty()) {
                    Trace trace = work.poll()._2();
                    while (!work.isEmpty() && work.peek()._2().equals(trace)) {
                        work.poll();
                    }

                    for (State sigma : process(memo.get(trace).some())) {
                        Option<State> memoSigma = memo.get(sigma.trace);
                        if (memoSigma.isNone()) {
                            memo.set(sigma.trace, sigma);
                            work.add(P.p(sigma.order(), sigma.trace));
                        }
                        else {
                            State merged = sigma.merge(memoSigma.some());
                            if (!memoSigma.some().equals(merged)) {
                                memo.set(sigma.trace, merged);
                                work.add(P.p(merged.order(), merged.trace));
                            }
                        }
                    }
                }

                for (Trace mtrace : Mutable.prunedInfo) {
                    Trace ptrace = Mutable.prunedInfo.get(mtrace).some()._1();
                    IRVar x = Mutable.prunedInfo.get(mtrace).some()._2();
                    FHashSet<Domains.AddressSpace.Address> as = Mutable.prunedInfo.get(mtrace).some()._3();
                    P2<Domains.Store, Domains.Scratchpad> valuePruned = PruneStoreToo.apply(ptrace);
                    Domains.Store pstore = valuePruned._1();
                    Domains.Scratchpad ppad = valuePruned._2();

                    Option<State> memoSigma = memo.get(mtrace);
                    if (memoSigma.isSome()) {
                        State sigma1 = memoSigma.some();
                        Domains.Store new_store;
                        Domains.Scratchpad new_pad;
                        if (x instanceof IRPVar && pstore.toValueContains(as)) {
                            new_store = pstore.extend(as, Domains.BValue.Bot).merge(sigma1.store);
                            new_pad = ppad.merge(sigma1.pad);
                        }
                        else if (x instanceof IRScratch) {
                            new_store = pstore.merge(sigma1.store);
                            new_pad = ppad.update(((IRScratch)x), Domains.BValue.Bot).merge(sigma1.pad);
                        }
                        else {
                            new_store = pstore.merge(sigma1.store);
                            new_pad = ppad.merge(sigma1.pad);
                        }
                        State merged = new State(sigma1.t, sigma1.env, new_store, new_pad, sigma1.ks, sigma1.trace);
                        if (!merged.equals(sigma1)) {
                            memo.set(mtrace, merged);
                            work.add(P.p(merged.order(), merged.trace));
                        }
                    }
                }
                Mutable.prunedInfo.clear();
            } while (!work.isEmpty());

            return Mutable.outputMap;
        } catch (Exception e) {
            System.out.println("Exception occurred: "+ e.getMessage() + "\n");
            for (StackTraceElement element : e.getStackTrace()) {
                System.out.println(element);
            }
            return Mutable.outputMap;
        }
    }

    public static Trace optionToTrace(String str) {
        // TODO
        return null;
    }

    public static Boolean shouldSplitStates(String str) {
        // TODO
        return null;
    }

    public static IRStmt readIR(final String file) throws IOException {
        final File f = new File(file);
        final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        String data;
        final StringBuilder builder = new StringBuilder();
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

    public static FHashSet<State> process(State initSigma) {
        List<State> todo = List.list(initSigma);
        FHashSet<State> done = FHashSet.empty();
        FHashSet<State> sigmas = FHashSet.empty();

        while (todo.isNotEmpty()) {
            sigmas = todo.head().next();
            todo = todo.tail();

            while (sigmas.size() == 1) {
                if (sigmas.head().isMerge()) {
                    done = done.insert(sigmas.head());
                    sigmas = FHashSet.empty();
                }
                else {
                    sigmas = sigmas.head().next();
                }
            }

            for (State sigma : sigmas) {
                if (sigma.isMerge()) {
                    done = done.insert(sigma);
                } else {
                    todo = todo.cons(sigma);
                }
            }
        }

        return done;
    }

    public static class PruneScratch {
        public static HashMap<Trace, Domains.Scratchpad> pruned = new HashMap<>(Equal.anyEqual(), Hash.anyHash());

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
        public static HashMap<Trace, P2<Domains.Store, Domains.Scratchpad>> pruned = new HashMap<>(Equal.anyEqual(), Hash.anyHash());

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
        int recordHash;
        boolean calced;
        static final Hash<P6<Domains.Term, Domains.Env, Domains.Store, Domains.Scratchpad, Domains.KontStack, Trace>> hash = Hash.p6Hash(Hash.anyHash(), Hash.anyHash(), Hash.anyHash(), Hash.anyHash(), Hash.anyHash(), Hash.anyHash());

        public State(Domains.Term t, Domains.Env env, Domains.Store store, Domains.Scratchpad pad, Domains.KontStack ks, Trace trace) {
            this.t = t;
            this.env = env;
            this.store = store;
            this.pad = pad;
            this.ks = ks;
            this.trace = trace;
            this.calced = false;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof State && t.equals(((State) obj).t) && env.equals(((State) obj).env) && store.equals(((State) obj).store) && pad.equals(((State) obj).pad) && ks.equals(((State) obj).ks) && trace.equals(((State) obj).trace));
        }

        @Override
        public int hashCode() {
            if (calced) {
                return recordHash;
            } else {
                recordHash = hash.hash(P.p(t, env, store, pad, ks, trace));
                calced = true;
                return recordHash;
            }
        }

        public State merge(State sigma) {
            // assert( t == ς.t && τ == ς.τ )
            return new State(t, env.merge(sigma.env), store.merge(sigma.store), pad.merge(sigma.pad), ks.merge(sigma.ks), trace);
        }

        public Boolean isMerge() {
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

        public FHashSet<State> next() {
            FHashSet<State> ret = FHashSet.empty();

            if (t instanceof Domains.StmtTerm) {
                IRStmt stmt = ((Domains.StmtTerm) t).s;

                if (stmt instanceof IRDecl) {
                    IRDecl irDecl = (IRDecl) stmt;
                    List<P2<IRPVar, IRExp>> bind = irDecl.bind;
                    IRStmt s = irDecl.s;
                    List<IRPVar> xs = bind.map((x) -> x._1());
                    List<IRExp> es = bind.map((x) -> x._2());
                    List<Domains.AddressSpace.Address> as = trace.makeAddrs(xs.map((x) -> ((IRVar) x)));
                    Domains.Store store1 = Utils.alloc(store, as, es.map((e) -> eval(e)));
                    ret = ret.insert(new State(new Domains.StmtTerm(s), env.extendAll(xs.zip(as)), store1, pad, ks, trace.update(s)));
                } else if (stmt instanceof IRSDecl) {
                    IRSDecl irSDecl = (IRSDecl) stmt;
                    Integer num = irSDecl.num;
                    IRStmt s = irSDecl.s;
                    ret = ret.insert(new State(new Domains.StmtTerm(s), env, store, Domains.Scratchpad.apply(num), ks, trace.update(s)));
                }
                else if (stmt instanceof IRSeq) {
                    IRSeq irSeq = (IRSeq) stmt;
                    IRStmt s = irSeq.ss.head();
                    List<IRStmt> ss = irSeq.ss.tail();
                    ret = ret.insert(new State(new Domains.StmtTerm(s), env, store, pad, ks.push(new Domains.SeqKont(ss)), trace.update(s)));
                }
                else if (stmt instanceof IRIf) {
                    IRIf irIf = (IRIf) stmt;
                    IRExp e = irIf.e;
                    IRStmt s1 = irIf.s1;
                    IRStmt s2 = irIf.s2;
                    Domains.Bool b = eval(e).b;
                    if (b.equals(Domains.Bool.True)) {
                        ret = ret.insert(new State(new Domains.StmtTerm(s1), env, store, pad, ks, trace.update(s1)));
                    }
                    else if (b.equals(Domains.Bool.False)) {
                        ret = ret.insert(new State(new Domains.StmtTerm(s2), env, store, pad, ks, trace.update(s2)));
                    }
                    else if (b.equals(Domains.Bool.Top)) {
                        ret = ret.insert(new State(new Domains.StmtTerm(s1), env, store, pad, ks, trace.update(s1)));
                        ret = ret.insert(new State(new Domains.StmtTerm(s2), env, store, pad, ks, trace.update(s2)));
                    }
                }
                else if (stmt instanceof IRAssign) {
                    IRAssign irAssign = (IRAssign) stmt;
                    IRVar x = irAssign.x;
                    IRExp e = irAssign.e;
                    Domains.BValue bv = eval(e);
                    if (x instanceof IRPVar) {
                        ret = ret.union(advanceBV(bv, store.extend(env.apply(((IRPVar) x)).some(), bv), pad, ks));
                    }
                    else if (x instanceof IRScratch) {
                        ret = ret.union(advanceBV(bv, store, pad.update(((IRScratch) x), bv), ks));
                    }

                }
                else if (stmt instanceof IRWhile) {
                    IRWhile irWhile = (IRWhile) stmt;
                    IRExp e = irWhile.e;
                    IRStmt s = irWhile.s;
                    Domains.Bool b = eval(e).b;
                    if (b.equals(Domains.Bool.True)) {
                        ret = ret.insert(new State(new Domains.StmtTerm(s),
                                env,
                                store,
                                pad,
                                ks.push(new Domains.WhileKont(e, s)),
                                trace.update(s)));
                    }
                    else if (b.equals(Domains.Bool.False)) {
                        ret = ret.union(advanceBV(Domains.Undef.BV, store, pad, ks));
                    }
                    else if (b.equals(Domains.Bool.Top)) {
                        ret = ret.union(advanceBV(Domains.Undef.BV, store, pad, ks));
                        ret = ret.insert(new State(new Domains.StmtTerm(s),
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
                        ret = ret.union(advanceBV(bv1, store1.extend(env.apply(((IRPVar) x)).some(), bv1), pad, ks));
                    }
                    else if (x instanceof IRScratch) {
                        ret = ret.union(advanceBV(bv1, store1, pad.update(((IRScratch) x), bv1), ks));
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
                        ret = ret.insert(new State(new Domains.ValueTerm(Utils.Errors.typeError), env, store, pad, ks, trace));
                    }
                    P2<Domains.Store, Domains.Scratchpad> sr = Utils.refineExc(e1, store3, env, pad1, Utils.Filters.IsFunc);
                    Domains.Store _store3 = sr._1();
                    Domains.Scratchpad _pad1 = sr._2();
                    ret = ret.union(Utils.applyClo(bv1, bv, bv2, x, env, _store3, _pad1, ks, trace));

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
                        ret = ret.union(advanceBV(bv, _store1, _pad1, ks));
                    }
                    if (exc.isSome()) {
                        Domains.EValue ev = exc.some();
                        ret = ret.union(advanceEV(ev, env, store, pad, ks, trace));
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
                        ret = ret.union(advanceBV(bv, _store1, _pad, ks));
                    }
                    if (exc.isSome()) {
                        Domains.EValue ev = exc.some();
                        ret = ret.union(advanceEV(ev, env, store, pad, ks, trace));
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
                        ret = ret.union(advanceBV(Domains.Undef.BV, _store1, _pad1, ks));
                    }
                    if (exc.isSome()) {
                        Domains.EValue ev = exc.some();
                        ret = ret.union(advanceEV(ev, env, store, pad, ks, trace));
                    }
                }
                else if (stmt instanceof IRTry) {
                    IRTry irTry = (IRTry) stmt;
                    IRStmt s1 = irTry.s1, s2 = irTry.s2, s3 = irTry.s3;
                    IRPVar x = irTry.x;
                    ret = ret.insert(new State(new Domains.StmtTerm(s1),
                            env,
                            store,
                            pad,
                            ks.push(new Domains.TryKont(x, s2, s3)),
                            trace.update(s1)));
                }
                else if (stmt instanceof IRThrow) {
                    IRThrow irThrow = (IRThrow) stmt;
                    IRExp e = irThrow.e;
                    ret = ret.union(advanceEV(new Domains.EValue(eval(e)), env, store, pad, ks, trace));
                }
                else if (stmt instanceof IRJump) {
                    IRJump irJump = (IRJump) stmt;
                    String lbl = irJump.lbl;
                    IRExp e = irJump.e;
                    ret = ret.union(advanceJV(new Domains.JValue(lbl, eval(e)), store, pad, ks));
                }
                else if (stmt instanceof IRLbl) {
                    IRLbl irLbl = (IRLbl) stmt;
                    String lbl = irLbl.lbl;
                    IRStmt s = irLbl.s;
                    ret = ret.insert(new State(new Domains.StmtTerm(s),
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
                        ret = ret.insert(new State(new Domains.ValueTerm(Utils.Errors.typeError), env, store, pad, ks, trace));
                    }
                    P2<Domains.Store, Domains.Scratchpad> sr = Utils.refineExc(e1, store, env, pad, Utils.Filters.IsFunc);
                    Domains.Store _store = sr._1();
                    Domains.Scratchpad _pad = sr._2();
                    ret = ret.union(Utils.applyClo(bv1, eval(e2), eval(e3), x, env, _store, _pad, ks, trace));
                }
                else if (stmt instanceof IRFor) {
                    IRFor irFor = (IRFor) stmt;
                    IRExp e = irFor.e;
                    IRVar x = irFor.x;
                    IRStmt s = irFor.s;
                    FHashSet<Domains.Str> keys = Utils.objAllKeys(eval(e), store);
                    if (keys.size() > 0) {
                        Domains.Str acc = Domains.Str.Bot;
                        for (Domains.Str key : keys) {
                            acc = acc.merge(key);
                        }
                        Domains.BValue uber = Domains.Str.inject(acc);
                        if (x instanceof IRPVar) {
                            ret = ret.insert(new State(new Domains.StmtTerm(s), env, store.extend(env.apply(((IRPVar) x)).some(), uber), pad, ks.push(new Domains.ForKont(uber, x, s)), trace.update(s)));
                        }
                        else if (x instanceof IRScratch) {
                            ret = ret.insert(new State(new Domains.StmtTerm(s), env, store, pad.update(((IRScratch) x), uber), ks.push(new Domains.ForKont(uber, x, s)), trace.update(s)));
                        }
                    }
                }
                else if (stmt instanceof IRMerge) {
                    IRMerge irMerge = (IRMerge) stmt;
                    ret = ret.union(advanceBV(Domains.Undef.BV, store, pad, ks));
                }
                else if (stmt instanceof IRPrint) {
                    IRExp e = ((IRPrint) stmt).e;
                    Option<FHashSet<Domains.BValue>> tmp = Mutable.outputMap.get(((IRPrint) stmt).id);
                    if (tmp.isNone()) {
                        Mutable.outputMap.set(((IRPrint) stmt).id, FHashSet.build(eval(e)));
                    } else {
                        Mutable.outputMap.set(((IRPrint) stmt).id, tmp.some().insert(eval(e)));
                    }
                    return advanceBV(Domains.Undef.BV, store, pad, ks);
                }
                else {
                    throw new RuntimeException("malformed program");
                }
            }
            else {
                Domains.Value v = ((Domains.ValueTerm)t).v;
                if (v instanceof Domains.BValue) {
                    Domains.BValue bv = (Domains.BValue)v;
                    ret = ret.union(advanceBV(bv, store, pad, ks));
                }
                else if (v instanceof Domains.EValue) {
                    Domains.EValue ev = (Domains.EValue) v;
                    ret = ret.union(advanceEV(ev, env, store, pad, ks, trace));
                }
                else if (v instanceof Domains.JValue) {
                    Domains.JValue jv = (Domains.JValue) v;
                    ret = ret.union(advanceJV(jv, store, pad, ks));
                }
            }

            return ret;
        }

        public FHashSet<State> advanceBV(Domains.BValue bv, Domains.Store store1, Domains.Scratchpad pad1, Domains.KontStack ks1){
            FHashSet<State> ret = FHashSet.empty();
            if (ks1.top() instanceof Domains.SeqKont) {
                Domains.SeqKont sk = (Domains.SeqKont) ks1.top();
                if (sk.ss.isNotEmpty()) {
                    IRStmt s = sk.ss.head();
                    List<IRStmt> ss = sk.ss.tail();
                    ret = ret.insert(new State(new Domains.StmtTerm(s), env, store1, pad1, ks1.repl(new Domains.SeqKont(ss)), trace.update(s)));
                }
                else {
                    ret = ret.union(advanceBV(Domains.Undef.BV, store1, pad1, ks1.pop()));
                }
            }
            else if (ks1.top() instanceof Domains.WhileKont){
                Domains.WhileKont wk = (Domains.WhileKont) ks1.top();
                IRExp e = wk.e;
                IRStmt s = wk.s;
                Domains.Bool b = Eval.eval(e, env, store1, pad1).b;
                if (b.equals(Domains.Bool.True)) {
                    ret = ret.insert(new State(new Domains.StmtTerm(s), env, store1, pad1, ks1, trace.update(s)));
                }
                else if (b.equals(Domains.Bool.False)) {
                    ret = ret.union(advanceBV(Domains.Undef.BV, store1, pad1, ks1.pop()));
                }
                else if (b.equals(Domains.Bool.Top)) {
                    ret = ret.insert(new State(new Domains.StmtTerm(s), env, store1, pad1, ks1, trace.update(s)));
                    ret = ret.union(advanceBV(Domains.Undef.BV, store1, pad1, ks1.pop()));
                }
            }
            else if (ks1.top() instanceof Domains.ForKont) {
                Domains.ForKont fk = (Domains.ForKont) ks1.top();
                Domains.BValue bv1 = fk.bv;
                IRVar x = fk.x;
                IRStmt s = fk.s;
                ret = ret.union(advanceBV(Domains.Undef.BV, store1, pad1, ks1.pop()));
                if (x instanceof IRPVar) {
                    ret = ret.insert(new State(new Domains.StmtTerm(s), env, store1.extend(env.apply(((IRPVar) x)).some(), bv1), pad1, ks1, trace.update(s)));
                }
                else if (x instanceof IRScratch) {
                    ret = ret.insert(new State(new Domains.StmtTerm(s), env, store1, pad1.update(((IRScratch) x), bv1), ks1, trace.update(s)));
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
                FHashSet<Domains.KontStack> konts = store3.getKont(a);
                for (Domains.KontStack tmpKS : konts) {
                    ret = ret.union(advanceBV(bv, store3, pad1, tmpKS));
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
                    FHashSet<Domains.AddressSpace.Address> vroots = envc.addrs();
                    FHashSet<Domains.AddressSpace.Address> oroots = bv.as.union(pad2.addrs()).union(Init.keepInStore);
                    FHashSet<Domains.AddressSpace.Address> kroots;
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

                FHashSet<State> call = FHashSet.empty();
                if (!isctor || (bv.as.size() > 0)) {
                    Domains.BValue bv1;
                    if (!isctor) {
                        bv1 = bv;
                    }
                    else {
                        bv1 = Domains.AddressSpace.Addresses.inject(bv.as);
                    }
                    if (x instanceof IRPVar) {
                        call = call.insert(new State(new Domains.ValueTerm(bv1),
                                envc,
                                store3.extend(envc.apply(((IRPVar) x)).some(), bv1),
                                pad2,
                                ks1.pop(),
                                trace.update(tracec)));
                    }
                    else if (x instanceof IRScratch ){
                        call = call.insert(new State(new Domains.ValueTerm(bv1),
                                envc,
                                store3,
                                pad2.update(((IRScratch) x), bv1),
                                ks1.pop(),
                                trace.update(tracec)));
                    }
                }

                FHashSet<State> ctor = FHashSet.empty();
                if (!isctor && !bv.defAddr()) {
                    if (x instanceof IRPVar) {
                        Domains.BValue t1 = Eval.eval(x, envc, store3, pad2);
                        ctor = ctor.insert(new State(new Domains.ValueTerm(t1), envc, store3, pad2, ks1.pop(), trace.update(tracec)));
                    }
                    else if (x instanceof IRScratch) {
                        ctor = ctor.insert(new State(new Domains.ValueTerm(pad2.apply((IRScratch) x)), envc, store3, pad2, ks1.pop(), trace.update(tracec)));
                    }
                }

                ret = ret.union(call);
                ret = ret.union(ctor);
            }
            else if (ks1.top() instanceof Domains.TryKont) {
                Domains.TryKont tk = (Domains.TryKont) ks1.top();
                IRStmt s3 = tk.sf;
                ret = ret.insert(new State(new Domains.StmtTerm(s3), env, store1, pad1, ks1.repl(new Domains.FinKont(FHashSet.build(Domains.Undef.BV))), trace.update(s3)));
            }
            else if (ks1.top() instanceof Domains.CatchKont) {
                Domains.CatchKont ck = (Domains.CatchKont) ks1.top();
                IRStmt s3 = ck.sf;
                ret = ret.insert(new State(new Domains.StmtTerm(s3), env, store1, pad1, ks1.repl(new Domains.FinKont(FHashSet.build(Domains.Undef.BV))), trace.update(s3)));
            }
            else if (ks1.top() instanceof Domains.FinKont) {
                Domains.FinKont fk = (Domains.FinKont) ks1.top();
                FHashSet<Domains.Value> vs = fk.vs;
                for (Domains.Value value : vs) {
                    if (value instanceof Domains.BValue) {
                        ret = ret.union(advanceBV(bv, store1, pad1, ks1.pop()));
                    }
                    else if (value instanceof Domains.EValue) {
                        ret = ret.union(advanceEV((Domains.EValue)value, env, store1, pad1, ks1.pop(), trace));
                    }
                    else if (value instanceof Domains.JValue) {
                        ret = ret.union(advanceJV((Domains.JValue)value, store1, pad1, ks1.pop()));
                    }
                }
            }
            else if (ks1.top() instanceof Domains.LblKont) {
                ret = ret.union(advanceBV(bv, store1, pad1, ks1.pop()));
            }
            return ret;
        }

        public FHashSet<State> advanceEV(Domains.EValue ev, Domains.Env env1, Domains.Store store1, Domains.Scratchpad pad1, Domains.KontStack ks1, Trace trace1) {
            FHashSet<State> ret = FHashSet.empty();
            HashSet<Domains.AddressSpace.Address> addrsSeen = new HashSet<Domains.AddressSpace.Address>(Equal.<Domains.AddressSpace.Address>anyEqual(), Hash.<Domains.AddressSpace.Address>anyHash());
            if (ks1.exc.isNotEmpty()) {
                ret = ret.union(innerAdvance(ev, env1, store1, pad1, ks1, trace1, addrsSeen));
            }
            return ret;
        }

        private FHashSet<State> innerAdvance(Domains.EValue ev, Domains.Env env1, Domains.Store store1, Domains.Scratchpad pad1, Domains.KontStack ks1, Trace trace1, HashSet<Domains.AddressSpace.Address> addrsSeen) {
            FHashSet<State> ret = FHashSet.empty();
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

                    FHashSet<Domains.KontStack> konts = store3.getKont(a);
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
                            FHashSet<Domains.AddressSpace.Address> vroots = envc.addrs();
                            FHashSet<Domains.AddressSpace.Address> oroots = ev.bv.as.union(pad2.addrs()).union(Init.keepInStore);
                            FHashSet<Domains.AddressSpace.Address> kroots;
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

                        ret = ret.union(innerAdvance(ev, envc, store5, pad2, ks2.pop(), trace1.update(tracec), addrsSeen));
                    }
                }
            }
            else if (ks1.exc.head() == 2) {
                Domains.KontStack ks2 = ks1.toHandler();
                if (ks2.top() instanceof Domains.TryKont) {
                    IRPVar x = ((Domains.TryKont) ks2.top()).x;
                    IRStmt s2 = ((Domains.TryKont) ks2.top()).sc;
                    IRStmt s3 = ((Domains.TryKont) ks2.top()).sf;
                    ret = ret.insert(new State(new Domains.StmtTerm(s2), env1, store1.extend(env1.apply(x).some(), ev.bv), pad1, ks2.repl(new Domains.CatchKont(s3)), trace1));
                }
                else if (ks2.top() instanceof Domains.CatchKont) {
                    IRStmt s3 = ((Domains.CatchKont) ks2.top()).sf;
                    ret = ret.insert(new State(new Domains.StmtTerm(s3), env1, store1, pad1, ks2.repl(new Domains.FinKont(FHashSet.build(ev))), trace1));
                }
                else {
                    throw new RuntimeException("inconceivable");
                }
            }
            return ret;
        }


        public FHashSet<State> advanceJV(Domains.JValue jv, Domains.Store store1, Domains.Scratchpad pad1, Domains.KontStack ks1) {
            FHashSet<State> ret = FHashSet.empty();
            if (ks1.top() instanceof Domains.TryKont) {
                Domains.TryKont tk = (Domains.TryKont) ks1.top();
                IRStmt s3 = tk.sf;
                ret = ret.insert(new State(new Domains.StmtTerm(s3), env, store1, pad1, ks1.repl(new Domains.FinKont(FHashSet.build(jv))), trace));
            }
            else if (ks1.top() instanceof Domains.CatchKont) {
                Domains.CatchKont ck = (Domains.CatchKont) ks1.top();
                IRStmt s3 = ck.sf;
                ret = ret.insert(new State(new Domains.StmtTerm(s3), env, store1, pad1, ks1.repl(new Domains.FinKont(FHashSet.build(jv))), trace));
            }
            else if (ks1.top() instanceof Domains.LblKont && ((Domains.LblKont)ks1.top()).lbl.equals(jv.lbl)) {
                ret = ret.union(advanceBV(jv.bv, store1, pad1, ks1.pop()));
            }
            else if (ks1.top() != Domains.HaltKont) {
                Domains.KontStack ks2 = ks1.toSpecial(jv.lbl);
                ret = ret.union(advanceJV(jv, store1, pad1, ks2));
            }
            return ret;
        }
    }
}
