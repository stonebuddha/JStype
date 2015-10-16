package ast;

/**
 * Created by wayne on 15/10/16.
 */
public interface LiteralVisitor {
    Object forBooleanLiteral(boolean value);
    Object forNullLiteral();
    Object forNumberLiteral(Number value);
    Object forRegExpLiteral(String pattern, String flags);
    Object forStringLiteral(String value);
}
