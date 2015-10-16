package ast;

import java.util.ArrayList;

/**
 * Created by wayne on 15/10/16.
 */
public interface ExpressionVisitor {
    Object forArrayExpression(ArrayList<Expression> elements);
    Object forAssignmentExpression(String operator, Object left, Expression right);
    Object forBinaryExpression(String operator, Expression left, Expression right);
    Object forCallExpression(Expression callee, ArrayList<Expression> arguments);
    Object forConditionalExpression(Expression test, Expression alternate, Expression consequent);
    Object forFunctionExpression(String id, ArrayList<String> params, BlockStatement body);
    Object forIdentifierExpression(String name);
    Object forLiteralExpression(Literal literal);
    Object forLogicalExpression(String operator, Expression left, Expression right);
    Object forMemberExpression(Expression object, Expression property, boolean computed);
    Object forNewExpression(Expression callee, ArrayList<Expression> arguments);
    Object forObjectExpression(ArrayList<Property> properties);
    Object forSequenceExpression(ArrayList<Expression> expressions);
    Object forThisExpression();
    Object forUnaryExpression(String operator, boolean prefix, Expression argument);
    Object forUpdateExpression(String operator, Expression argument, boolean prefix);
}
