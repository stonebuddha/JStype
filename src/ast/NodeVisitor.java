package ast;

import java.util.ArrayList;

/**
 * Created by wayne on 15/10/16.
 */
public interface NodeVisitor {
    Object forArrayExpression(ArrayList<Expression> elements);
    Object forAssignmentExpression(String operator, Object left, Expression right);
    Object forBinaryExpression(String operator, Expression left, Expression right);
    Object forBlockStatement(ArrayList<Statement> body);
    Object forBooleanLiteral(boolean value);
    Object forBreakStatement(String label);
    Object forCallExpression(Expression callee, ArrayList<Expression> arguments);
    Object forCatchClause(String param, BlockStatement body);
    Object forConditionalExpression(Expression test, Expression alternate, Expression consequent);
    Object forContinueStatement(String label);
    Object forDebuggerStatement();
    Object forDoWhileStatement(Statement body, Expression test);
    Object forEmptyStatement();
    Object forExpressionStatement(Expression expression);
    Object forForInStatement(Node left, Expression right, Statement body);
    Object forForStatement(Node init, Expression test, Expression update, Statement body);
    Object forFunctionDeclaration(String id, ArrayList<String> params, BlockStatement body);
    Object forFunctionExpression(String id, ArrayList<String> params, BlockStatement body);
    Object forIdentifierExpression(String name);
    Object forIfStatement(Expression test, Statement consequent, Statement alternate);
    Object forLabeledStatement(String label);
    Object forLiteralExpression(Literal literal);
    Object forLogicalExpression(String operator, Expression left, Expression right);
    Object forMemberExpression(Expression object, Expression property, boolean computed);
    Object forNewExpression(Expression callee, ArrayList<Expression> arguments);
    Object forNullLiteral();
    Object forNumberLiteral(Number value);
    Object forObjectExpression(ArrayList<Property> properties);
    Object forProgram(ArrayList<Statement> body);
    Object forProperty(Object key, Expression value, String kind);
    Object forRegExpLiteral(String pattern, String flags);
    Object forReturnExpression(Expression argument);
    Object forSequenceExpression(ArrayList<Expression> expressions);
    Object forStringLiteral(String value);
    Object forSwitchCase(Expression test, ArrayList<Statement> consequent);
    Object forSwitchStatement(Expression discriminant, ArrayList<SwitchCase> cases);
    Object forThisExpression();
    Object forThrowStatement(Expression argument);
    Object forTryStatement(BlockStatement block, CatchClause handler, BlockStatement finalizer);
    Object forUnaryExpression(String operator, boolean prefix, Expression argument);
    Object forUpdateExpression(String operator, Expression argument, boolean prefix);
    Object forVariableDeclaration(ArrayList<VariableDeclarator> declarations);
    Object forVariableDeclarator(String id, Expression init);
    Object forWhileStatement(Expression test, Statement body);
    Object forWithStatement(Expression object, Statement body);
}
