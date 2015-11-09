package translator;

import ast.*;
import fj.data.List;
import fj.data.Seq;

/**
 * Created by wayne on 15/11/9.
 */
public class AST2AST {

    public static class ReplaceEmptyWithUndefV implements ProgramVisitor, StatementVisitor, SwitchCaseVisitor, CatchClauseVisitor {
        @Override
        public Object forProgram(Program program) {
            List<Statement> body = program.getBody();
            return new Program(body.map(stmt -> (Statement)stmt.accept(this)));
        }

        @Override
        public Object forBlockStatement(BlockStatement blockStatement) {
            List<Statement> body = blockStatement.getBody();
            return new BlockStatement(body.map(stmt -> (Statement)stmt.accept(this)));
        }

        @Override
        public Object forBreakStatement(BreakStatement breakStatement) {
            return breakStatement;
        }

        @Override
        public Object forContinueStatement(ContinueStatement continueStatement) {
            return continueStatement;
        }

        @Override
        public Object forDebuggerStatement(DebuggerStatement debuggerStatement) {
            return debuggerStatement;
        }

        @Override
        public Object forDoWhileStatement(DoWhileStatement doWhileStatement) {
            Statement body = doWhileStatement.getBody();
            Expression test = doWhileStatement.getTest();
            return new DoWhileStatement((Statement)body.accept(this), test);
        }

        @Override
        public Object forEmptyStatement(EmptyStatement emptyStatement) {
            return new ExpressionStatement(new LiteralExpression(new UndefinedLiteral()));
        }

        @Override
        public Object forExpressionStatement(ExpressionStatement expressionStatement) {
            return expressionStatement;
        }

        @Override
        public Object forForInStatement(ForInStatement forInStatement) {
            Node left = forInStatement.getLeft();
            Expression right = forInStatement.getRight();
            Statement body = forInStatement.getBody();
            Node _left;
            if (left instanceof VariableDeclaration) {
                _left = (Node)((VariableDeclaration)left).accept(this);
            } else {
                _left = left;
            }
            return new ForInStatement(_left, right, (Statement)body.accept(this));
        }

        @Override
        public Object forForStatement(ForStatement forStatement) {
            Node init = forStatement.getInit();
            Expression test = forStatement.getTest();
            Expression update = forStatement.getUpdate();
            Statement body = forStatement.getBody();
            Node _init;
            if (init instanceof VariableDeclaration) {
                _init = (Node)((VariableDeclaration)init).accept(this);
            } else {
                _init = init;
            }
            return new ForStatement(_init, test, update, (Statement)body.accept(this));
        }

        @Override
        public Object forFunctionDeclaration(FunctionDeclaration functionDeclaration) {
            IdentifierExpression id = functionDeclaration.getId();
            Seq<IdentifierExpression> params = functionDeclaration.getParams();
            BlockStatement body = functionDeclaration.getBody();
            return new FunctionDeclaration(id, params, (BlockStatement)body.accept(this));
        }

        @Override
        public Object forIfStatement(IfStatement ifStatement) {
            Expression test = ifStatement.getTest();
            Statement consequent = ifStatement.getConsequent();
            Statement alternate = ifStatement.getAlternate();
            return new IfStatement(test, (Statement)consequent.accept(this), (Statement)alternate.accept(this));
        }

        @Override
        public Object forLabeledStatement(LabeledStatement labeledStatement) {
            IdentifierExpression label = labeledStatement.getLabel();
            Statement body = labeledStatement.getBody();
            return new LabeledStatement(label, (Statement)body.accept(this));
        }

        @Override
        public Object forReturnStatement(ReturnStatement returnStatement) {
            return returnStatement;
        }

        @Override
        public Object forSwitchStatement(SwitchStatement switchStatement) {
            Expression discriminant = switchStatement.getDiscriminant();
            List<SwitchCase> cases = switchStatement.getCases();
            return new SwitchStatement(discriminant, cases.map(kase -> (SwitchCase)kase.accept(this)));
        }

        @Override
        public Object forThrowStatement(ThrowStatement throwStatement) {
            return throwStatement;
        }

        @Override
        public Object forTryStatement(TryStatement tryStatement) {
            BlockStatement block = tryStatement.getBlock();
            CatchClause handler = tryStatement.getHandler();
            BlockStatement finalizer = tryStatement.getFinalizer();
            return new TryStatement((BlockStatement)block.accept(this), (CatchClause)handler.accept(this), (BlockStatement)finalizer.accept(this));
        }

        @Override
        public Object forVariableDeclaration(VariableDeclaration variableDeclaration) {
            return variableDeclaration;
        }

        @Override
        public Object forWhileStatement(WhileStatement whileStatement) {
            Expression test = whileStatement.getTest();
            Statement body = whileStatement.getBody();
            return new WhileStatement(test, (Statement)body.accept(this));
        }

        @Override
        public Object forWithStatement(WithStatement withStatement) {
            throw new RuntimeException("we don't expect with");
        }

        @Override
        public Object forSwitchCase(SwitchCase switchCase) {
            Expression test = switchCase.getTest();
            List<Statement> consequent = switchCase.getConsequent();
            return new SwitchCase(test, consequent.map(stmt -> (Statement)stmt.accept(this)));
        }

        @Override
        public Object forCatchClause(CatchClause catchClause) {
            IdentifierExpression param = catchClause.getParam();
            BlockStatement body = catchClause.getBody();
            return new CatchClause(param, (BlockStatement)body.accept(this));
        }
    }

    public static class FixContinueLabelsV implements ProgramVisitor {
        @Override
        public Object forProgram(Program program) {
            return program; // TODO
        }
    }

    public static class MakeAllAssignmentsSimpleV implements ProgramVisitor, StatementVisitor, ExpressionVisitor {
        @Override
        public Object forProgram(Program program) {
            List<Statement> body = program.getBody();
            return new Program(body.map(stmt -> (Statement)stmt.accept(this)));
        }

        @Override
        public Object forBlockStatement(BlockStatement blockStatement) {
            List<Statement> body = blockStatement.getBody();
            return new BlockStatement(body.map(stmt -> (Statement)stmt.accept(this)));
        }

        @Override
        public Object forBreakStatement(BreakStatement breakStatement) {
            return breakStatement;
        }

        @Override
        public Object forContinueStatement(ContinueStatement continueStatement) {
            return continueStatement;
        }

        @Override
        public Object forDebuggerStatement(DebuggerStatement debuggerStatement) {
            return debuggerStatement;
        }

        @Override
        public Object forDoWhileStatement(DoWhileStatement doWhileStatement) {
            Statement body = doWhileStatement.getBody();
            Expression test = doWhileStatement.getTest();
            return new DoWhileStatement((Statement)body.accept(this), (Expression)test.accept(this));
        }

        @Override
        public Object forEmptyStatement(EmptyStatement emptyStatement) {
            throw new RuntimeException("empty statement should have been replaced");
        }

        @Override
        public Object forExpressionStatement(ExpressionStatement expressionStatement) {
            return null;
        }

        @Override
        public Object forForInStatement(ForInStatement forInStatement) {
            return null;
        }

        @Override
        public Object forForStatement(ForStatement forStatement) {
            return null;
        }

        @Override
        public Object forFunctionDeclaration(FunctionDeclaration functionDeclaration) {
            return null;
        }

        @Override
        public Object forIfStatement(IfStatement ifStatement) {
            return null;
        }

        @Override
        public Object forLabeledStatement(LabeledStatement labeledStatement) {
            return null;
        }

        @Override
        public Object forReturnStatement(ReturnStatement returnStatement) {
            return null;
        }

        @Override
        public Object forSwitchStatement(SwitchStatement switchStatement) {
            return null;
        }

        @Override
        public Object forThrowStatement(ThrowStatement throwStatement) {
            return null;
        }

        @Override
        public Object forTryStatement(TryStatement tryStatement) {
            return null;
        }

        @Override
        public Object forVariableDeclaration(VariableDeclaration variableDeclaration) {
            return null;
        }

        @Override
        public Object forWhileStatement(WhileStatement whileStatement) {
            return null;
        }

        @Override
        public Object forWithStatement(WithStatement withStatement) {
            throw new RuntimeException("we don't expect with");
        }

        @Override
        public Object forArrayExpression(ArrayExpression arrayExpression) {
            return null;
        }

        @Override
        public Object forAssignmentExpression(AssignmentExpression assignmentExpression) {
            return null;
        }

        @Override
        public Object forBinaryExpression(BinaryExpression binaryExpression) {
            return null;
        }

        @Override
        public Object forCallExpression(CallExpression callExpression) {
            return null;
        }

        @Override
        public Object forConditionalExpression(ConditionalExpression conditionalExpression) {
            return null;
        }

        @Override
        public Object forFunctionExpression(FunctionExpression functionExpression) {
            return null;
        }

        @Override
        public Object forLiteralExpression(LiteralExpression literalExpression) {
            return null;
        }

        @Override
        public Object forLogicalExpression(LogicalExpression logicalExpression) {
            return null;
        }

        @Override
        public Object forMemberExpression(MemberExpression memberExpression) {
            return null;
        }

        @Override
        public Object forNewExpression(NewExpression newExpression) {
            return null;
        }

        @Override
        public Object forObjectExpression(ObjectExpression objectExpression) {
            return null;
        }

        @Override
        public Object forRealIdentifierExpression(RealIdentifierExpression realIdentifierExpression) {
            return null;
        }

        @Override
        public Object forScratchIdentifierExpression(ScratchIdentifierExpression scratchIdentifierExpression) {
            return null;
        }

        @Override
        public Object forSequenceExpression(SequenceExpression sequenceExpression) {
            return null;
        }

        @Override
        public Object forThisExpression(ThisExpression thisExpression) {
            return null;
        }

        @Override
        public Object forUnaryExpression(UnaryExpression unaryExpression) {
            return null;
        }

        @Override
        public Object forUpdateExpression(UpdateExpression updateExpression) {
            return null;
        }
    }


    public static Node transform(Node ast) {
        return null;
    }
}
