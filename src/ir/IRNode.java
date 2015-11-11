package ir;

import fj.data.Set;
import fj.P2;

/**
 * Created by wayne on 15/10/27.
 */
public abstract class IRNode {
    public Integer id;

    public IRNode() {
        this.id = IRNode.id(this);
    }

    static Integer genId = 0;
    static final Integer numClasses = 10;
    public static Integer id(IRNode node) {
        assert genId <= 210000000;
        genId += numClasses;
        return genId;
    }

    public static void reset() {
        genId = 0;
    }

    public static Set<IRPVar> free(IRNode node) {
        // TODO
        return null;
    }

    public static P2<Set<Integer>, Set<Integer>> escape(IRMethod m) {
        // TODO
        return null;
    }
}
