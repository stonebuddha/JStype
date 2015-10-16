package ast;

/**
 * Created by Hwhitetooth on 15/10/14.
 */
public class ThrowStatement extends Statement {
    Expression argument;
    public ThrowStatement(Expression argument) {
        this.argument = argument;
    }
    Object accept(StatementVisitor ask) {
        return ask.forThrowStatement(argument);
    }
}
