package ir;

import fj.P2;

/**
 * Created by wayne on 15/10/27.
 */
public class IRSDecl extends IRStmt {
    public Integer num;
    public IRStmt s;

    public IRSDecl(Integer num, IRStmt s) {
        this.num = num;
        this.s = s;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof IRSDecl && num.equals(((IRSDecl) obj).num) && s.equals(((IRSDecl) obj).s));
    }

    @Override
    public <T> T accept(IRStmtVisitor<T> ask) {
        return ask.forSDecl(this);
    }
    @Override
    public IRStmt accept(SimpleTransformVisitor ask) {
        return ask.forSDecl(this);
    }
    @Override
    public <T> P2<IRStmt, T> accept(TransformVisitor<T> ask) {
        return ask.forSDecl(this);
    }
}
