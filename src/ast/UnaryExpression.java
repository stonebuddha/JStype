package ast;

/**
 * Created by wayne on 15/10/15.
 */
public class UnaryExpression extends Expression {
    String operator;
    Boolean prefix;
    Expression argument;

    public UnaryExpression(String operator, Boolean prefix, Expression argument) {
        this.operator = operator;
        this.prefix = prefix;
        this.argument = argument;
    }

    public String getOperator() {
        return operator;
    }
    public Boolean getPrefix() {
        return prefix;
    }
    public Expression getArgument() {
        return argument;
    }

    public Object accept(ExpressionVisitor ask) {
        return ask.forUnaryExpression(this);
    }
}
