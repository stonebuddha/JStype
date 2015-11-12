package ir;

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
    public String toString() {
        return "(jump " + lbl + " " + e + ")";
    }

    @Override
    public Object accept(IRStmtVisitor ask) {
        return ask.forJump(this);
    }
}
