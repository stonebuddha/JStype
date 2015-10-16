/**
 * Created by wayne on 10/14/15.
 */

package ast;

public abstract class Expression extends Node {
    public abstract Object accept(ExpressionVisitor ask);
}
