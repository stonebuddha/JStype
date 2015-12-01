package ir;

import fj.P;
import fj.P2;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRThrow extends IRStmt {
    public final IRExp e;
    final int recordHash;

    public IRThrow(IRExp e) {
        this.e = e;
        this.recordHash = e.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRThrow && e.equals(((IRThrow) obj).e));
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public FHashSet<IRPVar> free() {
        return e.free();
    }

    @Override
    public P2<FHashSet<Integer>, FHashSet<Integer>> escape(FHashSet<IRPVar> local) {
        return P.p(FHashSet.empty(), FHashSet.empty());
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forThrow(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forThrow(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forThrow(this);
    }
}
