/**
 * Created by wayne on 10/14/15.
 */

package ast;

import fj.P2;

public class EmptyStatement extends Statement {
    @Override
    public <T> P2<Statement, T> accept(TransformVisitor<T> ask) {
        return ask.forEmptyStatement(this);
    }
    @Override
    public Statement accept(SimpleTransformVisitor ask) {
        return ask.forEmptyStatement(this);
    }
    @Override
    public <T> T accept(StatementVisitor<T> ask) {
        return ask.forEmptyStatement(this);
    }
}
