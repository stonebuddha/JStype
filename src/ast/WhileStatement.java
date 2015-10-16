package ast;

/**
 * Created by Hwhitetooth on 15/10/14.
 */
public class WhileStatement extends Statement {
    Expression test;
    Statement body;
    public WhileStatement(Expression test, Statement body) {
        this.test = test;
        this.body = body;
    }
    public Object accept(StatementVisitor ask) {
        return ask.forWhileStatement(test, body);
    }
}
