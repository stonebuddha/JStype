package ast;

/**
 * Created by wayne on 15/10/15.
 */
public abstract class Literal extends Node {
    public abstract Object accept(LiteralVisitor ask);
}
