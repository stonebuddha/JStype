package ir;

import fj.P2;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRPVar extends IRVar {
    public final Integer n;
    final int recordHash;

    public IRPVar(Integer n) {
        this.n = n;
        this.recordHash = n;
    }

    @Override
    public FHashSet<IRPVar> free() {
        return FHashSet.build(this);
    }

    @Override
    public String toString() {
        return "VAR[" + n.toString() + "]";
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRPVar && n.equals(((IRPVar) obj).n));
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public <T> T accept(IRExpVisitor<T> ask) {
        return ask.forPVar(this);
    }
    @Override
    public IRExp accept(SimpleTransformVisitor ask) {
        return ask.forPVar(this);
    }
    @Override
    public <T> P2<IRExp, T> accept(TransformVisitor<T> ask) {
        return ask.forPVar(this);
    }
}
