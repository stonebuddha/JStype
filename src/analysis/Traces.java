package analysis;

import fj.Hash;
import fj.P;
import fj.P4;
import fj.P3;
import fj.data.List;
import immutable.FHashMap;
import ir.IRMerge;
import ir.IRStmt;
import ir.IRVar;
import ir.JSClass;

import java.math.BigInteger;
import java.util.SortedSet;

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

    public static class StackCFA extends Trace {
        public Integer k, h, pp;
        public List<Integer> tr;
        final int recordHash;
        static final Hash<P4<Integer, Integer, Integer, List<Integer>>> hasher = Hash.p4Hash(Hash.anyHash(), Hash.anyHash(), Hash.anyHash(), Hash.anyHash());
        public StackCFA(Integer k, Integer h, Integer pp, List<Integer> tr) {
            assert k >= h;
            this.k = k; this.h = h; this.pp = pp;
            this.tr = tr;
            this.recordHash = hasher.hash(P.p(k, h, pp, tr));
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof StackCFA && k.equals(((StackCFA) obj).k) && h.equals(((StackCFA) obj).h) && pp.equals(((StackCFA) obj).pp) && tr.equals(((StackCFA) obj).tr));
        }

        @Override
        public int hashCode() {
            return recordHash;
        }

        public StackCFA update(IRStmt s) {
            return new StackCFA(k, h, s.id, tr);
        }

        public StackCFA update(Domains.Env env, Domains.Store store, Domains.BValue self, Domains.BValue args, IRStmt s){
            return new StackCFA(k, h, s.id, tr.cons(pp).take(k));
        }

        public Domains.AddressSpace.Address toAddr() {
            return new Domains.AddressSpace.Address(TraceUtils.IntsToBigInt(tr.take(h), pp));
        }

        public Domains.AddressSpace.Address makeAddr(IRVar x) {
            return new Domains.AddressSpace.Address(TraceUtils.IntsToBigInt(tr.take(h), x.id));
        }

        public static StackCFA apply(Integer k, Integer h) {
            return new StackCFA(k, h, 0, List.list());
        }
    }

    // K-merge Nodes sensetive
    public static class KMNS extends Trace {
        public Integer k, pp;
        public List<Integer> tr;
        final int recordHash;
        static final Hash<P3<Integer, Integer, List<Integer>>> hasher = Hash.p3Hash(Hash.anyHash(), Hash.anyHash(), Hash.anyHash());
        public KMNS(Integer k, Integer pp, List<Integer> tr) {
            this.k = k; this.pp = pp;
            this.tr = tr;
            this.recordHash = hasher.hash(P.p(k, pp, tr));
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof KMNS && k.equals(((KMNS) obj).k) && pp.equals(((KMNS) obj).pp) && tr.equals(((KMNS) obj).tr));
        }

        @Override
        public int hashCode() {
            return recordHash;
        }

        public KMNS update(IRStmt s) {
            if (s instanceof IRMerge)
                return new KMNS(k, s.id, tr.cons(s.id).take(k));
            else return new KMNS(k, s.id, tr);
        }

        public KMNS update(Domains.Env env, Domains.Store store, Domains.BValue self, Domains.BValue args, IRStmt s){
            return this.update(s);
            //return new KMNS(k, s.id, tr.cons(pp).take(k));
        }

        public Domains.AddressSpace.Address toAddr() {
            return new Domains.AddressSpace.Address(TraceUtils.IntsToBigInt(tr, pp));
        }

        public Domains.AddressSpace.Address makeAddr(IRVar x) {
            return new Domains.AddressSpace.Address(TraceUtils.IntsToBigInt(tr, x.id));
        }

        public static KMNS apply(Integer k) {
            return new KMNS(k, 0, List.list());
        }
    }

    public static class TraceUtils {
        public static BigInteger IntsToBigInt(List<Integer> tr, Integer pp) {
            BigInteger tracePart = tr.foldLeft((acc, e) ->
                acc.shiftLeft(32).add(BigInteger.valueOf(e)), BigInteger.valueOf(0));
            return tracePart.shiftLeft(32).add(BigInteger.valueOf(pp));
        }
    }

}
