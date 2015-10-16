/**
 * Created by wayne on 10/14/15.
 */

package ast;

public abstract class Node {
    abstract Object accept(NodeVisitor ask);
}
