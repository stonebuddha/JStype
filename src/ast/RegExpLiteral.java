package ast;

/**
 * Created by wayne on 15/10/15.
 */
public class RegExpLiteral extends Literal {
    String pattern;
    String flags;
    public RegExpLiteral(String pattern, String flags) {
        this.pattern = pattern;
        this.flags = flags;
    }
    Object accept(LiteralVisitor ask) {
        return ask.forRegExpLiteral(pattern, flags);
    }
}
