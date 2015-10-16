package ast;

/**
 * Created by Hwhitetooth on 15/10/14.
 */
public class DoWhileStatement extends Statement {
    Statement body;
    Expression test;
    public DoWhileStatement(Statement body, Expression test) {
        this.body = body;
        this.test = test;
    }
    Object accept(NodeVisitor ask) {
        return ask.forDoWhileStatement(body, test);
    }
}
