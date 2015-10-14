package ast;

import java.util.ArrayList;

/**
 * Created by Hwhitetooth on 15/10/14.
 */

public class SwitchStatement extends Statement {
    Expression discriminant;
    ArrayList<SwitchCase> cases;
    public SwitchStatement(Expression discrinimant, ArrayList<SwitchCase> cases) {
        this.discriminant = discrinimant;
        this.cases = cases;
    }
}
