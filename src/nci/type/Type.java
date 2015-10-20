package nci.type;

/**
 * Created by wayne on 15/10/20.
 */
public abstract class Type {
    public abstract Type merge(String operator, Type aType);
}
