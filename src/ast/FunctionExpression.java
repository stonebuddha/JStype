package ast;

import fj.data.Option;
import fj.data.Seq;

/**
 * Created by wayne on 15/10/15.
 */
public class FunctionExpression extends Expression {
    Option<IdentifierExpression> id;
    Seq<IdentifierExpression> params;
    BlockStatement body;

    public FunctionExpression(Option<IdentifierExpression> id, Seq<IdentifierExpression> params, BlockStatement body) {
        this.id = id;
        this.params = params;
        this.body = body;
    }

    public Option<IdentifierExpression> getId() {
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
