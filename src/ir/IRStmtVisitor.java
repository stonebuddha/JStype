package ir;

/**
 * Created by wayne on 15/10/29.
 */
public interface IRStmtVisitor {
    Object forDecl(IRDecl irDecl);
    Object forSDecl(IRSDecl irSDecl);
    Object forSeq(IRSeq irSeq);
    Object forIf(IRIf irIf);
    Object forWhile(IRWhile irWhile);
    Object forAssign(IRAssign irAssign);
    Object forCall(IRCall irCall);
    Object forNew(IRNew irNew);
    Object forNewfun(IRNewfun irNewfun);
    Object forToObj(IRToObj irToObj);
    Object forDel(IRDel irDel);
    Object forUpdate(IRUpdate irUpdate);
    Object forThrow(IRThrow irThrow);
    Object forTry(IRTry irTry);
    Object forLbl(IRLbl irLbl);
    Object forJump(IRJump irJump);
    Object forFor(IRFor irFor);
    Object forMerge(IRMerge irMerge);
}
