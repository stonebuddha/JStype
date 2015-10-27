package ast;

/**
 * Created by wayne on 15/10/15.
 */
public class UpdateExpression extends Expression {
    String operator;
    Expression argument;
    Boolean prefix;

    public UpdateExpression(String operator, Expression argument, Boolean prefix) {
        this.operator = operator;
        this.argument = argument;
        this.prefix = prefix;
    }

    public String getOperator() {
        return operator;
    }
    public Expression getArgument() {
        return argument;
    }
    public Boolean getPrefix() {
        return prefix;
    }

    public Object accept(ExpressionVisitor ask) {
        return ask.forUpdateExpression(this);
    }
}
