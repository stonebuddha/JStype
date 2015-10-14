package ast;

import java.util.ArrayList;

/**
 * Created by wayne on 10/15/15.
 */
public class FunctionDeclaration extends Declaration {
    String id;
    ArrayList<String> params;
    BlockStatement body;
    public FunctionDeclaration(String id, ArrayList<String> params, BlockStatement body) {
        this.id = id;
        this.params = params;
        this.body = body;
    }
}
