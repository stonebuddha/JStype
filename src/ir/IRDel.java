package ir;

import fj.Hash;
import fj.P;
import fj.P2;
import fj.P3;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRDel extends IRStmt {
    public final IRScratch x;
    public final IRExp e1, e2;
    final int recordHash;
    static final Hash<P3<IRScratch, IRExp, IRExp>> hash = Hash.p3Hash(Hash.anyHash(), Hash.anyHash(), Hash.anyHash());

    public IRDel(IRScratch x, IRExp e1, IRExp e2) {
        this.x = x;
        this.e1 = e1;
        this.e2 = e2;
        this.recordHash = hash.hash(P.p(x, e1, e2));
    }

    @Override
    public String toString() {
        return x + " := delete(" + e1 + "." + e2 + ");\n";
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRDel && x.equals(((IRDel) obj).x) && e1.equals(((IRDel) obj).e1) && e2.equals(((IRDel) obj).e2));
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public FHashSet<IRPVar> free() {
        return e1.free().union(e2.free());
    }

    @Override
    public P2<FHashSet<Integer>, FHashSet<Integer>> escape(FHashSet<IRPVar> local) {
        return P.p(FHashSet.empty(), FHashSet.empty());
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forDel(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forDel(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forDel(this);
    }
}
