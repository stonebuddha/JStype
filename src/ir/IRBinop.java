package ir;

import fj.P;
import fj.P2;

/**
 * Created by wayne on 15/10/27.
 */
public class IRBinop extends IRExp {
    public Bop op;
    public IRExp e1, e2;

    public IRBinop(Bop op, IRExp e1, IRExp e2) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRBinop && op.equals(((IRBinop) obj).op) && e1.equals(((IRBinop) obj).e1) && e2.equals(((IRBinop) obj).e2));
    }

    @Override
    public int hashCode() {
        return P.p(op, e1, e2).hashCode();
    }

    @Override
    public String toString() {
        return "(" + e1 + " " + op + " " + e2 + ")";
    }

    @Override
    public <T> T accept(IRExpVisitor<T> ask) {
        return ask.forBinop(this);
    }
    @Override
    public IRExp accept(SimpleTransformVisitor ask) {
        return ask.forBinop(this);
    }
    @Override
    public <T> P2<IRExp, T> accept(TransformVisitor<T> ask) {
        return ask.forBinop(this);
    }
}
