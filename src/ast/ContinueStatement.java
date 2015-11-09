/**
 * Created by wayne on 10/14/15.
 */

package ast;

import fj.data.Option;

public class ContinueStatement extends Statement {
    Option<IdentifierExpression> label;

    public ContinueStatement(Option<IdentifierExpression> label) {
        this.label = label;
    }

    public Option<IdentifierExpression> getLabel() {
        return label;
    }

    public Object accept(StatementVisitor ask) {
        return ask.forContinueStatement(this);
    }
}
