package ir;

/**
 * Created by wayne on 15/10/29.
 */
public interface IRStmtVisitor<T> {
    T forDecl(IRDecl irDecl);
    T forSDecl(IRSDecl irSDecl);
    T forSeq(IRSeq irSeq);
    T forIf(IRIf irIf);
    T forWhile(IRWhile irWhile);
    T forAssign(IRAssign irAssign);
    T forCall(IRCall irCall);
    T forNew(IRNew irNew);
    T forNewfun(IRNewfun irNewfun);
    T forToObj(IRToObj irToObj);
    T forDel(IRDel irDel);
    T forUpdate(IRUpdate irUpdate);
    T forThrow(IRThrow irThrow);
    T forTry(IRTry irTry);
    T forLbl(IRLbl irLbl);
    T forJump(IRJump irJump);
    T forFor(IRFor irFor);
    T forMerge(IRMerge irMerge);
}
