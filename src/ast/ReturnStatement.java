package ast;

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

    public Object accept(StatementVisitor ask) {
        return ask.forReturnStatement(this);
    }
}
