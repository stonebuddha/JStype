package translator;

import ast.*;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.Option;
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
            Option<Node> init = forStatement.getInit();
            Option<Expression> test = forStatement.getTest();
            Option<Expression> update = forStatement.getUpdate();
            Statement body = forStatement.getBody();
            Option<Node> _init;
            if (init.isNone()) {
                _init = Option.none();
            } else {
                Node initNode = init.some();
                if (initNode instanceof VariableDeclaration) {
                    _init = Option.some((Node)((VariableDeclaration)initNode).accept(this));
                } else {
                    _init = init;
                }
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
            Option<Statement> alternate = ifStatement.getAlternate();
            return new IfStatement(test, (Statement)consequent.accept(this), alternate.map(stmt -> (Statement)stmt.accept(this)));
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
            return new SwitchStatement(discriminant, cases.map(sc -> (SwitchCase)sc.accept(this)));
        }

        @Override
        public Object forThrowStatement(ThrowStatement throwStatement) {
            return throwStatement;
        }

        @Override
        public Object forTryStatement(TryStatement tryStatement) {
            BlockStatement block = tryStatement.getBlock();
            Option<CatchClause> handler = tryStatement.getHandler();
            Option<BlockStatement> finalizer = tryStatement.getFinalizer();
            return new TryStatement(
                    (BlockStatement)block.accept(this),
                    handler.map(cc -> (CatchClause)cc.accept(this)),
                    finalizer.map(stmt -> (BlockStatement)stmt.accept(this)));
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
            Option<Expression> test = switchCase.getTest();
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

    public static class MakeAllAssignmentsSimpleV implements ProgramVisitor, StatementVisitor, ExpressionVisitor, SwitchCaseVisitor, CatchClauseVisitor, VariableDeclaratorVisitor {
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
            Expression expression = expressionStatement.getExpression();
            return new ExpressionStatement((Expression)expression.accept(this));
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
                _left = (Node)((Expression)left).accept(this);
            }
            return new ForInStatement(_left, (Expression)right.accept(this), (Statement)right.accept(this));
        }

        @Override
        public Object forForStatement(ForStatement forStatement) {
            Option<Node> init = forStatement.getInit();
            Option<Expression> test = forStatement.getTest();
            Option<Expression> update = forStatement.getUpdate();
            Statement body = forStatement.getBody();
            Option<Node> _init;
            if (init.isNone()) {
                _init = Option.none();
            } else {
                Node initNode = init.some();
                if (initNode instanceof VariableDeclaration) {
                    _init = Option.some((Node)((VariableDeclaration)initNode).accept(this));
                } else {
                    _init = Option.some((Node)((Expression)initNode).accept(this));
                }
            }
            return new ForStatement(
                    _init,
                    test.map(exp -> (Expression)exp.accept(this)),
                    update.map(exp -> (Expression)exp.accept(this)),
                    (Statement)body.accept(this));
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
            Option<Statement> alternate = ifStatement.getAlternate();
            return new IfStatement(
                    (Expression)test.accept(this),
                    (Statement)consequent.accept(this),
                    alternate.map(stmt -> (Statement)stmt.accept(this)));
        }

        @Override
        public Object forLabeledStatement(LabeledStatement labeledStatement) {
            IdentifierExpression label = labeledStatement.getLabel();
            Statement body = labeledStatement.getBody();
            return new LabeledStatement(label, (Statement)body.accept(this));
        }

        @Override
        public Object forReturnStatement(ReturnStatement returnStatement) {
            Option<Expression> argument = returnStatement.getArgument();
            return new ReturnStatement(argument.map(exp -> (Expression)exp.accept(this)));
        }

        @Override
        public Object forSwitchStatement(SwitchStatement switchStatement) {
            Expression discriminant = switchStatement.getDiscriminant();
            List<SwitchCase> cases = switchStatement.getCases();
            return new SwitchStatement((Expression)discriminant.accept(this), cases.map(sc -> (SwitchCase)sc.accept(this)));
        }

        @Override
        public Object forThrowStatement(ThrowStatement throwStatement) {
            Expression argument = throwStatement.getArgument();
            return new ThrowStatement((Expression)argument.accept(this));
        }

        @Override
        public Object forTryStatement(TryStatement tryStatement) {
            BlockStatement block = tryStatement.getBlock();
            Option<CatchClause> handler = tryStatement.getHandler();
            Option<BlockStatement> finalizer = tryStatement.getFinalizer();
            return new TryStatement(
                    (BlockStatement)block.accept(this),
                    handler.map(cc -> (CatchClause)cc.accept(this)),
                    finalizer.map(stmt -> (BlockStatement)stmt.accept(this)));
        }

        @Override
        public Object forVariableDeclaration(VariableDeclaration variableDeclaration) {
            List<VariableDeclarator> declarations = variableDeclaration.getDeclarations();
            return new VariableDeclaration(declarations.map(decl -> (VariableDeclarator)decl.accept(this)));
        }

        @Override
        public Object forWhileStatement(WhileStatement whileStatement) {
            Expression test = whileStatement.getTest();
            Statement body = whileStatement.getBody();
            return new WhileStatement((Expression)test.accept(this), (Statement)body.accept(this));
        }

        @Override
        public Object forWithStatement(WithStatement withStatement) {
            throw new RuntimeException("we don't expect with");
        }

        @Override
        public Object forArrayExpression(ArrayExpression arrayExpression) {
            Seq<Option<Expression>> elements = arrayExpression.getElements();
            return new ArrayExpression(elements.map(oe -> oe.map(exp -> (Expression)exp.accept(this))));
        }

        @Override
        public Object forAssignmentExpression(AssignmentExpression assignmentExpression) {
            String operator = assignmentExpression.getOperator();
            Expression left = assignmentExpression.getLeft();
            Expression right = assignmentExpression.getRight();
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

        @Override
        public Object forSwitchCase(SwitchCase switchCase) {
            return null;
        }

        @Override
        public Object forCatchClause(CatchClause catchClause) {
            return null;
        }

        @Override
        public Object forVariableDeclarator(VariableDeclarator variableDeclarator) {
            return null;
        }
    }


    public static Node transform(Node ast) {
        return null;
    }
}
