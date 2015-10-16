/**
 * Created by wayne on 10/14/15.
 */

package ast;

public class EmptyStatement extends Statement {
    Object accept(NodeVisitor ask) {
        return ask.forEmptyStatement();
    }
}
