package ir;

import fj.P2;

/**
 * Created by wayne on 15/10/27.
 */
public class IRLbl extends IRStmt {
    public String lbl;
    public IRStmt s;

    public IRLbl(String lbl, IRStmt s) {
        this.lbl = lbl;
        this.s = s;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRLbl && lbl.equals(((IRLbl) obj).lbl) && s.equals(((IRLbl) obj).s));
    }

    @Override
    public String toString() {
        return lbl + ":\n" + s;
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forLbl(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forLbl(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forLbl(this);
    }
}
