/**
 * Created by wayne on 10/14/15.
 */

package ast;

import java.util.ArrayList;

public class BlockStatement extends Statement {
    ArrayList<Statement> body;

    public BlockStatement(ArrayList<Statement> body) {
        this.body = body;
    }

    public ArrayList<Statement> getBody() {
        return body;
    }

    public Object accept(StatementVisitor ask) {
        return ask.forBlockStatement(this);
    }
}
