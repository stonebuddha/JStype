package ast;

import java.util.ArrayList;

/**
 * Created by wayne on 15/10/15.
 */
public class FunctionExpression extends Expression {
    IdentifierExpression id;
    ArrayList<IdentifierExpression> params;
    BlockStatement body;
    public FunctionExpression(IdentifierExpression id, ArrayList<IdentifierExpression> params, BlockStatement body) {
        this.id = id;
        this.params = params;
        this.body = body;
    }
    public Object accept(ExpressionVisitor ask) {
        return ask.forFunctionExpression(this, id, params, body);
    }
}
