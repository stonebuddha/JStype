package ast;

import java.util.ArrayList;

/**
 * Created by wayne on 10/15/15.
 */
public class FunctionDeclaration extends Declaration {
    IdentifierExpression id;
    ArrayList<IdentifierExpression> params;
    BlockStatement body;
    public FunctionDeclaration(IdentifierExpression id, ArrayList<IdentifierExpression> params, BlockStatement body) {
        this.id = id;
        this.params = params;
        this.body = body;
    }
    public Object accept(StatementVisitor ask) {
        return ask.forFunctionDeclaration(id, params, body);
    }
}
