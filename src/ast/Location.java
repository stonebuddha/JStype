package ast;

/**
 * Created by wayne on 15/12/22.
 */
public class Location {
    String source;
    Position start;
    Position end;
    public Location(String source, Position start, Position end) {
        this.source = source;
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return start.toString();
    }

    public Position getStart() {
        return start;
    }

    public Position getEnd() {
        return end;
    }

    public String getSource() {
        return source;
    }
}
