package ast;

import fj.P2;

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

    @Override
    public <T> P2<Statement, T> accept(TransformVisitor<T> ask) {
        return ask.forWhileStatement(this);
    }
    @Override
    public Statement accept(SimpleTransformVisitor ask) {
        return ask.forWhileStatement(this);
    }
    @Override
    public <T> T accept(StatementVisitor<T> ask) {
        return ask.forWhileStatement(this);
    }
}
