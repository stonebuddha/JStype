package ir;

import fj.P;
import fj.P2;

/**
 * Created by wayne on 15/10/27.
 */
public class IRIf extends IRStmt {
    public IRExp e;
    public IRStmt s1, s2;

    public IRIf(IRExp e, IRStmt s1, IRStmt s2) {
        this.e = e;
        this.s1 = s1;
        this.s2 = s2;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRIf && e.equals(((IRIf) obj).e) && s1.equals(((IRIf) obj).s1) && s2.equals(((IRIf) obj).s2));
    }

    @Override
    public int hashCode() {
        return P.p(e, s1, s2).hashCode();
    }

    @Override
    public String toString() {
        return "if (" + e + ")\n" + s1 + "else\n" + s2;
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forIf(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forIf(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forIf(this);
    }
}
