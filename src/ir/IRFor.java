package ir;

import fj.Hash;
import fj.P;
import fj.P2;
import fj.P3;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRFor extends IRStmt {
    public final IRVar x;
    public final IRExp e;
    public final IRStmt s;
    final int recordHash;
    static final Hash<P3<IRVar, IRExp, IRStmt>> hash = Hash.p3Hash(Hash.anyHash(), Hash.anyHash(), Hash.anyHash());

    public IRFor(IRVar x, IRExp e, IRStmt s) {
        this.x = x;
        this.e = e;
        this.s = s;
        this.recordHash = hash.hash(P.p(x, e, s));
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRFor && x.equals(((IRFor) obj).x) && e.equals(((IRFor) obj).e) && s.equals(((IRFor) obj).s));
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public String toString() {
        return "for (" +  x + " in " + e + ")\n" + s;
    }

    @Override
    public FHashSet<IRPVar> free() {
        FHashSet<IRPVar> tmp = e.free().union(s.free());
        if (x instanceof IRPVar) {
            return tmp.insert((IRPVar)x);
        } else {
            return tmp;
        }
    }

    @Override
    public P2<FHashSet<Integer>, FHashSet<Integer>> escape(FHashSet<IRPVar> local) {
        return s.escape(local);
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forFor(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forFor(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forFor(this);
    }
}
