package ast;

import fj.P2;

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

    public String getPattern() {
        return pattern;
    }
    public String getFlags() {
        return flags;
    }

    @Override
    public Literal accept(SimpleTransformVisitor ask) {
        return ask.forRegExpLiteral(this);
    }
    @Override
    public <T> P2<Literal, T> accept(TransformVisitor<T> ask) {
        return ask.forRegExpLiteral(this);
    }
    @Override
    public <T> T accept(LiteralVisitor<T> ask) {
        return ask.forRegExpLiteral(this);
    }
}
