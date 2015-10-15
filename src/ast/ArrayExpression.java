package ast;

import java.util.ArrayList;

/**
 * Created by wayne on 15/10/15.
 */
public class ArrayExpression extends Expression {
    ArrayList<Expression> elements;
    public ArrayExpression(ArrayList<Expression> elements) {
        this.elements = elements;
    }
}
