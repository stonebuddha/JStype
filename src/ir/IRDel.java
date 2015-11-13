package ir;

import fj.P2;

/**
 * Created by wayne on 15/10/27.
 */
public class IRDel extends IRStmt {
    public IRScratch x;
    public IRExp e1, e2;

    public IRDel(IRScratch x, IRExp e1, IRExp e2) {
        this.x = x;
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRDel && x.equals(((IRDel) obj).x) && e1.equals(((IRDel) obj).e1) && e2.equals(((IRDel) obj).e2));
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forDel(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forDel(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forDel(this);
    }
}
