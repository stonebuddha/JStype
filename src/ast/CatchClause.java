package ast;

/**
 * Created by Hwhitetooth on 15/10/14.
 */
public class CatchClause extends Node {
    Pattern param;
    Expression guard;
    BlockStatement body;

    public CatchClause(Pattern param, Expression guard, BlockStatement body) {
        this.param = param;
        this.guard = guard;
        this.body = body;
    }
}
