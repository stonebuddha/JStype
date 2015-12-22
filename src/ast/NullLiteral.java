package ast;

import fj.P2;
import fj.data.Option;

/**
 * Created by wayne on 15/10/15.
 */
public class NullLiteral extends Literal {
    public NullLiteral(Option<Location> loc) {
        this.loc = loc;
    }

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
