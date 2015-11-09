package ast;

/**
 * Created by wayne on 15/11/9.
 */
public class ScratchIdentifierExpression extends IdentifierExpression {
    Integer num;

    public ScratchIdentifierExpression(Integer num) {
        this.num = num;
    }

    public Integer getNum() {
        return num;
    }

    @Override
    public Object accept(ExpressionVisitor ask) {
        return ask.forScratchIdentifierExpression(this);
    }

    static Integer count = 0;
    static public ScratchIdentifierExpression generate() {
        count += 1;
        return new ScratchIdentifierExpression(count);
    }
}
