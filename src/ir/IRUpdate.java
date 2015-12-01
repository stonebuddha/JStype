package ir;

import fj.Hash;
import fj.P;
import fj.P2;
import fj.P3;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRUpdate extends IRStmt {
    public final IRExp e1, e2, e3;
    final int recordHash;
    static final Hash<P3<IRExp, IRExp, IRExp>> hash = Hash.p3Hash(Hash.anyHash(), Hash.anyHash(), Hash.anyHash());

    public IRUpdate(IRExp e1, IRExp e2, IRExp e3) {
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
        this.recordHash = hash.hash(P.p(e1, e2, e3));
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRUpdate && e1.equals(((IRUpdate) obj).e1) && e2.equals(((IRUpdate) obj).e2) && e3.equals(((IRUpdate) obj).e3));
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public FHashSet<IRPVar> free() {
        return e1.free().union(e2.free()).union(e3.free());
    }

    @Override
    public P2<FHashSet<Integer>, FHashSet<Integer>> escape(FHashSet<IRPVar> local) {
        return P.p(FHashSet.empty(), FHashSet.empty());
    }

    @Override
    public String toString() {
        return e1 + "." + e2 + " := " + e3 + ";\n";
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forUpdate(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forUpdate(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forUpdate(this);
    }
}
