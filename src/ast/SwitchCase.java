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
    Object accept(NodeVisitor ask) {
        return ask.forSwitchCase(test, consequent);
    }
}
