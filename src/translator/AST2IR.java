package translator;

import ast.*;
import fj.*;
import fj.data.*;
import ir.*;

/**
 * Created by wayne on 15/11/11.
 */
public class AST2IR {

    public static class PVarMapper {
        static HashMap<Integer, String> numToName = new HashMap<>(Equal.intEqual, Hash.intHash);
        static HashMap<String, Integer> nameToNum = new HashMap<>(Equal.stringEqual, Hash.stringHash);
        static Integer counter = 0;

        public static final String WINDOW_NAME = "window";
        public static final String ARGUMENTS_NAME = "arguments";

        public static final IRPVar window = newMangledVar(WINDOW_NAME);
        public static final String windowName = getName(window);
        public static final IRPVar unmangledWindow = getPVar(WINDOW_NAME);

        public static final IRPVar global = window;
        public static final IRPVar dummy = newMangledVar("dummy");

        public static final IRPVar numberVar = newMangledVar("number");
        public static final IRPVar objectVar = newMangledVar("object");
        public static final IRPVar argumentsVar = newMangledVar("arguments");
        public static final IRPVar dummyAddressVar = newMangledVar("dummyAddress");

        public static final IRPVar selfVar = newMangledVar("self");
        public static final String selfName = getName(selfVar);

        public static final IRStmt nopStmt = new IRAssign(dummy, new IRUndef());

        public static TreeMap<Integer, String> getMapping() {
            return TreeMap.fromMutableMap(Ord.hashEqualsOrd(), numToName.toMap());
        }

        public static IRExp windowAccess(String str) {
            return new IRBinop(Bop.Access, window, new IRStr(str));
        }

        public static IRPVar getPVar(String name) {
            if (nameToNum.contains(name)) {
                return new IRPVar(nameToNum.get(name).some());
            } else {
                return newPVar(name);
            }
        }

        public static IRVar getVar(IdentifierExpression x) {
            if (x instanceof RealIdentifierExpression) {
                return getPVar(((RealIdentifierExpression) x).getName());
            } else {
                return new IRScratch(((ScratchIdentifierExpression)x).getNum());
            }
        }

        public static String getName(Integer i) {
            return numToName.get(i).some();
        }

        public static String getName(IRPVar x) {
            return getName(x.n);
        }

        static IRPVar newPVar(String name) {
            Integer res = counter;
            counter += 1;
            numToName.set(res, name);
            nameToNum.set(name, res);
            return new IRPVar(res);
        }

        static IRPVar freshPVar() {
            return newMangledVar("temp");
        }

        public static IRPVar newMangledVar(String name) {
            return getPVar("`" + name + "`" + counter);
        }
    }

    interface NodeVisitor<T> extends ProgramVisitor<T>, StatementVisitor<T>, ExpressionVisitor<T>, CatchClauseVisitor<T>, SwitchCaseVisitor<T>, VariableDeclaratorVisitor<T>, LiteralVisitor<T>, PropertyVisitor<T> {}

    public static final IRThrow throwTypeError = new IRThrow(new IRStr("TypeError"));

    public static class TranslateV implements NodeVisitor<P3<IRStmt, IRExp, Set<IRPVar>>> {
        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forWhileStatement(WhileStatement whileStatement) {
            Expression test = whileStatement.getTest();
            Statement body = whileStatement.getBody();
            P3<IRStmt, IRExp, Set<IRPVar>> _test = test.accept(this), _body = body.accept(this);
            P3<IRStmt, IRExp, Set<IRPVar>> tmp = whileHelper(_test._1(), _test._2(), _test._2(), _body._1(), _test._1());
            return P.p(tmp._1(), tmp._2(), _test._3().union(_body._3()).union(tmp._3()));
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forDoWhileStatement(DoWhileStatement doWhileStatement) {
            Statement body = doWhileStatement.getBody();
            Expression test = doWhileStatement.getTest();
            P3<IRStmt, IRExp, Set<IRPVar>> _test = test.accept(this), _body = body.accept(this);
            P3<IRStmt, IRExp, Set<IRPVar>> tmp = whileHelper(PVarMapper.nopStmt, new IRBool(true), _test._2(), _body._1(), _test._1());
            return P.p(tmp._1(), tmp._2(), _test._3().union(_body._3()).union(tmp._3()));
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forForStatement(ForStatement forStatement) {
            Option<Node> init = forStatement.getInit();
            Option<Expression> test = forStatement.getTest();
            Option<Expression> update = forStatement.getUpdate();
            Statement body = forStatement.getBody();
            Option<Statement> init1 = init.map(node -> new ExpressionStatement((Expression)node));
            Option<Statement> update1 = update.map(exp -> new ExpressionStatement(exp));
            Statement init2 = init1.orSome(new ExpressionStatement(new LiteralExpression(new UndefinedLiteral())));
            Statement update2 = update1.orSome(new ExpressionStatement(new LiteralExpression(new UndefinedLiteral())));
            Expression test1 = test.orSome(new LiteralExpression(new BooleanLiteral(true)));
            P3<IRStmt, IRExp, Set<IRPVar>> _init = init2.accept(this), _test = test1.accept(this), _update = update2.accept(this), _body = body.accept(this);
            P3<IRStmt, IRExp, Set<IRPVar>> tmp = whileHelper(new IRSeq(List.list(_init._1(), _test._1())), _test._2(), _test._2(), _body._1(), new IRSeq(List.list(_update._1(), _test._1())));
            return P.p(tmp._1(), tmp._2(), _init._3().union(_test._3()).union(_update._3()).union(_body._3()).union(tmp._3()));
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forForInStatement(ForInStatement forInStatement) {
            Node left = forInStatement.getLeft();
            Expression right = forInStatement.getRight();
            Statement body = forInStatement.getBody();
            P3<IRStmt, IRExp, Set<IRPVar>> _right = right.accept(this), _body = body.accept(this);
            P3<IRStmt, IRExp, Set<IRPVar>> obj = toObj(_right._2());
            Expression lhs = (Expression)left;
            F2<IRVar, IRStmt, P3<IRStmt, IRExp, Set<IRPVar>>> make = (x, s) -> {
                IRStmt inner = new IRSeq(List.list(s, makeLbl(":CONTINUE:", _body._1())));
                return P.p(new IRSeq(List.list(_right._1(), obj._1(), makeLbl(":BREAK:", makeFor(x, obj._2(), inner)))), new IRUndef(), _right._3().union(_body._3()).union(obj._3()));
            };
            F2<Expression, Expression, P3<IRStmt, IRExp, Set<IRPVar>>> access = (e1, e2) -> {
                P3<IRStmt, IRExp, Set<IRPVar>> _e1 = e1.accept(this);
                P3<IRStmt, IRExp, Set<IRPVar>> _e2 = e2.accept(this);
                P4<IRStmt, IRExp, IRExp, Set<IRPVar>> tmp1 = accessSetup(_e1._2(), _e2._2());
                //IRScratch y = freshScratch();
                IRPVar y = freshPVar();
                IRStmt inner = new IRSeq(List.list(_e1._1(), _e2._1(), tmp1._1(), new IRUpdate(tmp1._2(), tmp1._3(), y)));
                P3<IRStmt, IRExp, Set<IRPVar>> tmp = make.f(y, inner);
                return P.p(tmp._1(), tmp._2(), tmp._3().union(_e1._3()).union(_e2._3()).union(tmp1._4()).insert(y));
            };
            if (lhs instanceof IdentifierExpression) {
                IRVar v = PVarMapper.getVar((IdentifierExpression)lhs);
                return make.f(v, PVarMapper.nopStmt);

            } else if (lhs instanceof MemberExpression) {
                Expression object = ((MemberExpression) lhs).getObject();
                Expression property = ((MemberExpression) lhs).getProperty();
                return access.f(object, property);
            } else {
                throw new RuntimeException("ast2ast error");
            }
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forBinaryExpression(BinaryExpression binaryExpression) {
            String operator = binaryExpression.getOperator();
            Expression left = binaryExpression.getLeft();
            Expression right = binaryExpression.getRight();
            P3<IRStmt, IRExp, Set<IRPVar>> _left = left.accept(this), _right = right.accept(this);
            F3<IRStmt, IRExp, Set<IRPVar>, P3<IRStmt, IRExp, Set<IRPVar>>> withStatements = (s, e, ys) ->
                    P.p(new IRSeq(List.list(_left._1(), _right._1(), s)), e, ys.union(_left._3()).union(_right._3()));
            F<IRExp, P3<IRStmt, IRExp, Set<IRPVar>>> withStatementsExp = e ->
                    P.p(new IRSeq(List.list(_left._1(), _right._1())), e, _left._3().union(_right._3()));
            F<IRExp, P3<IRStmt, IRExp, Set<IRPVar>>> asPrimitive = e -> {
                P3<IRStmt, IRExp, Set<IRPVar>> val = valueOf(e);
                P3<IRStmt, IRExp, Set<IRPVar>> str = toSomething(val._2(), exp -> exp, AST2IR::callToString);
                P3<IRStmt, IRExp, Set<IRPVar>> fin = toSomething(str._2(), exp -> exp, (target, obj) -> P.p(throwTypeError, new IRUndef(), Set.empty(Ord.hashEqualsOrd())));
                return P.p(new IRSeq(List.list(val._1(), str._1(), fin._1())), fin._2(), fin._3().union(str._3()).union(val._3()));
            };
            F3<Bop, Bop, Boolean, P3<IRStmt, IRExp, Set<IRPVar>>> lessThanCore = (opStr, opNum, b) -> {
                IRExp theLeft = b ? _right._2() : _left._2();
                IRExp theRight = b ? _left._2() : _right._2();
                P3<IRStmt, IRExp, Set<IRPVar>> _theLeft = toNumber(theLeft), _theRight = toNumber(theRight);
                //IRScratch y = freshScratch();
                IRPVar y = freshPVar();
                return withStatements.f(makeIf(
                        new IRBinop(Bop.LogicalAnd,
                                new IRBinop(Bop.StrictEqual,
                                        new IRUnop(Uop.TypeOf, theLeft),
                                        new IRStr("string")),
                                new IRBinop(Bop.StrictEqual,
                                        new IRUnop(Uop.TypeOf, theRight),
                                        new IRStr("string"))),
                        new IRAssign(y, new IRBinop(opStr, theLeft, theRight)),
                        new IRSeq(List.list(_theLeft._1(), _theRight._1(), new IRAssign(y, new IRBinop(opNum, _theLeft._2(), _theRight._2()))))),
                        y, _theLeft._3().union(_theRight._3()).insert(y));
            };
            F<IRExp, P3<IRStmt, IRExp, Set<IRPVar>>> logicalHelper = e -> {
                //IRScratch y = freshScratch();
                IRPVar y = freshPVar();
                return P.p(
                        new IRSeq(List.list(
                                _left._1(),
                                makeIf(
                                        new IRBinop(Bop.StrictEqual, new IRUnop(Uop.ToBool, _left._2()), e),
                                        new IRSeq(List.list(_right._1(), new IRAssign(y, _right._2()))),
                                        new IRAssign(y, _left._2())
                                ))),
                        y, _left._3().union(_right._3()).insert(y));
            };
            switch (operator) {
                case "-":
                case "*":
                case "/":
                case "%":
                case "<<":
                case ">>":
                case ">>>":
                case "&":
                case "|":
                case "^": {
                    P3<IRStmt, IRExp, Set<IRPVar>> e1 = toNumber(_left._2()), e2 = toNumber(_right._2());
                    return withStatements.f(new IRSeq(List.list(e1._1(), e2._1())), new IRBinop(JSBopToBop(operator), e1._2(), e2._2()), e1._3().union(e2._3()));
                }
                case "+": {
                    P3<IRStmt, IRExp, Set<IRPVar>> e1 = asPrimitive.f(_left._2()), e2 = asPrimitive.f(_right._2());
                    //IRScratch y = freshScratch();
                    IRPVar y = freshPVar();
                    return withStatements.f(new IRSeq(List.list(
                            new IRSeq(interleave(((IRSeq)e1._1()).ss, ((IRSeq)e2._1()).ss)),
                            makeIf(
                                    new IRBinop(Bop.StrictEqual, new IRUnop(Uop.TypeOf, e1._2()), new IRStr("string")),
                                    new IRAssign(y, new IRBinop(Bop.StrConcat, e1._2(), isPrimToString(e2._2()))),
                                    makeIf(
                                            new IRBinop(Bop.StrictEqual, new IRUnop(Uop.TypeOf, e2._2()), new IRStr("string")),
                                            new IRAssign(y, new IRBinop(Bop.StrConcat, isPrimToString(e1._2()), e2._2())),
                                            new IRAssign(y, new IRBinop(Bop.Plus, isPrimToNumber(e1._2()), isPrimToNumber(e2._2())))
                                    )
                            )
                    )), y, e1._3().union(e2._3()).insert(y));
                }
                case "===": {
                    return withStatementsExp.f(new IRBinop(Bop.StrictEqual, _left._2(), _right._2()));
                }
                case "!==": {
                    return withStatementsExp.f(new IRUnop(Uop.LogicalNot, new IRBinop(Bop.StrictEqual, _left._2(), _right._2())));
                }
                case "==": {
                    return withStatementsExp.f(new IRBinop(Bop.NonStrictEqual, _left._2(), _right._2()));
                }
                case "!=": {
                    return withStatementsExp.f(new IRUnop(Uop.LogicalNot, new IRBinop(Bop.NonStrictEqual, _left._2(), _right._2())));
                }
                case "<": {
                    return lessThanCore.f(Bop.StrLessThan, Bop.LessThan, false);
                }
                case "<=": {
                    return lessThanCore.f(Bop.StrLessEqual, Bop.LessEqual, false);
                }
                case ">": {
                    return lessThanCore.f(Bop.StrLessThan, Bop.LessThan, true);
                }
                case ">=": {
                    return lessThanCore.f(Bop.StrLessEqual, Bop.LessEqual, true);
                }
                case "in": {
                    P3<IRStmt, IRExp, Set<IRPVar>> str = AST2IR.toString(_left._2());
                    return withStatements.f(
                            new IRSeq(List.list(
                                    str._1(),
                                    makeIf(
                                            new IRUnop(Uop.IsPrim, _right._2()),
                                            throwTypeError,
                                            PVarMapper.nopStmt
                                    )
                            )),
                            new IRBinop(Bop.In, str._2(), _right._2()),
                            str._3()
                    );
                }
                case "instanceOf": {
                    return withStatements.f(
                            makeIf(
                                    new IRUnop(Uop.LogicalNot, new IRBinop(Bop.StrictEqual, new IRUnop(Uop.TypeOf, _right._2()), new IRStr("function"))),
                                    throwTypeError,
                                    PVarMapper.nopStmt
                            ),
                            new IRBinop(Bop.InstanceOf, _left._2(), new IRBinop(Bop.Access, _right._2(), new IRStr("prototype"))),
                            Set.empty(Ord.hashEqualsOrd())
                    );
                }
                case ",": {
                    return withStatementsExp.f(_right._2());
                }
                default:
                    throw new RuntimeException("unknown operator");
            }
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forArrayExpression(ArrayExpression arrayExpression) {
            return null;
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forAssignmentExpression(AssignmentExpression assignmentExpression) {
            assert assignmentExpression.getOperator().equals("=");
            Expression left = assignmentExpression.getLeft();
            Expression right = assignmentExpression.getRight();
            if (left instanceof IdentifierExpression) {
                P3<IRStmt, IRExp, Set<IRPVar>> _right = right.accept(this);
                return P.p(
                        new IRSeq(List.list(_right._1(), new IRAssign(PVarMapper.getVar((IdentifierExpression)left), _right._2()))),
                        _right._2(),
                        _right._3());
            } else if (left instanceof MemberExpression) {
                // TODO: for window?
                Expression object = ((MemberExpression) left).getObject();
                Expression property = ((MemberExpression) left).getProperty();
                P3<IRStmt, IRExp, Set<IRPVar>> _right = right.accept(this), _object = object.accept(this), _property = property.accept(this);
                P4<IRStmt, IRExp, IRExp, Set<IRPVar>> tmp = accessSetup(_object._2(), _property._2());
                return P.p(
                        new IRSeq(List.list(_right._1(), _object._1(), _property._1(), tmp._1(), new IRUpdate(tmp._2(), tmp._3(), _right._2()))),
                        _right._2(),
                        _right._3().union(_object._3()).union(_property._3()).union(tmp._4()));
            } else {
                throw new RuntimeException("ast2ast error");
            }
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forBlockStatement(BlockStatement blockStatement) {
            List<Statement> body = blockStatement.getBody();
            if (body.isEmpty()) {
                return nopStmt(new IRUndef());
            } else {
                P3<List<IRStmt>, List<IRExp>, List<Set<IRPVar>>> _body = unzip3(body.map(stmt -> stmt.accept(this)));
                return P.p(new IRSeq(_body._1()), _body._2().last(), _body._3().foldLeft((a, b) -> a.union(b), Set.empty(Ord.hashEqualsOrd())));
            }
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forBooleanLiteral(BooleanLiteral booleanLiteral) {
            return nopStmt(new IRBool(booleanLiteral.getValue()));
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forBreakStatement(BreakStatement breakStatement) {
            String lbl = breakStatement.getLabel().map(id -> ((RealIdentifierExpression)id).getName()).orSome(":BREAK:");
            return P.p(new IRJump(lbl, new IRUndef()), new IRUndef(), Set.empty(Ord.hashEqualsOrd()));
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forCallExpression(CallExpression callExpression) {
            Expression callee = callExpression.getCallee();
            List<Expression> arguments = callExpression.getArguments();
            if (callee instanceof MemberExpression) {
                Expression object = ((MemberExpression) callee).getObject();
                Expression property = ((MemberExpression) callee).getProperty();
                P3<IRStmt, IRExp, Set<IRPVar>> _object = object.accept(this), _property = property.accept(this);
                P3<List<IRStmt>, List<IRExp>, List<Set<IRPVar>>> _arguments = unzip3(arguments.map(exp -> exp.accept(this)));
                P4<IRStmt, IRExp, IRExp, Set<IRPVar>> access = accessSetup(_object._2(), _property._2());
                //IRScratch y = freshScratch();
                IRPVar y = freshPVar();
                P3<IRStmt, IRExp, Set<IRPVar>> tmp = call(y, new IRBinop(Bop.Access, access._2(), access._3()), access._2(), _arguments._2());
                return P.p(
                        new IRSeq(List.list(_object._1(), _property._1(), new IRSeq(_arguments._1()), access._1(), tmp._1())),
                        y,
                        _object._3().union(_property._3()).union(_arguments._3().foldLeft((a, b) -> a.union(b), Set.empty(Ord.hashEqualsOrd()))).union(access._4()).union(tmp._3()).insert(y));
            } else {
                P3<IRStmt, IRExp, Set<IRPVar>> _callee = callee.accept(this);
                P3<List<IRStmt>, List<IRExp>, List<Set<IRPVar>>> _arguments = unzip3(arguments.map(exp -> exp.accept(this)));
                //IRScratch y = freshScratch();
                IRPVar y = freshPVar();
                P3<IRStmt, IRExp, Set<IRPVar>> tmp = call(y, _callee._2(), PVarMapper.global, _arguments._2());
                return P.p(
                        new IRSeq(List.list(_callee._1(), new IRSeq(_arguments._1()), tmp._1())),
                        y,
                        _callee._3().union(_arguments._3().foldLeft((a, b) -> a.union(b), Set.empty(Ord.hashEqualsOrd()))).union(tmp._3()).insert(y));
            }
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forCatchClause(CatchClause catchClause) {
            return null;
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forConditionalExpression(ConditionalExpression conditionalExpression) {
            return null;
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forContinueStatement(ContinueStatement continueStatement) {
            String lbl = continueStatement.getLabel().map(id -> ((RealIdentifierExpression)id).getName()).orSome(":CONTINUE:");
            return P.p(new IRJump(lbl, new IRUndef()), new IRUndef(), Set.empty(Ord.hashEqualsOrd()));
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forDebuggerStatement(DebuggerStatement debuggerStatement) {
            return null;
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forEmptyStatement(EmptyStatement emptyStatement) {
            throw new RuntimeException("ast2ast error");
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forExpressionStatement(ExpressionStatement expressionStatement) {
            Expression exp = expressionStatement.getExpression();
            P3<IRStmt, IRExp, Set<IRPVar>> _exp = exp.accept(this);
            return P.p(_exp._1(), new IRUndef(), _exp._3());
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forFunctionDeclaration(FunctionDeclaration functionDeclaration) {
            throw new RuntimeException("ast2ast error");
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forFunctionExpression(FunctionExpression functionExpression) {
            List<IdentifierExpression> params = functionExpression.getParams();
            List<Statement> body = functionExpression.getBody().getBody();
            assert body.isNotEmpty();
            assert body.head() instanceof VariableDeclaration;
            VariableDeclaration declaration = (VariableDeclaration)body.head();
            assert params.length() == Set.set(Ord.hashEqualsOrd(), params).size();
            List<Statement> tail = body.tail();
            P3<List<IRStmt>, List<IRExp>, List<Set<IRPVar>>> _tail = unzip3(tail.map(stmt -> stmt.accept(this)));
            IRPVar args = PVarMapper.getPVar(PVarMapper.ARGUMENTS_NAME);
            List<P2<IRPVar, IRExp>> declParams = params.zip(List.range(0, params.length())).map(p -> {
                RealIdentifierExpression id = (RealIdentifierExpression)p._1();
                Integer num = p._2();
                return P.p(PVarMapper.getPVar(id.getName()), new IRBinop(Bop.Access, args, new IRStr(num.toString())));
            });
            List<VariableDeclarator> bind = declaration.getDeclarations();
            List<P2<IRPVar, IRExp>> declLocal = bind.map(decl -> {
                RealIdentifierExpression id = (RealIdentifierExpression)decl.getId();
                IRExp rhs;
                Option<Expression> e = decl.getInit();
                if (e.isSome() && e.some() instanceof LiteralExpression && ((LiteralExpression)e.some()).getLiteral() instanceof UndefinedLiteral) {
                    rhs = new IRUndef();
                } else if (e.isSome() && e.some() instanceof IdentifierExpression) {
                    rhs = PVarMapper.getVar((IdentifierExpression)e.some());
                } else {
                    throw new RuntimeException("ast2ast error");
                }
                return P.p(PVarMapper.getPVar(id.getName()), rhs);
            });
            P3<IRStmt, IRExp, Set<IRPVar>> tmp = makeArguments(List.list());
            Set<IRPVar> ss = _tail._3().foldLeft((a, b) -> a.union(b), Set.empty(Ord.hashEqualsOrd()));
            List<P2<IRPVar, IRExp>> tmpBind = ss.toList().map(y -> P.p(y, new IRUndef()));
            IRDecl decl = new IRDecl(declParams.append(declLocal).append(tmpBind), new IRLbl(":RETURN:", new IRSeq(List.list(new IRMerge(), new IRSeq(_tail._1())))));
            IRMethod method = new IRMethod(PVarMapper.selfVar, args, decl);
            //IRScratch yProto = freshScratch();
            //IRScratch yRes = freshScratch();
            IRPVar yProto = freshPVar();
            IRPVar yRes = freshPVar();
            return P.p(
                    new IRSeq(List.list(new IRNewfun(yRes, method, new IRNum((double)params.length())), tmp._1(), makeNew(yProto, PVarMapper.objectVar, tmp._2()), new IRUpdate(yRes, new IRStr("prototype"), yProto))),
                    yRes,
                    tmp._3().insert(yProto).insert(yRes));
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forIfStatement(IfStatement ifStatement) {
            Expression test = ifStatement.getTest();
            Statement consequent = ifStatement.getConsequent();
            Statement alternate = ifStatement.getAlternate().orSome(new ExpressionStatement(new LiteralExpression(new UndefinedLiteral())));
            P3<IRStmt, IRExp, Set<IRPVar>> _test = test.accept(this), _consequent = consequent.accept(this), _alternate = alternate.accept(this);
            return P.p(
                    new IRSeq(List.list(_test._1(), makeIf(toBool(_test._2()), _consequent._1(), _alternate._1()))),
                    new IRUndef(),
                    _test._3().union(_consequent._3()).union(_alternate._3()));
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forLabeledStatement(LabeledStatement labeledStatement) {
            RealIdentifierExpression label = (RealIdentifierExpression)labeledStatement.getLabel();
            Statement body = labeledStatement.getBody();
            P3<IRStmt, IRExp, Set<IRPVar>> _body = body.accept(this);
            return P.p(makeLbl(label.getName(), _body._1()), new IRUndef(), _body._3());
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forLiteralExpression(LiteralExpression literalExpression) {
            Literal literal = literalExpression.getLiteral();
            return literal.accept(this);
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forLogicalExpression(LogicalExpression logicalExpression) {
            String operator = logicalExpression.getOperator();
            Expression left = logicalExpression.getLeft();
            Expression right = logicalExpression.getRight();
            P3<IRStmt, IRExp, Set<IRPVar>> _left = left.accept(this), _right = right.accept(this);
            F<IRExp, P3<IRStmt, IRExp, Set<IRPVar>>> logicalHelper = e -> {
                //IRScratch y = freshScratch();
                IRPVar y = freshPVar();
                return P.p(
                        new IRSeq(List.list(
                                _left._1(),
                                makeIf(
                                        new IRBinop(Bop.StrictEqual, new IRUnop(Uop.ToBool, _left._2()), e),
                                        new IRSeq(List.list(_right._1(), new IRAssign(y, _right._2()))),
                                        new IRAssign(y, _left._2())
                                ))),
                        y, _left._3().union(_right._3()).insert(y));
            };
            if (operator.equals("&&")) {
                return logicalHelper.f(new IRBool(true));
            } else if (operator.endsWith("||")) {
                return logicalHelper.f(new IRBool(false));
            } else {
                throw new RuntimeException("unknown operator");
            }
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forMemberExpression(MemberExpression memberExpression) {
            // TODO: for window?
            Expression object = memberExpression.getObject();
            Expression property = memberExpression.getProperty();
            P3<IRStmt, IRExp, Set<IRPVar>> _object = object.accept(this), _property = property.accept(this);
            P4<IRStmt, IRExp, IRExp, Set<IRPVar>> tmp = accessSetup(_object._2(), _property._2());
            return P.p(
                    new IRSeq(List.list(_object._1(), _property._1(), tmp._1())),
                    new IRBinop(Bop.Access, tmp._2(), tmp._3()),
                    _object._3().union(_property._3()).union(tmp._4()));
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forNewExpression(NewExpression newExpression) {
            Expression callee = newExpression.getCallee();
            List<Expression> arguments = newExpression.getArguments();
            P3<IRStmt, IRExp, Set<IRPVar>> _callee = callee.accept(this);
            P3<List<IRStmt>, List<IRExp>, List<Set<IRPVar>>> _arguments = unzip3(arguments.map(exp -> exp.accept(this)));
            IRStmt pred = new IRSeq(List.list(_callee._1(), new IRSeq(_arguments._1())));
            Set<IRPVar> ys = _callee._3().union(_arguments._3().foldLeft((a, b) -> a.union(b), Set.empty(Ord.hashEqualsOrd())));
            Option<Boolean> _ = staticIsPrim(_callee._2());
            if (_.isNone()) {
                P3<IRStmt, IRExp, Set<IRPVar>> args = makeArguments(_arguments._2());
                //IRScratch y = freshScratch();
                IRPVar y = freshPVar();
                return P.p(new IRSeq(List.list(pred, makeIf(
                        new IRUnop(Uop.IsPrim, _callee._2()),
                        throwTypeError,
                        new IRSeq(List.list(args._1(), makeNew(y, _callee._2(), args._2())))))),
                        y,
                        ys.union(args._3()).insert(y));
            } else {
                if (_.some()) {
                    return P.p(new IRSeq(List.list(pred, throwTypeError)), new IRUndef(), ys);
                } else {
                    P3<IRStmt, IRExp, Set<IRPVar>> args = makeArguments(_arguments._2());
                    //IRScratch y = freshScratch();
                    IRPVar y = freshPVar();
                    return P.p(new IRSeq(List.list(pred, args._1(), makeNew(y, _callee._2(), args._2()))), y, ys.union(args._3()).insert(y));
                }
            }
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forNullLiteral(NullLiteral nullLiteral) {
            return nopStmt(new IRNull());
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forNumberLiteral(NumberLiteral numberLiteral) {
            return nopStmt(new IRNum(numberLiteral.getValue().doubleValue()));
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forObjectExpression(ObjectExpression objectExpression) {
            List<Property> properties = objectExpression.getProperties();
            P4<List<IRExp>, List<IRStmt>, List<IRExp>, List<Set<IRPVar>>> tmp = unzip4(properties.map(p -> {
                Node key = p.getKey();
                StringLiteral key1;
                if (key instanceof StringLiteral) {
                    key1 = (StringLiteral) key;
                } else {
                    throw new RuntimeException("parser error");
                }
                Expression value = p.getValue();
                P3<IRStmt, IRExp, Set<IRPVar>> _value = value.accept(this);
                return P.p(new IRStr(key1.getValue()), _value._1(), _value._2(), _value._3());
            }));
            P3<IRStmt, IRExp, Set<IRPVar>> args = makeArguments(List.list());
            //IRScratch y = freshScratch();
            IRPVar y = freshPVar();
            List<P2<IRExp, IRExp>> _ = tmp._1().zip(tmp._3());
            Set<IRPVar> _ss = tmp._4().foldLeft((a, b) -> a.union(b), Set.empty(Ord.hashEqualsOrd()));
            return P.p(
                    new IRSeq(List.list(
                            new IRSeq(tmp._2()), args._1(), new IRNew(y, PVarMapper.objectVar, args._2()),
                            new IRSeq(_.map(p -> new IRUpdate(y, p._1(), p._2()))))),
                    y,
                    _ss.union(args._3()).insert(y));
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forProgram(Program program) {
            List<Statement> body = program.getBody();
            assert body.isNotEmpty();
            assert body.head() instanceof VariableDeclaration;
            VariableDeclaration declaration = (VariableDeclaration)body.head();
            List<P2<IRPVar, IRExp>> bind1 = declaration.getDeclarations().map(decl -> P.p(PVarMapper.getPVar(((RealIdentifierExpression) decl.getId()).getName()), new IRUndef()));
            P3<IRStmt, IRExp, Set<IRPVar>> tmp = new BlockStatement(body.tail()).accept(this);
            List<P2<IRPVar, IRExp>> bind2 = tmp._3().toList().map(x -> P.p(x, new IRUndef()));
            return P.p(new IRDecl(bind1.append(bind2), tmp._1()), new IRUndef(), Set.empty(Ord.hashEqualsOrd()));
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forProperty(Property property) {
            return null;
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forRealIdentifierExpression(RealIdentifierExpression realIdentifierExpression) {
            return nopStmt(PVarMapper.getVar(realIdentifierExpression));
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forRegExpLiteral(RegExpLiteral regExpLiteral) {
            return null;
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forReturnStatement(ReturnStatement returnStatement) {
            Option<Expression> argument = returnStatement.getArgument();
            P3<IRStmt, IRExp, Set<IRPVar>> _argument = argument.orSome(new LiteralExpression(new UndefinedLiteral())).accept(this);
            return P.p(new IRSeq(List.list(_argument._1(), new IRJump(":RETURN:", _argument._2()))), new IRUndef(), _argument._3());
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forScratchIdentifierExpression(ScratchIdentifierExpression scratchIdentifierExpression) {
            return nopStmt(PVarMapper.getVar(scratchIdentifierExpression));
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forScratchSequenceExpression(ScratchSequenceExpression scratchSequenceExpression) {
            throw new RuntimeException("ast2ast error");
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forSequenceExpression(SequenceExpression sequenceExpression) {
            List<Expression> exps = sequenceExpression.getExpressions();
            if (exps.isEmpty()) {
                return nopStmt(new IRUndef());
            } else {
                P3<List<IRStmt>, List<IRExp>, List<Set<IRPVar>>> _exps = unzip3(exps.map(exp -> exp.accept(this)));
                return P.p(new IRSeq(_exps._1()), _exps._2().last(), _exps._3().foldLeft((a, b) -> a.union(b), Set.empty(Ord.hashEqualsOrd())));
            }
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forStringLiteral(StringLiteral stringLiteral) {
            return nopStmt(new IRStr(stringLiteral.getValue()));
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forSwitchCase(SwitchCase switchCase) {
            return null;
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forSwitchStatement(SwitchStatement switchStatement) {
            return null;
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forThisExpression(ThisExpression thisExpression) {
            throw new RuntimeException("ast2ast error");
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forThrowStatement(ThrowStatement throwStatement) {
            return null;
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forTryStatement(TryStatement tryStatement) {
            return null;
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forUnaryExpression(UnaryExpression unaryExpression) {
            String op = unaryExpression.getOperator();
            Expression argument = unaryExpression.getArgument();
            P3<IRStmt, IRExp, Set<IRPVar>> _argument = argument.accept(this);
            F<P3<IRStmt, IRExp, Set<IRPVar>>, P3<IRStmt, IRExp, Set<IRPVar>>> withStatement = t -> {
                return P.p(new IRSeq(List.list(_argument._1(), t._1())), t._2(), t._3().union(_argument._3()));
            };
            F<IRExp, P3<IRStmt, IRExp, Set<IRPVar>>> withStatementExp = e -> {
                return P.p(_argument._1(), e, _argument._3());
            };
            F<Uop, P3<IRStmt, IRExp, Set<IRPVar>>> negationHelper = uop -> {
                P3<IRStmt, IRExp, Set<IRPVar>> num = toNumber(_argument._2());
                return withStatement.f(P.p(num._1(), new IRUnop(uop, num._2()), num._3()));
            };
            switch (op) {
                case "void": {
                    return withStatementExp.f(new IRUndef());
                }
                case "typeof": {
                    return withStatementExp.f(new IRUnop(Uop.TypeOf, _argument._2()));
                }
                case "+": {
                    return withStatement.f(toNumber(_argument._2()));
                }
                case "-": {
                    return negationHelper.f(Uop.Negate);
                }
                case "~": {
                    return negationHelper.f(Uop.Not);
                }
                case "!": {
                    return withStatementExp.f(new IRUnop(Uop.LogicalNot, toBool(_argument._2())));
                }
                case "toObj": {
                    return withStatement.f(toObj(_argument._2()));
                }
                default:
                    throw new RuntimeException("unknown operator");
            }
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forUndefinedLiteral(UndefinedLiteral undefinedLiteral) {
            return nopStmt(new IRUndef());
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forUpdateExpression(UpdateExpression updateExpression) {
            return null;
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forVariableDeclaration(VariableDeclaration variableDeclaration) {
            throw new RuntimeException("ast2ast error");
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forVariableDeclarator(VariableDeclarator variableDeclarator) {
            throw new RuntimeException("ast2ast error");
        }

        @Override
        public P3<IRStmt, IRExp, Set<IRPVar>> forWithStatement(WithStatement withStatement) {
            throw new RuntimeException("we don't expect with");
        }
    }

    static P3<IRStmt, IRExp, Set<IRPVar>> valueOf(IRExp e) {
        return toSomething(e, x -> x, AST2IR::callValueOf);
    }

    static <T> List<T> interleave(List<T> l1, List<T> l2) {
        if (l1.isEmpty() && l2.isEmpty()) {
            return List.list();
        } else {
            assert l1.isNotEmpty() && l2.isNotEmpty();
            return List.cons(l1.head(), List.cons(l2.head(), interleave(l1.tail(), l2.tail())));
        }
    }

    static <A,B,C> P3<List<A>, List<B>, List<C>> unzip3(List<P3<A, B, C>> list) {
        if (list.isEmpty()) {
            return P.p(List.list(), List.list(), List.list());
        } else {
            P3<A, B, C> head = list.head();
            P3<List<A>, List<B>, List<C>> tail = unzip3(list.tail());
            return P.p(tail._1().cons(head._1()), tail._2().cons(head._2()), tail._3().cons(head._3()));
        }
    }

    static <A,B,C,D> P4<List<A>,List<B>,List<C>,List<D>> unzip4(List<P4<A,B,C,D>> list) {
        if (list.isEmpty()) {
            return P.p(List.list(), List.list(), List.list(), List.list());
        } else {
            P4<A,B,C,D> head = list.head();
            P4<List<A>,List<B>,List<C>,List<D>> tail = unzip4(list.tail());
            return P.p(tail._1().cons(head._1()), tail._2().cons(head._2()), tail._3().cons(head._3()), tail._4().cons(head._4()));
        }
    }

    static Bop JSBopToBop(String op) {
        switch (op) {
            case "-":
                return Bop.Minus;
            case "*":
                return Bop.Times;
            case "/":
                return Bop.Divide;
            case "%":
                return Bop.Mod;
            case "<<":
                return Bop.SHL;
            case ">>":
                return Bop.SAR;
            case ">>>":
                return Bop.SHR;
            case "&":
                return Bop.And;
            case "|":
                return Bop.Or;
            case "^":
                return Bop.Xor;
            default:
                throw new RuntimeException("unknown operator");
        }
    }

    static P4<IRStmt, IRExp, IRExp, Set<IRPVar>> accessSetup(IRExp obj, IRExp field) {
        P3<IRStmt, IRExp, Set<IRPVar>> _obj = toObj(obj);
        P3<IRStmt, IRExp, Set<IRPVar>> _field = toString(field);
        return P.p(new IRSeq(List.list(_obj._1(), _field._1())), _obj._2(), _field._2(), _obj._3().union(_field._3()));
    }

    static P3<IRStmt, IRExp, Set<IRPVar>> whileHelper(IRStmt beforeLoop, IRExp initialTest, IRExp normalTest, IRStmt body, IRStmt afterBody) {
        //IRScratch y = freshScratch();
        IRPVar y = freshPVar();
        return P.p(
                makeLbl(
                        ":BREAK:",
                        new IRSeq(List.list(
                                beforeLoop,
                                new IRAssign(y, toBool(initialTest)),
                                makeWhile(
                                        y,
                                        new IRSeq(List.list(
                                                makeLbl(":CONTINUE:", body),
                                                afterBody,
                                                new IRAssign(y, toBool(normalTest)))))))),
                new IRUndef(),
                Set.set(Ord.hashEqualsOrd(), y)
        );
    }

    static P3<IRStmt, IRExp, Set<IRPVar>> toObj(IRExp e) {
        if (e.equals(PVarMapper.window)) {
            return nopStmt(e);
        } else {
            //IRScratch y = freshScratch();
            IRPVar y = freshPVar();
            return P.p(new IRToObj(y, e), y, Set.set(Ord.hashEqualsOrd(), y));
        }
    }

    static P3<IRStmt, IRExp, Set<IRPVar>> nopStmt(IRExp e) {
        return P.p(PVarMapper.nopStmt, e, Set.empty(Ord.hashEqualsOrd()));
    }

    static P3<IRStmt, IRExp, Set<IRPVar>> toSomething(IRExp e, F<IRExp, IRExp> ifPrim, F2<IRVar, IRExp, P3<IRStmt, IRExp, Set<IRPVar>>> ifNotPrim) {
        F<Unit, P3<IRStmt, IRExp, Set<IRPVar>>> notPrim = (unit) -> {
            //IRScratch y = freshScratch();
            IRPVar y = freshPVar();
            P3<IRStmt, IRExp, Set<IRPVar>> tmp = ifNotPrim.f(y, e);
            return P.p(tmp._1(), y, tmp._3().insert(y));
        };
        Option<Boolean> chk = staticIsPrim(e);
        if (chk.isSome() && chk.some()) {
            return nopStmt(ifPrim.f(e));
        } else if (chk.isSome() && !chk.some()) {
            return notPrim.f(Unit.unit());
        } else {
            P3<IRStmt, IRExp, Set<IRPVar>> tmp = notPrim.f(Unit.unit());
            //IRScratch y = (IRScratch)tmp._2();
            IRPVar y = (IRPVar)tmp._2();
            return P.p(makeIf(new IRUnop(Uop.IsPrim, e), new IRAssign(y, ifPrim.f(e)), tmp._1()), y, tmp._3().insert(y));
        }
    }

    static P3<IRStmt, IRExp, Set<IRPVar>> makeArguments(List<IRExp> args) {
        //IRScratch y = freshScratch();
        IRPVar y = freshPVar();
        List<P2<IRExp, Integer>> tmp = args.zip(List.range(0, args.length()));
        List<IRStmt> _tmp = tmp.map(p -> new IRUpdate(y, new IRStr(p._2().toString()), p._1()));
        IRStmt _s = new IRSeq(List.list(
                makeNew(y, PVarMapper.argumentsVar, PVarMapper.dummyAddressVar),
                new IRSeq(_tmp),
                new IRUpdate(y, new IRStr("length"), new IRNum((double)args.length()))));
        return P.p(_s, y, Set.set(Ord.hashEqualsOrd(), y));
    }

    static P3<IRStmt, IRExp, Set<IRPVar>> call(IRVar target, IRExp toCall, IRExp self, List<IRExp> args) {
        P3<IRStmt, IRExp, Set<IRPVar>> tmp = makeArguments(args);
        return P.p(new IRSeq(List.list(tmp._1(), makeCall(target, toCall, self, tmp._2()))), new IRUndef(), tmp._3());
    }

    static P3<IRStmt, IRExp, Set<IRPVar>> callMethodByName(IRVar target, IRExp obj, String name, List<IRExp> args) {
        return call(target, new IRBinop(Bop.Access, obj, new IRStr(name)), obj, args);
    }

    static P3<IRStmt, IRExp, Set<IRPVar>> callToString(IRVar target, IRExp obj) {
        return callMethodByName(target, obj, "toString", List.list());
    }

    static P3<IRStmt, IRExp, Set<IRPVar>> callToNumber(IRVar target, IRExp arg) {
        return call(target, PVarMapper.numberVar, PVarMapper.global, List.list(arg));
    }

    static P3<IRStmt, IRExp, Set<IRPVar>> callValueOf(IRVar target, IRExp obj) {
        return callMethodByName(target, obj, "valueOf", List.list(new IRBool(true)));
    }

    static Option<Boolean> staticIsPrim(IRExp e) {
        return TypeInfer.typeof(e).isPrim();
    }

    static IRExp toBool(IRExp e) {
        TypeInfer.JSType type = TypeInfer.typeof(e);
        if (type.equals(TypeInfer.BoolType)) {
            return e;
        } else {
            return new IRUnop(Uop.ToBool, e);
        }
    }

    static IRExp isPrimToString(IRExp e) {
        TypeInfer.JSType type = TypeInfer.typeof(e);
        if (type.equals(TypeInfer.StrType)) {
            return e;
        } else {
            return new IRUnop(Uop.ToStr, e);
        }
    }

    static IRExp isPrimToNumber(IRExp e) {
        TypeInfer.JSType type = TypeInfer.typeof(e);
        if (type.equals(TypeInfer.NumType)) {
            return e;
        } else {
            return new IRUnop(Uop.ToNum, e);
        }
    }

    static P3<IRStmt, IRExp, Set<IRPVar>> toString(IRExp e) {
        return toSomething(e, AST2IR::isPrimToString, AST2IR::callToString);
    }

    static P3<IRStmt, IRExp, Set<IRPVar>> toNumber(IRExp e) {
        return toSomething(e, AST2IR::isPrimToNumber, AST2IR::callToNumber);
    }

    static IRStmt makeIf(IRExp e, IRStmt s1, IRStmt s2) {
        return new IRSeq(List.list(new IRIf(e, s1, s2), new IRMerge()));
    }

    static IRStmt makeLbl(String lbl, IRStmt s) {
        return new IRSeq(List.list(new IRLbl(lbl, s), new IRMerge()));
    }

    static IRStmt makeWhile(IRExp e, IRStmt s) {
        return new IRSeq(List.list(new IRWhile(e, new IRSeq(List.list(new IRMerge(), s))), new IRMerge()));
    }

    static IRStmt makeFor(IRVar x, IRExp e, IRStmt s) {
        return new IRSeq(List.list(new IRFor(x, e, new IRSeq(List.list(new IRMerge(), s))), new IRMerge()));
    }

    static IRStmt makeNew(IRVar x, IRExp e1, IRExp e2) {
        return new IRSeq(List.list(new IRNew(x, e1, e2), new IRMerge()));
    }

    static IRStmt makeCall(IRVar x, IRExp e1, IRExp e2, IRExp e3) {
        return new IRSeq(List.list(new IRCall(x, e1, e2, e3), new IRMerge()));
    }

    static Integer nextScratch = 0;

    /*static IRScratch freshScratch() {
        Integer res = nextScratch;
        nextScratch += 1;
        return new IRScratch(res);
    }*/

    static IRPVar freshPVar() {
        return PVarMapper.freshPVar();
    }

    public static IRStmt transform(Program ast) {
        assert PVarMapper.window.n == 0;
        P3<IRStmt, IRExp, Set<IRPVar>> tmp = ast.accept(new TranslateV());
        return preamble(tmp._1());
    }

    public static final List<P2<IRPVar, IRExp>> preambleBindings =
            List.list(
                    P.p(PVarMapper.dummy, new IRUndef()),
                    P.p(PVarMapper.argumentsVar, PVarMapper.windowAccess("Arguments")),
                    P.p(PVarMapper.dummyAddressVar, PVarMapper.windowAccess("dummyAddress")),
                    P.p(PVarMapper.numberVar, PVarMapper.windowAccess("Number")),
                    P.p(PVarMapper.objectVar, PVarMapper.windowAccess("Object")),
                    P.p(PVarMapper.unmangledWindow, PVarMapper.window)
            );

    public static IRStmt preamble(IRStmt s) {
        F2<List<P2<IRPVar, IRExp>>, IRStmt, IRStmt> make = (extra, rest) -> {
            return new IRDecl(
                    extra.append(preambleBindings),
                    new IRSeq(List.list(
                            new IRUpdate(PVarMapper.window, new IRStr("dummyAddress"), new IRUndef()),
                            new IRUpdate(PVarMapper.window, new IRStr("Arguments"), new IRUndef()),
                            rest)));
        };
        if (s instanceof IRDecl) {
            return make.f(((IRDecl) s).bind, ((IRDecl) s).s);
        } else {
            return make.f(List.list(), s);
        }
    }
}
