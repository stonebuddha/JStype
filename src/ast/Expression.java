/**
 * Created by wayne on 10/14/15.
 */

package ast;

import fj.P2;

public abstract class Expression extends Node {
    public abstract <T> T accept(ExpressionVisitor<T> ask);
    public abstract <T> P2<Expression, T> accept(TransformVisitor<T> ask);
    public abstract Expression accept(SimpleTransformVisitor ask);
}
