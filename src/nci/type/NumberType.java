package nci.type;

/**
 * Created by wayne on 15/10/21.
 */
public class NumberType {

    public static final NumberType bottom = new NumberType();
    public static final NumberType jsINF = new NumberType();
    public static final NumberType jsPINF = new NumberType();
    public static final NumberType jsNINF = new NumberType();
    public static final NumberType jsNaN = new NumberType();
    public static final NumberType jsUInt = new NumberType();
    public static final NumberType jsNotUInt = new NumberType();
    public static final NumberType jsNumber = new NumberType();

    static NumberType merge(NumberType a, NumberType b) {
        if (a.equals(b)) {
            return a;
        } else if (a.equals(bottom)) {
            return b;
        } else if (b.equals(bottom)) {
            return a;
        } else {
            if (a instanceof ConcreteNumberType) {
                a = ((ConcreteNumberType)a).upType();
            }
            if (b instanceof ConcreteNumberType) {
                b = ((ConcreteNumberType)b).upType();
            }
            if (a.equals(b)) {
                return a;
            } else if (a.equals(jsPINF) && b.equals(jsNINF)) {
                return jsINF;
            } else if (a.equals(jsNINF) && b.equals(jsPINF)) {
                return jsINF;
            } else {
                return jsNumber;
            }
        }
    }
}
