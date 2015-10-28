package ir;

/**
 * Created by wayne on 15/10/27.
 */
public class IRBool extends IRExp {
    public Boolean v;

    public IRBool(Boolean v) {
        this.v = v;
    }

    @Override
    public Object accept(IRExpVisitor ask) {
        return ask.forBool(this);
    }
}
