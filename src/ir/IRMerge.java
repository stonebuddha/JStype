package ir;

import fj.P;
import fj.P2;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRMerge extends IRStmt {
    final int recordHash;

    public IRMerge() {
        this.recordHash = 0;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRMerge);
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public String toString() {
        return "(merge@" + id + ")\n";
    }

    @Override
    public FHashSet<IRPVar> free() {
        return FHashSet.empty();
    }

    @Override
    public P2<FHashSet<Integer>, FHashSet<Integer>> escape(FHashSet<IRPVar> local) {
        return P.p(FHashSet.empty(), FHashSet.empty());
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forMerge(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forMerge(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forMerge(this);
    }
    public Integer order() {
        return this.id;
    }
}
