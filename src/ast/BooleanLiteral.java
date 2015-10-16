package ast;

/**
 * Created by wayne on 15/10/15.
 */
public class BooleanLiteral extends Literal {
    boolean value;
    public BooleanLiteral(boolean value) {
        this.value = value;
    }
    public Object accept(LiteralVisitor ask) {
        return ask.forBooleanLiteral(value);
    }
}
