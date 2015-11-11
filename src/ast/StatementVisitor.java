package ast;

/**
 * Created by wayne on 15/10/16.
 */
public interface StatementVisitor<T> {
    T forBlockStatement(BlockStatement blockStatement);
    T forBreakStatement(BreakStatement breakStatement);
    T forContinueStatement(ContinueStatement continueStatement);
    T forDebuggerStatement(DebuggerStatement debuggerStatement);
    T forDoWhileStatement(DoWhileStatement doWhileStatement);
    T forEmptyStatement(EmptyStatement emptyStatement);
    T forExpressionStatement(ExpressionStatement expressionStatement);
    T forForInStatement(ForInStatement forInStatement);
    T forForStatement(ForStatement forStatement);
    T forFunctionDeclaration(FunctionDeclaration functionDeclaration);
    T forIfStatement(IfStatement ifStatement);
    T forLabeledStatement(LabeledStatement labeledStatement);
    T forReturnStatement(ReturnStatement returnStatement);
    T forSwitchStatement(SwitchStatement switchStatement);
    T forThrowStatement(ThrowStatement throwStatement);
    T forTryStatement(TryStatement tryStatement);
    T forVariableDeclaration(VariableDeclaration variableDeclaration);
    T forWhileStatement(WhileStatement whileStatement);
    T forWithStatement(WithStatement withStatement);
}
