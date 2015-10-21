package nci.type;

import ast.Node;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by wayne on 15/10/21.
 */
public class ObjectType {
    Set<Node> nodes;
    public ObjectType(Node node) {
        this.nodes = new HashSet<>();
        this.nodes.add(node);
    }
    public ObjectType(Set<Node> nodes) {
        this.nodes = nodes;
    }

    static ObjectType merge(ObjectType a, ObjectType b) {
        HashSet<Node> nodes = new HashSet<>();
        nodes.addAll(a.nodes);
        nodes.addAll(b.nodes);
        return new ObjectType(nodes);
    }
}
