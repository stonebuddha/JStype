/**
 * Created by wayne on 10/14/15.
 */

package ast;

public class BreakStatement extends Statement {
    IdentifierExpression label;
    public BreakStatement(IdentifierExpression label) {
        this.label = label;
    }
    public Object accept(StatementVisitor ask) {
        return ask.forBreakStatement(label);
    }
}
