package ast;

/**
 * Created by wayne on 15/10/15.
 */
public class NumberLiteral extends Literal {
    Number value;
    public NumberLiteral(Number value) {
        this.value = value;
    }
    Object accept(LiteralVisitor ask) {
        return ask.forNumberLiteral(value);
    }
}
