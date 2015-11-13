package ir;

import fj.P2;

/**
 * Created by wayne on 15/11/13.
 */
public interface TransformVisitor<T> {
    P2<IRStmt, T> forDecl(IRDecl irDecl);
    P2<IRStmt, T> forSDecl(IRSDecl irSDecl);
    P2<IRStmt, T> forSeq(IRSeq irSeq);
    P2<IRStmt, T> forIf(IRIf irIf);
    P2<IRStmt, T> forWhile(IRWhile irWhile);
    P2<IRStmt, T> forAssign(IRAssign irAssign);
    P2<IRStmt, T> forCall(IRCall irCall);
    P2<IRStmt, T> forNew(IRNew irNew);
    P2<IRStmt, T> forNewfun(IRNewfun irNewfun);
    P2<IRStmt, T> forToObj(IRToObj irToObj);
    P2<IRStmt, T> forDel(IRDel irDel);
    P2<IRStmt, T> forUpdate(IRUpdate irUpdate);
    P2<IRStmt, T> forThrow(IRThrow irThrow);
    P2<IRStmt, T> forTry(IRTry irTry);
    P2<IRStmt, T> forLbl(IRLbl irLbl);
    P2<IRStmt, T> forJump(IRJump irJump);
    P2<IRStmt, T> forFor(IRFor irFor);
    P2<IRStmt, T> forMerge(IRMerge irMerge);

    P2<IRExp, T> forNum(IRNum irNum);
    P2<IRExp, T> forBool(IRBool irBool);
    P2<IRExp, T> forStr(IRStr irStr);
    P2<IRExp, T> forUndef(IRUndef irUndef);
    P2<IRExp, T> forNull(IRNull irNull);
    P2<IRExp, T> forPVar(IRPVar irPVar);
    P2<IRExp, T> forScratch(IRScratch irScratch);
    P2<IRExp, T> forBinop(IRBinop irBinop);
    P2<IRExp, T> forUnop(IRUnop irUnop);

    P2<IRMethod, T> forMethod(IRMethod irMethod);
}
