package ir;

/**
 * Created by wayne on 15/10/27.
 */
public class IRBinop extends IRExp {
    public Bop op;
    public IRExp e1, e2;

    public IRBinop(Bop op, IRExp e1, IRExp e2) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public String toString() {
        return "(" + e1 + " " + op + " " + e2 + ")";
    }

    @Override
    public Object accept(IRExpVisitor ask) {
        return ask.forBinop(this);
    }
}
