/**
 * Created by wayne on 10/14/15.
 */

package ast;

public class LabeledStatement extends Statement {
    String label;
    Statement body;
    public LabeledStatement(String label, Statement body) {
        this.label = label;
        this.body = body;
    }
    Object accept(NodeVisitor ask) {
        return ask.forLabeledStatement(label, body);
    }
}
