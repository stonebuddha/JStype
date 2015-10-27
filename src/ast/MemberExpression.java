package ast;

/**
 * Created by wayne on 15/10/15.
 */
public class MemberExpression extends Expression {
    Expression object;
    Expression property;
    Boolean computed;

    public MemberExpression(Expression object, Expression property, Boolean computed) {
        this.object = object;
        this.property = property;
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

    public Object accept(ExpressionVisitor ask) {
        return ask.forMemberExpression(this);
    }
}
