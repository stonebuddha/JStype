package ast;

/**
 * Created by wayne on 15/10/15.
 */
public class Property extends Node {
    Object key;
    Expression value;
    String kind;
    public Property(Object key, Expression value, String kind) {
        this.key = key;
        this.value = value;
        this.kind = kind;
    }
    Object accept(NodeVisitor ask) {
        return ask.forProperty(key, value, kind);
    }
}
