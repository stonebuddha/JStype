package translator;

import ast.Node;
import ast.Program;
import ast.ProgramVisitor;

/**
 * Created by wayne on 15/11/9.
 */
public class AST2AST {

    public static class RemoveEmptyWithUndef implements ProgramVisitor {
        @Override
        public Object forProgram(Program program) {
            return null;
        }
    }

    public static Node transform(Node ast) {
        return null;
    }
}
