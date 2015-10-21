package ast;

import java.util.ArrayList;

/**
 * Created by wayne on 15/10/16.
 */
public interface ProgramVisitor {
    Object forProgram(Program program, ArrayList<Statement> body);
}
