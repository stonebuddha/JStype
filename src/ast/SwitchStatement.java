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
    public Object accept(StatementVisitor ask) {
        return ask.forSwitchStatement(discriminant, cases);
    }
}
