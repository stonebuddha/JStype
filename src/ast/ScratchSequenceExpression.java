package ast;

import fj.P2;
import fj.data.List;

/**
 * Created by wayne on 15/11/10.
 */
public class ScratchSequenceExpression extends Expression {
    List<P2<ScratchIdentifierExpression, Expression>> declarations;
    Expression body;

    public ScratchSequenceExpression(List<P2<ScratchIdentifierExpression, Expression>> declarations, Expression body) {
        this.declarations = declarations;
        this.body = body;
    }

    public List<P2<ScratchIdentifierExpression, Expression>> getDeclarations() {
        return declarations;
    }
    public Expression getBody() {
        return body;
    }

    @Override
    public Object accept(ExpressionVisitor ask) {
        return ask.forScratchSequenceExpression(this);
    }
}
