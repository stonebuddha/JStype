package ast;

import fj.P2;
import fj.data.List;
import fj.data.Option;

/**
 * Created by wayne on 15/10/15.
 */
public class ArrayExpression extends Expression {
    List<Option<Expression>> elements;

    public ArrayExpression(List<Option<Expression>> elements) {
        this.elements = elements;
    }

    public ArrayExpression(List<Option<Expression>> elements, Option<Location> loc) {
        this.elements = elements;
        this.loc = loc;
    }

    public List<Option<Expression>> getElements() {
        return elements;
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> ask) {
        return ask.forArrayExpression(this);
    }
    @Override
    public Expression accept(SimpleTransformVisitor ask) {
        return ask.forArrayExpression(this);
    }
    @Override
    public <T> P2<Expression, T> accept(TransformVisitor<T> ask) {
        return ask.forArrayExpression(this);
    }
}
