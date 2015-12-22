/**
 * Created by wayne on 10/14/15.
 */

package ast;

import fj.data.Option;

public abstract class Node {
    public Option<Location> loc = Option.none();
}
