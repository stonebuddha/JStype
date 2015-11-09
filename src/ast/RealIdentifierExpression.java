package ast;

/**
 * Created by wayne on 15/11/9.
 */
public class RealIdentifierExpression extends IdentifierExpression {
    String name;

    public RealIdentifierExpression(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Object accept(ExpressionVisitor ask) {
        return ask.forRealIdentifierExpression(this);
    }
}
