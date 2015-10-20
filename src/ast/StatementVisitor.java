package ast;

import java.util.ArrayList;

/**
 * Created by wayne on 15/10/16.
 */
public interface StatementVisitor {
    Object forBlockStatement(ArrayList<Statement> body);
    Object forBreakStatement(IdentifierExpression label);
    Object forContinueStatement(IdentifierExpression label);
    Object forDebuggerStatement();
    Object forDoWhileStatement(Statement body, Expression test);
    Object forEmptyStatement();
    Object forExpressionStatement(Expression expression);
    Object forForInStatement(Node left, Expression right, Statement body);
    Object forForStatement(Node init, Expression test, Expression update, Statement body);
    Object forFunctionDeclaration(IdentifierExpression id, ArrayList<IdentifierExpression> params, BlockStatement body);
    Object forIfStatement(Expression test, Statement consequent, Statement alternate);
    Object forLabeledStatement(IdentifierExpression label, Statement body);
    Object forReturnStatement(Expression argument);
    Object forSwitchStatement(Expression discriminant, ArrayList<SwitchCase> cases);
    Object forThrowStatement(Expression argument);
    Object forTryStatement(BlockStatement block, CatchClause handler, BlockStatement finalizer);
    Object forVariableDeclaration(ArrayList<VariableDeclarator> declarations);
    Object forWhileStatement(Expression test, Statement body);
    Object forWithStatement(Expression object, Statement body);
}
