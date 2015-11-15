package ir;

import fj.Ord;
import fj.P;
import fj.P2;
import fj.data.Set;

/**
 * Created by wayne on 15/10/27.
 */
public class IRJump extends IRStmt {
    public String lbl;
    public IRExp e;

    public IRJump(String lbl, IRExp e) {
        this.lbl = lbl;
        this.e = e;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRJump && lbl.equals(((IRJump) obj).lbl) && e.equals(((IRJump) obj).e));
    }

    @Override
    public int hashCode() {
        return P.p(lbl, e).hashCode();
    }

    @Override
    public String toString() {
        return "jump " + lbl + "(" + e + ");\n";
    }

    @Override
    public Set<IRPVar> free() {
        return e.free();
    }

    @Override
    public P2<Set<Integer>, Set<Integer>> escape(Set<IRPVar> local) {
        return P.p(Set.empty(Ord.intOrd), Set.empty(Ord.intOrd));
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forJump(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forJump(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forJump(this);
    }
}
