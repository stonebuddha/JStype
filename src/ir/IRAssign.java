package ir;

/**
 * Created by wayne on 15/10/27.
 */
public class IRAssign extends IRStmt {
    public IRVar x;
    public IRExp e;

    public IRAssign(IRVar x, IRExp e) {
        this.x = x;
        this.e = e;
    }

    @Override
    public String toString() {
        return "(assign " + x + " " + e + ")";
    }

    @Override
    public Object accept(IRStmtVisitor ask) {
        return ask.forAssign(this);
    }
}
