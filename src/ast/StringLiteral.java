package ast;

/**
 * Created by wayne on 15/10/15.
 */
public class StringLiteral extends Literal {
    String value;
    public StringLiteral(String value) {
        this.value = value;
    }
    Object accept(LiteralVisitor ask) {
        return ask.forStringLiteral(value);
    }
}
