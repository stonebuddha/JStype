package ast;

/**
 * Created by Hwhitetooth on 15/10/14.
 */
public class VariableDeclarator extends Node {
    IdentifierExpression id;
    Expression init;
    public VariableDeclarator(IdentifierExpression id, Expression init) {
        this.id = id;
        this.init = init;
    }
    public Object accept(VariableDeclaratorVisitor ask) {
        return ask.forVariableDeclarator(id, init);
    }
}
