package ast;

/**
 * Created by wayne on 15/10/15.
 */
public class LogicalExpression extends Expression {
    String operator;
    Expression left;
    Expression right;

    public LogicalExpression(String operator, Expression left, Expression right) {
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

    public Object accept(ExpressionVisitor ask) {
        return ask.forLogicalExpression(this);
    }
}
