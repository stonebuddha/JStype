package ast;

/**
 * Created by wayne on 15/10/15.
 */
public class BooleanLiteral extends Literal {
    boolean value;
    public BooleanLiteral(boolean value) {
        this.value = value;
    }
    Object accept(NodeVisitor ask) {
        return ask.forBooleanLiteral(value);
    }
}
