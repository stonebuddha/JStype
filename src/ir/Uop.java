package ir;

/**
 * Created by wayne on 15/10/27.
 */
public class Uop {

    public static final Uop Negate = new Uop();
    public static final Uop Not = new Uop();

    public static final Uop LogicalNot = new Uop();

    public static final Uop TypeOf = new Uop();
    public static final Uop ToBool = new Uop();
    public static final Uop IsPrim = new Uop();
    public static final Uop ToStr = new Uop();
    public static final Uop ToNum = new Uop();

    @Override
    public String toString() {
        if (equals(Negate)) {
            return "-";
        } else if (equals(Not)) {
            return "~";
        } else if (equals(LogicalNot)) {
            return "!";
        } else if (equals(TypeOf)) {
            return "typeof";
        } else if (equals(ToBool)) {
            return "tobool";
        } else if (equals(IsPrim)) {
            return "isprim";
        } else if (equals(ToStr)) {
            return "tostr";
        } else if (equals(ToNum)) {
            return "tonum";
        } else {
            return "UOP";
        }
    }
}
