package ir;

import fj.P;
import fj.P2;
import fj.Unit;

/**
 * Created by wayne on 15/10/27.
 */
public class IRMerge extends IRStmt {
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRMerge);
    }

    @Override
    public int hashCode() {
        return Unit.unit().hashCode();
    }

    @Override
    public String toString() {
        return "(merge)\n";
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forMerge(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forMerge(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forMerge(this);
    }
    public Integer order() {
        return this.id;
    }
}
