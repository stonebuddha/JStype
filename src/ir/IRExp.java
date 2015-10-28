package ir;

/**
 * Created by wayne on 15/10/27.
 */
public abstract class IRExp extends IRNode {
    public abstract Object accept(IRExpVisitor ask);
}
