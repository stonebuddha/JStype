package ast;

import fj.P2;
import fj.data.List;

/**
 * Created by wayne on 15/10/15.
 */
public class NewExpression extends CallExpression {
    public NewExpression(Expression callee, List<Expression> arguments) {
        super(callee, arguments);
    }

    @Override
    public Expression accept(SimpleTransformVisitor ask) {
        return ask.forNewExpression(this);
    }
    @Override
    public <T> P2<Expression, T> accept(TransformVisitor<T> ask) {
        return ask.forNewExpression(this);
    }
    @Override
    public <T> T accept(ExpressionVisitor<T> ask) {
        return ask.forNewExpression(this);
    }
}
