package ir;

import fj.P2;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRNull extends IRExp {
    final int recordHash;

    public IRNull() {
        this.recordHash = 0;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRNull);
    }

    @Override
    public int hashCode() {
        return this.recordHash;
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public FHashSet<IRPVar> free() {
        return FHashSet.empty();
    }

    @Override
    public <T> T accept(IRExpVisitor<T> ask) {
        return ask.forNull(this);
    }
    @Override
    public IRExp accept(SimpleTransformVisitor ask) {
        return ask.forNull(this);
    }
    @Override
    public <T> P2<IRExp, T> accept(TransformVisitor<T> ask) {
        return ask.forNull(this);
    }
}
