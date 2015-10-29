package concrete.init;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import concrete.Domains;
import concrete.Interpreter;
import ir.IRPVar;
import ir.IRStmt;
import ir.JSClass;

/**
 * Created by wayne on 15/10/29.
 */
public class Init {

    public static final IRPVar window_Variable = new IRPVar(0);

    // TODO

    public static Interpreter.State initState(IRStmt s) {
        Domains.Env env = new Domains.Env(
                ImmutableMap.<IRPVar, Domains.Address>of());
        Domains.Store store = new Domains.Store(
                ImmutableMap.<Domains.Address, Domains.BValue>of(),
                ImmutableMap.<Domains.Address, Domains.Object>of());
        return new Interpreter.State(
                Domains.Term.fromStmt(s),
                env,
                store,
                Domains.Scratchpad.apply(0),
                new Domains.KontStack(ImmutableList.<Domains.Kont>of(new Domains.HaltKont())));
    }

    public static final ImmutableMap<JSClass, ImmutableSet<Domains.Str>> noenum = null; // TODO

    public static final ImmutableMap<JSClass, ImmutableSet<Domains.Str>> nodelete = null; // TODO

    public static final ImmutableMap<JSClass, ImmutableSet<Domains.Str>> noupdate = null; // TODO

    public static final ImmutableMap<Domains.Address, JSClass> classFromAddress = null; // TODO
}
