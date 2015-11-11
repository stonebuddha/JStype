package ast;

import fj.P2;
import fj.data.List;

/**
 * Created by wayne on 10/15/15.
 */
public class VariableDeclaration extends Declaration {
    List<VariableDeclarator> declarations;

    public VariableDeclaration(List<VariableDeclarator> declarations) {
        this.declarations = declarations;
    }

    public List<VariableDeclarator> getDeclarations() {
        return declarations;
    }

    @Override
    public <T> P2<Statement, T> accept(TransformVisitor<T> ask) {
        return ask.forVariableDeclaration(this);
    }
    @Override
    public Statement accept(SimpleTransformVisitor ask) {
        return ask.forVariableDeclaration(this);
    }
    @Override
    public <T> T accept(StatementVisitor<T> ask) {
        return ask.forVariableDeclaration(this);
    }
}
