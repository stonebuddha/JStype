package ast;

import fj.P2;

/**
 * Created by wayne on 15/10/15.
 */
public abstract class Literal extends Node {
    public abstract <T> T accept(LiteralVisitor<T> ask);
    public abstract <T> P2<Literal, T> accept(TransformVisitor<T> ask);
    public abstract Literal accept(SimpleTransformVisitor ask);
}
