package ast;

import fj.P2;
import fj.data.Option;

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

    public CatchClause(IdentifierExpression param, BlockStatement body, Option<Location> loc) {
        this.param = param;
        this.body = body;
        this.loc = loc;
    }

    public IdentifierExpression getParam() {
        return param;
    }
    public BlockStatement getBody() {
        return body;
    }

    public <T> T accept(CatchClauseVisitor<T> ask) {
        return ask.forCatchClause(this);
    }
    public <T> P2<CatchClause, T> accept(TransformVisitor<T> ask) {
        return ask.forCatchClause(this);
    }
    public CatchClause accept(SimpleTransformVisitor ask) {
        return ask.forCatchClause(this);
    }
}
