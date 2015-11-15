package ir;

import fj.Ord;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.Set;

/**
 * Created by wayne on 15/10/27.
 */
public class IRNewfun extends IRStmt {
    public IRVar x;
    public IRMethod m;
    public IRNum n;

    public IRNewfun(IRVar x, IRMethod m, IRNum n) {
        this.x = x;
        this.m = m;
        this.n = n;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRNewfun && x.equals(((IRNewfun) obj).x) && m.equals(((IRNewfun) obj).m) && n.equals(((IRNewfun) obj).n));
    }

    @Override
    public int hashCode() {
        return P.p(x, m, n).hashCode();
    }

    @Override
    public String toString() {
        return x + " := newfun " + m + "(" + n + ");\n";
    }

    @Override
    public Set<IRPVar> free() {
        Set<IRPVar> _m = m.free();
        if (x instanceof IRPVar) {
            return _m.insert((IRPVar)x);
        } else {
            return _m;
        }
    }

    @Override
    public P2<Set<Integer>, Set<Integer>> escape(Set<IRPVar> local) {
        Set<Integer> ois = Set.set(Ord.intOrd, List.range(x.id, x.id + numClasses));
        Set<Integer> vis = local.intersect(m.freeVars).map(Ord.intOrd, v -> v.id);
        return P.p(vis, ois);
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forNewfun(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forNewfun(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forNewfun(this);
    }
}
