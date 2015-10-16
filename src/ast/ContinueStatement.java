/**
 * Created by wayne on 10/14/15.
 */

package ast;

public class ContinueStatement extends Statement {
    String label;
    public ContinueStatement(String label) {
        this.label = label;
    }
    Object accept(NodeVisitor ask) {
        return ask.forContinueStatement(label);
    }
}
