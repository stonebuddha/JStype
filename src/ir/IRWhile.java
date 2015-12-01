package ir;

import fj.Hash;
import fj.P;
import fj.P2;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRWhile extends IRStmt {
    public final IRExp e;
    public final IRStmt s;
    final int recordHash;
    static final Hash<P2<IRExp, IRStmt>> hash = Hash.p2Hash(Hash.anyHash(), Hash.anyHash());

    public IRWhile(IRExp e, IRStmt s) {
        this.e = e;
        this.s = s;
        this.recordHash = hash.hash(P.p(e, s));
    }

    @Override
    public String toString() {
        return "while (" + e + ")\n" + s;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRWhile && e.equals(((IRWhile) obj).e) && s.equals(((IRWhile) obj).s));
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public FHashSet<IRPVar> free() {
        return e.free().union(s.free());
    }

    @Override
    public P2<FHashSet<Integer>, FHashSet<Integer>> escape(FHashSet<IRPVar> local) {
        return s.escape(local);
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forWhile(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forWhile(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forWhile(this);
    }
}
