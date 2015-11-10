package ast;

/**
 * Created by wayne on 15/10/16.
 */
public interface ExpressionVisitor {
    Object forArrayExpression(ArrayExpression arrayExpression);
    Object forAssignmentExpression(AssignmentExpression assignmentExpression);
    Object forBinaryExpression(BinaryExpression binaryExpression);
    Object forCallExpression(CallExpression callExpression);
    Object forConditionalExpression(ConditionalExpression conditionalExpression);
    Object forFunctionExpression(FunctionExpression functionExpression);
    Object forLiteralExpression(LiteralExpression literalExpression);
    Object forLogicalExpression(LogicalExpression logicalExpression);
    Object forMemberExpression(MemberExpression memberExpression);
    Object forNewExpression(NewExpression newExpression);
    Object forObjectExpression(ObjectExpression objectExpression);
    Object forRealIdentifierExpression(RealIdentifierExpression realIdentifierExpression);
    Object forScratchIdentifierExpression(ScratchIdentifierExpression scratchIdentifierExpression);
    Object forScratchSequenceExpression(ScratchSequenceExpression scratchSequenceExpression);
    Object forSequenceExpression(SequenceExpression sequenceExpression);
    Object forThisExpression(ThisExpression thisExpression);
    Object forUnaryExpression(UnaryExpression unaryExpression);
    Object forUpdateExpression(UpdateExpression updateExpression);
}
