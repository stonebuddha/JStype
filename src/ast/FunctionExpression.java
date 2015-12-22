package ast;

import fj.P2;
import fj.data.List;
import fj.data.Option;

/**
 * Created by wayne on 15/10/15.
 */
public class FunctionExpression extends Expression {
    Option<IdentifierExpression> id;
    List<IdentifierExpression> params;
    BlockStatement body;

    public FunctionExpression(Option<IdentifierExpression> id, List<IdentifierExpression> params, BlockStatement body) {
        this.id = id;
        this.params = params;
        this.body = body;
    }

    public FunctionExpression(Option<IdentifierExpression> id, List<IdentifierExpression> params, BlockStatement body, Option<Location> loc) {
        this.id = id;
        this.params = params;
        this.body = body;
        this.loc = loc;
    }

    public Option<IdentifierExpression> getId() {
        return id;
    }
    public List<IdentifierExpression> getParams() {
        return params;
    }
    public BlockStatement getBody() {
        return body;
    }

    @Override
    public Expression accept(SimpleTransformVisitor ask) {
        return ask.forFunctionExpression(this);
    }
    @Override
    public <T> P2<Expression, T> accept(TransformVisitor<T> ask) {
        return ask.forFunctionExpression(this);
    }
    @Override
    public <T> T accept(ExpressionVisitor<T> ask) {
        return ask.forFunctionExpression(this);
    }
}
