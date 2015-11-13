package ir;

import fj.P;
import fj.P2;

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
