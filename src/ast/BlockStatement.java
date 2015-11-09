/**
 * Created by wayne on 10/14/15.
 */

package ast;

import fj.data.List;

public class BlockStatement extends Statement {
    List<Statement> body;

    public BlockStatement(List<Statement> body) {
        this.body = body;
    }

    public List<Statement> getBody() {
        return body;
    }

    public Object accept(StatementVisitor ask) {
        return ask.forBlockStatement(this);
    }
}
