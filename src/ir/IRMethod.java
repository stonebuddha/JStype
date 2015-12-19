package ir;

import fj.Hash;
import fj.P;
import fj.P3;
import fj.data.List;
import fj.P2;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public final class IRMethod extends IRNode {
    public final IRPVar self, args;
    public final IRStmt s;
    final int recordHash;
    static final Hash<P3<IRPVar, IRPVar, IRStmt>> hash = Hash.p3Hash(Hash.anyHash(), Hash.anyHash(), Hash.anyHash());

    public FHashSet<IRPVar> freeVars;
    public FHashSet<Integer> canEscapeVar, canEscapeObj, cannotEscape;

    @Override
    public String toString() {
        return "method(" + self + "," + args + ") " + s;
    }

    public IRMethod(IRPVar self, IRPVar args, IRStmt s) {
        this.self = self;
        this.args = args;
        this.s = s;
        this.recordHash = hash.hash(P.p(self, args, s));

        this.freeVars = free();
        P2<FHashSet<Integer>, FHashSet<Integer>> escapeSet = escape();
        this.canEscapeVar = escapeSet._1();
        this.canEscapeObj = escapeSet._2();
        if (s instanceof IRDecl) {
            List<P2<IRPVar, IRExp>> bind = ((IRDecl) s).bind;
            this.cannotEscape = FHashSet.build(bind.map(p -> p._1().id)).minus(this.canEscapeVar).insert(self.id).insert(args.id);
        } else {
            this.cannotEscape = FHashSet.empty();
        }
    }

    @Override
    public FHashSet<IRPVar> free() {
        return s.free().delete(self).delete(args);
    }

    P2<FHashSet<Integer>, FHashSet<Integer>> escape() {
        FHashSet<IRPVar> local;
        if (s instanceof IRDecl) {
            local = FHashSet.build(List.unzip(((IRDecl) s).bind)._1());
        } else {
            local = FHashSet.empty();
        }
        return s.escape(local);
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRMethod && self.equals(((IRMethod) obj).self) && args.equals(((IRMethod) obj).args) && s.equals(((IRMethod) obj).s));
    }

    public IRMethod accept(SimpleTransformVisitor ask) {
        return ask.forMethod(this);
    }
    public <T> P2<IRMethod, T> accept(TransformVisitor<T> ask) {
        return ask.forMethod(this);
    }
}
