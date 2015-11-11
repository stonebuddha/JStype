package ast;

import fj.P2;
import fj.data.List;

/**
 * Created by wayne on 10/15/15.
 */
public class FunctionDeclaration extends Declaration {
    IdentifierExpression id;
    List<IdentifierExpression> params;
    BlockStatement body;

    public FunctionDeclaration(IdentifierExpression id, List<IdentifierExpression> params, BlockStatement body) {
        this.id = id;
        this.params = params;
        this.body = body;
    }

    public IdentifierExpression getId() {
        return id;
    }
    public List<IdentifierExpression> getParams() {
        return params;
    }
    public BlockStatement getBody() {
        return body;
    }

    @Override
    public <T> P2<Statement, T> accept(TransformVisitor<T> ask) {
        return ask.forFunctionDeclaration(this);
    }
    @Override
    public Statement accept(SimpleTransformVisitor ask) {
        return ask.forFunctionDeclaration(this);
    }
    @Override
    public <T> T accept(StatementVisitor<T> ask) {
        return ask.forFunctionDeclaration(this);
    }
}
