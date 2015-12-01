package ir;

import fj.Hash;
import fj.P;
import fj.P2;
import fj.data.Set;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRLbl extends IRStmt {
    public final String lbl;
    public final IRStmt s;
    final int recordHash;
    static final Hash<P2<String, IRStmt>> hash = Hash.p2Hash(Hash.anyHash(), Hash.anyHash());

    public IRLbl(String lbl, IRStmt s) {
        this.lbl = lbl;
        this.s = s;
        this.recordHash = hash.hash(P.p(lbl, s));
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRLbl && lbl.equals(((IRLbl) obj).lbl) && s.equals(((IRLbl) obj).s));
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public String toString() {
        return lbl + ":\n" + s;
    }

    @Override
    public FHashSet<IRPVar> free() {
        return s.free();
    }

    @Override
    public P2<FHashSet<Integer>, FHashSet<Integer>> escape(FHashSet<IRPVar> local) {
        return s.escape(local);
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forLbl(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forLbl(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forLbl(this);
    }
}
