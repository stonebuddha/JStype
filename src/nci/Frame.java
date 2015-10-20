package nci;

import nci.type.Type;

import java.util.HashMap;

/**
 * Created by wayne on 15/10/20.
 */
public class Frame {
    HashMap<String, Type> env;
    Frame outer;
    public Frame() {
        this.env = new HashMap<>();
        this.outer = null;
    }
    public Frame(Frame outer) {
        this.env = new HashMap<>();
        this.outer = outer;
    }
}
