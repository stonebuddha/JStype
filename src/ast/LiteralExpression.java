package ast;

import fj.P2;

/**
 * Created by wayne on 15/10/15.
 */
public class LiteralExpression extends Expression {
    Literal literal;

    public LiteralExpression(Literal literal) {
        this.literal = literal;
    }

    public Literal getLiteral() {
        return literal;
    }

    @Override
    public Expression accept(SimpleTransformVisitor ask) {
        return ask.forLiteralExpression(this);
    }
    @Override
    public <T> P2<Expression, T> accept(TransformVisitor<T> ask) {
        return ask.forLiteralExpression(this);
    }
    @Override
    public <T> T accept(ExpressionVisitor<T> ask) {
        return ask.forLiteralExpression(this);
    }
}
