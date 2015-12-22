/**
 * Created by wayne on 10/14/15.
 */

package ast;

import fj.P2;
import fj.data.Option;

public class EmptyStatement extends Statement {
    public EmptyStatement(Option<Location> loc) {
        this.loc = loc;
    }
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
