package ast;

import fj.P2;

/**
 * Created by wayne on 15/10/15.
 */
public class BooleanLiteral extends Literal {
    Boolean value;

    public BooleanLiteral(Boolean value) {
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }

    @Override
    public Literal accept(SimpleTransformVisitor ask) {
        return ask.forBooleanLiteral(this);
    }
    @Override
    public <T> P2<Literal, T> accept(TransformVisitor<T> ask) {
        return ask.forBooleanLiteral(this);
    }
    @Override
    public <T> T accept(LiteralVisitor<T> ask) {
        return ask.forBooleanLiteral(this);
    }
}
