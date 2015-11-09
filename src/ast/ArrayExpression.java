package ast;

import fj.data.Seq;

/**
 * Created by wayne on 15/10/15.
 */
public class ArrayExpression extends Expression {
    Seq<Expression> elements;

    public ArrayExpression(Seq<Expression> elements) {
        this.elements = elements;
    }

    public Seq<Expression> getElements() {
        return elements;
    }

    public Object accept(ExpressionVisitor ask) {
        return ask.forArrayExpression(this);
    }
}
