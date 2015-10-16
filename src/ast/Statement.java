/**
 * Created by wayne on 10/14/15.
 */

package ast;

public abstract class Statement extends Node {
    public abstract Object accept(StatementVisitor ask);
}
