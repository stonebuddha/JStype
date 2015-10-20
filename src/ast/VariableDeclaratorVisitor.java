package ast;

/**
 * Created by wayne on 15/10/16.
 */
public interface VariableDeclaratorVisitor {
    Object forVariableDeclarator(IdentifierExpression id, Expression init);
}
