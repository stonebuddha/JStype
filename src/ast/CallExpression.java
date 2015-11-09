package ast;

import fj.data.Seq;

/**
 * Created by wayne on 15/10/15.
 */
public class CallExpression extends Expression {
    Expression callee;
    Seq<Expression> arguments;

    public CallExpression(Expression callee, Seq<Expression> arguments) {
        this.callee = callee;
        this.arguments = arguments;
    }

    public Expression getCallee() {
        return callee;
    }
    public Seq<Expression> getArguments() {
        return arguments;
    }

    public Object accept(ExpressionVisitor ask) {
        return ask.forCallExpression(this);
    }
}
