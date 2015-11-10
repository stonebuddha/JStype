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

    public static class PassV implements ProgramVisitor, StatementVisitor, ExpressionVisitor, CatchClauseVisitor, SwitchCaseVisitor, VariableDeclaratorVisitor, LiteralVisitor, PropertyVisitor {
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
            Option<IdentifierExpression> label = breakStatement.getLabel();
            return new BreakStatement(label.map(exp -> (IdentifierExpression)exp.accept(this)));
        }

        @Override
        public Object forContinueStatement(ContinueStatement continueStatement) {
            Option<IdentifierExpression> label = continueStatement.getLabel();
            return new ContinueStatement(label.map(exp -> (IdentifierExpression)exp.accept(this)));
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
            return emptyStatement;
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
            } else if (left instanceof Expression) {
                _left = (Node)((Expression)left).accept(this);
            } else {
                throw new RuntimeException("parser error");
            }
            return new ForInStatement(_left, (Expression)right.accept(this), (Statement)body.accept(this));
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
                } else if (initNode instanceof Expression) {
                    _init = Option.some((Node)((Expression)initNode).accept(this));
                } else {
                    throw new RuntimeException("parser error");
                }
            }
            return new ForStatement(_init, test.map(exp -> (Expression)exp.accept(this)), update.map(exp -> (Expression)exp.accept(this)), (Statement)body.accept(this));
        }

        @Override
        public Object forFunctionDeclaration(FunctionDeclaration functionDeclaration) {
            IdentifierExpression id = functionDeclaration.getId();
            Seq<IdentifierExpression> params = functionDeclaration.getParams();
            BlockStatement body = functionDeclaration.getBody();
            return new FunctionDeclaration(
                    (IdentifierExpression)id.accept(this),
                    params.map(exp -> (IdentifierExpression)exp.accept(this)),
                    (BlockStatement)body.accept(this));
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
            return new LabeledStatement(
                    (IdentifierExpression)label.accept(this),
                    (Statement)body.accept(this));
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
            return new SwitchStatement(
                    (Expression)discriminant.accept(this),
                    cases.map(sc -> (SwitchCase)sc.accept(this)));
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
            return new AssignmentExpression(operator, (Expression)left.accept(this), (Expression)right.accept(this));
        }

        @Override
        public Object forBinaryExpression(BinaryExpression binaryExpression) {
            String operator = binaryExpression.getOperator();
            Expression left = binaryExpression.getLeft();
            Expression right = binaryExpression.getRight();
            return new BinaryExpression(operator, (Expression)left.accept(this), (Expression)right.accept(this));
        }

        @Override
        public Object forCallExpression(CallExpression callExpression) {
            Expression callee = callExpression.getCallee();
            Seq<Expression> arguments = callExpression.getArguments();
            return new CallExpression(
                    (Expression)callee.accept(this),
                    arguments.map(exp -> (Expression)exp.accept(this)));
        }

        @Override
        public Object forConditionalExpression(ConditionalExpression conditionalExpression) {
            Expression test = conditionalExpression.getTest();
            Expression consequent = conditionalExpression.getConsequent();
            Expression alternate = conditionalExpression.getAlternate();
            return new ConditionalExpression((Expression)test.accept(this), (Expression)consequent.accept(this), (Expression)alternate.accept(this));
        }

        @Override
        public Object forFunctionExpression(FunctionExpression functionExpression) {
            Option<IdentifierExpression> id = functionExpression.getId();
            Seq<IdentifierExpression> params = functionExpression.getParams();
            BlockStatement body = functionExpression.getBody();
            return new FunctionExpression(
                    id.map(exp -> (IdentifierExpression)exp.accept(this)),
                    params.map(exp -> (IdentifierExpression)exp.accept(this)),
                    (BlockStatement)body.accept(this));
        }

        @Override
        public Object forLiteralExpression(LiteralExpression literalExpression) {
            Literal literal = literalExpression.getLiteral();
            return new LiteralExpression((Literal)literal.accept(this));
        }

        @Override
        public Object forLogicalExpression(LogicalExpression logicalExpression) {
            String operator = logicalExpression.getOperator();
            Expression left = logicalExpression.getLeft();
            Expression right = logicalExpression.getRight();
            return new LogicalExpression(operator, (Expression)left.accept(this), (Expression)right.accept(this));
        }

        @Override
        public Object forMemberExpression(MemberExpression memberExpression) {
            Expression object = memberExpression.getObject();
            Expression property = memberExpression.getProperty();
            Boolean computed = memberExpression.getComputed();
            return new MemberExpression((Expression)object.accept(this), (Expression)property.accept(this), computed);
        }

        @Override
        public Object forNewExpression(NewExpression newExpression) {
            Expression callee = newExpression.getCallee();
            Seq<Expression> arguments = newExpression.getArguments();
            return new NewExpression((Expression)callee.accept(this), arguments.map(exp -> (Expression)exp.accept(this)));
        }

        @Override
        public Object forObjectExpression(ObjectExpression objectExpression) {
            Seq<Property> properties = objectExpression.getProperties();
            return new ObjectExpression(properties.map(p -> (Property)p.accept(this)));
        }

        @Override
        public Object forRealIdentifierExpression(RealIdentifierExpression realIdentifierExpression) {
            return realIdentifierExpression;
        }

        @Override
        public Object forScratchIdentifierExpression(ScratchIdentifierExpression scratchIdentifierExpression) {
            return scratchIdentifierExpression;
        }

        @Override
        public Object forScratchSequenceExpression(ScratchSequenceExpression scratchSequenceExpression) {
            List<P2<ScratchIdentifierExpression, Expression>> declarations = scratchSequenceExpression.getDeclarations();
            Expression body = scratchSequenceExpression.getBody();
            return new ScratchSequenceExpression(
                    declarations.map(p -> P.p((ScratchIdentifierExpression)p._1().accept(this), (Expression)p._2().accept(this))),
                    (Expression)body.accept(this));
        }

        @Override
        public Object forSequenceExpression(SequenceExpression sequenceExpression) {
            List<Expression> expressions = sequenceExpression.getExpressions();
            return new SequenceExpression(expressions.map(exp -> (Expression)exp.accept(this)));
        }

        @Override
        public Object forThisExpression(ThisExpression thisExpression) {
            return thisExpression;
        }

        @Override
        public Object forUnaryExpression(UnaryExpression unaryExpression) {
            String operator = unaryExpression.getOperator();
            Boolean prefix = unaryExpression.getPrefix();
            Expression argument = unaryExpression.getArgument();
            return new UnaryExpression(operator, prefix, (Expression)argument.accept(this));
        }

        @Override
        public Object forUpdateExpression(UpdateExpression updateExpression) {
            String operator = updateExpression.getOperator();
            Boolean prefix = updateExpression.getPrefix();
            Expression argument = updateExpression.getArgument();
            return new UpdateExpression(operator, (Expression)argument.accept(this), prefix);
        }

        @Override
        public Object forCatchClause(CatchClause catchClause) {
            IdentifierExpression param = catchClause.getParam();
            BlockStatement body = catchClause.getBody();
            return new CatchClause((IdentifierExpression)param.accept(this), (BlockStatement)body.accept(this));
        }

        @Override
        public Object forSwitchCase(SwitchCase switchCase) {
            Option<Expression> test = switchCase.getTest();
            List<Statement> consequent = switchCase.getConsequent();
            return new SwitchCase(test.map(exp -> (Expression)exp.accept(this)), consequent.map(stmt -> (Statement)stmt.accept(this)));
        }

        @Override
        public Object forVariableDeclarator(VariableDeclarator variableDeclarator) {
            IdentifierExpression id = variableDeclarator.getId();
            Option<Expression> init = variableDeclarator.getInit();
            return new VariableDeclarator((IdentifierExpression)id.accept(this), init.map(exp -> (Expression)exp.accept(this)));
        }

        @Override
        public Object forBooleanLiteral(BooleanLiteral booleanLiteral) {
            return booleanLiteral;
        }

        @Override
        public Object forNullLiteral(NullLiteral nullLiteral) {
            return nullLiteral;
        }

        @Override
        public Object forNumberLiteral(NumberLiteral numberLiteral) {
            return numberLiteral;
        }

        @Override
        public Object forRegExpLiteral(RegExpLiteral regExpLiteral) {
            return regExpLiteral;
        }

        @Override
        public Object forStringLiteral(StringLiteral stringLiteral) {
            return stringLiteral;
        }

        @Override
        public Object forUndefinedLiteral(UndefinedLiteral undefinedLiteral) {
            return undefinedLiteral;
        }

        @Override
        public Object forProperty(Property property) {
            Node key = property.getKey();
            Expression value = property.getValue();
            String kind = property.getKind();
            Node _key;
            if (key instanceof Literal) {
                _key = (Node)((Literal)key).accept(this);
            } else if (key instanceof IdentifierExpression) {
                _key = (Node)((IdentifierExpression)key).accept(this);
            } else {
                throw new RuntimeException("parser error");
            }
            return new Property(_key, (Expression)value.accept(this), kind);
        }
    }

    public static class ReplaceEmptyWithUndefV extends PassV {
        @Override
        public Object forEmptyStatement(EmptyStatement emptyStatement) {
            return new ExpressionStatement(new LiteralExpression(new UndefinedLiteral()));
        }
    }

    public static class FixContinueLabelsV extends PassV {
    }

    public static class MakeAllAssignmentsSimpleV extends PassV {
        @Override
        public Object forAssignmentExpression(AssignmentExpression assignmentExpression) {
            String operator = assignmentExpression.getOperator();
            Expression left = assignmentExpression.getLeft();
            Expression right = assignmentExpression.getRight();
            if (operator.equals("=")) {
                return assignmentExpression;
            } else {
                String _operator = operator.substring(0, operator.length() - 1);
                if (left instanceof IdentifierExpression) {
                    return new AssignmentExpression("=", left,
                            new BinaryExpression(_operator, left, (Expression)right.accept(this)));
                } else if (left instanceof MemberExpression) {
                    ScratchIdentifierExpression temp1 = ScratchIdentifierExpression.generate();
                    ScratchIdentifierExpression temp2 = ScratchIdentifierExpression.generate();
                    Expression object = ((MemberExpression) left).getObject();
                    Expression property = ((MemberExpression) left).getProperty();
                    List<P2<ScratchIdentifierExpression, Expression>> declarations =
                            List.list(temp1, temp2).zip(List.list(new UnaryExpression("toObj", true, (Expression)object.accept(this)), (Expression)property.accept(this)));
                    return new AssignmentExpression("=", new MemberExpression(temp1, temp2, false),
                            new BinaryExpression(_operator, new MemberExpression(temp1, temp2, false), (Expression)right.accept(this)));
                } else {
                    throw new RuntimeException("parser error");
                }
            }
        }
    }


    public static Node transform(Node ast) {
        return null;
    }
}
