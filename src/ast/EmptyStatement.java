/**
 * Created by wayne on 10/14/15.
 */

package ast;

public class EmptyStatement extends Statement {
    public Object accept(StatementVisitor ask) {
        return ask.forEmptyStatement(this);
    }
}
