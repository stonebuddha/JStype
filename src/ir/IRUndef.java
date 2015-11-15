package ir;

import fj.Ord;
import fj.P2;
import fj.Unit;
import fj.data.Set;

/**
 * Created by wayne on 15/10/27.
 */
public class IRUndef extends IRExp {
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRUndef);
    }

    @Override
    public int hashCode() {
        return Unit.unit().hashCode();
    }

    @Override
    public Set<IRPVar> free() {
        return Set.empty(Ord.hashEqualsOrd());
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
