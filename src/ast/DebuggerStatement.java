package ast;

/**
 * Created by Hwhitetooth on 15/10/14.
 */
public class DebuggerStatement extends Statement {
    Object accept(NodeVisitor ask) {
        return ask.forDebuggerStatement();
    }
}
