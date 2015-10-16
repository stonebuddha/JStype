package ast;

/**
 * Created by wayne on 15/10/16.
 */
public interface VariableDeclaratorVisitor {
    Object forVariableDeclarator(String id, Expression init);
}
