package ir;

import java.util.ArrayList;

/**
 * Created by wayne on 15/10/27.
 */
public class IRSeq extends IRStmt {
    public ArrayList<IRStmt> ss;

    public IRSeq(ArrayList<IRStmt> ss) {
        this.ss = ss;
    }
}
