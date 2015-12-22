package translator;

import fj.*;
import fj.data.List;
import fj.data.Set;
import fj.data.TreeMap;
import ir.*;

/**
 * Created by wayne on 15/11/12.
 */
public class IR2IR {

    public static class DefaultClonePassV implements SimpleTransformVisitor {
        @Override
        public IRExp forBinop(IRBinop irBinop) {
            Bop op = irBinop.op;
            IRExp e1 = irBinop.e1;
            IRExp e2 = irBinop.e2;
            return new IRBinop(op, e1.accept(this), e2.accept(this));
        }

        @Override
        public IRExp forBool(IRBool irBool) {
            return new IRBool(irBool.v);
        }

        @Override
        public IRExp forNull(IRNull irNull) {
            return new IRNull();
        }

        @Override
        public IRExp forNum(IRNum irNum) {
            return new IRNum(irNum.v);
        }

        @Override
        public IRExp forPVar(IRPVar irPVar) {
            return new IRPVar(irPVar.n, irPVar.loc);
        }

        @Override
        public IRExp forScratch(IRScratch irScratch) {
            return new IRScratch(irScratch.n);
        }

        @Override
        public IRExp forStr(IRStr irStr) {
            return new IRStr(irStr.v);
        }

        @Override
        public IRExp forUndef(IRUndef irUndef) {
            return new IRUndef();
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
            return new IRMerge();
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
            return new IRSDecl(irSDecl.num, irSDecl.s.accept(this));
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

        @Override
        public IRStmt forPrint(IRPrint irPrint) {
            IRExp e = irPrint.e;
            return new IRPrint(e.accept(this));
        }
    }

    public static class FlattenSequencesV extends DefaultClonePassV {
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

    public static class RemoveEmptyDeclsV extends DefaultClonePassV {
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

    public static class RemoveNopsV extends DefaultClonePassV {
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
        Integer counter;
        Integer maxScratchMade;
        TreeMap<Integer, Integer> mapping;

        static final TreeMap<Integer, Integer> EMPTY = TreeMap.empty(Ord.intOrd);

        public InsertSDeclsV() {
            this.counter = 0;
            this.maxScratchMade = 0;
            this.mapping = EMPTY;
        }

        public InsertSDeclsV(Integer counter, Integer maxScratchMade, TreeMap<Integer, Integer> mapping) {
            this.counter = counter;
            this.maxScratchMade = maxScratchMade;
            this.mapping = mapping;
        }

        public InsertSDeclsV(P3<Integer, Integer, TreeMap<Integer, Integer>> p) {
            this.counter = p._1();
            this.maxScratchMade = p._2();
            this.mapping = p._3();
        }

        @Override
        public P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> forBinop(IRBinop irBinop) {
            Bop op = irBinop.op;
            IRExp e1 = irBinop.e1;
            IRExp e2 = irBinop.e2;
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _e1 = e1.accept(this);
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _e2 = e2.accept(new InsertSDeclsV(_e1._2()));
            return P.p(new IRBinop(op, _e1._1(), _e2._1()), _e2._2());
        }

        @Override
        public P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> forBool(IRBool irBool) {
            return P.p(irBool, P.p(counter, maxScratchMade, mapping));
        }

        @Override
        public P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> forNull(IRNull irNull) {
            return P.p(irNull, P.p(counter, maxScratchMade, mapping));
        }

        @Override
        public P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> forNum(IRNum irNum) {
            return P.p(irNum, P.p(counter, maxScratchMade, mapping));
        }

        @Override
        public P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> forPVar(IRPVar irPVar) {
            return P.p(irPVar, P.p(counter, maxScratchMade, mapping));
        }

        @Override
        public P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> forScratch(IRScratch irScratch) {
            Integer n = irScratch.n;
            if (mapping.contains(n)) {
                return P.p(new IRScratch(mapping.get(n).some()), P.p(counter, maxScratchMade, mapping));
            } else {
                Integer _counter = counter + 1;
                return P.p(new IRScratch(counter), P.p(_counter, Math.max(maxScratchMade, _counter), mapping.set(n, counter)));
            }
        }

        @Override
        public P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> forStr(IRStr irStr) {
            return P.p(irStr, P.p(counter, maxScratchMade, mapping));
        }

        @Override
        public P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> forUndef(IRUndef irUndef) {
            return P.p(irUndef, P.p(counter, maxScratchMade, mapping));
        }

        @Override
        public P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> forUnop(IRUnop irUnop) {
            Uop op = irUnop.op;
            IRExp e = irUnop.e;
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _e = e.accept(this);
            return P.p(new IRUnop(op, _e._1()), _e._2());
        }

        @Override
        public P2<IRMethod, P3<Integer, Integer, TreeMap<Integer, Integer>>> forMethod(IRMethod irMethod) {
            IRPVar self = irMethod.self;
            IRPVar args = irMethod.args;
            IRStmt s = irMethod.s;
            if (s instanceof IRDecl) {
                List<P2<IRPVar, IRExp>> bind = ((IRDecl) s).bind;
                IRStmt body = ((IRDecl) s).s;
                P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> _body = body.accept(new InsertSDeclsV());
                IRStmt _s = new IRDecl(bind, new IRSDecl(_body._2()._2(), _body._1()));
                return P.p(new IRMethod(self, args, _s), P.p(counter, maxScratchMade, mapping));
            } else {
                throw new RuntimeException("fake method");
            }
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forAssign(IRAssign irAssign) {
            IRVar x = irAssign.x;
            IRExp e = irAssign.e;
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _x = x.accept(this);
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _e = e.accept(new InsertSDeclsV(_x._2()));
            return P.p(new IRAssign((IRVar)_x._1(), _e._1()), _e._2());
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forCall(IRCall irCall) {
            IRVar x = irCall.x;
            IRExp e1 = irCall.e1, e2 = irCall.e2, e3 = irCall.e3;
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _x = x.accept(this);
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _e1 = e1.accept(new InsertSDeclsV(_x._2()));
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _e2 = e2.accept(new InsertSDeclsV(_e1._2()));
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _e3 = e3.accept(new InsertSDeclsV(_e2._2()));
            return P.p(new IRCall((IRVar)_x._1(), _e1._1(), _e2._1(), _e3._1()), _e3._2());
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forDecl(IRDecl irDecl) {
            List<P2<IRPVar, IRExp>> bind = irDecl.bind;
            IRStmt s = irDecl.s;
            if (bind.equals(AST2IR.PVarMapper.indicateSequence)) {
                List<IRStmt> ss;
                if (s instanceof IRSeq) {
                    ss = ((IRSeq) s).ss;
                } else {
                    ss = List.list(s);
                }
                P3<Integer, TreeMap<Integer, Integer>, List<IRStmt>> tmp =
                ss.foldLeft((res, cur) -> {
                    Integer curNumScratch = res._1();
                    TreeMap<Integer, Integer> curMapping = res._2();
                    List<IRStmt> curStmts = res._3();
                    P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> _cur = cur.accept(new InsertSDeclsV(0, curNumScratch, curMapping));
                    return P.p(_cur._2()._2(), _cur._2()._3(), curStmts.snoc(_cur._1()));
                }, P.p(maxScratchMade, mapping, List.<IRStmt>list()));
                return P.p(new IRSeq(tmp._3()), P.p(0, tmp._1(), tmp._2()));
            } else {
                P4<List<P2<IRPVar, IRExp>>, Integer, Integer, TreeMap<Integer, Integer>> _bind =
                bind.foldLeft((acc, cur) -> {
                    IRPVar x = cur._1();
                    IRExp e = cur._2();
                    P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _e = e.accept(new InsertSDeclsV(acc._2(), acc._3(), acc._4()));
                    return P.p(acc._1().snoc(P.p(x, _e._1())), _e._2()._1(), _e._2()._2(), _e._2()._3());
                }, P.p(List.list(), counter, maxScratchMade, mapping));
                P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> _s = s.accept(new InsertSDeclsV(_bind._2(), _bind._3(), _bind._4()));
                return P.p(new IRDecl(_bind._1(), _s._1()), _s._2());
            }
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forDel(IRDel irDel) {
            IRScratch x = irDel.x;
            IRExp e1 = irDel.e1, e2 = irDel.e2;
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _x = x.accept(this);
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _e1 = e1.accept(new InsertSDeclsV(_x._2()));
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _e2 = e2.accept(new InsertSDeclsV(_e1._2()));
            return P.p(new IRDel((IRScratch)_x._1(), _e1._1(), _e2._1()), _e2._2());
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forFor(IRFor irFor) {
            IRVar x = irFor.x;
            IRExp e = irFor.e;
            IRStmt s = irFor.s;
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _x = x.accept(this);
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _e = e.accept(new InsertSDeclsV(_x._2()));
            P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> _s = s.accept(new InsertSDeclsV(_e._2()));
            return P.p(new IRFor((IRVar)_x._1(), _e._1(), _s._1()), _s._2());
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forIf(IRIf irIf) {
            IRExp e = irIf.e;
            IRStmt s1 = irIf.s1, s2 = irIf.s2;
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _e = e.accept(this);
            P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> _s1 = s1.accept(new InsertSDeclsV(_e._2()));
            P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> _s2 = s2.accept(new InsertSDeclsV(_s1._2()));
            return P.p(new IRIf(_e._1(), _s1._1(), _s2._1()), _s2._2());
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forJump(IRJump irJump) {
            String lbl = irJump.lbl;
            IRExp e = irJump.e;
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _e = e.accept(this);
            return P.p(new IRJump(lbl, _e._1()), _e._2());
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forLbl(IRLbl irLbl) {
            String lbl = irLbl.lbl;
            IRStmt s = irLbl.s;
            P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> _s = s.accept(this);
            return P.p(new IRLbl(lbl, _s._1()), _s._2());
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forMerge(IRMerge irMerge) {
            return P.p(irMerge, P.p(counter, maxScratchMade, mapping));
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forNew(IRNew irNew) {
            IRVar x = irNew.x;
            IRExp e1 = irNew.e1, e2 = irNew.e2;
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _x = x.accept(this);
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _e1 = e1.accept(new InsertSDeclsV(_x._2()));
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _e2 = e2.accept(new InsertSDeclsV(_e1._2()));
            return P.p(new IRNew((IRVar)_x._1(), _e1._1(), _e2._1()), _e2._2());
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forNewfun(IRNewfun irNewfun) {
            IRVar x = irNewfun.x;
            IRMethod m = irNewfun.m;
            IRNum n = irNewfun.n;
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _x = x.accept(this);
            P2<IRMethod, P3<Integer, Integer, TreeMap<Integer, Integer>>> _m = m.accept(new InsertSDeclsV(_x._2()));
            return P.p(new IRNewfun((IRVar)_x._1(), _m._1(), n), _m._2());
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forSDecl(IRSDecl irSDecl) {
            throw new RuntimeException("ast2ir error");
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forSeq(IRSeq irSeq) {
            List<IRStmt> ss = irSeq.ss;
            P2<List<IRStmt>, P3<Integer, Integer, TreeMap<Integer, Integer>>> tmp =
            ss.foldLeft((acc, cur) -> {
                P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> _cur = cur.accept(new InsertSDeclsV(acc._2()));
                return P.p(acc._1().snoc(_cur._1()), _cur._2());
            }, P.p(List.list(), P.p(counter, maxScratchMade, mapping)));
            return P.p(new IRSeq(tmp._1()), tmp._2());
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forThrow(IRThrow irThrow) {
            IRExp e = irThrow.e;
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _e = e.accept(this);
            return P.p(new IRThrow(_e._1()), _e._2());
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forToObj(IRToObj irToObj) {
            IRVar x = irToObj.x;
            IRExp e = irToObj.e;
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _x = x.accept(this);
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _e = e.accept(new InsertSDeclsV(_x._2()));
            return P.p(new IRToObj((IRVar)_x._1(), _e._1()), _e._2());
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forTry(IRTry irTry) {
            IRStmt s1 = irTry.s1;
            IRStmt s2 = irTry.s2;
            IRStmt s3 = irTry.s3;
            IRPVar x = irTry.x;
            P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> _s1 = s1.accept(this);
            P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> _s2 = s2.accept(new InsertSDeclsV(_s1._2()));
            P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> _s3 = s3.accept(new InsertSDeclsV(_s2._2()));
            return P.p(new IRTry(_s1._1(), x, _s2._1(), _s3._1()), _s3._2());
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forUpdate(IRUpdate irUpdate) {
            IRExp e1 = irUpdate.e1, e2 = irUpdate.e2, e3 = irUpdate.e3;
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _e1 = e1.accept(this);
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _e2 = e2.accept(new InsertSDeclsV(_e1._2()));
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _e3 = e3.accept(new InsertSDeclsV(_e2._2()));
            return P.p(new IRUpdate(_e1._1(), _e2._1(), _e3._1()), _e3._2());
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forWhile(IRWhile irWhile) {
            IRExp e = irWhile.e;
            IRStmt s = irWhile.s;
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _e = e.accept(this);
            P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> _s = s.accept(new InsertSDeclsV(_e._2()));
            return P.p(new IRWhile(_e._1(), _s._1()), _s._2());
        }

        @Override
        public P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> forPrint(IRPrint irPrint) {
            IRExp e = irPrint.e;
            P2<IRExp, P3<Integer, Integer, TreeMap<Integer, Integer>>> _e = e.accept(this);
            return P.p(new IRPrint(_e._1()), _e._2());
        }
    }

    public static class RemoveRedundantMergeV extends DefaultClonePassV {
        @Override
        public IRStmt forSeq(IRSeq irSeq) {
            List<IRStmt> ss = irSeq.ss.map(s -> s.accept(this));
            if (ss.isNotEmpty()) {
                List<IRStmt> _ss = ss.foldRight((cur, res) -> {
                    if (cur instanceof IRMerge && res.isNotEmpty() && res.head() instanceof IRMerge) {
                        return res;
                    } else {
                        return res.cons(cur);
                    }
                }, List.list());
                return new IRSeq(_ss);
            } else {
                return irSeq;
            }
        }
    }

    public static IRStmt transform(IRStmt stmt) {
        P2<IRStmt, P3<Integer, Integer, TreeMap<Integer, Integer>>> tmp = stmt.accept(new InsertSDeclsV());
        if (tmp._1() instanceof IRDecl) {
            stmt = new IRDecl(((IRDecl) tmp._1()).bind, new IRSDecl(tmp._2()._2(), ((IRDecl) tmp._1()).s));
        } else {
            throw new RuntimeException("ir2ir error");
        }
        stmt = stmt.accept(new FlattenSequencesV());
        stmt = stmt.accept(new RemoveEmptyDeclsV());
        stmt = stmt.accept(new RemoveNopsV());
        stmt = stmt.accept(new RemoveRedundantMergeV());
        stmt = stmt.accept(new DefaultClonePassV());
        return stmt;
    }
}
