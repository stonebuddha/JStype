package ast;

/**
 * Created by wayne on 15/10/15.
 */
public class UnaryExpression extends Expression {
    String operator;
    boolean prefix;
    Expression argument;
    public UnaryExpression(String operator, boolean prefix, Expression argument) {
        this.operator = operator;
        this.prefix = prefix;
        this.argument = argument;
    }
    public Object accept(ExpressionVisitor ask) {
        return ask.forUnaryExpression(this, operator, prefix, argument);
    }
}
