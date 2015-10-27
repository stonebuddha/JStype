package ir;

/**
 * Created by wayne on 15/10/27.
 */
public class IRNewfun extends IRStmt {
    public IRVar x;
    public IRMethod m;
    public IRNum n;

    public IRNewfun(IRVar x, IRMethod m, IRNum n) {
        this.x = x;
        this.m = m;
        this.n = n;
    }
}
