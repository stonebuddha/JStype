package ast;

import fj.P2;
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

    public ForStatement(Option<Node> init, Option<Expression> test, Option<Expression> update, Statement body, Option<Location> loc) {
        this.init = init;
        this.test = test;
        this.update = update;
        this.body = body;
        this.loc = loc;
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

    @Override
    public <T> P2<Statement, T> accept(TransformVisitor<T> ask) {
        return ask.forForStatement(this);
    }
    @Override
    public Statement accept(SimpleTransformVisitor ask) {
        return ask.forForStatement(this);
    }
    @Override
    public <T> T accept(StatementVisitor<T> ask) {
        return ask.forForStatement(this);
    }
}
