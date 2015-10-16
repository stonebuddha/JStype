/**
 * Created by wayne on 10/14/15.
 */

package ast;

public class ExpressionStatement extends Statement {
    Expression expression;
    public ExpressionStatement(Expression expression) {
        this.expression = expression;
    }
    Object accept(NodeVisitor ask) {
        return ask.forExpressionStatement(expression);
    }
}
