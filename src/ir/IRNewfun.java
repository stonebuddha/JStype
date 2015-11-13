package ir;

import fj.P;
import fj.P2;

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
