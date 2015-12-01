package ir;

import fj.P2;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRBool extends IRExp {
    public final Boolean v;
    final int recordHash;

    public IRBool(Boolean v) {
        this.v = v;
        this.recordHash = v.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRBool && v.equals(((IRBool) obj).v));
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public String toString() {
        return v.toString();
    }

    @Override
    public FHashSet<IRPVar> free() {
        return FHashSet.empty();
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
