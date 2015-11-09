package ast;

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

    public Object accept(StatementVisitor ask) {
        return ask.forVariableDeclaration(this);
    }
}
