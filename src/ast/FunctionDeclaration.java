package ast;

import fj.data.Seq;

/**
 * Created by wayne on 10/15/15.
 */
public class FunctionDeclaration extends Declaration {
    IdentifierExpression id;
    Seq<IdentifierExpression> params;
    BlockStatement body;

    public FunctionDeclaration(IdentifierExpression id, Seq<IdentifierExpression> params, BlockStatement body) {
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

    public Object accept(StatementVisitor ask) {
        return ask.forFunctionDeclaration(this);
    }
}
