package ast;

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

    public Object accept(ExpressionVisitor ask) {
        return ask.forSequenceExpression(this);
    }
}
