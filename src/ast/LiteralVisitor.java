package ast;

/**
 * Created by wayne on 15/10/16.
 */
public interface LiteralVisitor<T> {
    T forBooleanLiteral(BooleanLiteral booleanLiteral);
    T forNullLiteral(NullLiteral nullLiteral);
    T forNumberLiteral(NumberLiteral numberLiteral);
    T forRegExpLiteral(RegExpLiteral regExpLiteral);
    T forStringLiteral(StringLiteral stringLiteral);
    T forUndefinedLiteral(UndefinedLiteral undefinedLiteral);
}
