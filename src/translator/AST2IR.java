package translator;

import ast.*;
import fj.P;
import fj.P3;
import fj.data.Set;
import ir.IRExp;
import ir.IRScratch;
import ir.IRStmt;
import ir.IRVar;

/**
 * Created by wayne on 15/11/11.
 */
public class AST2IR {

    interface NodeVisitor<T> extends ProgramVisitor<T>, StatementVisitor<T>, ExpressionVisitor<T>, CatchClauseVisitor<T>, SwitchCaseVisitor<T>, VariableDeclaratorVisitor<T>, LiteralVisitor<T>, PropertyVisitor<T> {}

    /*public static class TranslateV implements NodeVisitor<P3<IRStmt, IRExp, Set<IRVar>>> {
        @Override
        public P3<IRStmt, IRExp, Set<IRVar>> forWhileStatement(WhileStatement whileStatement) {
            Expression test = whileStatement.getTest();
            Statement body = whileStatement.getBody();
            P3<IRStmt, IRExp, Set<IRVar>> _test = test.accept(this), _body = body.accept(this);
            P3<IRStmt, IRExp, Set<IRVar>> tmp = whileHelper(_test._1(), _test._2(), _test._2(), _body._1(), _test._1());
            return P.p(tmp._1(), tmp._2(), _test._3().union(_body._3()).union(tmp._3()));
        }
    }*/

    static P3<IRStmt, IRExp, Set<IRVar>> whileHelper(IRStmt beforeLoop, IRExp initialTest, IRExp normalTest, IRStmt body, IRStmt afterBody) {
        return null;
    }
}
