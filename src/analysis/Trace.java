package analysis;

import ir.*;

/**
 * Created by BenZ on 15/11/6.
 */
public abstract class Trace {
    public abstract Trace update(Domains.StmtTerm s);
    public abstract Trace update(Domains.Env env, Domains.Store store, Domains.BValue self, Domains.BValue args, Domains.StmtTerm s);

    public Trace update(Trace trace) {
        return trace;
    }

    public abstract Domains.AddressSpace.Address toAddr();
    public abstract Domains.AddressSpace.Address makeAddr(IRVar x);
    // TODO

    public static Integer getBase(Domains.AddressSpace.Address a) {
        return a.loc.intValue();
    }
}


