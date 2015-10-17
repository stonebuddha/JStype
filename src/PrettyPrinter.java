import ast.*;

import java.util.ArrayList;

/**
 * Created by wayne on 15/10/16.
 */
public class PrettyPrinter {

    static final FormatProgramV formatProgram = new FormatProgramV();
    static final FormatStatementV formatStatement = new FormatStatementV();
    static final FormatExpressionV formatExpression = new FormatExpressionV();
    static final FormatLiteralV formatLiteral = new FormatLiteralV();
    static final FormatSwitchCaseV formatSwitchCase = new FormatSwitchCaseV();
    static final FormatCatchClauseV formatCatchClause = new FormatCatchClauseV();
    static final FormatVariableDeclaratorV formatVariableDeclarator = new FormatVariableDeclaratorV();
    static final FormatPropertyV formatProperty = new FormatPropertyV();

    static class FormatProgramV implements ProgramVisitor {
        public Object forProgram(ArrayList<Statement> body) {
            StringBuilder builder = new StringBuilder();
            for (Statement stmt : body) {
                builder.append((String)stmt.accept(formatStatement));
                builder.append("\n");
            }
            return builder.toString();
        }
    }

    static class FormatStatementV implements StatementVisitor {
        public Object forBlockStatement(ArrayList<Statement> body) {
            StringBuilder builder = new StringBuilder();
            builder.append("{\n");
            for (Statement stmt : body) {
                builder.append((String)stmt.accept(formatStatement));
                builder.append("\n");
            }
            builder.append("}");
            return builder.toString();
        }
        public Object forBreakStatement(String label) {
            StringBuilder builder = new StringBuilder();
            builder.append("break");
            if (label == null) {
                builder.append(";");
            } else {
                builder.append(" ");
                builder.append(label);
                builder.append(";");
            }
            return builder.toString();
        }
        public Object forContinueStatement(String label) {
            StringBuilder builder = new StringBuilder();
            builder.append("continue");
            if (label == null) {
                builder.append(";");
            } else {
                builder.append(" ");
                builder.append(label);
                builder.append(";");
            }
            return builder.toString();
        }
        public Object forDebuggerStatement() {
            return "debug();";
        }
        public Object forDoWhileStatement(Statement body, Expression test) {
            StringBuilder builder = new StringBuilder();
            builder.append("do ");
            builder.append((String)body.accept(formatStatement));
            builder.append(" while (");
            builder.append((String)test.accept(formatExpression));
            builder.append(");");
            return builder.toString();
        }
        public Object forEmptyStatement() {
            return ";";
        }
        public Object forExpressionStatement(Expression expression) {
            StringBuilder builder = new StringBuilder();
            builder.append((String)expression.accept(formatExpression));
            builder.append(";");
            return builder.toString();
        }
        public Object forForInStatement(Node left, Expression right, Statement body) {
            StringBuilder builder = new StringBuilder();
            builder.append("for (");
            if (left instanceof VariableDeclaration) {
                builder.append((String)((VariableDeclaration)left).accept(formatStatement));
            } else {
                builder.append((String)((Expression)left).accept(formatExpression));
            }
            builder.append(" in ");
            builder.append((String)right.accept(formatExpression));
            builder.append(") ");
            builder.append((String)body.accept(formatStatement));
            return builder.toString();
        }
        public Object forForStatement(Node init, Expression test, Expression update, Statement body) {
            StringBuilder builder = new StringBuilder();
            builder.append("for (");
            if (init instanceof VariableDeclaration) {
                builder.append((String)((VariableDeclaration)init).accept(formatStatement));
            } else {
                builder.append((String)((Expression)init).accept(formatExpression));
            }
            builder.append("; ");
            builder.append((String)test.accept(formatExpression));
            builder.append("; ");
            builder.append((String)update.accept(formatExpression));
            builder.append(") ");
            builder.append((String)body.accept(formatStatement));
            return builder.toString();
        }
        public Object forFunctionDeclaration(String id, ArrayList<String> params, BlockStatement body) {
            StringBuilder builder = new StringBuilder();
            builder.append("function ");
            builder.append(id);
            builder.append("(");
            boolean first = true;
            for (String param : params) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append(param);
            }
            builder.append(") ");
            builder.append((String)body.accept(formatStatement));
            return builder.toString();
        }
        public Object forIfStatement(Expression test, Statement consequent, Statement alternate) {
            StringBuilder builder = new StringBuilder();
            builder.append("if (");
            builder.append((String)test.accept(formatExpression));
            builder.append(") ");
            builder.append((String)consequent.accept(formatStatement));
            if (alternate != null) {
                builder.append(" else ");
                builder.append((String)alternate.accept(formatStatement));
            }
            return builder.toString();
        }
        public Object forLabeledStatement(String label, Statement body) {
            StringBuilder builder = new StringBuilder();
            builder.append(label);
            builder.append(":\n");
            builder.append((String)body.accept(formatStatement));
            return builder.toString();
        }
        public Object forReturnStatement(Expression argument) {
            StringBuilder builder = new StringBuilder();
            builder.append("return");
            if (argument == null) {
                builder.append(";");
            } else {
                builder.append(" ");
                builder.append((String)argument.accept(formatExpression));
                builder.append(";");
            }
            return builder.toString();
        }
        public Object forSwitchStatement(Expression discriminant, ArrayList<SwitchCase> cases) {
            StringBuilder builder = new StringBuilder();
            builder.append("switch (");
            builder.append((String)discriminant.accept(formatExpression));
            builder.append(") {\n");
            for (SwitchCase sc : cases) {
                builder.append((String)sc.accept(formatSwitchCase));
            }
            builder.append("}");
            return builder.toString();
        }
        public Object forThrowStatement(Expression argument) {
            StringBuilder builder = new StringBuilder();
            builder.append("throw ");
            builder.append((String)argument.accept(formatExpression));
            builder.append(";");
            return builder.toString();
        }
        public Object forTryStatement(BlockStatement block, CatchClause handler, BlockStatement finalizer) {
            StringBuilder builder = new StringBuilder();
            builder.append("try ");
            builder.append((String)block.accept(formatStatement));
            builder.append((String)handler.accept(formatCatchClause));
            builder.append(" final ");
            builder.append((String)finalizer.accept(formatStatement));
            return block.toString();
        }
        public Object forVariableDeclaration(ArrayList<VariableDeclarator> declarations) {
            StringBuilder builder = new StringBuilder();
            builder.append("var ");
            boolean first = true;
            for (VariableDeclarator decl : declarations) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append((String)decl.accept(formatVariableDeclarator));
            }
            builder.append(";");
            return builder.toString();
        }
        public Object forWhileStatement(Expression test, Statement body) {
            StringBuilder builder = new StringBuilder();
            builder.append("while (");
            builder.append((String)test.accept(formatExpression));
            builder.append(") ");
            builder.append((String)body.accept(formatStatement));
            return builder.toString();
        }
        public Object forWithStatement(Expression object, Statement body) {
            StringBuilder builder = new StringBuilder();
            builder.append("with ");
            builder.append((String)object.accept(formatExpression));
            builder.append(" ");
            builder.append((String)body.accept(formatStatement));
            return builder.toString();
        }
    }

    static class FormatExpressionV implements ExpressionVisitor {
        @Override
        public Object forArrayExpression(ArrayList<Expression> elements) {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            boolean first = true;
            for (Expression exp : elements) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                if (exp != null) {
                    builder.append((String)exp.accept(formatExpression));
                }
            }
            builder.append("]");
            return builder.toString();
        }

        @Override
        public Object forAssignmentExpression(String operator, Object left, Expression right) {
            StringBuilder builder = new StringBuilder();
            if (left instanceof String) {
                builder.append(left);
            } else {
                builder.append((String)((Expression)left).accept(formatExpression));
            }
            builder.append(" ");
            builder.append(operator);
            builder.append(" ");
            builder.append((String)right.accept(formatExpression));
            return builder.toString();
        }

        @Override
        public Object forBinaryExpression(String operator, Expression left, Expression right) {
            StringBuilder builder = new StringBuilder();
            builder.append((String)left.accept(formatExpression));
            builder.append(" ");
            builder.append(operator);
            builder.append(" ");
            builder.append((String)right.accept(formatExpression));
            return builder.toString();
        }

        @Override
        public Object forCallExpression(Expression callee, ArrayList<Expression> arguments) {
            StringBuilder builder = new StringBuilder();
            builder.append((String)callee.accept(formatExpression));
            builder.append("(");
            boolean first = true;
            for (Expression exp : arguments) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append((String)exp.accept(formatExpression));
            }
            builder.append(")");
            return builder.toString();
        }

        @Override
        public Object forConditionalExpression(Expression test, Expression alternate, Expression consequent) {
            StringBuilder builder = new StringBuilder();
            builder.append((String)test.accept(formatExpression));
            builder.append(" ? ");
            builder.append((String)consequent.accept(formatExpression));
            builder.append(" : ");
            builder.append((String)alternate.accept(formatExpression));
            return builder.toString();
        }

        @Override
        public Object forFunctionExpression(String id, ArrayList<String> params, BlockStatement body) {
            StringBuilder builder = new StringBuilder();
            builder.append("function ");
            if (id != null) {
                builder.append(id);
            }
            builder.append("(");
            boolean first = true;
            for (String p : params) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append(p);
            }
            builder.append(") ");
            builder.append((String)body.accept(formatStatement));
            return builder.toString();
        }

        @Override
        public Object forIdentifierExpression(String name) {
            return name;
        }

        @Override
        public Object forLiteralExpression(Literal literal) {
            return literal.accept(formatLiteral);
        }

        @Override
        public Object forLogicalExpression(String operator, Expression left, Expression right) {
            StringBuilder builder = new StringBuilder();
            builder.append((String)left.accept(formatExpression));
            builder.append(" ");
            builder.append(operator);
            builder.append(" ");
            builder.append((String)right.accept(formatExpression));
            return builder.toString();
        }

        @Override
        public Object forMemberExpression(Expression object, Expression property, boolean computed) {
            StringBuilder builder = new StringBuilder();
            builder.append((String)object.accept(formatExpression));
            builder.append(".");
            builder.append((String)property.accept(formatExpression));
            return builder.toString();
        }

        @Override
        public Object forNewExpression(Expression callee, ArrayList<Expression> arguments) {
            StringBuilder builder = new StringBuilder();
            builder.append("new ");
            builder.append((String)callee.accept(formatExpression));
            builder.append("(");
            boolean first = true;
            for (Expression exp : arguments) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append((String)exp.accept(formatExpression));
            }
            builder.append(")");
            return builder.toString();
        }

        @Override
        public Object forObjectExpression(ArrayList<Property> properties) {
            StringBuilder builder = new StringBuilder();
            builder.append("{");
            boolean first = true;
            for (Property p : properties) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append((String)p.accept(formatProperty));
            }
            builder.append("}");
            return builder.toString();
        }

        @Override
        public Object forSequenceExpression(ArrayList<Expression> expressions) {
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for (Expression exp : expressions) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append((String)exp.accept(formatExpression));
            }
            return builder.toString();
        }

        @Override
        public Object forThisExpression() {
            return "this";
        }

        @Override
        public Object forUnaryExpression(String operator, boolean prefix, Expression argument) {
            StringBuilder builder = new StringBuilder();
            if (prefix) {
                builder.append(operator);
                builder.append((String)argument.accept(formatExpression));
            } else {
                builder.append((String)argument.accept(formatExpression));
                builder.append(operator);
            }
            return builder.toString();
        }

        @Override
        public Object forUpdateExpression(String operator, Expression argument, boolean prefix) {
            StringBuilder builder = new StringBuilder();
            if (prefix) {
                builder.append(operator);
                builder.append((String)argument.accept(formatExpression));
            } else {
                builder.append((String)argument.accept(formatExpression));
                builder.append(operator);
            }
            return builder.toString();
        }
    }

    static class FormatLiteralV implements LiteralVisitor {
        @Override
        public Object forBooleanLiteral(boolean value) {
            if (value) {
                return "true";
            } else {
                return "false";
            }
        }

        @Override
        public Object forNullLiteral() {
            return "null";
        }

        @Override
        public Object forNumberLiteral(Number value) {
            return value.toString();
        }

        @Override
        public Object forRegExpLiteral(String pattern, String flags) {
            return "/" + pattern + "/" + flags + "/";
        }

        @Override
        public Object forStringLiteral(String value) {
            return "\"" + value + "\"";
        }
    }

    static class FormatSwitchCaseV implements SwitchCaseVisitor {
        @Override
        public Object forSwitchCase(Expression test, ArrayList<Statement> consequent) {
            StringBuilder builder = new StringBuilder();
            builder.append("case ");
            builder.append((String)test.accept(formatExpression));
            builder.append(":\n");
            for (Statement stmt : consequent) {
                builder.append((String)stmt.accept(formatStatement));
            }
            return builder.toString();
        }
    }

    static class FormatCatchClauseV implements CatchClauseVisitor {
        @Override
        public Object forCatchClause(String param, BlockStatement body) {
            StringBuilder builder = new StringBuilder();
            builder.append("catch (");
            builder.append(param);
            builder.append(") ");
            builder.append((String)body.accept(formatStatement));
            return builder.toString();
        }
    }

    static class FormatVariableDeclaratorV implements VariableDeclaratorVisitor {
        @Override
        public Object forVariableDeclarator(String id, Expression init) {
            StringBuilder builder = new StringBuilder();
            builder.append(id);
            if (init != null) {
                builder.append(" = ");
                builder.append((String)init.accept(formatExpression));
            }
            return builder.toString();
        }
    }

    static class FormatPropertyV implements PropertyVisitor {
        @Override
        public Object forProperty(Object key, Expression value, String kind) {
            StringBuilder builder = new StringBuilder();
            if (key instanceof String) {
                builder.append(key);
            } else {
                builder.append((String)((Literal)key).accept(formatLiteral));
            }
            builder.append(" : ");
            builder.append((String)value.accept(formatExpression));
            return builder.toString();
        }
    }
}
