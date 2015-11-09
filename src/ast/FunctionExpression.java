package ast;

import fj.data.Seq;

/**
 * Created by wayne on 15/10/15.
 */
public class FunctionExpression extends Expression {
    IdentifierExpression id;
    Seq<IdentifierExpression> params;
    BlockStatement body;

    public FunctionExpression(IdentifierExpression id, Seq<IdentifierExpression> params, BlockStatement body) {
        this.id = id;
        this.params = params;
        this.body = body;
    }

    public IdentifierExpression getId() {
        return id;
    }
    public Seq<IdentifierExpression> getParams() {
        return params;
    }
    public BlockStatement getBody() {
        return body;
    }

    public Object accept(ExpressionVisitor ask) {
        return ask.forFunctionExpression(this);
    }
}
