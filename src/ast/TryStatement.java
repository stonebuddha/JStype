package ast;

import fj.P2;
import fj.data.Option;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Hwhitetooth on 15/10/14.
 */
public class TryStatement extends Statement {
    BlockStatement block;
    Option<CatchClause> handler;
    Option<BlockStatement> finalizer;

    public TryStatement(BlockStatement block, Option<CatchClause> handler, Option<BlockStatement> finalizer) {
        this.block = block;
        this.handler = handler;
        this.finalizer = finalizer;
    }

    public BlockStatement getBlock() {
        return block;
    }
    public Option<CatchClause> getHandler() {
        return handler;
    }
    public Option<BlockStatement> getFinalizer() {
        return finalizer;
    }

    @Override
    public <T> P2<Statement, T> accept(TransformVisitor<T> ask) {
        return ask.forTryStatement(this);
    }
    @Override
    public Statement accept(SimpleTransformVisitor ask) {
        return ask.forTryStatement(this);
    }
    @Override
    public <T> T accept(StatementVisitor<T> ask) {
        return ask.forTryStatement(this);
    }
}
