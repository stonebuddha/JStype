package ast;

import fj.P;
import fj.P2;

/**
 * Created by wayne on 15/11/9.
 */
public class ScratchIdentifierExpression extends IdentifierExpression {
    Integer num;

    public ScratchIdentifierExpression(Integer num) {
        this.num = num;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ScratchIdentifierExpression && num.equals(((ScratchIdentifierExpression) obj).num));
    }

    @Override
    public int hashCode() {
        return num;
    }

    public Integer getNum() {
        return num;
    }

    @Override
    public Expression accept(SimpleTransformVisitor ask) {
        return ask.forScratchIdentifierExpression(this);
    }
    @Override
    public <T> P2<Expression, T> accept(TransformVisitor<T> ask) {
        return ask.forScratchIdentifierExpression(this);
    }
    @Override
    public <T> T accept(ExpressionVisitor<T> ask) {
        return ask.forScratchIdentifierExpression(this);
    }
}
