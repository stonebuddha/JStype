package ast;

import java.util.ArrayList;

/**
 * Created by wayne on 15/10/16.
 */
public interface StatementVisitor {
    Object forBlockStatement(BlockStatement blockStatement);
    Object forBreakStatement(BreakStatement breakStatement);
    Object forContinueStatement(ContinueStatement continueStatement);
    Object forDebuggerStatement(DebuggerStatement debuggerStatement);
    Object forDoWhileStatement(DoWhileStatement doWhileStatement);
    Object forEmptyStatement(EmptyStatement emptyStatement);
    Object forExpressionStatement(ExpressionStatement expressionStatement);
    Object forForInStatement(ForInStatement forInStatement);
    Object forForStatement(ForStatement forStatement);
    Object forFunctionDeclaration(FunctionDeclaration functionDeclaration);
    Object forIfStatement(IfStatement ifStatement);
    Object forLabeledStatement(LabeledStatement labeledStatement);
    Object forReturnStatement(ReturnStatement returnStatement);
    Object forSwitchStatement(SwitchStatement switchStatement);
    Object forThrowStatement(ThrowStatement throwStatement);
    Object forTryStatement(TryStatement tryStatement);
    Object forVariableDeclaration(VariableDeclaration variableDeclaration);
    Object forWhileStatement(WhileStatement whileStatement);
    Object forWithStatement(WithStatement withStatement);
}
