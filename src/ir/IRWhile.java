package ir;

import fj.P;
import fj.P2;

/**
 * Created by wayne on 15/10/27.
 */
public class IRWhile extends IRStmt {
    public IRExp e;
    public IRStmt s;

    public IRWhile(IRExp e, IRStmt s) {
        this.e = e;
        this.s = s;
    }

    @Override
    public String toString() {
        return "while (" + e + ")\n" + s;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRWhile && e.equals(((IRWhile) obj).e) && s.equals(((IRWhile) obj).s));
    }

    @Override
    public int hashCode() {
        return P.p(e, s).hashCode();
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forWhile(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forWhile(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forWhile(this);
    }
}
