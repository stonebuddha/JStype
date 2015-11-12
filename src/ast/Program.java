/**
 * Created by wayne on 10/14/15.
 */

package ast;

import fj.P2;
import fj.data.List;

public class Program extends Node {
    List<Statement> body;

    public Program(List<Statement> body) {
        this.body = body;
    }

    public List<Statement> getBody() {
        return body;
    }

    public <T> T accept(ProgramVisitor<T> ask) {
        return ask.forProgram(this);
    }
    public <T> P2<Program, T> accept(TransformVisitor<T> ask) {
        return ask.forProgram(this);
    }
    public Program accept(SimpleTransformVisitor ask) {
        return ask.forProgram(this);
    }
}
