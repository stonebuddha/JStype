package ir;

import fj.P;
import fj.P2;

/**
 * Created by wayne on 15/10/27.
 */
public class IRPVar extends IRVar {
    public Integer n;

    public IRPVar(Integer n) {
        this.n = n;
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
        return P.p(n).hashCode();
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
