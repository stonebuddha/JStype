package ir;

import ast.Location;
import fj.data.Option;
import fj.data.Set;
import fj.P2;
import immutable.FHashSet;

/**
 * Created by wayne on 15/10/27.
 */
public abstract class IRNode {
    public Integer id;
    public Option<Location> loc = Option.none();

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

    public abstract FHashSet<IRPVar> free();
}
