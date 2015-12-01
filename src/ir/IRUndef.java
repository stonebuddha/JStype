package ir;

import fj.P2;
import fj.Unit;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRUndef extends IRExp {
    final int recordHash;

    public IRUndef() {
        this.recordHash = 0;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRUndef);
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public FHashSet<IRPVar> free() {
        return FHashSet.empty();
    }

    @Override
    public String toString() {
        return "undefined";
    }

    @Override
    public <T> T accept(IRExpVisitor<T> ask) {
        return ask.forUndef(this);
    }
    @Override
    public IRExp accept(SimpleTransformVisitor ask) {
        return ask.forUndef(this);
    }
    @Override
    public <T> P2<IRExp, T> accept(TransformVisitor<T> ask) {
        return ask.forUndef(this);
    }
}
