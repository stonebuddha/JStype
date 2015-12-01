package ir;

import fj.Hash;
import fj.P;
import fj.P2;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRJump extends IRStmt {
    public final String lbl;
    public final IRExp e;
    final int recordHash;
    static final Hash<P2<String, IRExp>> hash = Hash.p2Hash(Hash.anyHash(), Hash.anyHash());

    public IRJump(String lbl, IRExp e) {
        this.lbl = lbl;
        this.e = e;
        this.recordHash = hash.hash(P.p(lbl, e));
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRJump && lbl.equals(((IRJump) obj).lbl) && e.equals(((IRJump) obj).e));
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public String toString() {
        return "jump " + lbl + "(" + e + ");\n";
    }

    @Override
    public FHashSet<IRPVar> free() {
        return e.free();
    }

    @Override
    public P2<FHashSet<Integer>, FHashSet<Integer>> escape(FHashSet<IRPVar> local) {
        return P.p(FHashSet.empty(), FHashSet.empty());
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forJump(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forJump(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forJump(this);
    }
}
