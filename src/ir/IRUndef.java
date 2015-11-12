package ir;

/**
 * Created by wayne on 15/10/27.
 */
public class IRUndef extends IRExp {
    @Override
    public String toString() {
        return "undefined";
    }

    @Override
    public Object accept(IRExpVisitor ask) {
        return ask.forUndef(this);
    }
}
