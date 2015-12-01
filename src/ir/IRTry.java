package ir;

import fj.Hash;
import fj.P;
import fj.P2;
import fj.P4;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRTry extends IRStmt {
    public final IRStmt s1;
    public final IRPVar x;
    public final IRStmt s2, s3;
    final int recordHash;
    static final Hash<P4<IRStmt, IRPVar, IRStmt, IRStmt>> hash = Hash.p4Hash(Hash.anyHash(), Hash.anyHash(), Hash.anyHash(), Hash.anyHash());

    public IRTry(IRStmt s1, IRPVar x, IRStmt s2, IRStmt s3) {
        this.s1 = s1;
        this.x = x;
        this.s2 = s2;
        this.s3 = s3;
        this.recordHash = hash.hash(P.p(s1, x, s2, s3));
    }

    @Override
    public String toString() {
        return "try {\n" + s1 + "} catch (" + x + ") {\n" + s2 + "} finally {\n" + s3 + "}";
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRTry && s1.equals(((IRTry) obj).s1) && x.equals(((IRTry) obj).x) && s2.equals(((IRTry) obj).s2) && s3.equals(((IRTry) obj).s3));
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public FHashSet<IRPVar> free() {
        return s1.free().union(s2.free()).union(s3.free()).insert(x);
    }

    @Override
    public P2<FHashSet<Integer>, FHashSet<Integer>> escape(FHashSet<IRPVar> local) {
        P2<FHashSet<Integer>, FHashSet<Integer>> v1 = s1.escape(local), v2 = s2.escape(local), v3 = s3.escape(local);
        return P.p(v1._1().union(v2._1()).union(v3._1()), v1._2().union(v2._2()).union(v3._2()));
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forTry(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forTry(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forTry(this);
    }
}
