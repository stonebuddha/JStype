package ast;

import java.util.ArrayList;

/**
 * Created by wayne on 15/10/15.
 */
public class SequenceExpression extends Expression {
    ArrayList<Expression> expressions;
    public SequenceExpression(ArrayList<Expression> expressions) {
        this.expressions = expressions;
    }
    Object accept(NodeVisitor ask) {
        return ask.forSequenceExpression(expressions);
    }
}
