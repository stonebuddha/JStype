package ir;

import fj.Hash;
import fj.P;
import fj.P2;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRUnop extends IRExp {
    public final Uop op;
    public final IRExp e;
    final int recordHash;
    static final Hash<P2<Uop, IRExp>> hash = Hash.p2Hash(Hash.anyHash(), Hash.anyHash());

    public IRUnop(Uop op, IRExp e) {
        this.op = op;
        this.e = e;
        this.recordHash = hash.hash(P.p(op, e));
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRUnop && op.equals(((IRUnop) obj).op) && e.equals(((IRUnop) obj).e));
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public FHashSet<IRPVar> free() {
        return e.free();
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
