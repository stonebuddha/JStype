package ast;

import fj.P2;
import fj.data.Option;

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

    public DoWhileStatement(Statement body, Expression test, Option<Location> loc) {
        this.body = body;
        this.test = test;
        this.loc = loc;
    }

    public Statement getBody() {
        return body;
    }
    public Expression getTest() {
        return test;
    }

    @Override
    public <T> P2<Statement, T> accept(TransformVisitor<T> ask) {
        return ask.forDoWhileStatement(this);
    }
    @Override
    public Statement accept(SimpleTransformVisitor ask) {
        return ask.forDoWhileStatement(this);
    }
    @Override
    public <T> T accept(StatementVisitor<T> ask) {
        return ask.forDoWhileStatement(this);
    }
}
