package ast;

import java.util.ArrayList;

/**
 * Created by Hwhitetooth on 15/10/14.
 */
public class SwitchCase extends Node {
    Expression test;
    ArrayList<Statement> consequent;
}
