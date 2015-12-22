/**
 * Created by wayne on 10/14/15.
 */

package ast;

import fj.P2;
import fj.data.Option;

public class ExpressionStatement extends Statement {
    Expression expression;

    public ExpressionStatement(Expression expression) {
        this.expression = expression;
    }

    public ExpressionStatement(Expression expression, Option<Location> loc) {
        this.expression = expression;
        this.loc = loc;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public <T> P2<Statement, T> accept(TransformVisitor<T> ask) {
        return ask.forExpressionStatement(this);
    }
    @Override
    public Statement accept(SimpleTransformVisitor ask) {
        return ask.forExpressionStatement(this);
    }
    @Override
    public <T> T accept(StatementVisitor<T> ask) {
        return ask.forExpressionStatement(this);
    }
}
