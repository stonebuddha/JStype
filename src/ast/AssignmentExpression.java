package ast;

import fj.P2;

/**
 * Created by wayne on 15/10/15.
 */
public class AssignmentExpression extends Expression {
    String operator;
    Expression left;
    Expression right;

    public AssignmentExpression(String operator, Expression left, Expression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public String getOperator() {
        return operator;
    }
    public Expression getLeft() {
        return left;
    }
    public Expression getRight() {
        return right;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> ask) {
        return ask.forAssignmentExpression(this);
    }
    @Override
    public Expression accept(SimpleTransformVisitor ask) {
        return ask.forAssignmentExpression(this);
    }
    @Override
    public <T> P2<Expression, T> accept(TransformVisitor<T> ask) {
        return ask.forAssignmentExpression(this);
    }
}
