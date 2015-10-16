package ast;

/**
 * Created by wayne on 15/10/15.
 */
public class ThisExpression extends Expression {
    Object accept(NodeVisitor ask) {
        return ask.forThisExpression();
    }
}
