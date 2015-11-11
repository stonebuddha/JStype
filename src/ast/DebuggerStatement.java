package ast;

import fj.P2;

/**
 * Created by Hwhitetooth on 15/10/14.
 */
public class DebuggerStatement extends Statement {
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
