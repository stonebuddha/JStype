package translator;

import ast.*;
import fj.P2;
import fj.data.List;
import fj.data.Option;

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

    static class FormatProgramV implements ProgramVisitor<Object> {
        @Override
        public Object forProgram(Program program) {
            List<Statement> body = program.getBody();
            StringBuilder builder = new StringBuilder();
            for (Statement stmt : body) {
                builder.append((String) stmt.accept(formatStatement));
                builder.append("\n");
            }
            return builder.toString();
        }
    }

    static class FormatStatementV implements StatementVisitor<Object> {
        @Override
        public Object forBlockStatement(BlockStatement blockStatement) {
            List<Statement> body = blockStatement.getBody();
            StringBuilder builder = new StringBuilder();
            builder.append("{\n");
            for (Statement stmt : body) {
                builder.append((String) stmt.accept(formatStatement));
                builder.append("\n");
            }
            builder.append("}");
            return builder.toString();
        }

        @Override
        public Object forBreakStatement(BreakStatement breakStatement) {
            Option<IdentifierExpression> label = breakStatement.getLabel();
            StringBuilder builder = new StringBuilder();
            builder.append("break");
            if (label.isNone()) {
                builder.append(";");
            } else {
                builder.append(" ");
                builder.append(label.some().accept(formatExpression));
                builder.append(";");
            }
            return builder.toString();
        }

        @Override
        public Object forContinueStatement(ContinueStatement continueStatement) {
            Option<IdentifierExpression> label = continueStatement.getLabel();
            StringBuilder builder = new StringBuilder();
            builder.append("continue");
            if (label.isNone()) {
                builder.append(";");
            } else {
                builder.append(" ");
                builder.append(label.some().accept(formatExpression));
                builder.append(";");
            }
            return builder.toString();
        }

        @Override
        public Object forDebuggerStatement(DebuggerStatement debuggerStatement) {
            return "debug();";
        }

        @Override
        public Object forDoWhileStatement(DoWhileStatement doWhileStatement) {
            Expression test = doWhileStatement.getTest();
            Statement body = doWhileStatement.getBody();
            StringBuilder builder = new StringBuilder();
            builder.append("do ");
            builder.append((String) body.accept(formatStatement));
            builder.append(" while (");
            builder.append((String) test.accept(formatExpression));
            builder.append(");");
            return builder.toString();
        }

        @Override
        public Object forEmptyStatement(EmptyStatement emptyStatement) {
            return ";";
        }

        @Override
        public Object forExpressionStatement(ExpressionStatement expressionStatement) {
            Expression expression = expressionStatement.getExpression();
            StringBuilder builder = new StringBuilder();
            builder.append((String) expression.accept(formatExpression));
            builder.append(";");
            return builder.toString();
        }

        @Override
        public Object forForInStatement(ForInStatement forInStatement) {
            Node left = forInStatement.getLeft();
            Expression right = forInStatement.getRight();
            Statement body = forInStatement.getBody();
            StringBuilder builder = new StringBuilder();
            builder.append("for (");
            if (left instanceof VariableDeclaration) {
                builder.append((String) ((VariableDeclaration) left).accept(formatStatement));
            } else {
                builder.append((String) ((Expression) left).accept(formatExpression));
            }
            builder.append(" in ");
            builder.append((String) right.accept(formatExpression));
            builder.append(") ");
            builder.append((String) body.accept(formatStatement));
            return builder.toString();
        }

        @Override
        public Object forForStatement(ForStatement forStatement) {
            Option<Node> init = forStatement.getInit();
            Option<Expression> test = forStatement.getTest();
            Option<Expression> update = forStatement.getUpdate();
            Statement body = forStatement.getBody();
            StringBuilder builder = new StringBuilder();
            builder.append("for (");
            if (init.isNone()) {

            } else {
                if (init.some() instanceof VariableDeclaration) {
                    builder.append((String) ((VariableDeclaration) init.some()).accept(formatStatement));
                } else {
                    builder.append((String) ((Expression) init.some()).accept(formatExpression));
                }
            }
            builder.append("; ");
            builder.append((String) test.map(exp -> exp.accept(formatExpression)).orSome(""));
            builder.append("; ");
            builder.append((String) update.map(exp -> exp.accept(formatExpression)).orSome(""));
            builder.append(") ");
            builder.append((String) body.accept(formatStatement));
            return builder.toString();
        }

        @Override
        public Object forFunctionDeclaration(FunctionDeclaration functionDeclaration) {
            IdentifierExpression id = functionDeclaration.getId();
            List<IdentifierExpression> params = functionDeclaration.getParams();
            BlockStatement body = functionDeclaration.getBody();
            StringBuilder builder = new StringBuilder();
            builder.append("function ");
            builder.append(id.accept(formatExpression));
            builder.append("(");
            boolean first = true;
            for (IdentifierExpression param : params) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append(param.accept(formatExpression));
            }
            builder.append(") ");
            builder.append((String) body.accept(formatStatement));
            return builder.toString();
        }

        @Override
        public Object forIfStatement(IfStatement ifStatement) {
            Expression test = ifStatement.getTest();
            Statement consequent = ifStatement.getConsequent();
            Option<Statement> alternate = ifStatement.getAlternate();
            StringBuilder builder = new StringBuilder();
            builder.append("if (");
            builder.append((String) test.accept(formatExpression));
            builder.append(") ");
            builder.append((String) consequent.accept(formatStatement));
            if (alternate.isSome()) {
                builder.append(" else ");
                builder.append((String) alternate.some().accept(formatStatement));
            }
            return builder.toString();
        }

        @Override
        public Object forLabeledStatement(LabeledStatement labeledStatement) {
            IdentifierExpression label = labeledStatement.getLabel();
            Statement body = labeledStatement.getBody();
            StringBuilder builder = new StringBuilder();
            builder.append(label.accept(formatExpression));
            builder.append(":\n");
            builder.append((String) body.accept(formatStatement));
            return builder.toString();
        }

        @Override
        public Object forReturnStatement(ReturnStatement returnStatement) {
            Option<Expression> argument = returnStatement.getArgument();
            StringBuilder builder = new StringBuilder();
            builder.append("return");
            if (argument.isNone()) {
                builder.append(";");
            } else {
                builder.append(" ");
                builder.append((String) argument.some().accept(formatExpression));
                builder.append(";");
            }
            return builder.toString();
        }

        @Override
        public Object forSwitchStatement(SwitchStatement switchStatement) {
            Expression discriminant = switchStatement.getDiscriminant();
            List<SwitchCase> cases = switchStatement.getCases();
            StringBuilder builder = new StringBuilder();
            builder.append("switch (");
            builder.append((String) discriminant.accept(formatExpression));
            builder.append(") {\n");
            for (SwitchCase sc : cases) {
                builder.append((String) sc.accept(formatSwitchCase));
            }
            builder.append("}");
            return builder.toString();
        }

        @Override
        public Object forThrowStatement(ThrowStatement throwStatement) {
            Expression argument = throwStatement.getArgument();
            StringBuilder builder = new StringBuilder();
            builder.append("throw ");
            builder.append((String) argument.accept(formatExpression));
            builder.append(";");
            return builder.toString();
        }

        @Override
        public Object forTryStatement(TryStatement tryStatement) {
            BlockStatement block = tryStatement.getBlock();
            Option<CatchClause> handler = tryStatement.getHandler();
            Option<BlockStatement> finalizer = tryStatement.getFinalizer();
            StringBuilder builder = new StringBuilder();
            builder.append("try ");
            builder.append((String) block.accept(formatStatement));
            builder.append(handler.map(stmt -> (String)stmt.accept(formatCatchClause)).orSome(""));
            builder.append(" final ");
            builder.append(finalizer.map(stmt -> (String)stmt.accept(formatStatement)).orSome(""));
            return builder.toString();
        }

        @Override
        public Object forVariableDeclaration(VariableDeclaration variableDeclaration) {
            List<VariableDeclarator> declarations = variableDeclaration.getDeclarations();
            StringBuilder builder = new StringBuilder();
            builder.append("var ");
            boolean first = true;
            for (VariableDeclarator decl : declarations) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append((String) decl.accept(formatVariableDeclarator));
            }
            builder.append(";");
            return builder.toString();
        }

        @Override
        public Object forWhileStatement(WhileStatement whileStatement) {
            Expression test = whileStatement.getTest();
            Statement body = whileStatement.getBody();
            StringBuilder builder = new StringBuilder();
            builder.append("while (");
            builder.append((String) test.accept(formatExpression));
            builder.append(") ");
            builder.append((String) body.accept(formatStatement));
            return builder.toString();
        }

        @Override
        public Object forWithStatement(WithStatement withStatement) {
            throw new RuntimeException("we don't expect with");
        }
    }

    static class FormatExpressionV implements ExpressionVisitor<Object> {
        @Override
        public Object forArrayExpression(ArrayExpression arrayExpression) {
            List<Option<Expression>> elements = arrayExpression.getElements();
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            boolean first = true;
            for (Option<Expression> exp : elements) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                if (exp.isSome()) {
                    builder.append(exp.map(e -> (String)e.accept(formatExpression)).orSome(""));
                }
            }
            builder.append("]");
            return builder.toString();
        }

        @Override
        public Object forAssignmentExpression(AssignmentExpression assignmentExpression) {
            Node left = assignmentExpression.getLeft();
            String operator = assignmentExpression.getOperator();
            Expression right = assignmentExpression.getRight();
            StringBuilder builder = new StringBuilder();
            builder.append((String) ((Expression) left).accept(formatExpression));
            builder.append(" ");
            builder.append(operator);
            builder.append(" ");
            builder.append((String) right.accept(formatExpression));
            return builder.toString();
        }

        @Override
        public Object forBinaryExpression(BinaryExpression binaryExpression) {
            String operator = binaryExpression.getOperator();
            Expression left = binaryExpression.getLeft();
            Expression right = binaryExpression.getRight();
            StringBuilder builder = new StringBuilder();
            builder.append((String) left.accept(formatExpression));
            builder.append(" ");
            builder.append(operator);
            builder.append(" ");
            builder.append((String) right.accept(formatExpression));
            return builder.toString();
        }

        @Override
        public Object forCallExpression(CallExpression callExpression) {
            Expression callee = callExpression.getCallee();
            List<Expression> arguments = callExpression.getArguments();
            StringBuilder builder = new StringBuilder();
            builder.append((String) callee.accept(formatExpression));
            builder.append("(");
            boolean first = true;
            for (Expression exp : arguments) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append((String) exp.accept(formatExpression));
            }
            builder.append(")");
            return builder.toString();
        }

        @Override
        public Object forConditionalExpression(ConditionalExpression conditionalExpression) {
            Expression test = conditionalExpression.getTest();
            Expression consequent = conditionalExpression.getConsequent();
            Expression alternate = conditionalExpression.getAlternate();
            StringBuilder builder = new StringBuilder();
            builder.append((String) test.accept(formatExpression));
            builder.append(" ? ");
            builder.append((String) consequent.accept(formatExpression));
            builder.append(" : ");
            builder.append((String) alternate.accept(formatExpression));
            return builder.toString();
        }

        @Override
        public Object forFunctionExpression(FunctionExpression functionExpression) {
            Option<IdentifierExpression> id = functionExpression.getId();
            List<IdentifierExpression> params = functionExpression.getParams();
            BlockStatement body = functionExpression.getBody();
            StringBuilder builder = new StringBuilder();
            builder.append("function ");
            if (id.isSome()) {
                builder.append(id.some().accept(formatExpression));
            }
            builder.append("(");
            boolean first = true;
            for (IdentifierExpression p : params) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append(p.accept(formatExpression));
            }
            builder.append(") ");
            builder.append((String) body.accept(formatStatement));
            return builder.toString();
        }

        @Override
        public Object forRealIdentifierExpression(RealIdentifierExpression realIdentifierExpression) {
            return realIdentifierExpression.getName();
        }

        @Override
        public Object forScratchIdentifierExpression(ScratchIdentifierExpression scratchIdentifierExpression) {
            return "Scratch(" + scratchIdentifierExpression.getNum() + ")";
        }

        @Override
        public Object forLiteralExpression(LiteralExpression literalExpression) {
            Literal literal = literalExpression.getLiteral();
            return literal.accept(formatLiteral);
        }

        @Override
        public Object forLogicalExpression(LogicalExpression logicalExpression) {
            String operator = logicalExpression.getOperator();
            Expression left = logicalExpression.getLeft();
            Expression right = logicalExpression.getRight();
            StringBuilder builder = new StringBuilder();
            builder.append((String) left.accept(formatExpression));
            builder.append(" ");
            builder.append(operator);
            builder.append(" ");
            builder.append((String) right.accept(formatExpression));
            return builder.toString();
        }

        @Override
        public Object forMemberExpression(MemberExpression memberExpression) {
            Expression object = memberExpression.getObject();
            Expression property = memberExpression.getProperty();
            StringBuilder builder = new StringBuilder();
            builder.append((String) object.accept(formatExpression));
            builder.append(".");
            builder.append((String) property.accept(formatExpression));
            return builder.toString();
        }

        @Override
        public Object forNewExpression(NewExpression newExpression) {
            Expression callee = newExpression.getCallee();
            List<Expression> arguments = newExpression.getArguments();
            StringBuilder builder = new StringBuilder();
            builder.append("new ");
            builder.append((String) callee.accept(formatExpression));
            builder.append("(");
            boolean first = true;
            for (Expression exp : arguments) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append((String) exp.accept(formatExpression));
            }
            builder.append(")");
            return builder.toString();
        }

        @Override
        public Object forObjectExpression(ObjectExpression objectExpression) {
            List<Property> properties = objectExpression.getProperties();
            StringBuilder builder = new StringBuilder();
            builder.append("{");
            boolean first = true;
            for (Property p : properties) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append((String) p.accept(formatProperty));
            }
            builder.append("}");
            return builder.toString();
        }

        @Override
        public Object forSequenceExpression(SequenceExpression sequenceExpression) {
            List<Expression> expressions = sequenceExpression.getExpressions();
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for (Expression exp : expressions) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append((String) exp.accept(formatExpression));
            }
            return builder.toString();
        }

        @Override
        public Object forThisExpression(ThisExpression thisExpression) {
            return "this";
        }

        @Override
        public Object forUnaryExpression(UnaryExpression unaryExpression) {
            String operator = unaryExpression.getOperator();
            Boolean prefix = unaryExpression.getPrefix();
            Expression argument = unaryExpression.getArgument();
            StringBuilder builder = new StringBuilder();
            if (prefix) {
                builder.append(operator);
                builder.append((String) argument.accept(formatExpression));
            } else {
                builder.append((String) argument.accept(formatExpression));
                builder.append(operator);
            }
            return builder.toString();
        }

        @Override
        public Object forUpdateExpression(UpdateExpression updateExpression) {
            String operator = updateExpression.getOperator();
            Expression argument = updateExpression.getArgument();
            Boolean prefix = updateExpression.getPrefix();
            StringBuilder builder = new StringBuilder();
            if (prefix) {
                builder.append(operator);
                builder.append((String) argument.accept(formatExpression));
            } else {
                builder.append((String) argument.accept(formatExpression));
                builder.append(operator);
            }
            return builder.toString();
        }

        @Override
        public Object forScratchSequenceExpression(ScratchSequenceExpression scratchSequenceExpression) {
            List<P2<IdentifierExpression, Expression>> decls = scratchSequenceExpression.getDeclarations();
            Expression body = scratchSequenceExpression.getBody();
            StringBuilder builder = new StringBuilder();
            for (P2<IdentifierExpression, Expression> p : decls) {
                builder.append("var "+ p._1().accept(formatExpression) + " = " + p._2().accept(formatExpression) + ";\n");
            }
            builder.append(body.accept(formatExpression));
            return builder.toString();
        }
    }

    static class FormatLiteralV implements LiteralVisitor<Object> {
        @Override
        public Object forBooleanLiteral(BooleanLiteral booleanLiteral) {
            Boolean value = booleanLiteral.getValue();
            if (value) {
                return "true";
            } else {
                return "false";
            }
        }

        @Override
        public Object forNullLiteral(NullLiteral nullLiteral) {
            return "null";
        }

        @Override
        public Object forNumberLiteral(NumberLiteral numberLiteral) {
            Number value = numberLiteral.getValue();
            return value.toString();
        }

        @Override
        public Object forRegExpLiteral(RegExpLiteral regExpLiteral) {
            String pattern = regExpLiteral.getPattern();
            String flags = regExpLiteral.getFlags();
            return "/" + pattern + "/" + flags + "/";
        }

        @Override
        public Object forStringLiteral(StringLiteral stringLiteral) {
            String value = stringLiteral.getValue();
            return "\"" + value + "\"";
        }

        @Override
        public Object forUndefinedLiteral(UndefinedLiteral undefinedLiteral) {
            return "undefined";
        }
    }

    static class FormatSwitchCaseV implements SwitchCaseVisitor<Object> {
        @Override
        public Object forSwitchCase(SwitchCase switchCase) {
            Option<Expression> test = switchCase.getTest();
            List<Statement> consequent = switchCase.getConsequent();
            StringBuilder builder = new StringBuilder();
            builder.append("case ");
            builder.append((String) test.map(exp -> exp.accept(formatExpression)).orSome(""));
            builder.append(":\n");
            for (Statement stmt : consequent) {
                builder.append((String) stmt.accept(formatStatement));
            }
            return builder.toString();
        }
    }

    static class FormatCatchClauseV implements CatchClauseVisitor<Object> {
        @Override
        public Object forCatchClause(CatchClause catchClause) {
            IdentifierExpression param = catchClause.getParam();
            BlockStatement body = catchClause.getBody();
            StringBuilder builder = new StringBuilder();
            builder.append("catch (");
            builder.append(param.accept(formatExpression));
            builder.append(") ");
            builder.append((String) body.accept(formatStatement));
            return builder.toString();
        }
    }

    static class FormatVariableDeclaratorV implements VariableDeclaratorVisitor<Object> {
        @Override
        public Object forVariableDeclarator(VariableDeclarator variableDeclarator) {
            IdentifierExpression id = variableDeclarator.getId();
            Option<Expression> init = variableDeclarator.getInit();
            StringBuilder builder = new StringBuilder();
            builder.append(id.accept(formatExpression));
            if (init.isSome()) {
                builder.append(" = ");
                builder.append((String) init.some().accept(formatExpression));
            }
            return builder.toString();
        }
    }

    static class FormatPropertyV implements PropertyVisitor<Object> {
        @Override
        public Object forProperty(Property property) {
            Node key = property.getKey();
            Expression value = property.getValue();
            StringBuilder builder = new StringBuilder();
            if (key instanceof IdentifierExpression) {
                builder.append(((IdentifierExpression) key).accept(formatExpression));
            } else {
                builder.append((String) ((Literal) key).accept(formatLiteral));
            }
            builder.append(" : ");
            builder.append((String) value.accept(formatExpression));
            return builder.toString();
        }
    }
}