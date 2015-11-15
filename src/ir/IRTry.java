package ir;

import fj.P;
import fj.P2;
import fj.data.Set;

/**
 * Created by wayne on 15/10/27.
 */
public class IRTry extends IRStmt {
    public IRStmt s1;
    public IRPVar x;
    public IRStmt s2, s3;

    public IRTry(IRStmt s1, IRPVar x, IRStmt s2, IRStmt s3) {
        this.s1 = s1;
        this.x = x;
        this.s2 = s2;
        this.s3 = s3;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRTry && s1.equals(((IRTry) obj).s1) && x.equals(((IRTry) obj).x) && s2.equals(((IRTry) obj).s2) && s3.equals(((IRTry) obj).s3));
    }

    @Override
    public int hashCode() {
        return P.p(s1, x, s2, s3).hashCode();
    }

    @Override
    public Set<IRPVar> free() {
        return s1.free().union(s2.free()).union(s3.free()).insert(x);
    }

    @Override
    public P2<Set<Integer>, Set<Integer>> escape(Set<IRPVar> local) {
        P2<Set<Integer>, Set<Integer>> v1 = s1.escape(local), v2 = s2.escape(local), v3 = s3.escape(local);
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
