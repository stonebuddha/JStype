package ast;

import fj.P2;
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
