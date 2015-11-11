package ast;

import fj.P2;

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

    public Node getKey() {
        return key;
    }
    public Expression getValue() {
        return value;
    }
    public String getKind() {
        return kind;
    }

    public <T> T accept(PropertyVisitor<T> ask) {
        return ask.forProperty(this);
    }
    public <T> P2<Property, T> accept(TransformVisitor<T> ask) {
        return ask.forProperty(this);
    }
    public Property accept(SimpleTransformVisitor ask) {
        return ask.forProperty(this);
    }
}
