package ir;

import fj.P;
import fj.P2;

/**
 * Created by wayne on 15/10/27.
 */
public class IRStr extends IRExp {
    public String v;

    public IRStr(String v) {
        this.v = v;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRStr && v.equals(((IRStr) obj).v));
    }

    @Override
    public int hashCode() {
        return P.p(v).hashCode();
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
