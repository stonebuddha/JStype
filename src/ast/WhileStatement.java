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

    public Expression getTest() {
        return test;
    }
    public Statement getBody() {
        return body;
    }

    public Object accept(StatementVisitor ask) {
        return ask.forWhileStatement(this);
    }
}
