package ir;

/**
 * Created by wayne on 15/10/27.
 */
public class IRIf extends IRStmt {
    public IRExp e;
    public IRStmt s1, s2;

    public IRIf(IRExp e, IRStmt s1, IRStmt s2) {
        this.e = e;
        this.s1 = s1;
        this.s2 = s2;
    }

    @Override
    public String toString() {
        return "if (" + e + ")\n" + s1 + "else\n" + s2;
    }

    @Override
    public Object accept(IRStmtVisitor ask) {
        return ask.forIf(this);
    }
}
