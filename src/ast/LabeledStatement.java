/**
 * Created by wayne on 10/14/15.
 */

package ast;

import fj.P2;

public class LabeledStatement extends Statement {
    IdentifierExpression label;
    Statement body;

    public LabeledStatement(IdentifierExpression label, Statement body) {
        this.label = label;
        this.body = body;
    }

    public IdentifierExpression getLabel() {
        return label;
    }
    public Statement getBody() {
        return body;
    }

    @Override
    public <T> P2<Statement, T> accept(TransformVisitor<T> ask) {
        return ask.forLabeledStatement(this);
    }
    @Override
    public Statement accept(SimpleTransformVisitor ask) {
        return ask.forLabeledStatement(this);
    }
    @Override
    public <T> T accept(StatementVisitor<T> ask) {
        return ask.forLabeledStatement(this);
    }
}
