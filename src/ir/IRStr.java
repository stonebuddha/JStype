package ir;

import fj.P2;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRStr extends IRExp {
    public final String v;
    final int recordHash;

    public IRStr(String v) {
        this.v = v;
        this.recordHash = v.hashCode();
    }

    @Override
    public FHashSet<IRPVar> free() {
        return FHashSet.empty();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRStr && v.equals(((IRStr) obj).v));
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public String toString() {
        return "\"" + v + "\"";
    }

    @Override
    public <T> T accept(IRExpVisitor<T> ask) {
        return ask.forStr(this);
    }
    @Override
    public IRExp accept(SimpleTransformVisitor ask) {
        return ask.forStr(this);
    }
    @Override
    public <T> P2<IRExp, T> accept(TransformVisitor<T> ask) {
        return ask.forStr(this);
    }
}
