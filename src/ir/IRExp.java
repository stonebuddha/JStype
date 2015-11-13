package ir;

import fj.P2;

/**
 * Created by wayne on 15/10/27.
 */
public abstract class IRExp extends IRNode {
    public abstract <T> T accept(IRExpVisitor<T> ask);
    public abstract IRExp accept(SimpleTransformVisitor ask);
    public abstract <T> P2<IRExp, T> accept(TransformVisitor<T> ask);
}
