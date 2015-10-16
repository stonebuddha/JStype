/**
 * Created by wayne on 10/14/15.
 */

package ast;

public class LabeledStatement extends Statement {
    String label;
    public LabeledStatement(String label) {
        this.label = label;
    }
    Object accept(NodeVisitor ask) {
        return ask.forLabeledStatement(label);
    }
}
