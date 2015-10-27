package ir;

/**
 * Created by wayne on 15/10/27.
 */
public class IRMethod extends IRNode {
    public IRPVar self, args;
    public IRStmt s;

    public IRMethod(IRPVar self, IRPVar args, IRStmt s) {
        this.self = self;
        this.args = args;
        this.s = s;
    }
}
