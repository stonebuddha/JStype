package ast;

/**
 * Created by wayne on 15/10/15.
 */
public class UpdateExpression extends Expression {
    String operator;
    Expression argument;
    boolean prefix;
    public UpdateExpression(String operator, Expression argument, boolean prefix) {
        this.operator = operator;
        this.argument = argument;
        this.prefix = prefix;
    }
}
