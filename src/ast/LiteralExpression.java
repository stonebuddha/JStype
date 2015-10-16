package ast;

/**
 * Created by wayne on 15/10/15.
 */
public class LiteralExpression extends Expression {
    Literal literal;
    public LiteralExpression(Literal literal) {
        this.literal = literal;
    }
    Object accept(ExpressionVisitor ask) {
        return ask.forLiteralExpression(literal);
    }
}
