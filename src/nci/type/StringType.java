package nci.type;

/**
 * Created by wayne on 15/10/21.
 */
public class StringType {

    public static final StringType bottom = new StringType();
    public static final StringType jsUIntString = new StringType();
    public static final StringType jsNotUIntString = new StringType();
    public static final StringType jsString = new StringType();

    static StringType merge(StringType a, StringType b) {
        if (a.equals(b)) {
            return a;
        } else {
            if (a instanceof ConcreteStringType) {
                a = ((ConcreteStringType)a).upType();
            }
            if (b instanceof ConcreteStringType) {
                b = ((ConcreteStringType)b).upType();
            }
            if (a.equals(b)) {
                return a;
            } else {
                return jsString;
            }
        }
    }
}
