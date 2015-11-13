package ir;

import fj.P2;

/**
 * Created by wayne on 15/10/27.
 */
public class IRFor extends IRStmt {
    public IRVar x;
    public IRExp e;
    public IRStmt s;

    public IRFor(IRVar x, IRExp e, IRStmt s) {
        this.x = x;
        this.e = e;
        this.s = s;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRFor && x.equals(((IRFor) obj).x) && e.equals(((IRFor) obj).e) && s.equals(((IRFor) obj).s));
    }

    @Override
    public String toString() {
        return "for (" +  x + " in " + e + ")\n" + s;
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forFor(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forFor(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forFor(this);
    }
}
