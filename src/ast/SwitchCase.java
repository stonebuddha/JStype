package ast;

import fj.P2;
import fj.data.List;
import fj.data.Option;

/**
 * Created by Hwhitetooth on 15/10/14.
 */
public class SwitchCase extends Node {
    Option<Expression> test;
    List<Statement> consequent;

    public SwitchCase(Option<Expression> test, List<Statement> consequent) {
        this.test = test;
        this.consequent = consequent;
    }

    public SwitchCase(Option<Expression> test, List<Statement> consequent, Option<Location> loc) {
        this.test = test;
        this.consequent = consequent;
        this.loc = loc;
    }

    public Option<Expression> getTest() {
        return test;
    }
    public List<Statement> getConsequent() {
        return consequent;
    }

    public <T> T accept(SwitchCaseVisitor<T> ask) {
        return ask.forSwitchCase(this);
    }
    public <T> P2<SwitchCase, T> accept(TransformVisitor<T> ask) {
        return ask.forSwitchCase(this);
    }
    public SwitchCase accept(SimpleTransformVisitor ask) {
        return ask.forSwitchCase(this);
    }
}
