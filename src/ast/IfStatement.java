/**
 * Created by wayne on 10/14/15.
 */

package ast;

import fj.P2;
import fj.data.Option;

public class IfStatement extends Statement {
    Expression test;
    Statement consequent;
    Option<Statement> alternate;

    public IfStatement(Expression test, Statement consequent, Option<Statement> alternate) {
        this.test = test;
        this.consequent = consequent;
        this.alternate = alternate;
    }

    public Expression getTest() {
        return test;
    }
    public Statement getConsequent() {
        return consequent;
    }
    public Option<Statement> getAlternate() {
        return alternate;
    }

    @Override
    public <T> P2<Statement, T> accept(TransformVisitor<T> ask) {
        return ask.forIfStatement(this);
    }
    @Override
    public Statement accept(SimpleTransformVisitor ask) {
        return ask.forIfStatement(this);
    }
    @Override
    public <T> T accept(StatementVisitor<T> ask) {
        return ask.forIfStatement(this);
    }
}
