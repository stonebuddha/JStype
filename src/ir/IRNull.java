package ir;

/**
 * Created by wayne on 15/10/27.
 */
public class IRNull extends IRExp {
    @Override
    public String toString() {
        return "null";
    }

    @Override
    public Object accept(IRExpVisitor ask) {
        return ask.forNull(this);
    }
}
