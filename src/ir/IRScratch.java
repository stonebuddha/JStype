package ir;

import fj.P2;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRScratch extends IRVar {
    public final Integer n;
    final int recordHash;

    public IRScratch(Integer n) {
        this.n = n;
        this.recordHash = n;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRScratch && n.equals(((IRScratch) obj).n));
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public String toString() {
        return "SCRATCH[" + n + "]";
    }

    @Override
    public FHashSet<IRPVar> free() {
        return FHashSet.empty();
    }

    @Override
    public <T> T accept(IRExpVisitor<T> ask) {
        return ask.forScratch(this);
    }
    @Override
    public IRExp accept(SimpleTransformVisitor ask) {
        return ask.forScratch(this);
    }
    @Override
    public <T> P2<IRExp, T> accept(TransformVisitor<T> ask) {
        return ask.forScratch(this);
    }
}
