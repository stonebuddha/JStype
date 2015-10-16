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
    Object accept(NodeVisitor ask) {
        return ask.forUnaryExpression(operator, prefix, argument);
    }
}
