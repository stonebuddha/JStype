package ast;

import fj.P2;

/**
 * Created by wayne on 15/10/15.
 */
public class NullLiteral extends Literal {
    @Override
    public Literal accept(SimpleTransformVisitor ask) {
        return ask.forNullLiteral(this);
    }
    @Override
    public <T> P2<Literal, T> accept(TransformVisitor<T> ask) {
        return ask.forNullLiteral(this);
    }
    @Override
    public <T> T accept(LiteralVisitor<T> ask) {
        return ask.forNullLiteral(this);
    }
}
