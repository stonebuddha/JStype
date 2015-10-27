package ast;

/**
 * Created by Hwhitetooth on 15/10/14.
 */
public class CatchClause extends Node {
    IdentifierExpression param;
    BlockStatement body;

    public CatchClause(IdentifierExpression param, BlockStatement body) {
        this.param = param;
        this.body = body;
    }

    public IdentifierExpression getParam() {
        return param;
    }
    public BlockStatement getBody() {
        return body;
    }

    public Object accept(CatchClauseVisitor ask) {
        return ask.forCatchClause(this);
    }
}
