package ast;

import java.util.ArrayList;

/**
 * Created by wayne on 15/10/16.
 */
public interface ExpressionVisitor {
    Object forArrayExpression(ArrayExpression arrayExpression, ArrayList<Expression> elements);
    Object forAssignmentExpression(AssignmentExpression assignmentExpression, String operator, Expression left, Expression right);
    Object forBinaryExpression(BinaryExpression binaryExpression, String operator, Expression left, Expression right);
    Object forCallExpression(CallExpression callExpression, Expression callee, ArrayList<Expression> arguments);
    Object forConditionalExpression(ConditionalExpression conditionalExpression, Expression test, Expression alternate, Expression consequent);
    Object forFunctionExpression(FunctionExpression functionExpression, IdentifierExpression id, ArrayList<IdentifierExpression> params, BlockStatement body);
    Object forIdentifierExpression(IdentifierExpression identifierExpression, String name);
    Object forLiteralExpression(LiteralExpression literalExpression, Literal literal);
    Object forLogicalExpression(LogicalExpression logicalExpression, String operator, Expression left, Expression right);
    Object forMemberExpression(MemberExpression memberExpression, Expression object, Expression property, boolean computed);
    Object forNewExpression(NewExpression newExpression, Expression callee, ArrayList<Expression> arguments);
    Object forObjectExpression(ObjectExpression objectExpression, ArrayList<Property> properties);
    Object forSequenceExpression(SequenceExpression sequenceExpression, ArrayList<Expression> expressions);
    Object forThisExpression(ThisExpression thisExpression);
    Object forUnaryExpression(UnaryExpression unaryExpression, String operator, boolean prefix, Expression argument);
    Object forUpdateExpression(UpdateExpression updateExpression, String operator, Expression argument, boolean prefix);
}
