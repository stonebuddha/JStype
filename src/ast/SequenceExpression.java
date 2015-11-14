package ast;

import fj.P2;
import fj.data.List;

/**
 * Created by wayne on 15/10/15.
 */
public class SequenceExpression extends Expression {
    List<Expression> expressions;
    Boolean isOrig;

    public SequenceExpression(List<Expression> expressions) {
        this.expressions = expressions;
        this.isOrig = true;
    }

    public SequenceExpression(List<Expression> expressions, Boolean isOrig) {
        this.expressions = expressions;
        this.isOrig = isOrig;
    }

    public Boolean getOrig() {
        return isOrig;
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
