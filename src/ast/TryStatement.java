package ast;

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

    public Object accept(StatementVisitor ask) {
        return ask.forTryStatement(this);
    }
}
