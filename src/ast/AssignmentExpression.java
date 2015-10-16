package ast;

/**
 * Created by wayne on 15/10/15.
 */
public class AssignmentExpression extends Expression {
    String operator;
    Object left;
    Expression right;
    public AssignmentExpression(String operator, Object left, Expression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }
    public Object accept(ExpressionVisitor ask) {
        return ask.forAssignmentExpression(operator, left, right);
    }
}
