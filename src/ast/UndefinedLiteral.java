package ast;

import fj.P2;

/**
 * Created by wayne on 15/11/9.
 */
public class UndefinedLiteral extends Literal {
    @Override
    public Literal accept(SimpleTransformVisitor ask) {
        return ask.forUndefinedLiteral(this);
    }
    @Override
    public <T> P2<Literal, T> accept(TransformVisitor<T> ask) {
        return ask.forUndefinedLiteral(this);
    }
    @Override
    public <T> T accept(LiteralVisitor<T> ask) {
        return ask.forUndefinedLiteral(this);
    }
}
