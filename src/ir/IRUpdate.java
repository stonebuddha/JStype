package ir;

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
    public String toString() {
        return "(assign " + e1 + "." + e2 + " " + e3 + ")";
    }

    @Override
    public Object accept(IRStmtVisitor ask) {
        return ask.forUpdate(this);
    }
}
