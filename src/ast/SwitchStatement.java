package ast;

import java.util.ArrayList;

/**
 * Created by Hwhitetooth on 15/10/14.
 */

public class SwitchStatement extends Statement {
    Expression discriminant;
    ArrayList<SwitchCase> cases;

    public SwitchStatement(Expression discriminant, ArrayList<SwitchCase> cases) {
        this.discriminant = discriminant;
        this.cases = cases;
    }

    public Expression getDiscriminant() {
        return discriminant;
    }
    public ArrayList<SwitchCase> getCases() {
        return cases;
    }

    public Object accept(StatementVisitor ask) {
        return ask.forSwitchStatement(this);
    }
}
