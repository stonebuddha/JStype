package ast;

/**
 * Created by Hwhitetooth on 15/10/14.
 */
public class VariableDeclarator extends Node {
    Pattern id;
    Expression init;
    public VariableDeclarator(Pattern id, Expression init) {
        this.id = id;
        this.init = init;
    }
}
