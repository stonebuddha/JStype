package ast;

import java.util.ArrayList;

/**
 * Created by Hwhitetooth on 15/10/14.
 */

public class SwitchStatement extends Statement {
    Expression discriminant;
    ArrayList<SwitchCase> cases;
    boolean lexical;
    public SwitchStatement(Expression discrinimant, ArrayList<SwitchCase> cases, boolean lexical) {
        this.discriminant = discrinimant;
        this.cases = cases;
        this.lexical = lexical;
    }
}
