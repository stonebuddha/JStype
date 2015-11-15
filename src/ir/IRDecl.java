package ir;

import fj.Ord;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.Set;

/**
 * Created by wayne on 15/10/27.
 */
public class IRDecl extends IRStmt {
    public List<P2<IRPVar, IRExp>> bind;
    public IRStmt s;

    public IRDecl(List<P2<IRPVar, IRExp>> bind, IRStmt s) {
        this.bind = bind;
        this.s = s;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRDecl && bind.equals(((IRDecl) obj).bind) && s.equals(((IRDecl) obj).s));
    }

    @Override
    public int hashCode() {
        return P.p(bind, s).hashCode();
    }

    @Override
    public String toString() {
        return "var " + bind.map(p -> p._1() + " = " + p._2()).foldLeft((a, b) -> a + b + ", ", "") + "\n" + s;
    }

    @Override
    public Set<IRPVar> free() {
        P2<List<IRPVar>, List<IRExp>> tmp = List.unzip(bind);
        return s.free().minus(Set.set(Ord.hashEqualsOrd(), tmp._1())).union(tmp._2().map(exp -> exp.free()).foldLeft((a, b) -> a.union(b), Set.empty(Ord.hashEqualsOrd())));
    }

    @Override
    public P2<Set<Integer>, Set<Integer>> escape(Set<IRPVar> local) {
        return s.escape(local);
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forDecl(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forDecl(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forDecl(this);
    }
}
