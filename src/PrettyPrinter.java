import ast.*;

import java.util.ArrayList;

/**
 * Created by wayne on 15/10/16.
 */
public class PrettyPrinter {

    static final FormatProgramV formatProgram = new FormatProgramV();
    static final FormatStatementV formatStatement = new FormatStatementV();
    static final FormatExpressionV formatExpression = new FormatExpressionV();
    static final FormatSwitchCaseV formatSwitchCase = new FormatSwitchCaseV();
    static final FormatCatchClauseV formatCatchClause = new FormatCatchClauseV();
    static final FormatVariableDeclaratorV formatVariableDeclarator = new FormatVariableDeclaratorV();

    static class FormatProgramV implements ProgramVisitor {
        public Object forProgram(ArrayList<Statement> body) {
            StringBuilder builder = new StringBuilder();
            for (Statement stmt : body) {
                builder.append((String)stmt.accept(formatStatement));
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
            return "debug();\n";
        }
        public Object forDoWhileStatement(Statement body, Expression test) {
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
            return expression.accept(formatExpression);
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
            builder.append(")\n");
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
            builder.append(")\n");
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
            builder.append(")\n");
            builder.append((String)body.accept(formatStatement));
            return builder.toString();
        }
        public Object forIfStatement(Expression test, Statement consequent, Statement alternate) {
            StringBuilder builder = new StringBuilder();
            builder.append("if (");
            builder.append((String)test.accept(formatExpression));
            builder.append(")\n");
            builder.append((String)consequent.accept(formatStatement));
            if (alternate != null) {
                builder.append("else\n");
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
                builder.append(";\n");
            } else {
                builder.append(" ");
                builder.append((String)argument.accept(formatExpression));
                builder.append(";\n");
            }
            return builder.toString();
        }
        public Object forSwitchStatement(Expression discriminant, ArrayList<SwitchCase> cases) {
            StringBuilder builder = new StringBuilder();
            builder.append("switch (");
            builder.append((String)discriminant.accept(formatExpression));
            builder.append(")\n{\n");
            for (SwitchCase sc : cases) {
                builder.append((String)sc.accept(formatSwitchCase));
            }
            builder.append("}\n");
            return builder.toString();
        }
        public Object forThrowStatement(Expression argument) {
            StringBuilder builder = new StringBuilder();
            builder.append("throw ");
            builder.append((String)argument.accept(formatExpression));
            builder.append(";\n");
            return builder.toString();
        }
        public Object forTryStatement(BlockStatement block, CatchClause handler, BlockStatement finalizer) {
            StringBuilder builder = new StringBuilder();
            builder.append("try\n");
            builder.append((String)block.accept(formatStatement));
            builder.append((String)handler.accept(formatCatchClause));
            builder.append("\nfinal\n");
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
            builder.append(";\n");
            return builder.toString();
        }
        public Object forWhileStatement(Expression test, Statement body) {
            StringBuilder builder = new StringBuilder();
            builder.append("while (");
            builder.append((String)test.accept(formatExpression));
            builder.append(")\n");
            builder.append((String)body.accept(formatStatement));
            return builder.toString();
        }
        public Object forWithStatement(Expression object, Statement body) {
            StringBuilder builder = new StringBuilder();
            builder.append("with ");
            builder.append((String)object.accept(formatExpression));
            builder.append("\n");
            builder.append((String)body.accept(formatStatement));
            return builder.toString();
        }
    }

    static class FormatExpressionV implements ExpressionVisitor {
        @Override
        public Object forArrayExpression(ArrayList<Expression> elements) {
            return null;
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
            builder.append(")\n");
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
}
