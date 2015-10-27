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

    public IdentifierExpression getId() {
        return id;
    }
    public ArrayList<IdentifierExpression> getParams() {
        return params;
    }
    public BlockStatement getBody() {
        return body;
    }

    public Object accept(ExpressionVisitor ask) {
        return ask.forFunctionExpression(this);
    }
}
