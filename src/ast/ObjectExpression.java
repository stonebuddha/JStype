package ast;

import java.util.ArrayList;

/**
 * Created by wayne on 15/10/15.
 */
public class ObjectExpression extends Expression {
    ArrayList<Property> properties;
    public ObjectExpression(ArrayList<Property> properties) {
        this.properties = properties;
    }
}
