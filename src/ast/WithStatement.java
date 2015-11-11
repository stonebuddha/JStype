/**
 * Created by wayne on 10/14/15.
 */

package ast;

import fj.P2;

public class WithStatement extends Statement {
    Expression object;
    Statement body;

    public WithStatement(Expression object, Statement body) {
        this.object = object;
        this.body = body;
    }

    public Expression getObject() {
        return object;
    }
    public Statement getBody() {
        return body;
    }

    @Override
    public <T> P2<Statement, T> accept(TransformVisitor<T> ask) {
        return ask.forWithStatement(this);
    }
    @Override
    public Statement accept(SimpleTransformVisitor ask) {
        return ask.forWithStatement(this);
    }
    @Override
    public <T> T accept(StatementVisitor<T> ask) {
        return ask.forWithStatement(this);
    }
}
