package ir;

/**
 * Created by wayne on 15/10/28.
 */
public interface IRExpVisitor {
    Object forNum(IRNum irNum);
    Object forBool(IRBool irBool);
    Object forStr(IRStr irStr);
    Object forUndef(IRUndef irUndef);
    Object forNull(IRNull irNull);
    Object forPVar(IRPVar irPVar);
    Object forScratch(IRScratch irScratch);
    Object forBinop(IRBinop irBinop);
    Object forUnop(IRUnop irUnop);
}
