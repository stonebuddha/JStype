package ast;

import java.util.ArrayList;

/**
 * Created by wayne on 15/10/15.
 */
public class NewExpression extends CallExpression {
    public NewExpression(Expression callee, ArrayList<Expression> arguments) {
        super(callee, arguments);
    }
    public Object accept(ExpressionVisitor ask) {
        return ask.forNewExpression(callee, arguments);
    }
}
