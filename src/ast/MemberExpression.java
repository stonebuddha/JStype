package ast;

import fj.P2;

/**
 * Created by wayne on 15/10/15.
 */
public class MemberExpression extends Expression {
    Expression object;
    Expression property;
    Boolean computed;

    public MemberExpression(Expression object, Expression property, Boolean computed) {
        this.object = object;
        if (property instanceof RealIdentifierExpression) {
            this.property = new LiteralExpression(new StringLiteral(((RealIdentifierExpression) property).getName()));
        } else {
            this.property = property;
        }
        this.computed = computed;
    }

    public Expression getObject() {
        return object;
    }
    public Expression getProperty() {
        return property;
    }
    public Boolean getComputed() {
        return computed;
    }

    @Override
    public Expression accept(SimpleTransformVisitor ask) {
        return ask.forMemberExpression(this);
    }
    @Override
    public <T> P2<Expression, T> accept(TransformVisitor<T> ask) {
        return ask.forMemberExpression(this);
    }
    @Override
    public <T> T accept(ExpressionVisitor<T> ask) {
        return ask.forMemberExpression(this);
    }
}
