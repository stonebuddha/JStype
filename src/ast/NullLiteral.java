package ast;

/**
 * Created by wayne on 15/10/15.
 */
public class NullLiteral extends Literal {
    Object accept(LiteralVisitor ask) {
        return ask.forNullLiteral();
    }
}
