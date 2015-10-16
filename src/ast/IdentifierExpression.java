package ast;

/**
 * Created by wayne on 10/15/15.
 */
public class IdentifierExpression extends Expression {
    String name;
    public IdentifierExpression(String name) {
        this.name = name;
    }
    Object accept(ExpressionVisitor ask) {
        return ask.forIdentifierExpression(name);
    }
}
