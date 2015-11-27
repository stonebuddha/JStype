package ir;

import fj.Ord;
import fj.P;
import fj.P2;
import fj.data.Set;

/**
 * Created by wayne on 15/11/27.
 */
public class IRPrint extends IRStmt {
    public IRExp e;

    public IRPrint(IRExp e) {
        this.e = e;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRPrint && e.equals(((IRPrint) obj).e));
    }

    @Override
    public int hashCode() {
        return P.p(e).hashCode();
    }

    @Override
    public String toString() {
        return "print(" + e + ");\n";
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
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forPrint(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forPrint(this);
    }
    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forPrint(this);
    }
}
