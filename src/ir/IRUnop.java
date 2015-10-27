package ir;

/**
 * Created by wayne on 15/10/27.
 */
public class IRUnop extends IRExp {
    public Uop op;
    public IRExp e;

    public IRUnop(Uop op, IRExp e) {
        this.op = op;
        this.e = e;
    }
}
