package ast;

/**
 * Created by wayne on 15/10/15.
 */
public class BooleanLiteral extends Literal {
    Boolean value;

    public BooleanLiteral(Boolean value) {
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }

    public Object accept(LiteralVisitor ask) {
        return ask.forBooleanLiteral(this);
    }
}
