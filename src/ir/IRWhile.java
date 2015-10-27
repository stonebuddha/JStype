package ir;

/**
 * Created by wayne on 15/10/27.
 */
public class IRWhile extends IRStmt {
    public IRExp e;
    public IRStmt s;

    public IRWhile(IRExp e, IRStmt s) {
        this.e = e;
        this.s = s;
    }
}
