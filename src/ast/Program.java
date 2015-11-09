/**
 * Created by wayne on 10/14/15.
 */

package ast;

import fj.data.List;

public class Program extends Node {
    List<Statement> body;

    public Program(List<Statement> body) {
        this.body = body;
    }

    public List<Statement> getBody() {
        return body;
    }

    public Object accept(ProgramVisitor ask) {
        return ask.forProgram(this);
    }
}
