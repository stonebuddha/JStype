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
public final class IRNew extends IRStmt {
    public final IRVar x;
    public final IRExp e1, e2;
    final int recordHash;
    static final Hash<P3<IRVar, IRExp, IRExp>> hash = Hash.p3Hash(Hash.anyHash(), Hash.anyHash(), Hash.anyHash());

    public IRNew(IRVar x, IRExp e1, IRExp e2) {
        this.x = x;
        this.e1 = e1;
        this.e2 = e2;
        this.recordHash = hash.hash(P.p(x, e1, e2));
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRNew && x.equals(((IRNew) obj).x) && e1.equals(((IRNew) obj).e1) && e2.equals(((IRNew) obj).e2));
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public String toString() {
        return x + " := new " + e1 + "(" + e2 + ");\n";
    }

    @Override
    public FHashSet<IRPVar> free() {
        FHashSet<IRPVar> _e = e1.free().union(e2.free());
        if (x instanceof IRPVar) {
            return _e.insert((IRPVar)x);
        } else {
            return _e;
        }
    }

    @Override
    public P2<FHashSet<Integer>, FHashSet<Integer>> escape(FHashSet<IRPVar> local) {
        return P.p(FHashSet.empty(), FHashSet.build(List.range(x.id, x.id + numClasses)));
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forNew(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forNew(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forNew(this);
    }
}
