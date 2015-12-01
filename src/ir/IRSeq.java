package ir;

import fj.P;
import fj.P2;
import fj.data.List;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRSeq extends IRStmt {
    public final List<IRStmt> ss;
    final int recordHash;

    public IRSeq(List<IRStmt> ss) {
        this.ss = ss;
        this.recordHash = ss.hashCode();
    }

    @Override
    public FHashSet<IRPVar> free() {
        return ss.foldLeft((acc, cur) -> acc.union(cur.free()), FHashSet.empty());
    }

    @Override
    public P2<FHashSet<Integer>, FHashSet<Integer>> escape(FHashSet<IRPVar> local) {
        return ss.foldLeft((acc, s) -> {
            P2<FHashSet<Integer>, FHashSet<Integer>> v = s.escape(local);
            return P.p(acc._1().union(v._1()), acc._2().union(v._2()));
        }, P.p(FHashSet.empty(), FHashSet.empty()));
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRSeq && ss.equals(((IRSeq) obj).ss));
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public String toString() {
        return "{\n" + ss.map(s -> s.toString()).foldLeft((a, b) -> a + b, "") + "}\n";
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forSeq(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forSeq(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forSeq(this);
    }
}
