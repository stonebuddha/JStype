package ast;

/**
 * Created by wayne on 15/11/11.
 */
public interface SimpleTransformVisitor {
    Program forProgram(Program program);

    Statement forBlockStatement(BlockStatement blockStatement);
    Statement forBreakStatement(BreakStatement breakStatement);
    Statement forContinueStatement(ContinueStatement continueStatement);
    Statement forDebuggerStatement(DebuggerStatement debuggerStatement);
    Statement forDoWhileStatement(DoWhileStatement doWhileStatement);
    Statement forEmptyStatement(EmptyStatement emptyStatement);
    Statement forExpressionStatement(ExpressionStatement expressionStatement);
    Statement forForInStatement(ForInStatement forInStatement);
    Statement forForStatement(ForStatement forStatement);
    Statement forFunctionDeclaration(FunctionDeclaration functionDeclaration);
    Statement forIfStatement(IfStatement ifStatement);
    Statement forLabeledStatement(LabeledStatement labeledStatement);
    Statement forReturnStatement(ReturnStatement returnStatement);
    Statement forSwitchStatement(SwitchStatement switchStatement);
    Statement forThrowStatement(ThrowStatement throwStatement);
    Statement forTryStatement(TryStatement tryStatement);
    Statement forVariableDeclaration(VariableDeclaration variableDeclaration);
    Statement forWhileStatement(WhileStatement whileStatement);
    Statement forWithStatement(WithStatement withStatement);

    Expression forArrayExpression(ArrayExpression arrayExpression);
    Expression forAssignmentExpression(AssignmentExpression assignmentExpression);
    Expression forBinaryExpression(BinaryExpression binaryExpression);
    Expression forCallExpression(CallExpression callExpression);
    Expression forConditionalExpression(ConditionalExpression conditionalExpression);
    Expression forFunctionExpression(FunctionExpression functionExpression);
    Expression forLiteralExpression(LiteralExpression literalExpression);
    Expression forLogicalExpression(LogicalExpression logicalExpression);
    Expression forMemberExpression(MemberExpression memberExpression);
    Expression forNewExpression(NewExpression newExpression);
    Expression forObjectExpression(ObjectExpression objectExpression);
    Expression forRealIdentifierExpression(RealIdentifierExpression realIdentifierExpression);
    Expression forScratchIdentifierExpression(ScratchIdentifierExpression scratchIdentifierExpression);
    Expression forScratchSequenceExpression(ScratchSequenceExpression scratchSequenceExpression);
    Expression forSequenceExpression(SequenceExpression sequenceExpression);
    Expression forThisExpression(ThisExpression thisExpression);
    Expression forUnaryExpression(UnaryExpression unaryExpression);
    Expression forUpdateExpression(UpdateExpression updateExpression);

    CatchClause forCatchClause(CatchClause catchClause);

    SwitchCase forSwitchCase(SwitchCase switchCase);

    VariableDeclarator forVariableDeclarator(VariableDeclarator variableDeclarator);

    Literal forBooleanLiteral(BooleanLiteral booleanLiteral);
    Literal forNullLiteral(NullLiteral nullLiteral);
    Literal forNumberLiteral(NumberLiteral numberLiteral);
    Literal forRegExpLiteral(RegExpLiteral regExpLiteral);
    Literal forStringLiteral(StringLiteral stringLiteral);
    Literal forUndefinedLiteral(UndefinedLiteral undefinedLiteral);

    Property forProperty(Property property);
}
