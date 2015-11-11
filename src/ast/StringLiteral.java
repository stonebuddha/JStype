package ast;

import fj.P2;

/**
 * Created by wayne on 15/10/15.
 */
public class StringLiteral extends Literal {
    String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public Literal accept(SimpleTransformVisitor ask) {
        return ask.forStringLiteral(this);
    }
    @Override
    public <T> P2<Literal, T> accept(TransformVisitor<T> ask) {
        return ask.forStringLiteral(this);
    }
    @Override
    public <T> T accept(LiteralVisitor<T> ask) {
        return ask.forStringLiteral(this);
    }
}
