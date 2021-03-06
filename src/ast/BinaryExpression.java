package ast;

import fj.P2;
import fj.data.Option;

/**
 * Created by wayne on 15/10/15.
 */
public class BinaryExpression extends Expression {
    String operator;
    Expression left;
    Expression right;

    public BinaryExpression(String operator, Expression left, Expression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public BinaryExpression(String operator, Expression left, Expression right, Option<Location> loc) {
        this.operator = operator;
        this.left = left;
        this.right = right;
        this.loc = loc;
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
        return ask.forBinaryExpression(this);
    }
    @Override
    public Expression accept(SimpleTransformVisitor ask) {
        return ask.forBinaryExpression(this);
    }
    @Override
    public <T> P2<Expression, T> accept(TransformVisitor<T> ask) {
        return ask.forBinaryExpression(this);
    }
}
