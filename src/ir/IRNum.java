package ir;

/**
 * Created by wayne on 15/10/27.
 */
public class IRNum extends IRExp {
    public Double v;

    public IRNum(Double v) {
        this.v = v;
    }

    @Override
    public Object accept(IRExpVisitor ask) {
        return ask.forNum(this);
    }
}
