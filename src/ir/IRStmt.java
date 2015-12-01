package ir;

import fj.P2;
import fj.data.Set;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public abstract class IRStmt extends IRNode {
    public abstract P2<FHashSet<Integer>, FHashSet<Integer>> escape(FHashSet<IRPVar> local);

    public abstract <T> T accept(IRStmtVisitor<T> ask);
    public abstract IRStmt accept(SimpleTransformVisitor ask);
    public abstract <T> P2<IRStmt, T> accept(TransformVisitor<T> ask);
}
