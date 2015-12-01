package ir;

import fj.Hash;
import fj.P;
import fj.P2;
import fj.P4;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRCall extends IRStmt {
    public final IRVar x;
    public final IRExp e1, e2, e3;
    final int recordHash;
    static final Hash<P4<IRVar, IRExp, IRExp, IRExp>> hash = Hash.p4Hash(Hash.anyHash(), Hash.anyHash(), Hash.anyHash(), Hash.anyHash());

    public IRCall(IRVar x, IRExp e1, IRExp e2, IRExp e3) {
        this.x = x;
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
        this.recordHash = hash.hash(P.p(x, e1, e2, e3));
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRCall && x.equals(((IRCall) obj).x) && e1.equals(((IRCall) obj).e1) && e2.equals(((IRCall) obj).e2) && e3.equals(((IRCall) obj).e3));
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public String toString() {
        return x + " := " + e1 + "." + e2 + "(" + e3 + ");\n";
    }

    @Override
    public FHashSet<IRPVar> free() {
        FHashSet<IRPVar> _e = e1.free().union(e2.free()).union(e3.free());
        if (x instanceof IRPVar) {
            return _e.insert((IRPVar)x);
        } else {
            return _e;
        }
    }

    @Override
    public P2<FHashSet<Integer>, FHashSet<Integer>> escape(FHashSet<IRPVar> local) {
        return P.p(FHashSet.empty(), FHashSet.empty());
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forCall(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forCall(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forCall(this);
    }
}
