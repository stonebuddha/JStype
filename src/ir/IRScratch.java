package ir;

import fj.P;
import fj.P2;

/**
 * Created by wayne on 15/10/27.
 */
public class IRScratch extends IRVar {
    public Integer n;

    public IRScratch(Integer n) {
        this.n = n;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRScratch && n.equals(((IRScratch) obj).n));
    }

    @Override
    public int hashCode() {
        return P.p(n).hashCode();
    }

    @Override
    public String toString() {
        return "SCRATCH[" + n + "]";
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
