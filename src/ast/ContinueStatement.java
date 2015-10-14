package ast;

/**
 * Created by wayne on 10/14/15.
 */
public class ContinueStatement extends Statement {
    String label;
    public ContinueStatement(String label) {
        this.label = label;
    }
}
