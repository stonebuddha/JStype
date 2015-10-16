package ast;

import java.util.ArrayList;

/**
 * Created by wayne on 10/15/15.
 */
public class VariableDeclaration extends Declaration {
    ArrayList<VariableDeclarator> declarations;
    public VariableDeclaration(ArrayList<VariableDeclarator> declarations) {
        this.declarations = declarations;
    }
    Object accept(NodeVisitor ask) {
        return ask.forVariableDeclaration(declarations);
    }
}
