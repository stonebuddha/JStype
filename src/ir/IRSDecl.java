package ir;

import fj.Hash;
import fj.P;
import fj.P2;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRSDecl extends IRStmt {
    public final Integer num;
    public final IRStmt s;
    final int recordHash;
    static final Hash<P2<Integer, IRStmt>> hash = Hash.p2Hash(Hash.anyHash(), Hash.anyHash());

    public IRSDecl(Integer num, IRStmt s) {
        this.num = num;
        this.s = s;
        this.recordHash = hash.hash(P.p(num, s));
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRSDecl && num.equals(((IRSDecl) obj).num) && s.equals(((IRSDecl) obj).s));
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public String toString() {
        return "SCRATCH(" + num + ") {\n" + s.toString() + "}\n";
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
        return ask.forSDecl(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forSDecl(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forSDecl(this);
    }
}
