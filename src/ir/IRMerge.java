package ir;

/**
 * Created by wayne on 15/10/27.
 */
public class IRMerge extends IRStmt {
    @Override
    public String toString() {
        return "";
    }

    @Override
    public Object accept(IRStmtVisitor ask) {
        return ask.forMerge(this);
    }
    public Integer order() {
        return this.id;
    }
}
