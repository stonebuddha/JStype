package ir;

/**
 * Created by wayne on 15/10/27.
 */
public class IRThrow extends IRStmt {
    public IRExp e;

    public IRThrow(IRExp e) {
        this.e = e;
    }

    @Override
    public Object accept(IRStmtVisitor ask) {
        return ask.forThrow(this);
    }
}
