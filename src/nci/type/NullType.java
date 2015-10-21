package nci.type;

/**
 * Created by wayne on 15/10/21.
 */
public class NullType {

    public static final NullType bottom = new NullType();
    public static final NullType jsNull = new NullType();

    static NullType merge(NullType a, NullType b) {
        if (a.equals(jsNull) || b.equals(jsNull)) {
            return jsNull;
        } else {
            return bottom;
        }
    }
}
