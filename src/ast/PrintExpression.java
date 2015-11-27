package ast;

import fj.P2;

/**
 * Created by wayne on 15/11/27.
 */
public class PrintExpression extends Expression {
    Expression expression;

    public PrintExpression(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public Expression accept(SimpleTransformVisitor ask) {
        return ask.forPrintExpression(this);
    }
    @Override
    public <T> P2<Expression, T> accept(TransformVisitor<T> ask) {
        return ask.forPrintExpression(this);
    }
    @Override
    public <T> T accept(ExpressionVisitor<T> ask) {
        return ask.forPrintExpression(this);
    }
}
