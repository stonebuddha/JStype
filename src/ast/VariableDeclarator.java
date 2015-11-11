package ast;

import fj.P2;
import fj.data.Option;

/**
 * Created by Hwhitetooth on 15/10/14.
 */
public class VariableDeclarator extends Node {
    IdentifierExpression id;
    Option<Expression> init;

    public VariableDeclarator(IdentifierExpression id, Option<Expression> init) {
        this.id = id;
        this.init = init;
    }

    public IdentifierExpression getId() {
        return id;
    }
    public Option<Expression> getInit() {
        return init;
    }

    public <T> T accept(VariableDeclaratorVisitor<T> ask) {
        return ask.forVariableDeclarator(this);
    }
    public <T> P2<VariableDeclarator, T> accept(TransformVisitor<T> ask) {
        return ask.forVariableDeclarator(this);
    }
    public VariableDeclarator accept(SimpleTransformVisitor ask) {
        return ask.forVariableDeclarator(this);
    }
}
