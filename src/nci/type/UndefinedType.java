package nci.type;

/**
 * Created by wayne on 15/10/21.
 */
public class UndefinedType {

    public static final UndefinedType bottom = new UndefinedType();
    public static final UndefinedType jsUndefined = new UndefinedType();

    static UndefinedType merge(UndefinedType a, UndefinedType b) {
        if (a.equals(jsUndefined) || b.equals(jsUndefined)) {
            return jsUndefined;
        } else {
            return bottom;
        }
    }
}
