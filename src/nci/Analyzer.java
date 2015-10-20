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
        public Object forBreakStatement(IdentifierExpression label) {
            return null;
        }

        @Override
        public Object forContinueStatement(IdentifierExpression label) {
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
        public Object forFunctionDeclaration(IdentifierExpression id, ArrayList<IdentifierExpression> params, BlockStatement body) {
            return null;
        }

        @Override
        public Object forIfStatement(Expression test, Statement consequent, Statement alternate) {
            return null;
        }

        @Override
        public Object forLabeledStatement(IdentifierExpression label, Statement body) {
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
        public Object forArrayExpression(ArrayExpression arrayExpression, ArrayList<Expression> elements) {
            return null;
        }

        @Override
        public Object forAssignmentExpression(AssignmentExpression assignmentExpression, String operator, Expression left, Expression right) {
            return null;
        }

        @Override
        public Object forBinaryExpression(BinaryExpression binaryExpression, String operator, Expression left, Expression right) {
            Result result = (Result)left.accept(new AnalyzeExpressionV(this.frame, this.stack));
            Type leftType = result.type;
            result = (Result)right.accept(new AnalyzeExpressionV(result.frame, result.stack));
            Type rightType = result.type;
            Type combinedType = leftType.merge(operator, rightType);
            GIT.put(binaryExpression, combinedType);
            return new Result(result.frame, result.stack, combinedType);
        }

        @Override
        public Object forCallExpression(CallExpression callExpression, Expression callee, ArrayList<Expression> arguments) {
            return null;
        }

        @Override
        public Object forConditionalExpression(ConditionalExpression conditionalExpression, Expression test, Expression alternate, Expression consequent) {
            return null;
        }

        @Override
        public Object forFunctionExpression(FunctionExpression functionExpression, IdentifierExpression id, ArrayList<IdentifierExpression> params, BlockStatement body) {
            return null;
        }

        @Override
        public Object forIdentifierExpression(IdentifierExpression identifierExpression, String name) {
            Type type = this.frame.lookup(name);
            GIT.put(identifierExpression, type);
            return new Result(this.frame, this.stack, type);
        }

        @Override
        public Object forLiteralExpression(LiteralExpression literalExpression, Literal literal) {
            return null;
        }

        @Override
        public Object forLogicalExpression(LogicalExpression logicalExpression, String operator, Expression left, Expression right) {
            return null;
        }

        @Override
        public Object forMemberExpression(MemberExpression memberExpression, Expression object, Expression property, boolean computed) {
            return null;
        }

        @Override
        public Object forNewExpression(NewExpression newExpression, Expression callee, ArrayList<Expression> arguments) {
            return null;
        }

        @Override
        public Object forObjectExpression(ObjectExpression objectExpression, ArrayList<Property> properties) {
            return null;
        }

        @Override
        public Object forSequenceExpression(SequenceExpression sequenceExpression, ArrayList<Expression> expressions) {
            return null;
        }

        @Override
        public Object forThisExpression(ThisExpression thisExpression) {
            return null;
        }

        @Override
        public Object forUnaryExpression(UnaryExpression unaryExpression, String operator, boolean prefix, Expression argument) {
            return null;
        }

        @Override
        public Object forUpdateExpression(UpdateExpression updateExpression, String operator, Expression argument, boolean prefix) {
            return null;
        }
    }
}
