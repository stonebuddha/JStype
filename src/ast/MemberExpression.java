package ast;

/**
 * Created by wayne on 15/10/15.
 */
public class MemberExpression extends Expression {
    Expression object;
    Expression property;
    boolean computed;
    public MemberExpression(Expression object, Expression property, boolean computed) {
        this.object = object;
        this.property = property;
        this.computed = computed;
    }
}
