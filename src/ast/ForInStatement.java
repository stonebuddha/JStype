package ast;

import fj.P2;

/**
 * Created by wayne on 10/15/15.
 */
public class ForInStatement extends Statement {
    Node left;
    Expression right;
    Statement body;

    public ForInStatement(Node left, Expression right, Statement body) {
        this.left = left;
        this.right = right;
        this.body = body;
    }

    public Node getLeft() {
        return left;
    }
    public Expression getRight() {
        return right;
    }
    public Statement getBody() {
        return body;
    }

    @Override
    public <T> P2<Statement, T> accept(TransformVisitor<T> ask) {
        return ask.forForInStatement(this);
    }
    @Override
    public Statement accept(SimpleTransformVisitor ask) {
        return ask.forForInStatement(this);
    }
    @Override
    public <T> T accept(StatementVisitor<T> ask) {
        return ask.forForInStatement(this);
    }
}
