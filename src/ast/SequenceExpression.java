package ast;

import fj.P2;
import fj.data.List;

/**
 * Created by wayne on 15/10/15.
 */
public class SequenceExpression extends Expression {
    List<Expression> expressions;

    public SequenceExpression(List<Expression> expressions) {
        this.expressions = expressions;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    @Override
    public Expression accept(SimpleTransformVisitor ask) {
        return ask.forSequenceExpression(this);
    }
    @Override
    public <T> P2<Expression, T> accept(TransformVisitor<T> ask) {
        return ask.forSequenceExpression(this);
    }
    @Override
    public <T> T accept(ExpressionVisitor<T> ask) {
        return ask.forSequenceExpression(this);
    }
}
