package nci;

import nci.type.Type;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wayne on 15/10/20.
 */
public class Frame {
    Map<String, Type> env;
    Frame outer;
    public Frame() {
        this.env = new HashMap<>();
        this.outer = null;
    }
    public Frame(Frame outer) {
        this.env = new HashMap<>();
        this.outer = outer;
    }
    public Type lookup(String name) {
        if (this.env.containsKey(name)) {
            return this.env.get(name);
        } else if (this.outer != null) {
            return this.outer.lookup(name);
        } else {
            return null;
        }
    }
    public void extend(String name, Type type) {
        env.put(name, type);
    }
}
