package ir;

/**
 * Created by wayne on 15/10/27.
 */
public class IRLbl extends IRStmt {
    public String lbl;
    public IRStmt s;

    public IRLbl(String lbl, IRStmt s) {
        this.lbl = lbl;
        this.s = s;
    }
}
