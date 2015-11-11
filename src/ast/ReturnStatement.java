package ast;

import fj.P2;
import fj.data.Option;

/**
 * Created by Hwhitetooth on 15/10/14.
 */
public class ReturnStatement extends Statement {
    Option<Expression> argument;

    public ReturnStatement(Option<Expression> argument) {
        this.argument = argument;
    }

    public Option<Expression> getArgument() {
        return argument;
    }

    @Override
    public <T> P2<Statement, T> accept(TransformVisitor<T> ask) {
        return ask.forReturnStatement(this);
    }
    @Override
    public Statement accept(SimpleTransformVisitor ask) {
        return ask.forReturnStatement(this);
    }
    @Override
    public <T> T accept(StatementVisitor<T> ask) {
        return ask.forReturnStatement(this);
    }
}
