package ir;

import fj.Ord;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.Set;

/**
 * Created by wayne on 15/10/27.
 */
public class IRNew extends IRStmt {
    public IRVar x;
    public IRExp e1, e2;

    public IRNew(IRVar x, IRExp e1, IRExp e2) {
        this.x = x;
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRNew && x.equals(((IRNew) obj).x) && e1.equals(((IRNew) obj).e1) && e2.equals(((IRNew) obj).e2));
    }

    @Override
    public int hashCode() {
        return P.p(x, e1, e2).hashCode();
    }

    @Override
    public String toString() {
        return x + " := new " + e1 + "(" + e2 + ");\n";
    }

    @Override
    public Set<IRPVar> free() {
        Set<IRPVar> _e = e1.free().union(e2.free());
        if (x instanceof IRPVar) {
            return _e.insert((IRPVar)x);
        } else {
            return _e;
        }
    }

    @Override
    public P2<Set<Integer>, Set<Integer>> escape(Set<IRPVar> local) {
        return P.p(Set.empty(Ord.intOrd), Set.set(Ord.intOrd ,List.range(x.id, x.id + numClasses)));
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forNew(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forNew(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forNew(this);
    }
}
