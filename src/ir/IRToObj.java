package ir;

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
    public String toString() {
        return "(assign " + x + " toObj(" + e + "))";
    }

    @Override
    public Object accept(IRStmtVisitor ask) {
        return ask.forToObj(this);
    }
}
