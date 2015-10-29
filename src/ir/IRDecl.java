package ir;

import com.google.common.collect.ImmutableList;

import java.util.Map;

/**
 * Created by wayne on 15/10/27.
 */
public class IRDecl extends IRStmt {
    public ImmutableList<Map.Entry<IRPVar, IRExp>> bind;
    public IRStmt s;

    public IRDecl(ImmutableList<Map.Entry<IRPVar, IRExp>> bind, IRStmt s) {
        this.bind = bind;
        this.s = s;
    }

    @Override
    public Object accept(IRStmtVisitor ask) {
        return ask.forDecl(this);
    }
}
