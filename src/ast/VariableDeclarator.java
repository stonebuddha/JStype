package ast;

/**
 * Created by Hwhitetooth on 15/10/14.
 */
public class VariableDeclarator extends Node {
    String id;
    Expression init;
    public VariableDeclarator(String id, Expression init) {
        this.id = id;
        this.init = init;
    }
    public Object accept(VariableDeclaratorVisitor ask) {
        return ask.forVariableDeclarator(id, init);
    }
}
