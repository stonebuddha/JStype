package ast;

import java.util.ArrayList;

/**
 * Created by Hwhitetooth on 15/10/14.
 */
public class SwitchCase extends Node {
    Expression test;
    ArrayList<Statement> consequent;

    public SwitchCase(Expression test, ArrayList<Statement> consequent) {
        this.test = test;
        this.consequent = consequent;
    }

    public Expression getTest() {
        return test;
    }
    public ArrayList<Statement> getConsequent() {
        return consequent;
    }

    public Object accept(SwitchCaseVisitor ask) {
        return ask.forSwitchCase(this);
    }
}
