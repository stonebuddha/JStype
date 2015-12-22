package ast;

import fj.P2;
import fj.data.List;
import fj.data.Option;

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

    public SwitchStatement(Expression discriminant, List<SwitchCase> cases, Option<Location> loc) {
        this.discriminant = discriminant;
        this.cases = cases;
        this.loc = loc;
    }

    public Expression getDiscriminant() {
        return discriminant;
    }
    public List<SwitchCase> getCases() {
        return cases;
    }

    @Override
    public <T> P2<Statement, T> accept(TransformVisitor<T> ask) {
        return ask.forSwitchStatement(this);
    }
    @Override
    public Statement accept(SimpleTransformVisitor ask) {
        return ask.forSwitchStatement(this);
    }
    @Override
    public <T> T accept(StatementVisitor<T> ask) {
        return ask.forSwitchStatement(this);
    }
}
