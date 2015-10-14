/**
 * Created by wayne on 10/14/15.
 */

package ast;

import java.util.ArrayList;

public class Program extends Node {
    ArrayList<Statement> body;
    public Program(ArrayList<Statement> body) {
        this.body = body;
    }
}
