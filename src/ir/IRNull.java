package ir;

import fj.P2;
import fj.Unit;

/**
 * Created by wayne on 15/10/27.
 */
public class IRNull extends IRExp {
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRNull);
    }

    @Override
    public int hashCode() {
        return Unit.unit().hashCode();
    }

    @Override
    public String toString() {
        return "null";
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
