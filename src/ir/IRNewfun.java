package ir;

import fj.Hash;
import fj.P;
import fj.P2;
import fj.P3;
import fj.data.List;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRNewfun extends IRStmt {
    public final IRVar x;
    public final IRMethod m;
    public final IRNum n;
    final int recordHash;
    static final Hash<P3<IRVar, IRMethod, IRNum>> hash = Hash.p3Hash(Hash.anyHash(), Hash.anyHash(), Hash.anyHash());

    public IRNewfun(IRVar x, IRMethod m, IRNum n) {
        this.x = x;
        this.m = m;
        this.n = n;
        this.recordHash = hash.hash(P.p(x, m, n));
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRNewfun && x.equals(((IRNewfun) obj).x) && m.equals(((IRNewfun) obj).m) && n.equals(((IRNewfun) obj).n));
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public String toString() {
        return x + " := newfun " + m + "(" + n + ");\n";
    }

    @Override
    public FHashSet<IRPVar> free() {
        FHashSet<IRPVar> _m = m.free();
        if (x instanceof IRPVar) {
            return _m.insert((IRPVar)x);
        } else {
            return _m;
        }
    }

    @Override
    public P2<FHashSet<Integer>, FHashSet<Integer>> escape(FHashSet<IRPVar> local) {
        FHashSet<Integer> ois = FHashSet.build(List.range(x.id, x.id + numClasses));
        FHashSet<Integer> vis = local.intersect(m.freeVars).map(v -> v.id);
        return P.p(vis, ois);
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forNewfun(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forNewfun(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forNewfun(this);
    }
}
