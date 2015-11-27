package ir;

/**
 * Created by wayne on 15/11/13.
 */
public interface SimpleTransformVisitor {
    IRStmt forDecl(IRDecl irDecl);
    IRStmt forSDecl(IRSDecl irSDecl);
    IRStmt forSeq(IRSeq irSeq);
    IRStmt forIf(IRIf irIf);
    IRStmt forWhile(IRWhile irWhile);
    IRStmt forAssign(IRAssign irAssign);
    IRStmt forCall(IRCall irCall);
    IRStmt forNew(IRNew irNew);
    IRStmt forNewfun(IRNewfun irNewfun);
    IRStmt forToObj(IRToObj irToObj);
    IRStmt forDel(IRDel irDel);
    IRStmt forUpdate(IRUpdate irUpdate);
    IRStmt forThrow(IRThrow irThrow);
    IRStmt forTry(IRTry irTry);
    IRStmt forLbl(IRLbl irLbl);
    IRStmt forJump(IRJump irJump);
    IRStmt forFor(IRFor irFor);
    IRStmt forMerge(IRMerge irMerge);
    IRStmt forPrint(IRPrint irPrint);

    IRExp forNum(IRNum irNum);
    IRExp forBool(IRBool irBool);
    IRExp forStr(IRStr irStr);
    IRExp forUndef(IRUndef irUndef);
    IRExp forNull(IRNull irNull);
    IRExp forPVar(IRPVar irPVar);
    IRExp forScratch(IRScratch irScratch);
    IRExp forBinop(IRBinop irBinop);
    IRExp forUnop(IRUnop irUnop);

    IRMethod forMethod(IRMethod irMethod);
}
