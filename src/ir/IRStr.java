package ir;

/**
 * Created by wayne on 15/10/27.
 */
public class IRStr extends IRExp {
    public String v;

    public IRStr(String v) {
        this.v = v;
    }

    @Override
    public Object accept(IRExpVisitor ask) {
        return ask.forStr(this);
    }
}
