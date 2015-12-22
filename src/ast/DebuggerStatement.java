package ast;

import fj.P2;
import fj.data.Option;

/**
 * Created by Hwhitetooth on 15/10/14.
 */
public class DebuggerStatement extends Statement {
    public DebuggerStatement(Option<Location> loc) {
        this.loc = loc;
    }

    @Override
    public <T> P2<Statement, T> accept(TransformVisitor<T> ask) {
        return ask.forDebuggerStatement(this);
    }
    @Override
    public Statement accept(SimpleTransformVisitor ask) {
        return ask.forDebuggerStatement(this);
    }
    @Override
    public <T> T accept(StatementVisitor<T> ask) {
        return ask.forDebuggerStatement(this);
    }
}
