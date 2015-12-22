package ast;

import fj.Hash;
import fj.P;
import fj.P2;

/**
 * Created by wayne on 15/12/22.
 */
public class Position {
    int line;
    int column;
    int recordHash;
    static Hash<P2<Integer, Integer>> hasher = Hash.p2Hash(Hash.anyHash(), Hash.anyHash());
    public Position(int line, int column) {
        this.line = line;
        this.column = column;
        this.recordHash = hasher.hash(P.p(line, column));
    }

    @Override
    public int hashCode() {
        return recordHash;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Position && line == ((Position) obj).line && column == ((Position) obj).column);
    }

    @Override
    public String toString() {
        return line + ":" + column;
    }

    public int getColumn() {
        return column;
    }

    public int getLine() {
        return line;
    }
}
