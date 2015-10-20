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
    public Object accept(ExpressionVisitor ask) {
        return ask.forLogicalExpression(this, operator, left, right);
    }
}
