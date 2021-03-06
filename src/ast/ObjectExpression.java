package ast;

import fj.P2;
import fj.data.List;
import fj.data.Option;

/**
 * Created by wayne on 15/10/15.
 */
public class ObjectExpression extends Expression {
    List<Property> properties;

    public ObjectExpression(List<Property> properties) {
        this.properties = properties;
    }

    public ObjectExpression(List<Property> properties, Option<Location> loc) {
        this.properties = properties;
        this.loc = loc;
    }

    public List<Property> getProperties() {
        return properties;
    }

    @Override
    public Expression accept(SimpleTransformVisitor ask) {
        return ask.forObjectExpression(this);
    }
    @Override
    public <T> P2<Expression, T> accept(TransformVisitor<T> ask) {
        return ask.forObjectExpression(this);
    }
    @Override
    public <T> T accept(ExpressionVisitor<T> ask) {
        return ask.forObjectExpression(this);
    }
}
