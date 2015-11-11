/**
 * Created by wayne on 10/14/15.
 */

package ast;

import fj.P2;
import fj.data.List;

public class BlockStatement extends Statement {
    List<Statement> body;

    public BlockStatement(List<Statement> body) {
        this.body = body;
    }

    public List<Statement> getBody() {
        return body;
    }

    @Override
    public <T> P2<Statement, T> accept(TransformVisitor<T> ask) {
        return ask.forBlockStatement(this);
    }
    @Override
    public Statement accept(SimpleTransformVisitor ask) {
        return ask.forBlockStatement(this);
    }
    @Override
    public <T> T accept(StatementVisitor<T> ask) {
        return ask.forBlockStatement(this);
    }
}
