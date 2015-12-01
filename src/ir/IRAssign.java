package ir;

import fj.Hash;
import fj.P;
import fj.P2;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRAssign extends IRStmt {
    public final IRVar x;
    public final IRExp e;
    final int recordHash;
    static final Hash<P2<IRVar, IRExp>> hash = Hash.p2Hash(Hash.anyHash(), Hash.anyHash());

    public IRAssign(IRVar x, IRExp e) {
        this.x = x;
        this.e = e;
        this.recordHash = hash.hash(P.p(x, e));
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRAssign && x.equals(((IRAssign) obj).x) && e.equals(((IRAssign) obj).e));
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public String toString() {
        return x + " := " + e + ";\n";
    }

    @Override
    public FHashSet<IRPVar> free() {
        FHashSet<IRPVar> _e = e.free();
        if (x instanceof IRPVar) {
            return _e.insert((IRPVar)x);
        } else {
            return _e;
        }
    }

    @Override
    public P2<FHashSet<Integer>, FHashSet<Integer>> escape(FHashSet<IRPVar> local) {
        return P.p(FHashSet.empty(), FHashSet.empty());
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forAssign(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forAssign(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forAssign(this);
    }
}
