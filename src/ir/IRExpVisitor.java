package ir;

/**
 * Created by wayne on 15/10/28.
 */
public interface IRExpVisitor<T> {
    T forNum(IRNum irNum);
    T forBool(IRBool irBool);
    T forStr(IRStr irStr);
    T forUndef(IRUndef irUndef);
    T forNull(IRNull irNull);
    T forPVar(IRPVar irPVar);
    T forScratch(IRScratch irScratch);
    T forBinop(IRBinop irBinop);
    T forUnop(IRUnop irUnop);
}
