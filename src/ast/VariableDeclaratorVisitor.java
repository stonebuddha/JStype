package ast;

/**
 * Created by wayne on 15/10/16.
 */
public interface VariableDeclaratorVisitor<T> {
    T forVariableDeclarator(VariableDeclarator variableDeclarator);
}
