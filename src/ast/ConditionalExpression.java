package ast;

/**
 * Created by wayne on 15/10/15.
 */
public class ConditionalExpression extends Expression {
    Expression test;
    Expression alternate;
    Expression consequent;
    public ConditionalExpression(Expression test, Expression alternate, Expression consequent) {
        this.test = test;
        this.alternate = alternate;
        this.consequent = consequent;
    }
}
