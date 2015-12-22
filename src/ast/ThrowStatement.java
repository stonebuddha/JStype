package ast;

import fj.P2;
import fj.data.Option;

/**
 * Created by Hwhitetooth on 15/10/14.
 */
public class ThrowStatement extends Statement {
    Expression argument;

    public ThrowStatement(Expression argument) {
        this.argument = argument;
    }

    public ThrowStatement(Expression argument, Option<Location> loc) {
        this.argument = argument;
        this.loc = loc;
    }

    public Expression getArgument() {
        return argument;
    }

    @Override
    public <T> P2<Statement, T> accept(TransformVisitor<T> ask) {
        return ask.forThrowStatement(this);
    }
    @Override
    public Statement accept(SimpleTransformVisitor ask) {
        return ask.forThrowStatement(this);
    }
    @Override
    public <T> T accept(StatementVisitor<T> ask) {
        return ask.forThrowStatement(this);
    }
}
