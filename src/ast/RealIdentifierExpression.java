package ast;

import fj.P;
import fj.P2;
import fj.data.Option;

/**
 * Created by wayne on 15/11/9.
 */
public class RealIdentifierExpression extends IdentifierExpression {
    String name;
    final int recordHash;

    public RealIdentifierExpression(String name) {
        this.name = name;
        this.recordHash = name.hashCode();
    }

    public RealIdentifierExpression(String name, Option<Location> loc) {
        this.name = name;
        this.recordHash = name.hashCode();
        this.loc = loc;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof RealIdentifierExpression && name.equals(((RealIdentifierExpression) obj).name));
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    public String getName() {
        return name;
    }

    @Override
    public Expression accept(SimpleTransformVisitor ask) {
        return ask.forRealIdentifierExpression(this);
    }
    @Override
    public <T> P2<Expression, T> accept(TransformVisitor<T> ask) {
        return ask.forRealIdentifierExpression(this);
    }
    @Override
    public <T> T accept(ExpressionVisitor<T> ask) {
        return ask.forRealIdentifierExpression(this);
    }
}
