package ast;

import fj.P2;
import fj.data.Option;

/**
 * Created by wayne on 15/10/15.
 */
public class NumberLiteral extends Literal {
    Number value;

    public NumberLiteral(Number value) {
        this.value = value;
    }

    public NumberLiteral(Number value, Option<Location> loc) {
        this.value = value;
        this.loc = loc;
    }

    public Number getValue() {
        return value;
    }

    @Override
    public Literal accept(SimpleTransformVisitor ask) {
        return ask.forNumberLiteral(this);
    }
    @Override
    public <T> P2<Literal, T> accept(TransformVisitor<T> ask) {
        return ask.forNumberLiteral(this);
    }
    @Override
    public <T> T accept(LiteralVisitor<T> ask) {
        return ask.forNumberLiteral(this);
    }
}
