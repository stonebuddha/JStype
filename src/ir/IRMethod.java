package ir;

import fj.Ord;
import fj.P;
import fj.data.List;
import fj.data.Set;
import fj.P2;

/**
 * Created by wayne on 15/10/27.
 */
public class IRMethod extends IRNode {
    public IRPVar self, args;
    public IRStmt s;

    public Set<IRPVar> freeVars;
    public Set<Integer> canEscapeVar, canEscapeObj, cannotEscape;

    @Override
    public String toString() {
        return "method(" + self + "," + args + ") " + s;
    }

    public IRMethod(IRPVar self, IRPVar args, IRStmt s) {
        this.self = self;
        this.args = args;
        this.s = s;

        this.freeVars = free();
        P2<Set<Integer>, Set<Integer>> escapeSet = escape();
        this.canEscapeVar = escapeSet._1();
        this.canEscapeObj = escapeSet._2();
        if (s instanceof IRDecl) {
            List<P2<IRPVar, IRExp>> bind = ((IRDecl) s).bind;
            this.cannotEscape = Set.set(Ord.intOrd, bind.map(p -> p._1().n)).minus(this.canEscapeVar).insert(self.n).insert(args.n);
        } else {
            this.cannotEscape = Set.empty(Ord.hashEqualsOrd());
        }
    }

    @Override
    public Set<IRPVar> free() {
        return s.free().delete(self).delete(args);
    }

    P2<Set<Integer>, Set<Integer>> escape() {
        Set<IRPVar> local;
        if (s instanceof IRDecl) {
            local = Set.set(Ord.hashEqualsOrd(), List.unzip(((IRDecl) s).bind)._1());
        } else {
            local = Set.empty(Ord.hashEqualsOrd());
        }
        return s.escape(local);
    }

    @Override
    public int hashCode() {
        return P.p(self, args, s).hashCode();
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
