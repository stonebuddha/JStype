package translator;

import fj.P;
import fj.P2;
import fj.P3;
import fj.data.List;
import fj.data.TreeMap;
import ir.*;

/**
 * Created by wayne on 15/11/12.
 */
public class IR2IR {

    public static class DefaultPassV implements SimpleTransformVisitor {
        @Override
        public IRExp forBinop(IRBinop irBinop) {
            Bop op = irBinop.op;
            IRExp e1 = irBinop.e1;
            IRExp e2 = irBinop.e2;
            return new IRBinop(op, e1.accept(this), e2.accept(this));
        }

        @Override
        public IRExp forBool(IRBool irBool) {
            return irBool;
        }

        @Override
        public IRExp forNull(IRNull irNull) {
            return irNull;
        }

        @Override
        public IRExp forNum(IRNum irNum) {
            return irNum;
        }

        @Override
        public IRExp forPVar(IRPVar irPVar) {
            return irPVar;
        }

        @Override
        public IRExp forScratch(IRScratch irScratch) {
            return irScratch;
        }

        @Override
        public IRExp forStr(IRStr irStr) {
            return irStr;
        }

        @Override
        public IRExp forUndef(IRUndef irUndef) {
            return irUndef;
        }

        @Override
        public IRExp forUnop(IRUnop irUnop) {
            Uop op = irUnop.op;
            IRExp e = irUnop.e;
            return new IRUnop(op, e.accept(this));
        }

        @Override
        public IRMethod forMethod(IRMethod irMethod) {
            IRPVar self = irMethod.self, args = irMethod.args;
            IRStmt s = irMethod.s;
            return new IRMethod((IRPVar)self.accept(this), (IRPVar)args.accept(this), s.accept(this));
        }

        @Override
        public IRStmt forAssign(IRAssign irAssign) {
            IRVar x = irAssign.x;
            IRExp e = irAssign.e;
            return new IRAssign((IRVar)x.accept(this), e.accept(this));
        }

        @Override
        public IRStmt forCall(IRCall irCall) {
            IRVar x = irCall.x;
            IRExp e1 = irCall.e1, e2 = irCall.e2, e3 = irCall.e3;
            return new IRCall((IRVar)x.accept(this), e1.accept(this), e2.accept(this), e3.accept(this));
        }

        @Override
        public IRStmt forDecl(IRDecl irDecl) {
            List<P2<IRPVar, IRExp>> bind = irDecl.bind;
            IRStmt s = irDecl.s;
            List<P2<IRPVar, IRExp>> _bind = bind.map(p -> P.p((IRPVar)p._1().accept(this), p._2().accept(this)));
            return new IRDecl(_bind, s.accept(this));
        }

        @Override
        public IRStmt forDel(IRDel irDel) {
            IRScratch x = irDel.x;
            IRExp e1 = irDel.e1;
            IRExp e2 = irDel.e2;
            return new IRDel((IRScratch)x.accept(this), e1.accept(this), e2.accept(this));
        }

        @Override
        public IRStmt forFor(IRFor irFor) {
            IRVar x = irFor.x;
            IRExp e = irFor.e;
            IRStmt s = irFor.s;
            return new IRFor((IRVar)x.accept(this), e.accept(this), s.accept(this));
        }

        @Override
        public IRStmt forIf(IRIf irIf) {
            IRExp e = irIf.e;
            IRStmt s1 = irIf.s1;
            IRStmt s2 = irIf.s2;
            return new IRIf(e.accept(this), s1.accept(this), s2.accept(this));
        }

        @Override
        public IRStmt forJump(IRJump irJump) {
            String lbl = irJump.lbl;
            IRExp e = irJump.e;
            return new IRJump(lbl, e.accept(this));
        }

        @Override
        public IRStmt forLbl(IRLbl irLbl) {
            String lbl = irLbl.lbl;
            IRStmt s = irLbl.s;
            return new IRLbl(lbl, s.accept(this));
        }

        @Override
        public IRStmt forMerge(IRMerge irMerge) {
            return irMerge;
        }

        @Override
        public IRStmt forNew(IRNew irNew) {
            IRVar x = irNew.x;
            IRExp e1 = irNew.e1;
            IRExp e2 = irNew.e2;
            return new IRNew((IRVar)x.accept(this), e1.accept(this), e2.accept(this));
        }

        @Override
        public IRStmt forNewfun(IRNewfun irNewfun) {
            IRVar x = irNewfun.x;
            IRMethod m = irNewfun.m;
            IRNum n = irNewfun.n;
            return new IRNewfun((IRVar)x.accept(this), m.accept(this), (IRNum)n.accept(this));
        }

        @Override
        public IRStmt forSDecl(IRSDecl irSDecl) {
            return irSDecl;
        }

        @Override
        public IRStmt forSeq(IRSeq irSeq) {
            List<IRStmt> ss = irSeq.ss;
            return new IRSeq(ss.map(stmt -> stmt.accept(this)));
        }

        @Override
        public IRStmt forThrow(IRThrow irThrow) {
            IRExp e = irThrow.e;
            return new IRThrow(e.accept(this));
        }

        @Override
        public IRStmt forToObj(IRToObj irToObj) {
            IRVar x = irToObj.x;
            IRExp e = irToObj.e;
            return new IRToObj((IRVar)x.accept(this), e.accept(this));
        }

        @Override
        public IRStmt forTry(IRTry irTry) {
            IRStmt s1 = irTry.s1;
            IRPVar x = irTry.x;
            IRStmt s2 = irTry.s2;
            IRStmt s3 = irTry.s3;
            return new IRTry(s1.accept(this), (IRPVar)x.accept(this), s2.accept(this), s3.accept(this));
        }

        @Override
        public IRStmt forUpdate(IRUpdate irUpdate) {
            IRExp e1 = irUpdate.e1;
            IRExp e2 = irUpdate.e2;
            IRExp e3 = irUpdate.e3;
            return new IRUpdate(e1.accept(this), e2.accept(this), e3.accept(this));
        }

        @Override
        public IRStmt forWhile(IRWhile irWhile) {
            IRExp e = irWhile.e;
            IRStmt s = irWhile.s;
            return new IRWhile(e.accept(this), s.accept(this));
        }
    }

    public static class FlattenSequencesV extends DefaultPassV {
        @Override
        public IRStmt forSeq(IRSeq irSeq) {
            List<IRStmt> ss = irSeq.ss;
            List<IRStmt> _ss = ss.map(s -> s.accept(this)).foldLeft((acc, stmt) -> {
                if (stmt instanceof IRSeq) {
                    return acc.append(((IRSeq)stmt).ss);
                } else {
                    return acc.snoc(stmt);
                }
            }, List.list());
            if (_ss.isEmpty()) {
                return AST2IR.PVarMapper.nopStmt;
            } else if (_ss.length() == 1) {
                return _ss.head();
            } else {
                return new IRSeq(_ss);
            }
        }
    }

    public static class RemoveEmptyDeclsV extends DefaultPassV {
        @Override
        public IRStmt forDecl(IRDecl irDecl) {
            List<P2<IRPVar, IRExp>> bind = irDecl.bind;
            IRStmt s = irDecl.s;
            if (bind.isEmpty()) {
                return s;
            } else {
                return irDecl;
            }
        }
    }

    public static class RemoveNopsV extends DefaultPassV {
        @Override
        public IRStmt forSeq(IRSeq irSeq) {
            List<IRStmt> ss = irSeq.ss;
            List<IRStmt> _ss = ss.map(s -> s.accept(this)).filter(s -> !s.equals(AST2IR.PVarMapper.nopStmt));
            if (_ss.isEmpty()) {
                return AST2IR.PVarMapper.nopStmt;
            } else if (_ss.length() == 1) {
                return _ss.head();
            } else {
                return new IRSeq(_ss);
            }
        }
    }

    public static class InsertSDeclsV implements TransformVisitor<P3<Integer, Integer, TreeMap<Integer, Integer>>> {
        @Override
        public P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> forBinop(IRBinop irBinop) {
            return null;
        }

        @Override
        public P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> forBool(IRBool irBool) {
            return null;
        }

        @Override
        public P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> forNull(IRNull irNull) {
            return null;
        }

        @Override
        public P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> forNum(IRNum irNum) {
            return null;
        }

        @Override
        public P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> forPVar(IRPVar irPVar) {
            return null;
        }

        @Override
        public P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> forScratch(IRScratch irScratch) {
            return null;
        }

        @Override
        public P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> forStr(IRStr irStr) {
            return null;
        }

        @Override
        public P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> forUndef(IRUndef irUndef) {
            return null;
        }

        @Override
        public P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> forUnop(IRUnop irUnop) {
            return null;
        }

        @Override
        public P2<IRMethod, P3<Integer, Integer, TreeMap<Integer, Integer>>> forMethod(IRMethod irMethod) {
            return null;
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forAssign(IRAssign irAssign) {
            return null;
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forCall(IRCall irCall) {
            return null;
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forDecl(IRDecl irDecl) {
            return null;
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forDel(IRDel irDel) {
            return null;
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forFor(IRFor irFor) {
            return null;
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forIf(IRIf irIf) {
            return null;
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forJump(IRJump irJump) {
            return null;
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forLbl(IRLbl irLbl) {
            return null;
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forMerge(IRMerge irMerge) {
            return null;
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forNew(IRNew irNew) {
            return null;
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forNewfun(IRNewfun irNewfun) {
            return null;
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forSDecl(IRSDecl irSDecl) {
            return null;
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forSeq(IRSeq irSeq) {
            return null;
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forThrow(IRThrow irThrow) {
            return null;
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forToObj(IRToObj irToObj) {
            return null;
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forTry(IRTry irTry) {
            return null;
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forUpdate(IRUpdate irUpdate) {
            return null;
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forWhile(IRWhile irWhile) {
            return null;
        }
    }

    public static IRStmt transform(IRStmt stmt) {
        stmt = stmt.accept(new InsertSDeclsV())._1();
        stmt = stmt.accept(new FlattenSequencesV());
        stmt = stmt.accept(new RemoveEmptyDeclsV());
        stmt = stmt.accept(new RemoveNopsV());
        return stmt;
    }
}
