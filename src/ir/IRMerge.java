package ir;

/**
 * Created by wayne on 15/10/27.
 */
public class IRMerge extends IRStmt {
    @Override
    public String toString() {
        return "(merge)\n";
    }

    @Override
    public Object accept(IRStmtVisitor ask) {
        return ask.forMerge(this);
    }
}
