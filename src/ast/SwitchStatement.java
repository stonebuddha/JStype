package ast;

import fj.data.List;

/**
 * Created by Hwhitetooth on 15/10/14.
 */

public class SwitchStatement extends Statement {
    Expression discriminant;
    List<SwitchCase> cases;

    public SwitchStatement(Expression discriminant, List<SwitchCase> cases) {
        this.discriminant = discriminant;
        this.cases = cases;
    }

    public Expression getDiscriminant() {
        return discriminant;
    }
    public List<SwitchCase> getCases() {
        return cases;
    }

    public Object accept(StatementVisitor ask) {
        return ask.forSwitchStatement(this);
    }
}
