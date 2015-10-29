package ir;

/**
 * Created by wayne on 15/10/27.
 */
public class IRCall extends IRStmt {
    public IRVar x;
    public IRExp e1, e2, e3;

    public IRCall(IRVar x, IRExp e1, IRExp e2, IRExp e3) {
        this.x = x;
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
    }

    @Override
    public Object accept(IRStmtVisitor ask) {
        return ask.forCall(this);
    }
}
