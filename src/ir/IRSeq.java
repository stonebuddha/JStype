package ir;

import com.google.common.collect.ImmutableList;

/**
 * Created by wayne on 15/10/27.
 */
public class IRSeq extends IRStmt {
    public ImmutableList<IRStmt> ss;

    public IRSeq(ImmutableList<IRStmt> ss) {
        this.ss = ss;
    }

    @Override
    public Object accept(IRStmtVisitor ask) {
        return ask.forSeq(this);
    }
}
