package ast;

import fj.P2;

/**
 * Created by wayne on 15/10/15.
 */
public class ConditionalExpression extends Expression {
    Expression test;
    Expression alternate;
    Expression consequent;

    public ConditionalExpression(Expression test, Expression consequent, Expression alternate) {
        this.test = test;
        this.alternate = alternate;
        this.consequent = consequent;
    }

    public Expression getTest() {
        return test;
    }
    public Expression getAlternate() {
        return alternate;
    }
    public Expression getConsequent() {
        return consequent;
    }

    @Override
    public Expression accept(SimpleTransformVisitor ask) {
        return ask.forConditionalExpression(this);
    }
    @Override
    public <T> P2<Expression, T> accept(TransformVisitor<T> ask) {
        return ask.forConditionalExpression(this);
    }
    @Override
    public <T> T accept(ExpressionVisitor<T> ask) {
        return ask.forConditionalExpression(this);
    }
}
