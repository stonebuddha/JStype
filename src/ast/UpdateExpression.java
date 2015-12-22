package ast;

import fj.P2;
import fj.data.Option;

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

    public UpdateExpression(String operator, Expression argument, Boolean prefix, Option<Location> loc) {
        this.operator = operator;
        this.argument = argument;
        this.prefix = prefix;
        this.loc = loc;
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

    @Override
    public Expression accept(SimpleTransformVisitor ask) {
        return ask.forUpdateExpression(this);
    }
    @Override
    public <T> P2<Expression, T> accept(TransformVisitor<T> ask) {
        return ask.forUpdateExpression(this);
    }
    @Override
    public <T> T accept(ExpressionVisitor<T> ask) {
        return ask.forUpdateExpression(this);
    }
}
