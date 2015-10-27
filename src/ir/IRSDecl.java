package ir;

/**
 * Created by wayne on 15/10/27.
 */
public class IRSDecl extends IRStmt {
    public Integer num;
    public IRStmt s;

    public IRSDecl(Integer num, IRStmt s) {
        this.num = num;
        this.s = s;
    }
}
