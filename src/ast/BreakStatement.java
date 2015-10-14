/**
 * Created by wayne on 10/14/15.
 */

package ast;

public class BreakStatement extends Statement {
    String label;
    public BreakStatement(String label) {
        this.label = label;
    }
}
