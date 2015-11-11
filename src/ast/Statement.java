/**
 * Created by wayne on 10/14/15.
 */

package ast;

import fj.P2;

public abstract class Statement extends Node {
    public abstract <T> T accept(StatementVisitor<T> ask);
    public abstract <T> P2<Statement, T> accept(TransformVisitor<T> ask);
    public abstract Statement accept(SimpleTransformVisitor ask);
}
