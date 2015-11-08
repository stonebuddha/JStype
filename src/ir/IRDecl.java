package ir;

import fj.P2;
import fj.data.List;

/**
 * Created by wayne on 15/10/27.
 */
public class IRDecl extends IRStmt {
    public List<P2<IRPVar, IRExp>> bind;
    public IRStmt s;

    public IRDecl(List<P2<IRPVar, IRExp>> bind, IRStmt s) {
        this.bind = bind;
        this.s = s;
    }

    @Override
    public Object accept(IRStmtVisitor ask) {
        return ask.forDecl(this);
    }
}
