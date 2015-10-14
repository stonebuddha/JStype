package ast;

/**
 * Created by Hwhitetooth on 15/10/14.
 */
public class CatchClause extends Node {
    String param;
    BlockStatement body;

    public CatchClause(String param, BlockStatement body) {
        this.param = param;
        this.body = body;
    }
}
