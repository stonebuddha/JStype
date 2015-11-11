package ir;

import fj.data.Set;
import fj.P2;

/**
 * Created by wayne on 15/10/27.
 */
public class IRMethod extends IRNode {
    public IRPVar self, args;
    public IRStmt s;
    public Set<IRPVar> freeVars;
    public Set<Integer> canEscapeVar, canEscapeObj, cannotEscape;

    public IRMethod(IRPVar self, IRPVar args, IRStmt s) {
        this.self = self;
        this.args = args;
        this.s = s;
        freeVars = IRNode.free(this);
        P2<Set<Integer>, Set<Integer>> escapeSet = IRNode.escape(this);
        canEscapeVar = escapeSet._1();
        canEscapeObj = escapeSet._2();
        // TODO cannotEscape
    }
}
