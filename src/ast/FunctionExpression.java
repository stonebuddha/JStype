package ast;

import java.util.ArrayList;

/**
 * Created by wayne on 15/10/15.
 */
public class FunctionExpression extends Expression {
    String id;
    ArrayList<String> params;
    BlockStatement body;
    public FunctionExpression(String id, ArrayList<String> params, BlockStatement body) {
        this.id = id;
        this.params = params;
        this.body = body;
    }
    Object accept(ExpressionVisitor ask) {
        return ask.forFunctionExpression(id, params, body);
    }
}
