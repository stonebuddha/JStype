package ast;

import fj.P2;

/**
 * Created by wayne on 15/11/11.
 */
public interface TransformVisitor<T> {
    P2<Program, T> forProgram(Program program);

    P2<Statement, T> forBlockStatement(BlockStatement blockStatement);
    P2<Statement, T> forBreakStatement(BreakStatement breakStatement);
    P2<Statement, T> forContinueStatement(ContinueStatement continueStatement);
    P2<Statement, T> forDebuggerStatement(DebuggerStatement debuggerStatement);
    P2<Statement, T> forDoWhileStatement(DoWhileStatement doWhileStatement);
    P2<Statement, T> forEmptyStatement(EmptyStatement emptyStatement);
    P2<Statement, T> forExpressionStatement(ExpressionStatement expressionStatement);
    P2<Statement, T> forForInStatement(ForInStatement forInStatement);
    P2<Statement, T> forForStatement(ForStatement forStatement);
    P2<Statement, T> forFunctionDeclaration(FunctionDeclaration functionDeclaration);
    P2<Statement, T> forIfStatement(IfStatement ifStatement);
    P2<Statement, T> forLabeledStatement(LabeledStatement labeledStatement);
    P2<Statement, T> forReturnStatement(ReturnStatement returnStatement);
    P2<Statement, T> forSwitchStatement(SwitchStatement switchStatement);
    P2<Statement, T> forThrowStatement(ThrowStatement throwStatement);
    P2<Statement, T> forTryStatement(TryStatement tryStatement);
    P2<Statement, T> forVariableDeclaration(VariableDeclaration variableDeclaration);
    P2<Statement, T> forWhileStatement(WhileStatement whileStatement);
    P2<Statement, T> forWithStatement(WithStatement withStatement);

    P2<Expression, T> forArrayExpression(ArrayExpression arrayExpression);
    P2<Expression, T> forAssignmentExpression(AssignmentExpression assignmentExpression);
    P2<Expression, T> forBinaryExpression(BinaryExpression binaryExpression);
    P2<Expression, T> forCallExpression(CallExpression callExpression);
    P2<Expression, T> forConditionalExpression(ConditionalExpression conditionalExpression);
    P2<Expression, T> forFunctionExpression(FunctionExpression functionExpression);
    P2<Expression, T> forLiteralExpression(LiteralExpression literalExpression);
    P2<Expression, T> forLogicalExpression(LogicalExpression logicalExpression);
    P2<Expression, T> forMemberExpression(MemberExpression memberExpression);
    P2<Expression, T> forNewExpression(NewExpression newExpression);
    P2<Expression, T> forObjectExpression(ObjectExpression objectExpression);
    P2<Expression, T> forRealIdentifierExpression(RealIdentifierExpression realIdentifierExpression);
    P2<Expression, T> forScratchIdentifierExpression(ScratchIdentifierExpression scratchIdentifierExpression);
    P2<Expression, T> forScratchSequenceExpression(ScratchSequenceExpression scratchSequenceExpression);
    P2<Expression, T> forSequenceExpression(SequenceExpression sequenceExpression);
    P2<Expression, T> forThisExpression(ThisExpression thisExpression);
    P2<Expression, T> forUnaryExpression(UnaryExpression unaryExpression);
    P2<Expression, T> forUpdateExpression(UpdateExpression updateExpression);
    P2<Expression, T> forPrintExpression(PrintExpression printExpression);

    P2<CatchClause, T> forCatchClause(CatchClause catchClause);

    P2<SwitchCase, T> forSwitchCase(SwitchCase switchCase);

    P2<VariableDeclarator, T> forVariableDeclarator(VariableDeclarator variableDeclarator);

    P2<Literal, T> forBooleanLiteral(BooleanLiteral booleanLiteral);
    P2<Literal, T> forNullLiteral(NullLiteral nullLiteral);
    P2<Literal, T> forNumberLiteral(NumberLiteral numberLiteral);
    P2<Literal, T> forRegExpLiteral(RegExpLiteral regExpLiteral);
    P2<Literal, T> forStringLiteral(StringLiteral stringLiteral);
    P2<Literal, T> forUndefinedLiteral(UndefinedLiteral undefinedLiteral);

    P2<Property, T> forProperty(Property property);
}
