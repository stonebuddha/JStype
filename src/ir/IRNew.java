package ir;

/**
 * Created by wayne on 15/10/27.
 */
public class IRNew extends IRStmt {
    public IRVar x;
    public IRExp e1, e2;

    public IRNew(IRVar x, IRExp e1, IRExp e2) {
        this.x = x;
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public String toString() {
        return "(assign " + x + " " + e1 + "(" + e2 + "))";
    }

    @Override
    public Object accept(IRStmtVisitor ask) {
        return ask.forNew(this);
    }
}
