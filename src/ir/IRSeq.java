package ir;

import fj.P2;
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
    public boolean equals(Object obj) {
        return (obj instanceof IRSeq && ss.equals(((IRSeq) obj).ss));
    }

    @Override
    public String toString() {
        return "{\n" + ss.map(s -> s.toString()).foldLeft((a, b) -> a + b, "") + "}\n";
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forSeq(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forSeq(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forSeq(this);
    }
}
