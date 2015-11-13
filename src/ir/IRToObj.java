package ir;

import fj.P;
import fj.P2;

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
    public String toString() {
        return x + " := toObj(" + e + ");\n";
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
