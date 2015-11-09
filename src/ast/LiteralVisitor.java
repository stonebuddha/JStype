package ast;

/**
 * Created by wayne on 15/10/16.
 */
public interface LiteralVisitor {
    Object forBooleanLiteral(BooleanLiteral booleanLiteral);
    Object forNullLiteral(NullLiteral nullLiteral);
    Object forNumberLiteral(NumberLiteral numberLiteral);
    Object forRegExpLiteral(RegExpLiteral regExpLiteral);
    Object forStringLiteral(StringLiteral stringLiteral);
    Object forUndefinedLiteral(UndefinedLiteral undefinedLiteral);
}
