package ast;

/**
 * Created by wayne on 15/10/16.
 */
public interface PropertyVisitor<T> {
    T forProperty(Property property);
}
