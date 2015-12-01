package analysis;

import fj.data.List;
import immutable.FHashMap;
import ir.IRStmt;
import ir.IRVar;
import ir.JSClass;

/**
 * Created by BenZ on 15/11/28.
 */

public class Traces {
    public static abstract class Trace {
        public abstract Trace update(IRStmt s);
        public abstract Trace update(Domains.Env env, Domains.Store store, Domains.BValue self, Domains.BValue args, IRStmt s);

        public Trace update(Trace trace) {
            return trace;
        }

        public abstract Domains.AddressSpace.Address toAddr();
        public abstract Domains.AddressSpace.Address makeAddr(IRVar x);

        public List<Domains.AddressSpace.Address> makeAddrs(List<IRVar> xs) {
            return xs.map(x -> makeAddr(x));
        }

        public Domains.AddressSpace.Address modAddr(Domains.AddressSpace.Address a, JSClass c) {
            if (c2off.get(c).isNone()) {
                return a;
            }
            else {
                return Domains.AddressSpace.Address.apply(a.loc.intValue() + c2off.get(c).some());
            }
        }

        public static FHashMap<JSClass, Integer> c2off = FHashMap.build(
                JSClass.CObject, 0,
                JSClass.CArguments, 1,
                JSClass.CArray, 2,
                JSClass.CString, 3,
                JSClass.CBoolean, 4,
                JSClass.CNumber, 5,
                JSClass.CDate, 6,
                JSClass.CError, 7,
                JSClass.CRegexp, 8);

        public static Integer getBase(Domains.AddressSpace.Address a) {
            return a.loc.intValue();
        }
    }

    public static class FSCI extends Trace {
        public Integer pp;
        final int recordHash;
        public FSCI(Integer pp) { this.pp = pp; this.recordHash = pp; }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof FSCI && pp.equals(((FSCI) obj).pp));
        }

        @Override
        public int hashCode() {
            return recordHash;
        }

        public FSCI update(IRStmt s) {
            return new FSCI(s.id);
        }

        public FSCI update(Domains.Env env, Domains.Store store, Domains.BValue self, Domains.BValue args, IRStmt s){
            return new FSCI(s.id);
        }

        public Domains.AddressSpace.Address toAddr() {
            return Domains.AddressSpace.Address.apply(pp);
        }

        public Domains.AddressSpace.Address makeAddr(IRVar x) {
            return Domains.AddressSpace.Address.apply(x.id);
        }

        public static FSCI apply() {
            return new FSCI(0);
        }
    }

}
