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
    public String toString() {
        return "var " + bind.map(p -> p._1() + " = " + p._2()).foldLeft((a, b) -> a + b + ", ", "") + "\n{" + s + "}\n";
    }

    @Override
    public Object accept(IRStmtVisitor ask) {
        return ask.forDecl(this);
    }
}
