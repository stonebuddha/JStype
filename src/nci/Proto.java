package nci;

import nci.type.Type;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wayne on 15/10/21.
 */
public class Proto {
    Map<String, Type> properties;
    // TODO: scope chain
    public Proto() {
        this.properties = new HashMap<>();
    }
}
