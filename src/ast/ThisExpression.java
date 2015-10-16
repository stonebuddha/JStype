package ast;

/**
 * Created by wayne on 15/10/15.
 */
public class ThisExpression extends Expression {
    public Object accept(ExpressionVisitor ask) {
        return ask.forThisExpression();
    }
}
