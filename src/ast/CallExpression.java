package ast;

import fj.P2;
import fj.data.List;

/**
 * Created by wayne on 15/10/15.
 */
public class CallExpression extends Expression {
    Expression callee;
    List<Expression> arguments;

    public CallExpression(Expression callee, List<Expression> arguments) {
        this.callee = callee;
        this.arguments = arguments;
    }

    public Expression getCallee() {
        return callee;
    }
    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public Expression accept(SimpleTransformVisitor ask) {
        return ask.forCallExpression(this);
    }
    @Override
    public <T> P2<Expression, T> accept(TransformVisitor<T> ask) {
        return ask.forCallExpression(this);
    }
    @Override
    public <T> T accept(ExpressionVisitor<T> ask) {
        return ask.forCallExpression(this);
    }
}
