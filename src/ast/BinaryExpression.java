package ast;

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
}
