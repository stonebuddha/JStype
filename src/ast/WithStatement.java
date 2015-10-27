/**
 * Created by wayne on 10/14/15.
 */

package ast;

public class WithStatement extends Statement {
    Expression object;
    Statement body;

    public WithStatement(Expression object, Statement body) {
        this.object = object;
        this.body = body;
    }

    public Expression getObject() {
        return object;
    }
    public Statement getBody() {
        return body;
    }

    public Object accept(StatementVisitor ask) {
        return ask.forWithStatement(this);
    }
}
