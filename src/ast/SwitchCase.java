package ast;

import fj.data.List;

/**
 * Created by Hwhitetooth on 15/10/14.
 */
public class SwitchCase extends Node {
    Expression test;
    List<Statement> consequent;

    public SwitchCase(Expression test, List<Statement> consequent) {
        this.test = test;
        this.consequent = consequent;
    }

    public Expression getTest() {
        return test;
    }
    public List<Statement> getConsequent() {
        return consequent;
    }

    public Object accept(SwitchCaseVisitor ask) {
        return ask.forSwitchCase(this);
    }
}
