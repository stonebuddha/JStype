package ast;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Hwhitetooth on 15/10/14.
 */
public class TryStatement extends Statement {
    BlockStatement block;
    CatchClause handler;
    ArrayList<CatchClause> guardedHandler;
    BlockStatement finalizer;

    public TryStatement(BlockStatement block, CatchClause handler, ArrayList<CatchClause> guardedHandler, BlockStatement finalizer) {
        this.block = block;
        this.handler = handler;
        this.guardedHandler = guardedHandler;
        this.finalizer = finalizer;
    }
}
