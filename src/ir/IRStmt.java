package ir;

/**
 * Created by wayne on 15/10/27.
 */
public abstract class IRStmt extends IRNode {
    public abstract Object accept(IRStmtVisitor ask);
}
