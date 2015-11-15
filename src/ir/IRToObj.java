package ir;

import fj.Ord;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.Set;

/**
 * Created by wayne on 15/10/27.
 */
public class IRToObj extends IRStmt {
    public IRVar x;
    public IRExp e;

    public IRToObj(IRVar x, IRExp e) {
        this.x = x;
        this.e = e;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRToObj && x.equals(((IRToObj) obj).x) && e.equals(((IRToObj) obj).e));
    }

    @Override
    public int hashCode() {
        return P.p(x, e).hashCode();
    }

    @Override
    public Set<IRPVar> free() {
        Set<IRPVar> _e = e.free();
        if (x instanceof IRPVar) {
            return _e.insert((IRPVar)x);
        } else {
            return _e;
        }
    }

    @Override
    public String toString() {
        return x + " := toObj(" + e + ");\n";
    }

    @Override
    public P2<Set<Integer>, Set<Integer>> escape(Set<IRPVar> local) {
        return P.p(Set.empty(Ord.intOrd), Set.set(Ord.intOrd, List.range(x.id, x.id + numClasses)));
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forToObj(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forToObj(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forToObj(this);
    }
}
