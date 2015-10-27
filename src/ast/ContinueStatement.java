/**
 * Created by wayne on 10/14/15.
 */

package ast;

public class ContinueStatement extends Statement {
    IdentifierExpression label;

    public ContinueStatement(IdentifierExpression label) {
        this.label = label;
    }

    public IdentifierExpression getLabel() {
        return label;
    }

    public Object accept(StatementVisitor ask) {
        return ask.forContinueStatement(this);
    }
}
