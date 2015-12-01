package ir;

import fj.Hash;
import fj.P;
import fj.P2;
import fj.P3;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRIf extends IRStmt {
    public final IRExp e;
    public final IRStmt s1, s2;
    final int recordHash;
    static final Hash<P3<IRExp, IRStmt, IRStmt>> hash = Hash.p3Hash(Hash.anyHash(), Hash.anyHash(), Hash.anyHash());

    public IRIf(IRExp e, IRStmt s1, IRStmt s2) {
        this.e = e;
        this.s1 = s1;
        this.s2 = s2;
        this.recordHash = hash.hash(P.p(e, s1, s2));
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRIf && e.equals(((IRIf) obj).e) && s1.equals(((IRIf) obj).s1) && s2.equals(((IRIf) obj).s2));
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public String toString() {
        return "if (" + e + ")\n" + s1 + "else\n" + s2;
    }

    @Override
    public FHashSet<IRPVar> free() {
        return e.free().union(s1.free()).union(s2.free());
    }

    @Override
    public P2<FHashSet<Integer>, FHashSet<Integer>> escape(FHashSet<IRPVar> local) {
        P2<FHashSet<Integer>, FHashSet<Integer>> v1 = s1.escape(local), v2 = s2.escape(local);
        return P.p(v1._1().union(v2._1()), v1._2().union(v2._2()));
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forIf(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forIf(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forIf(this);
    }
}
