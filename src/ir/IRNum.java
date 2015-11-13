package ir;

import fj.P;
import fj.P2;

/**
 * Created by wayne on 15/10/27.
 */
public class IRNum extends IRExp {
    public Double v;

    public IRNum(Double v) {
        this.v = v;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRNum && v.equals(((IRNum) obj).v));
    }

    @Override
    public int hashCode() {
        return P.p(v).hashCode();
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
