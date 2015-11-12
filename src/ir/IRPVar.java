package ir;

/**
 * Created by wayne on 15/10/27.
 */
public class IRPVar extends IRVar {
    public Integer n;

    public IRPVar(Integer n) {
        this.n = n;
    }

    @Override
    public String toString() {
        return "VAR[" + n.toString() + "]";
    }

    @Override
    public Object accept(IRExpVisitor ask) {
        return ask.forPVar(this);
    }
}
