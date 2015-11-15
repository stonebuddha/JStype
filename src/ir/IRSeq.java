package ir;

import fj.Ord;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.Set;

/**
 * Created by wayne on 15/10/27.
 */
public class IRSeq extends IRStmt {
    public List<IRStmt> ss;

    public IRSeq(List<IRStmt> ss) {
        this.ss = ss;
    }

    @Override
    public Set<IRPVar> free() {
        return ss.foldLeft((acc, cur) -> acc.union(cur.free()), Set.empty(Ord.hashEqualsOrd()));
    }

    @Override
    public P2<Set<Integer>, Set<Integer>> escape(Set<IRPVar> local) {
        return ss.foldLeft((acc, s) -> {
            P2<Set<Integer>, Set<Integer>> v = s.escape(local);
            return P.p(acc._1().union(v._1()), acc._2().union(v._2()));
        }, P.p(Set.empty(Ord.intOrd), Set.empty(Ord.intOrd)));
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRSeq && ss.equals(((IRSeq) obj).ss));
    }

    @Override
    public int hashCode() {
        return P.p(ss).hashCode();
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
