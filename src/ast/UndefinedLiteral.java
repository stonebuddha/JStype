package ast;

import fj.P2;
import fj.data.Option;

/**
 * Created by wayne on 15/11/9.
 */
public class UndefinedLiteral extends Literal {
    public UndefinedLiteral(Option<Location> loc) {
        this.loc = loc;
    }

    public UndefinedLiteral() {}

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
