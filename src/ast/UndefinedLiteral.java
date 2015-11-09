package ast;

/**
 * Created by wayne on 15/11/9.
 */
public class UndefinedLiteral extends Literal {
    @Override
    public Object accept(LiteralVisitor ask) {
        return ask.forUndefinedLiteral(this);
    }
}
