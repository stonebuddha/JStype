package ir;

import fj.P2;

/**
 * Created by wayne on 15/10/27.
 */
public class IRUpdate extends IRStmt {
    public IRExp e1, e2, e3;

    public IRUpdate(IRExp e1, IRExp e2, IRExp e3) {
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRUpdate && e1.equals(((IRUpdate) obj).e1) && e2.equals(((IRUpdate) obj).e2) && e3.equals(((IRUpdate) obj).e3));
    }

    @Override
    public String toString() {
        return e1 + "." + e2 + " := " + e3 + ";\n";
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forUpdate(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forUpdate(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forUpdate(this);
    }
}
