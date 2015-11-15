package ir;

import fj.Ord;
import fj.P;
import fj.P2;
import fj.data.Set;

/**
 * Created by wayne on 15/10/27.
 */
public class IRCall extends IRStmt {
    public IRVar x;
    public IRExp e1, e2, e3;

    public IRCall(IRVar x, IRExp e1, IRExp e2, IRExp e3) {
        this.x = x;
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRCall && x.equals(((IRCall) obj).x) && e1.equals(((IRCall) obj).e1) && e2.equals(((IRCall) obj).e2) && e3.equals(((IRCall) obj).e3));
    }

    @Override
    public int hashCode() {
        return P.p(x, e1, e2, e3).hashCode();
    }

    @Override
    public String toString() {
        return x + " := " + e1 + "." + e2 + "(" + e3 + ");\n";
    }

    @Override
    public Set<IRPVar> free() {
        Set<IRPVar> _e = e1.free().union(e2.free()).union(e3.free());
        if (x instanceof IRPVar) {
            return _e.insert((IRPVar)x);
        } else {
            return _e;
        }
    }

    @Override
    public P2<Set<Integer>, Set<Integer>> escape(Set<IRPVar> local) {
        return P.p(Set.empty(Ord.intOrd), Set.empty(Ord.intOrd));
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forCall(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forCall(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forCall(this);
    }
}
