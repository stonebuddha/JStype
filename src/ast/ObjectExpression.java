package ast;

import fj.data.Seq;

/**
 * Created by wayne on 15/10/15.
 */
public class ObjectExpression extends Expression {
    Seq<Property> properties;

    public ObjectExpression(Seq<Property> properties) {
        this.properties = properties;
    }

    public Seq<Property> getProperties() {
        return properties;
    }

    public Object accept(ExpressionVisitor ask) {
        return ask.forObjectExpression(this);
    }
}
