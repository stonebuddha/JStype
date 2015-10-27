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
}
