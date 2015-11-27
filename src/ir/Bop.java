package ir;

/**
 * Created by wayne on 15/10/27.
 */
public class Bop {

    public static final Bop Plus = new Bop();
    public static final Bop Minus = new Bop();
    public static final Bop Times = new Bop();
    public static final Bop Divide = new Bop();
    public static final Bop Mod = new Bop();
    public static final Bop SHL = new Bop();
    public static final Bop SAR = new Bop();
    public static final Bop SHR = new Bop();
    public static final Bop LessThan = new Bop();
    public static final Bop LessEqual = new Bop();
    public static final Bop And = new Bop();
    public static final Bop Or = new Bop();
    public static final Bop Xor = new Bop();
    public static final Bop LogicalAnd = new Bop();
    public static final Bop LogicalOr = new Bop();

    public static final Bop StrConcat = new Bop();
    public static final Bop StrLessThan = new Bop();
    public static final Bop StrLessEqual = new Bop();

    public static final Bop StrictEqual = new Bop();
    public static final Bop NonStrictEqual = new Bop();
    public static final Bop Access = new Bop();
    public static final Bop InstanceOf = new Bop();
    public static final Bop In = new Bop();

    @Override
    public String toString() {
        if (this.equals(Plus)) return "+";
        else if (this.equals(Minus)) return "-";
        else if (this.equals(StrictEqual)) return "===";
        else if (this.equals(NonStrictEqual)) return "==";
        else return "BOP";
    }
}
