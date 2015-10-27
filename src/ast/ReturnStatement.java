package ast;

/**
 * Created by Hwhitetooth on 15/10/14.
 */
public class ReturnStatement extends Statement {
    Expression argument;

    public ReturnStatement(Expression argument) {
        this.argument = argument;
    }

    public Expression getArgument() {
        return argument;
    }

    public Object accept(StatementVisitor ask) {
        return ask.forReturnStatement(this);
    }
}
