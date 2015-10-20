package ast;

/**
 * Created by wayne on 15/10/16.
 */
public interface PropertyVisitor {
    Object forProperty(Node key, Expression value, String kind);
}
