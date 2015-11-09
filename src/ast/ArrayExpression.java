package ast;

import fj.data.Option;
import fj.data.Seq;

/**
 * Created by wayne on 15/10/15.
 */
public class ArrayExpression extends Expression {
    Seq<Option<Expression>> elements;

    public ArrayExpression(Seq<Option<Expression>> elements) {
        this.elements = elements;
    }

    public Seq<Option<Expression>> getElements() {
        return elements;
    }

    public Object accept(ExpressionVisitor ask) {
        return ask.forArrayExpression(this);
    }
}
