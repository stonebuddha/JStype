/**
 * Created by wayne on 10/14/15.
 */

package ast;

public class LabeledStatement extends Statement {
    IdentifierExpression label;
    Statement body;
    public LabeledStatement(IdentifierExpression label, Statement body) {
        this.label = label;
        this.body = body;
    }
    public Object accept(StatementVisitor ask) {
        return ask.forLabeledStatement(label, body);
    }
}
