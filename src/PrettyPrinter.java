import ast.*;

import java.util.ArrayList;

/**
 * Created by wayne on 15/10/16.
 */
public class PrettyPrinter {

    static class FormatProgramV implements ProgramVisitor {
        public Object forProgram(ArrayList<Statement> body) {
            FormatStatementV formatStatement = new FormatStatementV();
            StringBuilder builder = new StringBuilder();
            for (Statement stmt : body) {
                builder.append((String)stmt.accept(formatStatement));
            }
            return builder.toString();
        }
    }

    static class FormatStatementV implements StatementVisitor {
        public Object forBlockStatement(ArrayList<Statement> body) {
            FormatStatementV formatStatement = new FormatStatementV();
            StringBuilder builder = new StringBuilder();
            builder.append("{\n");
            for (Statement stmt : body) {
                builder.append((String)stmt.accept(formatStatement));
            }
            builder.append("}\n");
            return builder.toString();
        }
        public Object forBreakStatement(String label) {
            StringBuilder builder = new StringBuilder();
            builder.append("break");
            if (label == null) {
                builder.append(";\n");
            } else {
                builder.append(" ");
                builder.append(label);
                builder.append(";\n");
            }
            return builder.toString();
        }
        public Object forContinueStatement(String label) {
            StringBuilder builder = new StringBuilder();
            builder.append("continue");
            if (label == null) {
                builder.append(";\n");
            } else {
                builder.append(" ");
                builder.append(label);
                builder.append(";\n");
            }
            return builder.toString();
        }
        public Object forDebuggerStatement() {

        }
        public Object forDoWhileStatement(Statement body, Expression test) {
            FormatStatementV formatStatement = new FormatStatementV();
            FormatExpressionV formatExpression = new FormatExpressionV();
            StringBuilder builder = new StringBuilder();
            builder.append("do\n");
            builder.append((String)body.accept(formatStatement));
            builder.append("while (");
            builder.append((String)test.accept(formatExpression));
            builder.append(");\n");
            return builder.toString();
        }
        public Object forEmptyStatement() {
            return ";\n";
        }
        public Object forExpressionStatement(Expression expression) {
            FormatExpressionV formatExpression = new FormatExpressionV();
            return expression.accept(formatExpression);
        }
        public Object forForInStatement(Node left, Expression right, Statement body) {
            FormatExpressionV formatExpression = new FormatExpressionV();
            FormatStatementV formatStatement = new FormatStatementV();
            StringBuilder builder = new StringBuilder();
            builder.append("for (");
            if (left instanceof VariableDeclaration) {
                builder.append((String)((VariableDeclaration)left).accept(formatStatement));
            } else {
                builder.append((String)((Expression)left).accept(formatExpression));
            }
            builder.append(" in ");
            builder.append((String)right.accept(formatExpression));
            builder.append(")\n");
            builder.append((String)body.accept(formatStatement));
            return builder.toString();
        }
        public Object forForStatement(Node init, Expression test, Expression update, Statement body) {
            FormatExpressionV formatExpression = new FormatExpressionV();
            FormatStatementV formatStatement = new FormatStatementV();
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
            builder.append(")\n");
            builder.append((String)body.accept(formatStatement));
            return builder.toString();
        }
        public Object forFunctionDeclaration(String id, ArrayList<String> params, BlockStatement body) {
            FormatStatementV formatStatement = new FormatStatementV();
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
            builder.append(")\n");
            builder.append((String)body.accept(formatStatement));
            return builder.toString();
        }
        Object forIfStatement(Expression test, Statement consequent, Statement alternate);
        Object forLabeledStatement(String label, Statement body);
        Object forReturnStatement(Expression argument);
        Object forSwitchStatement(Expression discriminant, ArrayList<SwitchCase> cases);
        Object forThrowStatement(Expression argument);
        Object forTryStatement(BlockStatement block, CatchClause handler, BlockStatement finalizer);
        Object forVariableDeclaration(ArrayList<VariableDeclarator> declarations);
        Object forWhileStatement(Expression test, Statement body);
        Object forWithStatement(Expression object, Statement body);
    }

    static class FormatExpressionV implements ExpressionVisitor {

    }
}
