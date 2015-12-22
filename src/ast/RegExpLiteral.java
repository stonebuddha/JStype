package ast;

import fj.P2;
import fj.data.Option;

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

    public RegExpLiteral(String pattern, String flags, Option<Location> loc) {
        this.pattern = pattern;
        this.flags = flags;
        this.loc = loc;
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
