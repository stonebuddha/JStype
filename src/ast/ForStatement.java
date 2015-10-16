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
    public Object accept(StatementVisitor ask) {
        return ask.forForStatement(init, test, update, body);
    }
}
