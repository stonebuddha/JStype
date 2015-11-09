package concrete.init;

import concrete.Domains;
import fj.Ord;
import fj.data.TreeMap;
import ir.JSClass;

/**
 * Created by wayne on 15/11/9.
 */
public class Utils {

    public static Domains.Object createObj(TreeMap<Domains.Str, Domains.BValue> external) {
        return createObj(external, TreeMap.empty(Ord.hashEqualsOrd()));
    }

    public static Domains.Object createObj(TreeMap<Domains.Str, Domains.BValue> external, TreeMap<Domains.Str, Object> internal) {
        TreeMap<Domains.Str, Object> intern;
        if (!internal.contains(concrete.Utils.Fields.proto)) {
            intern = internal.set(concrete.Utils.Fields.proto, Init.Object_prototype_Addr);
        } else {
            intern = internal;
        }
        TreeMap<Domains.Str, Object> intern1;
        if (!internal.contains(concrete.Utils.Fields.classname)) {
            intern1 = intern.set(concrete.Utils.Fields.classname, JSClass.CObject);
        } else {
            intern1 = intern;
        }
        return new Domains.Object(external, intern1);
    }
}
