package ir;

import fj.Hash;
import fj.P;
import fj.P2;
import fj.data.List;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRDecl extends IRStmt {
    public final List<P2<IRPVar, IRExp>> bind;
    public final IRStmt s;
    final int recordHash;
    static final Hash<P2<List<P2<IRPVar, IRExp>>, IRStmt>> hash = Hash.p2Hash(Hash.anyHash(), Hash.anyHash());

    public IRDecl(List<P2<IRPVar, IRExp>> bind, IRStmt s) {
        this.bind = bind;
        this.s = s;
        this.recordHash = hash.hash(P.p(bind, s));
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRDecl && bind.equals(((IRDecl) obj).bind) && s.equals(((IRDecl) obj).s));
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public String toString() {
        return "var " + bind.map(p -> p._1() + " = " + p._2()).foldLeft((a, b) -> a + b + ", ", "") + "\n" + s;
    }

    @Override
    public FHashSet<IRPVar> free() {
        P2<List<IRPVar>, List<IRExp>> tmp = List.unzip(bind);
        return s.free().minus(FHashSet.build(tmp._1())).union(tmp._2().map(exp -> exp.free()).foldLeft((a, b) -> a.union(b), FHashSet.empty()));
    }

    @Override
    public P2<FHashSet<Integer>, FHashSet<Integer>> escape(FHashSet<IRPVar> local) {
        return s.escape(local);
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forDecl(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forDecl(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forDecl(this);
    }
}
