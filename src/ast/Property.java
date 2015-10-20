package ast;

/**
 * Created by wayne on 15/10/15.
 */
public class Property extends Node {
    Node key;
    Expression value;
    String kind;
    public Property(Node key, Expression value, String kind) {
        this.key = key;
        this.value = value;
        this.kind = kind;
    }
    public Object accept(PropertyVisitor ask) {
        return ask.forProperty(key, value, kind);
    }
}
