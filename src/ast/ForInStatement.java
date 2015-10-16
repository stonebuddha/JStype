package ast;

/**
 * Created by wayne on 10/15/15.
 */
public class ForInStatement extends Statement {
    Node left;
    Expression right;
    Statement body;
    public ForInStatement(Node left, Expression right, Statement body) {
        this.left = left;
        this.right = right;
        this.body = body;
    }
    Object accept(StatementVisitor ask) {
        return ask.forForInStatement(left, right, body);
    }
}
