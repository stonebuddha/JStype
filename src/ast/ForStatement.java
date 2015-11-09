package ast;

import fj.data.Option;

/**
 * Created by wayne on 10/15/15.
 */
public class ForStatement extends Statement {
    Option<Node> init;
    Option<Expression> test;
    Option<Expression> update;
    Statement body;

    public ForStatement(Option<Node> init, Option<Expression> test, Option<Expression> update, Statement body) {
        this.init = init;
        this.test = test;
        this.update = update;
        this.body = body;
    }

    public Option<Node> getInit() {
        return init;
    }
    public Option<Expression> getTest() {
        return test;
    }
    public Option<Expression> getUpdate() {
        return update;
    }
    public Statement getBody() {
        return body;
    }

    public Object accept(StatementVisitor ask) {
        return ask.forForStatement(this);
    }
}
