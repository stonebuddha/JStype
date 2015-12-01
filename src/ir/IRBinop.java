package ir;

import fj.Hash;
import fj.P;
import fj.P2;
import fj.P3;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRBinop extends IRExp {
    public final Bop op;
    public final IRExp e1, e2;
    final int recordHash;
    static final Hash<P3<Bop, IRExp, IRExp>> hash = Hash.p3Hash(Hash.anyHash(), Hash.anyHash(), Hash.anyHash());

    public IRBinop(Bop op, IRExp e1, IRExp e2) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
        this.recordHash = hash.hash(P.p(op, e1, e2));
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRBinop && op.equals(((IRBinop) obj).op) && e1.equals(((IRBinop) obj).e1) && e2.equals(((IRBinop) obj).e2));
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public String toString() {
        return "(" + e1 + " " + op + " " + e2 + ")";
    }

    @Override
    public FHashSet<IRPVar> free() {
        return e1.free().union(e2.free());
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
