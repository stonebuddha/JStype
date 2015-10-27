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
}