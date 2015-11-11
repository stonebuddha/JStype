package ast;

import fj.P2;

/**
 * Created by wayne on 15/11/9.
 */
public class RealIdentifierExpression extends IdentifierExpression {
    String name;

    public RealIdentifierExpression(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Expression accept(SimpleTransformVisitor ask) {
        return ask.forRealIdentifierExpression(this);
    }
    @Override
    public <T> P2<Expression, T> accept(TransformVisitor<T> ask) {
        return ask.forRealIdentifierExpression(this);
    }
    @Override
    public <T> T accept(ExpressionVisitor<T> ask) {
        return ask.forRealIdentifierExpression(this);
    }
}
