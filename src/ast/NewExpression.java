package ast;

import fj.data.Seq;

import java.util.ArrayList;

/**
 * Created by wayne on 15/10/15.
 */
public class NewExpression extends CallExpression {
    public NewExpression(Expression callee, Seq<Expression> arguments) {
        super(callee, arguments);
    }

    public Object accept(ExpressionVisitor ask) {
        return ask.forNewExpression(this);
    }
}
