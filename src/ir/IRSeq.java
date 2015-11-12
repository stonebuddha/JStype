package ir;

import fj.data.List;

/**
 * Created by wayne on 15/10/27.
 */
public class IRSeq extends IRStmt {
    public List<IRStmt> ss;

    public IRSeq(List<IRStmt> ss) {
        this.ss = ss;
    }

    @Override
    public String toString() {
        return ss.map(s -> s.toString()).foldLeft((a, b) -> a + b + ";\n", "");
    }

    @Override
    public Object accept(IRStmtVisitor ask) {
        return ask.forSeq(this);
    }
}
