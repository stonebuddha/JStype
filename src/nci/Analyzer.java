package nci;

import ast.*;
import nci.type.Type;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wayne on 15/10/20.
 */
public class Analyzer {

    static HashMap<Node, Type> GIT;

    static class State {
        Frame frame;
        Stack stack;
        public State(Frame frame, Stack stack) {
            this.frame = frame;
            this.stack = stack;
        }
    }

    static class Result extends State {
        Type type;
        public Result(Frame frame, Stack stack, Type type) {
            super(frame, stack);
            this.type = type;
        }
    }

    static class AnalyzeProgramV implements ProgramVisitor {
        @Override
        public Object forProgram(ArrayList<Statement> body) {
            GIT = new HashMap<>();
            State state = new State(new Frame(), new Stack());
            for (Statement stmt : body) {
                state = (State)stmt.accept(new AnalyzeStatementV(state.frame, state.stack));
            }
            return null;
        }
    }

    static class AnalyzeStatementV implements StatementVisitor {
        Frame frame;
        Stack stack;
        public AnalyzeStatementV(Frame frame, Stack stack) {
            this.frame = frame;
            this.stack = stack;
        }

        @Override
        public Object forBlockStatement(ArrayList<Statement> body) {
            State state = new State(this.frame, this.stack);
            for (Statement stmt : body) {
                state = (State)stmt.accept(new AnalyzeStatementV(state.frame, state.stack));
            }
            return state;
        }

        @Override
        public Object forBreakStatement(String label) {
            return null;
        }

        @Override
        public Object forContinueStatement(String label) {
            return null;
        }

        @Override
        public Object forDebuggerStatement() {
            return null;
        }

        @Override
        public Object forDoWhileStatement(Statement body, Expression test) {
            return null;
        }

        @Override
        public Object forEmptyStatement() {
            return new State(this.frame, this.stack);
        }

        @Override
        public Object forExpressionStatement(Expression expression) {
            Result result = (Result)expression.accept(new AnalyzeExpressionV(this.frame, this.stack));
            return new State(result.frame, result.stack);
        }

        @Override
        public Object forForInStatement(Node left, Expression right, Statement body) {
            return null;
        }

        @Override
        public Object forForStatement(Node init, Expression test, Expression update, Statement body) {
            return null;
        }

        @Override
        public Object forFunctionDeclaration(String id, ArrayList<String> params, BlockStatement body) {
            return null;
        }

        @Override
        public Object forIfStatement(Expression test, Statement consequent, Statement alternate) {
            return null;
        }

        @Override
        public Object forLabeledStatement(String label, Statement body) {
            return null;
        }

        @Override
        public Object forReturnStatement(Expression argument) {
            return null;
        }

        @Override
        public Object forSwitchStatement(Expression discriminant, ArrayList<SwitchCase> cases) {
            return null;
        }

        @Override
        public Object forThrowStatement(Expression argument) {
            return null;
        }

        @Override
        public Object forTryStatement(BlockStatement block, CatchClause handler, BlockStatement finalizer) {
            return null;
        }

        @Override
        public Object forVariableDeclaration(ArrayList<VariableDeclarator> declarations) {
            return null;
        }

        @Override
        public Object forWhileStatement(Expression test, Statement body) {
            return null;
        }

        @Override
        public Object forWithStatement(Expression object, Statement body) {
            return null;
        }
    }

    static class AnalyzeExpressionV implements ExpressionVisitor {
        Frame frame;
        Stack stack;
        public AnalyzeExpressionV(Frame frame, Stack stack) {
            this.frame = frame;
            this.stack = stack;
        }

        @Override
        public Object forArrayExpression(ArrayList<Expression> elements) {
            return null;
        }

        @Override
        public Object forAssignmentExpression(String operator, Object left, Expression right) {
            return null;
        }

        @Override
        public Object forBinaryExpression(String operator, Expression left, Expression right) {
            return null;
        }

        @Override
        public Object forCallExpression(Expression callee, ArrayList<Expression> arguments) {
            return null;
        }

        @Override
        public Object forConditionalExpression(Expression test, Expression alternate, Expression consequent) {
            return null;
        }

        @Override
        public Object forFunctionExpression(String id, ArrayList<String> params, BlockStatement body) {
            return null;
        }

        @Override
        public Object forIdentifierExpression(String name) {
            return null;
        }

        @Override
        public Object forLiteralExpression(Literal literal) {
            return null;
        }

        @Override
        public Object forLogicalExpression(String operator, Expression left, Expression right) {
            return null;
        }

        @Override
        public Object forMemberExpression(Expression object, Expression property, boolean computed) {
            return null;
        }

        @Override
        public Object forNewExpression(Expression callee, ArrayList<Expression> arguments) {
            return null;
        }

        @Override
        public Object forObjectExpression(ArrayList<Property> properties) {
            return null;
        }

        @Override
        public Object forSequenceExpression(ArrayList<Expression> expressions) {
            return null;
        }

        @Override
        public Object forThisExpression() {
            return null;
        }

        @Override
        public Object forUnaryExpression(String operator, boolean prefix, Expression argument) {
            return null;
        }

        @Override
        public Object forUpdateExpression(String operator, Expression argument, boolean prefix) {
            return null;
        }
    }
}
