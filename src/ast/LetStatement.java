package ast;

import java.util.ArrayList;

/**
 * Created by Hwhitetooth on 15/10/14.
 */
public class LetStatement extends Statement {
    ArrayList<VariableDeclarator> head;
    Statement body;
    public LetStatement(ArrayList<VariableDeclarator> head, Statement body) {
        this.head = head;
        this.body = body;
    }
}
