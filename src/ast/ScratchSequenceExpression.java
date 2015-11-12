package ast;

import fj.P2;
import fj.data.List;

/**
 * Created by wayne on 15/11/10.
 */
public class ScratchSequenceExpression extends Expression {
    List<P2<IdentifierExpression, Expression>> declarations;
    Expression body;

    public ScratchSequenceExpression(List<P2<IdentifierExpression, Expression>> declarations, Expression body) {
        this.declarations = declarations;
        this.body = body;
    }

    public List<P2<IdentifierExpression, Expression>> getDeclarations() {
        return declarations;
    }
    public Expression getBody() {
        return body;
    }

    @Override
    public Expression accept(SimpleTransformVisitor ask) {
        return ask.forScratchSequenceExpression(this);
    }
    @Override
    public <T> P2<Expression, T> accept(TransformVisitor<T> ask) {
        return ask.forScratchSequenceExpression(this);
    }
    @Override
    public <T> T accept(ExpressionVisitor<T> ask) {
        return ask.forScratchSequenceExpression(this);
    }
}
