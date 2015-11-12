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
        return "UOP";
    }
}
