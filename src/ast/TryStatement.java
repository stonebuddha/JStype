package ast;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Hwhitetooth on 15/10/14.
 */
public class TryStatement extends Statement {
    BlockStatement block;
    CatchClause handler;
    BlockStatement finalizer;

    public TryStatement(BlockStatement block, CatchClause handler, BlockStatement finalizer) {
        this.block = block;
        this.handler = handler;
        this.finalizer = finalizer;
    }

    public BlockStatement getBlock() {
        return block;
    }
    public CatchClause getHandler() {
        return handler;
    }
    public BlockStatement getFinalizer() {
        return finalizer;
    }

    public Object accept(StatementVisitor ask) {
        return ask.forTryStatement(this);
    }
}
