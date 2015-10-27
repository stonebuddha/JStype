package ir;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by wayne on 15/10/27.
 */
public class IRDecl extends IRStmt {
    public ArrayList<Map.Entry<IRPVar, IRExp>> bind;
    public IRStmt s;

    public IRDecl(ArrayList<Map.Entry<IRPVar, IRExp>> bind, IRStmt s) {
        this.bind = bind;
        this.s = s;
    }
}
