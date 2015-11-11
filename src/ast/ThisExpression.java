package ast;

import fj.P2;

/**
 * Created by wayne on 15/10/15.
 */
public class ThisExpression extends Expression {
    @Override
    public Expression accept(SimpleTransformVisitor ask) {
        return ask.forThisExpression(this);
    }
    @Override
    public <T> P2<Expression, T> accept(TransformVisitor<T> ask) {
        return ask.forThisExpression(this);
    }
    @Override
    public <T> T accept(ExpressionVisitor<T> ask) {
        return ask.forThisExpression(this);
    }
}
