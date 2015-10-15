package ast;

import java.util.ArrayList;

/**
 * Created by wayne on 15/10/15.
 */
public class FunctionExpression extends Expression {
    String id;
    ArrayList<Expression> params;
    BlockStatement body;
    public FunctionExpression(String id, ArrayList<Expression> params, BlockStatement body) {
        this.id = id;
        this.params = params;
        this.body = body;
    }
}
