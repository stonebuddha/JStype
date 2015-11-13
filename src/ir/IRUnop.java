package ir;

import fj.P2;

/**
 * Created by wayne on 15/10/27.
 */
public class IRUnop extends IRExp {
    public Uop op;
    public IRExp e;

    public IRUnop(Uop op, IRExp e) {
        this.op = op;
        this.e = e;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRUnop && op.equals(((IRUnop) obj).op) && e.equals(((IRUnop) obj).e));
    }

    @Override
    public String toString() {
        return op + "(" + e + ")";
    }

    @Override
    public <T> T accept(IRExpVisitor<T> ask) {
        return ask.forUnop(this);
    }
    @Override
    public IRExp accept(SimpleTransformVisitor ask) {
        return ask.forUnop(this);
    }
    @Override
    public <T> P2<IRExp, T> accept(TransformVisitor<T> ask) {
        return ask.forUnop(this);
    }
}
