/**
 * Created by wayne on 10/14/15.
 */

package ast;

import fj.P2;
import fj.data.Option;

public class BreakStatement extends Statement {
    Option<IdentifierExpression> label;

    public BreakStatement(Option<IdentifierExpression> label) {
        this.label = label;
    }

    public BreakStatement(Option<IdentifierExpression> label, Option<Location> loc) {
        this.label = label;
        this.loc = loc;
    }

    public Option<IdentifierExpression> getLabel() {
        return label;
    }

    @Override
    public <T> P2<Statement, T> accept(TransformVisitor<T> ask) {
        return ask.forBreakStatement(this);
    }
    @Override
    public Statement accept(SimpleTransformVisitor ask) {
        return ask.forBreakStatement(this);
    }
    @Override
    public <T> T accept(StatementVisitor<T> ask) {
        return ask.forBreakStatement(this);
    }
}
