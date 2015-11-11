package ast;

/**
 * Created by wayne on 15/10/16.
 */
public interface ExpressionVisitor<T> {
    T forArrayExpression(ArrayExpression arrayExpression);
    T forAssignmentExpression(AssignmentExpression assignmentExpression);
    T forBinaryExpression(BinaryExpression binaryExpression);
    T forCallExpression(CallExpression callExpression);
    T forConditionalExpression(ConditionalExpression conditionalExpression);
    T forFunctionExpression(FunctionExpression functionExpression);
    T forLiteralExpression(LiteralExpression literalExpression);
    T forLogicalExpression(LogicalExpression logicalExpression);
    T forMemberExpression(MemberExpression memberExpression);
    T forNewExpression(NewExpression newExpression);
    T forObjectExpression(ObjectExpression objectExpression);
    T forRealIdentifierExpression(RealIdentifierExpression realIdentifierExpression);
    T forScratchIdentifierExpression(ScratchIdentifierExpression scratchIdentifierExpression);
    T forScratchSequenceExpression(ScratchSequenceExpression scratchSequenceExpression);
    T forSequenceExpression(SequenceExpression sequenceExpression);
    T forThisExpression(ThisExpression thisExpression);
    T forUnaryExpression(UnaryExpression unaryExpression);
    T forUpdateExpression(UpdateExpression updateExpression);
}
