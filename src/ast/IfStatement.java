/**
 * Created by wayne on 10/14/15.
 */

package ast;

public class IfStatement extends Statement {
    Expression test;
    Statement consequent;
    Statement alternate;

    public IfStatement(Expression test, Statement consequent, Statement alternate) {
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
    public Statement getAlternate() {
        return alternate;
    }

    public Object accept(StatementVisitor ask) {
        return ask.forIfStatement(this);
    }
}
