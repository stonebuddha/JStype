package nci.type;

/**
 * Created by wayne on 15/10/21.
 */
public class BooleanType {

    public static final BooleanType bottom = new BooleanType();
    public static final BooleanType jsTrue = new BooleanType();
    public static final BooleanType jsFalse = new BooleanType();
    public static final BooleanType jsBoolean = new BooleanType();

    static BooleanType merge(BooleanType a, BooleanType b) {
        if (a.equals(b)) {
            return a;
        } else if (a.equals(bottom)) {
            return b;
        } else if (b.equals(bottom)) {
            return a;
        } else {
            return jsBoolean;
        }
    }
}
