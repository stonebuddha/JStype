package analysis;

import fj.Ord;
import fj.P;
import fj.data.List;
import fj.data.TreeMap;
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

        public static final TreeMap<JSClass, Integer> c2off = TreeMap.treeMap(Ord.<JSClass>hashEqualsOrd(),
                P.p(JSClass.CObject, 0),
                P.p(JSClass.CArguments, 1),
                P.p(JSClass.CArray, 2),
                P.p(JSClass.CString, 3),
                P.p(JSClass.CBoolean, 4),
                P.p(JSClass.CNumber, 5),
                P.p(JSClass.CDate, 6),
                P.p(JSClass.CError, 7),
                P.p(JSClass.CRegexp, 8));

        public static Integer getBase(Domains.AddressSpace.Address a) {
            return a.loc.intValue();
        }
    }

    public static class FSCI extends Trace {
        public Integer pp;
        public FSCI(Integer pp) { this.pp = pp; }

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
