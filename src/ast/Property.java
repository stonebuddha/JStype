package ast;

import fj.P2;
import fj.data.Option;

/**
 * Created by wayne on 15/10/15.
 */
public class Property extends Node {
    String key;
    Expression value;
    String kind;

    public Property(String key, Expression value, String kind) {
        this.key = key;
        this.value = value;
        this.kind = kind;
    }

    public Property(String key, Expression value, String kind, Option<Location> loc) {
        this.key = key;
        this.value = value;
        this.kind = kind;
        this.loc = loc;
    }

    public String getKey() {
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
