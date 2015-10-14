package ast;

/**
 * Created by wayne on 10/15/15.
 */
public class ForStatement extends Statement {
    Object init;
    Expression test;
    Expression update;
    Statement body;
    public ForStatement(Object init, Expression test, Expression update, Statement body) {
        this.init = init;
        this.test = test;
        this.update = update;
        this.body = body;
    }
}
