package ir;

/**
 * Created by wayne on 15/10/27.
 */
public class IRTry extends IRStmt {
    public IRStmt s1;
    public IRPVar x;
    public IRStmt s2, s3;

    public IRTry(IRStmt s1, IRPVar x, IRStmt s2, IRStmt s3) {
        this.s1 = s1;
        this.x = x;
        this.s2 = s2;
        this.s3 = s3;
    }
}
