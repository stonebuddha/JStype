package ast;

/**
 * Created by wayne on 10/15/15.
 */
public class ForStatement extends Statement {
    Node init;
    Expression test;
    Expression update;
    Statement body;

    public ForStatement(Node init, Expression test, Expression update, Statement body) {
        this.init = init;
        this.test = test;
        this.update = update;
        this.body = body;
    }

    public Node getInit() {
        return init;
    }
    public Expression getTest() {
        return test;
    }
    public Expression getUpdate() {
        return update;
    }
    public Statement getBody() {
        return body;
    }

    public Object accept(StatementVisitor ask) {
        return ask.forForStatement(this);
    }
}
