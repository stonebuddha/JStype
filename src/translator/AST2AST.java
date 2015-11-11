package translator;

import ast.*;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.Option;

/**
 * Created by wayne on 15/11/9.
 */
public class AST2AST {

    public static class DefaultPassV implements SimpleTransformVisitor {
        @Override
        public Program forProgram(Program program) {
            List<Statement> body = program.getBody();
            return new Program(body.map(stmt -> stmt.accept(this)));
        }

        @Override
        public Statement forBlockStatement(BlockStatement blockStatement) {
            List<Statement> body = blockStatement.getBody();
            return new BlockStatement(body.map(stmt -> stmt.accept(this)));
        }

        @Override
        public Statement forBreakStatement(BreakStatement breakStatement) {
            Option<IdentifierExpression> label = breakStatement.getLabel();
            return new BreakStatement(label.map(exp -> (IdentifierExpression)exp.accept(this)));
        }

        @Override
        public Statement forContinueStatement(ContinueStatement continueStatement) {
            Option<IdentifierExpression> label = continueStatement.getLabel();
            return new ContinueStatement(label.map(exp -> (IdentifierExpression)exp.accept(this)));
        }

        @Override
        public Statement forDebuggerStatement(DebuggerStatement debuggerStatement) {
            return debuggerStatement;
        }

        @Override
        public Statement forDoWhileStatement(DoWhileStatement doWhileStatement) {
            Statement body = doWhileStatement.getBody();
            Expression test = doWhileStatement.getTest();
            return new DoWhileStatement(body.accept(this), test.accept(this));
        }

        @Override
        public Statement forEmptyStatement(EmptyStatement emptyStatement) {
            return emptyStatement;
        }

        @Override
        public Statement forExpressionStatement(ExpressionStatement expressionStatement) {
            Expression expression = expressionStatement.getExpression();
            return new ExpressionStatement(expression.accept(this));
        }

        @Override
        public Statement forForInStatement(ForInStatement forInStatement) {
            Node left = forInStatement.getLeft();
            Expression right = forInStatement.getRight();
            Statement body = forInStatement.getBody();
            Node _left;
            if (left instanceof VariableDeclaration) {
                _left = ((VariableDeclaration)left).accept(this);
            } else if (left instanceof Expression) {
                _left = ((Expression)left).accept(this);
            } else {
                throw new RuntimeException("parser error");
            }
            return new ForInStatement(_left, right.accept(this), body.accept(this));
        }

        @Override
        public Statement forForStatement(ForStatement forStatement) {
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
            return new ForStatement(_init, test.map(exp -> exp.accept(this)), update.map(exp -> exp.accept(this)), body.accept(this));
        }

        @Override
        public Statement forFunctionDeclaration(FunctionDeclaration functionDeclaration) {
            IdentifierExpression id = functionDeclaration.getId();
            List<IdentifierExpression> params = functionDeclaration.getParams();
            BlockStatement body = functionDeclaration.getBody();
            return new FunctionDeclaration(
                    (IdentifierExpression)id.accept(this),
                    params.map(exp -> (IdentifierExpression)exp.accept(this)),
                    (BlockStatement)body.accept(this));
        }

        @Override
        public Statement forIfStatement(IfStatement ifStatement) {
            Expression test = ifStatement.getTest();
            Statement consequent = ifStatement.getConsequent();
            Option<Statement> alternate = ifStatement.getAlternate();
            return new IfStatement(
                    test.accept(this),
                    consequent.accept(this),
                    alternate.map(stmt -> stmt.accept(this)));
        }

        @Override
        public Statement forLabeledStatement(LabeledStatement labeledStatement) {
            IdentifierExpression label = labeledStatement.getLabel();
            Statement body = labeledStatement.getBody();
            return new LabeledStatement(
                    (IdentifierExpression)label.accept(this),
                    body.accept(this));
        }

        @Override
        public Statement forReturnStatement(ReturnStatement returnStatement) {
            Option<Expression> argument = returnStatement.getArgument();
            return new ReturnStatement(argument.map(exp -> exp.accept(this)));
        }

        @Override
        public Statement forSwitchStatement(SwitchStatement switchStatement) {
            Expression discriminant = switchStatement.getDiscriminant();
            List<SwitchCase> cases = switchStatement.getCases();
            return new SwitchStatement(
                    discriminant.accept(this),
                    cases.map(sc -> sc.accept(this)));
        }

        @Override
        public Statement forThrowStatement(ThrowStatement throwStatement) {
            Expression argument = throwStatement.getArgument();
            return new ThrowStatement(argument.accept(this));
        }

        @Override
        public Statement forTryStatement(TryStatement tryStatement) {
            BlockStatement block = tryStatement.getBlock();
            Option<CatchClause> handler = tryStatement.getHandler();
            Option<BlockStatement> finalizer = tryStatement.getFinalizer();
            return new TryStatement(
                    (BlockStatement)block.accept(this),
                    handler.map(cc -> cc.accept(this)),
                    finalizer.map(stmt -> (BlockStatement)stmt.accept(this)));
        }

        @Override
        public Statement forVariableDeclaration(VariableDeclaration variableDeclaration) {
            List<VariableDeclarator> declarations = variableDeclaration.getDeclarations();
            return new VariableDeclaration(declarations.map(decl -> decl.accept(this)));
        }

        @Override
        public Statement forWhileStatement(WhileStatement whileStatement) {
            Expression test = whileStatement.getTest();
            Statement body = whileStatement.getBody();
            return new WhileStatement(test.accept(this), body.accept(this));
        }

        @Override
        public Statement forWithStatement(WithStatement withStatement) {
            throw new RuntimeException("we don't expect with");
        }

        @Override
        public Expression forArrayExpression(ArrayExpression arrayExpression) {
            List<Option<Expression>> elements = arrayExpression.getElements();
            return new ArrayExpression(elements.map(oe -> oe.map(exp -> exp.accept(this))));
        }

        @Override
        public Expression forAssignmentExpression(AssignmentExpression assignmentExpression) {
            String operator = assignmentExpression.getOperator();
            Expression left = assignmentExpression.getLeft();
            Expression right = assignmentExpression.getRight();
            return new AssignmentExpression(operator, left.accept(this), right.accept(this));
        }

        @Override
        public Expression forBinaryExpression(BinaryExpression binaryExpression) {
            String operator = binaryExpression.getOperator();
            Expression left = binaryExpression.getLeft();
            Expression right = binaryExpression.getRight();
            return new BinaryExpression(operator, left.accept(this), right.accept(this));
        }

        @Override
        public Expression forCallExpression(CallExpression callExpression) {
            Expression callee = callExpression.getCallee();
            List<Expression> arguments = callExpression.getArguments();
            return new CallExpression(
                    callee.accept(this),
                    arguments.map(exp -> exp.accept(this)));
        }

        @Override
        public Expression forConditionalExpression(ConditionalExpression conditionalExpression) {
            Expression test = conditionalExpression.getTest();
            Expression consequent = conditionalExpression.getConsequent();
            Expression alternate = conditionalExpression.getAlternate();
            return new ConditionalExpression(test.accept(this), consequent.accept(this), alternate.accept(this));
        }

        @Override
        public Expression forFunctionExpression(FunctionExpression functionExpression) {
            Option<IdentifierExpression> id = functionExpression.getId();
            List<IdentifierExpression> params = functionExpression.getParams();
            BlockStatement body = functionExpression.getBody();
            return new FunctionExpression(
                    id.map(exp -> (IdentifierExpression)exp.accept(this)),
                    params.map(exp -> (IdentifierExpression)exp.accept(this)),
                    (BlockStatement)body.accept(this));
        }

        @Override
        public Expression forLiteralExpression(LiteralExpression literalExpression) {
            Literal literal = literalExpression.getLiteral();
            return new LiteralExpression(literal.accept(this));
        }

        @Override
        public Expression forLogicalExpression(LogicalExpression logicalExpression) {
            String operator = logicalExpression.getOperator();
            Expression left = logicalExpression.getLeft();
            Expression right = logicalExpression.getRight();
            return new LogicalExpression(operator, left.accept(this), right.accept(this));
        }

        @Override
        public Expression forMemberExpression(MemberExpression memberExpression) {
            Expression object = memberExpression.getObject();
            Expression property = memberExpression.getProperty();
            Boolean computed = memberExpression.getComputed();
            return new MemberExpression(object.accept(this), property.accept(this), computed);
        }

        @Override
        public Expression forNewExpression(NewExpression newExpression) {
            Expression callee = newExpression.getCallee();
            List<Expression> arguments = newExpression.getArguments();
            return new NewExpression(callee.accept(this), arguments.map(exp -> exp.accept(this)));
        }

        @Override
        public Expression forObjectExpression(ObjectExpression objectExpression) {
            List<Property> properties = objectExpression.getProperties();
            return new ObjectExpression(properties.map(p -> p.accept(this)));
        }

        @Override
        public Expression forRealIdentifierExpression(RealIdentifierExpression realIdentifierExpression) {
            return realIdentifierExpression;
        }

        @Override
        public Expression forScratchIdentifierExpression(ScratchIdentifierExpression scratchIdentifierExpression) {
            return scratchIdentifierExpression;
        }

        @Override
        public Expression forScratchSequenceExpression(ScratchSequenceExpression scratchSequenceExpression) {
            List<P2<ScratchIdentifierExpression, Expression>> declarations = scratchSequenceExpression.getDeclarations();
            Expression body = scratchSequenceExpression.getBody();
            return new ScratchSequenceExpression(
                    declarations.map(p -> P.p((ScratchIdentifierExpression)p._1().accept(this), p._2().accept(this))),
                    body.accept(this));
        }

        @Override
        public Expression forSequenceExpression(SequenceExpression sequenceExpression) {
            List<Expression> expressions = sequenceExpression.getExpressions();
            return new SequenceExpression(expressions.map(exp -> exp.accept(this)));
        }

        @Override
        public Expression forThisExpression(ThisExpression thisExpression) {
            return thisExpression;
        }

        @Override
        public Expression forUnaryExpression(UnaryExpression unaryExpression) {
            String operator = unaryExpression.getOperator();
            Boolean prefix = unaryExpression.getPrefix();
            Expression argument = unaryExpression.getArgument();
            return new UnaryExpression(operator, prefix, argument.accept(this));
        }

        @Override
        public Expression forUpdateExpression(UpdateExpression updateExpression) {
            String operator = updateExpression.getOperator();
            Boolean prefix = updateExpression.getPrefix();
            Expression argument = updateExpression.getArgument();
            return new UpdateExpression(operator, argument.accept(this), prefix);
        }

        @Override
        public CatchClause forCatchClause(CatchClause catchClause) {
            IdentifierExpression param = catchClause.getParam();
            BlockStatement body = catchClause.getBody();
            return new CatchClause((IdentifierExpression)param.accept(this), (BlockStatement)body.accept(this));
        }

        @Override
        public SwitchCase forSwitchCase(SwitchCase switchCase) {
            Option<Expression> test = switchCase.getTest();
            List<Statement> consequent = switchCase.getConsequent();
            return new SwitchCase(test.map(exp -> exp.accept(this)), consequent.map(stmt -> stmt.accept(this)));
        }

        @Override
        public VariableDeclarator forVariableDeclarator(VariableDeclarator variableDeclarator) {
            IdentifierExpression id = variableDeclarator.getId();
            Option<Expression> init = variableDeclarator.getInit();
            return new VariableDeclarator((IdentifierExpression)id.accept(this), init.map(exp -> exp.accept(this)));
        }

        @Override
        public Literal forBooleanLiteral(BooleanLiteral booleanLiteral) {
            return booleanLiteral;
        }

        @Override
        public Literal forNullLiteral(NullLiteral nullLiteral) {
            return nullLiteral;
        }

        @Override
        public Literal forNumberLiteral(NumberLiteral numberLiteral) {
            return numberLiteral;
        }

        @Override
        public Literal forRegExpLiteral(RegExpLiteral regExpLiteral) {
            return regExpLiteral;
        }

        @Override
        public Literal forStringLiteral(StringLiteral stringLiteral) {
            return stringLiteral;
        }

        @Override
        public Literal forUndefinedLiteral(UndefinedLiteral undefinedLiteral) {
            return undefinedLiteral;
        }

        @Override
        public Property forProperty(Property property) {
            Node key = property.getKey();
            Expression value = property.getValue();
            String kind = property.getKind();
            Node _key;
            if (key instanceof Literal) {
                _key = ((Literal)key).accept(this);
            } else if (key instanceof IdentifierExpression) {
                _key = ((IdentifierExpression)key).accept(this);
            } else {
                throw new RuntimeException("parser error");
            }
            return new Property(_key, value.accept(this), kind);
        }
    }

    public static class ReplaceEmptyWithUndefV extends DefaultPassV {
        @Override
        public Statement forEmptyStatement(EmptyStatement emptyStatement) {
            return new ExpressionStatement(new LiteralExpression(new UndefinedLiteral()));
        }
    }

    public static class FixContinueLabelsV extends DefaultPassV {
    }

    public static class MakeAllAssignmentsSimpleV extends DefaultPassV {
        @Override
        public Statement forEmptyStatement(EmptyStatement emptyStatement) {
            throw new RuntimeException("parser error");
        }

        @Override
        public Expression forAssignmentExpression(AssignmentExpression assignmentExpression) {
            String operator = assignmentExpression.getOperator();
            Expression left = assignmentExpression.getLeft();
            Expression right = assignmentExpression.getRight();
            if (operator.equals("=")) {
                return assignmentExpression;
            } else {
                String _operator = operator.substring(0, operator.length() - 1);
                if (left instanceof IdentifierExpression) {
                    return new AssignmentExpression("=", left,
                            new BinaryExpression(_operator, left, right.accept(this)));
                } else if (left instanceof MemberExpression) {
                    ScratchIdentifierExpression temp1 = ScratchIdentifierExpression.generate();
                    ScratchIdentifierExpression temp2 = ScratchIdentifierExpression.generate();
                    Expression object = ((MemberExpression) left).getObject();
                    Expression property = ((MemberExpression) left).getProperty();
                    List<P2<ScratchIdentifierExpression, Expression>> declarations =
                            List.list(temp1, temp2).zip(List.list(new UnaryExpression("toObj", true, object.accept(this)), property.accept(this)));
                    return new AssignmentExpression("=", new MemberExpression(temp1, temp2, false),
                            new BinaryExpression(_operator, new MemberExpression(temp1, temp2, false), right.accept(this)));
                } else {
                    throw new RuntimeException("parser error");
                }
            }
        }

        @Override
        public Expression forUpdateExpression(UpdateExpression updateExpression) {
            String operator = updateExpression.getOperator();
            Expression argument = updateExpression.getArgument();
            Boolean prefix = updateExpression.getPrefix();
            String _operator = operator.substring(0, 1);
            if (prefix) {
                AssignmentExpression exp = new AssignmentExpression(_operator.concat("="), argument, new LiteralExpression(new NumberLiteral(1)));
                return exp.accept(this);
            } else {
                ScratchIdentifierExpression temp = ScratchIdentifierExpression.generate();
                SequenceExpression exp = new SequenceExpression(List.list(
                        new AssignmentExpression(_operator.concat("="), argument, new LiteralExpression(new NumberLiteral(1))),
                        temp));
                return new ScratchSequenceExpression(List.list(P.p(temp, argument)), exp).accept(this);
            }
        }
    }

    public static class HoistFunctionsV implements TransformVisitor<List<Statement>> {
        static List<Statement> combine(List<Statement> l1, List<Statement> l2) {
            return l1.append(l2);
        }

        @Override
        public P2<Program, List<Statement>> forProgram(Program program) {
            List<Statement> body = program.getBody();
            List<P2<Statement, List<Statement>>> tmp = body.map(stmt -> stmt.accept(this));
            P2<List<Statement>, List<List<Statement>>> tmp1 = List.unzip(tmp);
            return P.p(new Program(tmp1._2().foldLeft1(HoistFunctionsV::combine).append(tmp1._1())), List.list());
        }

        @Override
        public P2<Statement, List<Statement>> forBlockStatement(BlockStatement blockStatement) {
            List<Statement> body = blockStatement.getBody();
            List<P2<Statement, List<Statement>>> tmp = body.map(stmt -> stmt.accept(this));
            P2<List<Statement>, List<List<Statement>>> tmp1 = List.unzip(tmp);
            return P.p(new BlockStatement(tmp1._1()), tmp1._2().foldLeft1(HoistFunctionsV::combine));
        }

        @Override
        public P2<Statement, List<Statement>> forBreakStatement(BreakStatement breakStatement) {
            return P.p(breakStatement, List.list());
        }

        @Override
        public P2<Statement, List<Statement>> forContinueStatement(ContinueStatement continueStatement) {
            return P.p(continueStatement, List.list());
        }

        @Override
        public P2<Statement, List<Statement>> forDebuggerStatement(DebuggerStatement debuggerStatement) {
            return P.p(debuggerStatement, List.list());
        }

        @Override
        public P2<Statement, List<Statement>> forDoWhileStatement(DoWhileStatement doWhileStatement) {
            Statement body = doWhileStatement.getBody();
            Expression test = doWhileStatement.getTest();
            P2<Statement, List<Statement>> tmp = body.accept(this);
            P2<Expression, List<Statement>> tmp1 = test.accept(this);
            assert tmp1._2().isEmpty();
            return P.p(new DoWhileStatement(tmp._1(), tmp1._1()), tmp._2());
        }

        @Override
        public P2<Statement, List<Statement>> forEmptyStatement(EmptyStatement emptyStatement) {
            throw new RuntimeException("parser error");
        }

        @Override
        public P2<Statement, List<Statement>> forExpressionStatement(ExpressionStatement expressionStatement) {
            Expression expression = expressionStatement.getExpression();
            P2<Expression, List<Statement>> tmp = expression.accept(this);
            assert tmp._2().isEmpty();
            return P.p(new ExpressionStatement(tmp._1()), List.list());
        }

        @Override
        public P2<Statement, List<Statement>> forForInStatement(ForInStatement forInStatement) {
            Node left = forInStatement.getLeft();
            Expression right = forInStatement.getRight();
            Statement body = forInStatement.getBody();
            P2<Statement, List<Statement>> tmp = body.accept(this);
            P2<Node, List<Statement>> tmp1;
            if (left instanceof VariableDeclaration) {
                P2<Statement, List<Statement>> _tmp1 = ((VariableDeclaration) left).accept(this);
                tmp1 = P.p(_tmp1._1(), _tmp1._2());
            } else if (left instanceof Expression) {
                P2<Expression, List<Statement>> _tmp1 = ((Expression) left).accept(this);
                assert _tmp1._2().isEmpty();
                tmp1 = P.p(_tmp1._1(), List.list());
            } else {
                throw new RuntimeException("parser error");
            }
            P2<Expression, List<Statement>> tmp2 = right.accept(this);
            assert tmp2._2().isEmpty();
            return P.p(new ForInStatement(tmp1._1(), tmp2._1(), tmp._1()), tmp1._2().append(tmp._2()));
        }

        @Override
        public P2<Statement, List<Statement>> forForStatement(ForStatement forStatement) {
            Option<Node> init = forStatement.getInit();
            Option<Expression> test = forStatement.getTest();
            Option<Expression> update = forStatement.getUpdate();
            Statement body = forStatement.getBody();
            P2<Statement, List<Statement>> tmp = body.accept(this);
            P2<Option<Node>, List<Statement>> tmp1 = P.p(Option.none(), List.list());
            P2<Option<Expression>, List<Statement>> tmp2 = P.p(Option.none(), List.list());
            P2<Option<Expression>, List<Statement>> tmp3 = P.p(Option.none(), List.list());
            if (init.isSome()) {
                Node initNode = init.some();
                if (initNode instanceof VariableDeclaration) {
                    P2<Statement, List<Statement>> _tmp1 = ((VariableDeclaration) initNode).accept(this);
                    tmp1 = P.p(Option.some(_tmp1._1()), _tmp1._2());
                } else if (initNode instanceof Expression) {
                    P2<Expression, List<Statement>> _tmp1 = ((Expression) initNode).accept(this);
                    assert _tmp1._2().isEmpty();
                    tmp1 = P.p(Option.some(_tmp1._1()), List.list());
                } else {
                    throw new RuntimeException("parser error");
                }
            }
            if (test.isSome()) {
                P2<Expression, List<Statement>> _tmp2 = test.some().accept(this);
                assert _tmp2._2().isEmpty();
                tmp2 = P.p(Option.some(_tmp2._1()), List.list());
            }
            if (update.isSome()) {
                P2<Expression, List<Statement>> _tmp3 = update.some().accept(this);
                assert _tmp3._2().isEmpty();
                tmp3 = P.p(Option.some(_tmp3._1()), List.list());
            }
            return P.p(new ForStatement(tmp1._1(), tmp2._1(), tmp3._1(), tmp._1()), tmp1._2().append(tmp._2()));
        }

        @Override
        public P2<Statement, List<Statement>> forFunctionDeclaration(FunctionDeclaration functionDeclaration) {
            IdentifierExpression id = functionDeclaration.getId();
            List<IdentifierExpression> params = functionDeclaration.getParams();
            BlockStatement body = functionDeclaration.getBody();
            P2<Statement, List<Statement>> tmp = body.accept(this);
            assert tmp._1() instanceof BlockStatement;
            BlockStatement _body = new BlockStatement(tmp._2().append(((BlockStatement)tmp._1()).getBody()));
            return P.p(
                    new ExpressionStatement(new LiteralExpression(new UndefinedLiteral())),
                    List.list(new FunctionDeclaration(id, params, _body)));
        }

        @Override
        public P2<Statement, List<Statement>> forIfStatement(IfStatement ifStatement) {
            Expression test = ifStatement.getTest();
            Statement consequent = ifStatement.getConsequent();
            Option<Statement> alternate = ifStatement.getAlternate();
            P2<Statement, List<Statement>> tmp = consequent.accept(this);
            Option<P2<Statement, List<Statement>>> tmp1 = alternate.map(stmt -> stmt.accept(this));
            return P.p(
                    new IfStatement(test, tmp._1(), tmp1.map(P2.__1())),
                    tmp1.isNone() ? tmp._2() : tmp._2().append(tmp1.some()._2()));
        }

        @Override
        public P2<Statement, List<Statement>> forLabeledStatement(LabeledStatement labeledStatement) {
            IdentifierExpression label = labeledStatement.getLabel();
            Statement body = labeledStatement.getBody();
            P2<Statement, List<Statement>> tmp = body.accept(this);
            return P.p(new LabeledStatement(label, tmp._1()), tmp._2());
        }

        @Override
        public P2<Statement, List<Statement>> forReturnStatement(ReturnStatement returnStatement) {
            Option<Expression> argument = returnStatement.getArgument();
            if (argument.isNone()) {
                return P.p(returnStatement, List.list());
            } else {
                P2<Expression, List<Statement>> tmp = argument.some().accept(this);
                assert tmp._2().isEmpty();
                return P.p(new ReturnStatement(Option.some(tmp._1())), List.list());
            }
        }

        @Override
        public P2<Statement, List<Statement>> forSwitchStatement(SwitchStatement switchStatement) {
            Expression discriminant = switchStatement.getDiscriminant();
            List<SwitchCase> cases = switchStatement.getCases();
            List<P2<SwitchCase, List<Statement>>> tmp = cases.map(sc -> sc.accept(this));
            P2<Expression, List<Statement>> tmp1 = discriminant.accept(this);
            P2<List<SwitchCase>, List<List<Statement>>> tmp2 = List.unzip(tmp);
            assert tmp1._2().isEmpty();
            return P.p(new SwitchStatement(tmp1._1(), tmp2._1()), tmp2._2().foldLeft1(HoistFunctionsV::combine));
        }

        @Override
        public P2<Statement, List<Statement>> forThrowStatement(ThrowStatement throwStatement) {
            Expression argument = throwStatement.getArgument();
            P2<Expression, List<Statement>> tmp = argument.accept(this);
            assert tmp._2().isEmpty();
            return P.p(new ThrowStatement(tmp._1()), List.list());
        }

        @Override
        public P2<Statement, List<Statement>> forTryStatement(TryStatement tryStatement) {
            BlockStatement block = tryStatement.getBlock();
            Option<CatchClause> handler = tryStatement.getHandler();
            Option<BlockStatement> finalizer = tryStatement.getFinalizer();
            P2<Statement, List<Statement>> tmp = block.accept(this);
            assert tmp._1() instanceof BlockStatement;
            P2<Option<CatchClause>, List<Statement>> tmp1 = P.p(Option.none(), List.list());
            if (handler.isSome()) {
                P2<CatchClause, List<Statement>> _tmp1 = handler.some().accept(this);
                tmp1 = P.p(Option.some(_tmp1._1()), _tmp1._2());
            }
            P2<Option<BlockStatement>, List<Statement>> tmp2 = P.p(Option.none(), List.list());
            if (finalizer.isSome()) {
                P2<Statement, List<Statement>> _tmp2 = finalizer.some().accept(this);
                assert _tmp2._1() instanceof BlockStatement;
                tmp2 = P.p(Option.some((BlockStatement)_tmp2._1()), _tmp2._2());
            }
            return P.p(new TryStatement((BlockStatement)tmp._1(), tmp1._1(), tmp2._1()), tmp._2().append(tmp1._2()).append(tmp2._2()));
        }

        @Override
        public P2<Statement, List<Statement>> forVariableDeclaration(VariableDeclaration variableDeclaration) {
            List<VariableDeclarator> declarations = variableDeclaration.getDeclarations();
            List<P2<VariableDeclarator, List<Statement>>> tmp = declarations.map(decl -> decl.accept(this));
            P2<List<VariableDeclarator>, List<List<Statement>>> tmp1 = List.unzip(tmp);
            assert tmp1._2().forall(List.isEmpty_());
            return P.p(new VariableDeclaration(tmp1._1()), List.list());
        }

        @Override
        public P2<Statement, List<Statement>> forWhileStatement(WhileStatement whileStatement) {
            Expression test = whileStatement.getTest();
            Statement body = whileStatement.getBody();
            P2<Expression, List<Statement>> tmp = test.accept(this);
            P2<Statement, List<Statement>> tmp1 = body.accept(this);
            assert tmp._2().isEmpty();
            return P.p(new WhileStatement(tmp._1(), tmp1._1()), tmp1._2());
        }

        @Override
        public P2<Statement, List<Statement>> forWithStatement(WithStatement withStatement) {
            throw new RuntimeException("we don't expect with");
        }

        @Override
        public P2<Expression, List<Statement>> forArrayExpression(ArrayExpression arrayExpression) {
            List<Option<Expression>> elements = arrayExpression.getElements();
            List<Option<P2<Expression, List<Statement>>>> tmp = elements.map(oe -> oe.map(exp -> exp.accept(this)));
            List<Option<Expression>> _elements = tmp.map(p -> p.isNone() ? Option.none() : Option.some(p.some()._1()));
            List<List<Statement>> tmp1 = tmp.filter(Option.isSome_()).map(p -> p.some()).map(P2.__2());
            assert tmp1.forall(List.isEmpty_());
            return P.p(new ArrayExpression(_elements), List.list());
        }

        @Override
        public P2<Expression, List<Statement>> forAssignmentExpression(AssignmentExpression assignmentExpression) {
            String operator = assignmentExpression.getOperator();
            Expression left = assignmentExpression.getLeft();
            Expression right = assignmentExpression.getRight();
            P2<Expression, List<Statement>> tmp = left.accept(this);
            P2<Expression, List<Statement>> tmp1 = right.accept(this);
            assert tmp._2().isEmpty() && tmp1._2().isEmpty();
            return P.p(new AssignmentExpression(operator, tmp._1(), tmp1._1()), List.list());
        }

        @Override
        public P2<Expression, List<Statement>> forBinaryExpression(BinaryExpression binaryExpression) {
            String operator = binaryExpression.getOperator();
            Expression left = binaryExpression.getLeft();
            Expression right = binaryExpression.getRight();
            P2<Expression, List<Statement>> tmp = left.accept(this);
            P2<Expression, List<Statement>> tmp1 = right.accept(this);
            assert tmp._2().isEmpty() && tmp1._2().isEmpty();
            return P.p(new BinaryExpression(operator, tmp._1(), tmp1._1()), List.list());
        }

        @Override
        public P2<Expression, List<Statement>> forCallExpression(CallExpression callExpression) {
            Expression callee = callExpression.getCallee();
            List<Expression> arguments = callExpression.getArguments();
            P2<Expression, List<Statement>> tmp = callee.accept(this);
            assert tmp._2().isEmpty();
            List<P2<Expression, List<Statement>>> tmp1 = arguments.map(exp -> exp.accept(this));
            P2<List<Expression>, List<List<Statement>>> tmp2 = List.unzip(tmp1);
            assert tmp2._2().forall(List.isEmpty_());
            return P.p(new CallExpression(tmp._1(), tmp2._1()), List.list());
        }

        @Override
        public P2<Expression, List<Statement>> forConditionalExpression(ConditionalExpression conditionalExpression) {
            Expression test = conditionalExpression.getTest();
            Expression consequent = conditionalExpression.getConsequent();
            Expression alternate = conditionalExpression.getAlternate();
            P2<Expression, List<Statement>>
                    tmp = test.accept(this),
                    tmp1 = consequent.accept(this),
                    tmp2 = alternate.accept(this);
            assert tmp._2().isEmpty() && tmp1._2().isEmpty() && tmp2._2().isEmpty();
            return P.p(new ConditionalExpression(tmp._1(), tmp1._1(), tmp2._1()), List.list());
        }

        @Override
        public P2<Expression, List<Statement>> forFunctionExpression(FunctionExpression functionExpression) {
            Option<IdentifierExpression> id = functionExpression.getId();
            List<IdentifierExpression> params = functionExpression.getParams();
            BlockStatement body = functionExpression.getBody();
            P2<Statement, List<Statement>> tmp = body.accept(this);
            assert tmp._1() instanceof BlockStatement;
            BlockStatement _body = new BlockStatement(tmp._2().append(((BlockStatement)tmp._1()).getBody()));
            return P.p(new FunctionExpression(id, params, _body), List.list());
        }

        @Override
        public P2<Expression, List<Statement>> forLiteralExpression(LiteralExpression literalExpression) {
            return P.p(literalExpression, List.list());
        }

        @Override
        public P2<Expression, List<Statement>> forLogicalExpression(LogicalExpression logicalExpression) {
            String operator = logicalExpression.getOperator();
            Expression left = logicalExpression.getLeft();
            Expression right = logicalExpression.getRight();
            P2<Expression, List<Statement>>
                    tmp = left.accept(this),
                    tmp1 = right.accept(this);
            assert tmp._2().isEmpty() && tmp1._2().isEmpty();
            return P.p(new LogicalExpression(operator, tmp._1(), tmp1._1()), List.list());
        }

        @Override
        public P2<Expression, List<Statement>> forMemberExpression(MemberExpression memberExpression) {
            Expression object = memberExpression.getObject();
            Expression property = memberExpression.getProperty();
            Boolean computed = memberExpression.getComputed();
            P2<Expression, List<Statement>>
                    tmp = object.accept(this),
                    tmp1 = property.accept(this);
            assert tmp._2().isEmpty() && tmp1._2().isEmpty();
            return P.p(new MemberExpression(tmp._1(), tmp1._1(), computed), List.list());
        }

        @Override
        public P2<Expression, List<Statement>> forNewExpression(NewExpression newExpression) {
            Expression callee = newExpression.getCallee();
            List<Expression> arguments = newExpression.getArguments();
            P2<Expression, List<Statement>> tmp = callee.accept(this);
            assert tmp._2().isEmpty();
            List<P2<Expression, List<Statement>>> tmp1 = arguments.map(exp -> exp.accept(this));
            P2<List<Expression>, List<List<Statement>>> tmp2 = List.unzip(tmp1);
            assert tmp2._2().forall(List.isEmpty_());
            return P.p(new NewExpression(tmp._1(), tmp2._1()), List.list());
        }

        @Override
        public P2<Expression, List<Statement>> forObjectExpression(ObjectExpression objectExpression) {
            List<Property> properties = objectExpression.getProperties();
            List<P2<Property, List<Statement>>> tmp = properties.map(p -> p.accept(this));
            P2<List<Property>, List<List<Statement>>> tmp1 = List.unzip(tmp);
            assert tmp1._2().forall(List.isEmpty_());
            return P.p(new ObjectExpression(tmp1._1()), List.list());
        }

        @Override
        public P2<Expression, List<Statement>> forRealIdentifierExpression(RealIdentifierExpression realIdentifierExpression) {
            return P.p(realIdentifierExpression, List.list());
        }

        @Override
        public P2<Expression, List<Statement>> forScratchIdentifierExpression(ScratchIdentifierExpression scratchIdentifierExpression) {
            return P.p(scratchIdentifierExpression, List.list());
        }

        @Override
        public P2<Expression, List<Statement>> forScratchSequenceExpression(ScratchSequenceExpression scratchSequenceExpression) {
            List<P2<ScratchIdentifierExpression, Expression>> declarations = scratchSequenceExpression.getDeclarations();
            Expression body = scratchSequenceExpression.getBody();
            List<P2<Expression, List<Statement>>> tmp = declarations.map(p -> p._2().accept(this));
            P2<List<Expression>, List<List<Statement>>> tmp1 = List.unzip(tmp);
            assert tmp1._2().forall(List.isEmpty_());
            P2<Expression, List<Statement>> tmp2 = body.accept(this);
            assert tmp2._2().isEmpty();
            return P.p(new ScratchSequenceExpression(List.unzip(declarations)._1().zip(tmp1._1()), tmp2._1()), List.list());
        }

        @Override
        public P2<Expression, List<Statement>> forSequenceExpression(SequenceExpression sequenceExpression) {
            List<Expression> expressions = sequenceExpression.getExpressions();
            P2<List<Expression>, List<List<Statement>>> tmp = List.unzip(expressions.map(exp -> exp.accept(this)));
            assert tmp._2().forall(List.isEmpty_());
            return P.p(new SequenceExpression(tmp._1()), List.list());
        }

        @Override
        public P2<Expression, List<Statement>> forThisExpression(ThisExpression thisExpression) {
            return P.p(thisExpression, List.list());
        }

        @Override
        public P2<Expression, List<Statement>> forUnaryExpression(UnaryExpression unaryExpression) {
            String operator = unaryExpression.getOperator();
            Boolean prefix = unaryExpression.getPrefix();
            Expression argument = unaryExpression.getArgument();
            P2<Expression, List<Statement>> tmp = argument.accept(this);
            assert tmp._2().isEmpty();
            return P.p(new UnaryExpression(operator, prefix, tmp._1()), List.list());
        }

        @Override
        public P2<Expression, List<Statement>> forUpdateExpression(UpdateExpression updateExpression) {
            throw new RuntimeException("parser error");
        }

        @Override
        public P2<CatchClause, List<Statement>> forCatchClause(CatchClause catchClause) {
            IdentifierExpression param = catchClause.getParam();
            BlockStatement body = catchClause.getBody();
            P2<Statement, List<Statement>> tmp = body.accept(this);
            assert tmp._1() instanceof BlockStatement;
            return P.p(new CatchClause(param, (BlockStatement)tmp._1()), tmp._2());
        }

        @Override
        public P2<SwitchCase, List<Statement>> forSwitchCase(SwitchCase switchCase) {
            Option<Expression> test = switchCase.getTest();
            List<Statement> consequent = switchCase.getConsequent();
            Option<P2<Expression, List<Statement>>> tmp = test.map(exp -> exp.accept(this));
            P2<List<Statement>, List<List<Statement>>> tmp1 = List.unzip(consequent.map(stmt -> stmt.accept(this)));
            assert tmp.isNone() || tmp.some()._2().isEmpty();
            return P.p(new SwitchCase(tmp.map(P2.__1()), tmp1._1()), tmp1._2().foldLeft1(HoistFunctionsV::combine));
        }

        @Override
        public P2<VariableDeclarator, List<Statement>> forVariableDeclarator(VariableDeclarator variableDeclarator) {
            IdentifierExpression id = variableDeclarator.getId();
            Option<Expression> init = variableDeclarator.getInit();
            Option<P2<Expression, List<Statement>>> tmp = init.map(exp -> exp.accept(this));
            assert tmp.isNone() || tmp.some()._2().isEmpty();
            return P.p(new VariableDeclarator(id, tmp.map(P2.__1())), List.list());
        }

        @Override
        public P2<Literal, List<Statement>> forBooleanLiteral(BooleanLiteral booleanLiteral) {
            throw new RuntimeException("should not be here");
        }

        @Override
        public P2<Literal, List<Statement>> forNullLiteral(NullLiteral nullLiteral) {
            throw new RuntimeException("should not be here");
        }

        @Override
        public P2<Literal, List<Statement>> forNumberLiteral(NumberLiteral numberLiteral) {
            throw new RuntimeException("should not be here");
        }

        @Override
        public P2<Literal, List<Statement>> forRegExpLiteral(RegExpLiteral regExpLiteral) {
            throw new RuntimeException("should not be here");
        }

        @Override
        public P2<Literal, List<Statement>> forStringLiteral(StringLiteral stringLiteral) {
            throw new RuntimeException("should not be here");
        }

        @Override
        public P2<Literal, List<Statement>> forUndefinedLiteral(UndefinedLiteral undefinedLiteral) {
            throw new RuntimeException("should not be here");
        }

        @Override
        public P2<Property, List<Statement>> forProperty(Property property) {
            Node key = property.getKey();
            Expression value = property.getValue();
            String kind = property.getKind();
            P2<Expression, List<Statement>> tmp = value.accept(this);
            assert tmp._2().isEmpty();
            return P.p(new Property(key, tmp._1(), kind), List.list());
        }
    }

    public static Node transform(Node ast) {
        return null;
    }
}
