package ir;

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
    public Object accept(IRStmtVisitor ask) {
        return ask.forFor(this);
    }
}
