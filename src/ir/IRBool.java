package ir;

import fj.P2;

/**
 * Created by wayne on 15/10/27.
 */
public class IRBool extends IRExp {
    public Boolean v;

    public IRBool(Boolean v) {
        this.v = v;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRBool && v.equals(((IRBool) obj).v));
    }

    @Override
    public String toString() {
        return v.toString();
    }

    @Override
    public <T> T accept(IRExpVisitor<T> ask) {
        return ask.forBool(this);
    }
    @Override
    public IRExp accept(SimpleTransformVisitor ask) {
        return ask.forBool(this);
    }
    @Override
    public <T> P2<IRExp, T> accept(TransformVisitor<T> ask) {
        return ask.forBool(this);
    }
}
