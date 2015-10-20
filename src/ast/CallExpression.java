package ast;

import java.util.ArrayList;

/**
 * Created by wayne on 15/10/15.
 */
public class CallExpression extends Expression {
    Expression callee;
    ArrayList<Expression> arguments;
    public CallExpression(Expression callee, ArrayList<Expression> arguments) {
        this.callee = callee;
        this.arguments = arguments;
    }
    public Object accept(ExpressionVisitor ask) {
        return ask.forCallExpression(this, callee, arguments);
    }
}
