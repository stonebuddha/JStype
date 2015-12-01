package ir;

import fj.P;
import fj.P2;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRNum extends IRExp {
    public final Double v;
    final int recordHash;

    public IRNum(Double v) {
        this.v = v;
        this.recordHash = v.hashCode();
    }

    @Override
    public FHashSet<IRPVar> free() {
        return FHashSet.empty();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRNum && v.equals(((IRNum) obj).v));
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
    public <T> T accept(IRExpVisitor<T> ask) {
        return ask.forNum(this);
    }
    @Override
    public IRExp accept(SimpleTransformVisitor ask) {
        return ask.forNum(this);
    }
    @Override
    public <T> P2<IRExp, T> accept(TransformVisitor<T> ask) {
        return ask.forNum(this);
    }
}
