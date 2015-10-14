package ast;

/**
 * Created by wayne on 10/15/15.
 */
public class ForInStatement extends Statement {
    Object left;
    Expression right;
    Statement body;
    public ForInStatement(Object left, Expression right, Statement body) {
        this.left = left;
        this.right = right;
        this.body = body;
    }
}
