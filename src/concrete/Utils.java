package concrete;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by wayne on 15/10/28.
 */
public class Utils {

    public static class Recursive<I> {
        public I func;
    }

    public static class Fields {
        public static final Domains.Str proto = new Domains.Str("proto");
        public static final Domains.Str classname = new Domains.Str("class");
        public static final Domains.Str code = new Domains.Str("code");
        public static final Domains.Str prototype = new Domains.Str("prototype");
        public static final Domains.Str length = new Domains.Str("length");
        public static final Domains.Str value = new Domains.Str("value");
        public static final Domains.Str message = new Domains.Str("message");
        public static final Domains.Str constructor = new Domains.Str("constructor");
    }

    public static Map.Entry<Domains.Store, ArrayList<Domains.Address>> alloc(Domains.Store store, ArrayList<Domains.BValue> bvs) {
        // TODO
        return null;
    }

    public static Domains.BValue lookup(Domains.Object o, Domains.Str str, Domains.Store store) {
        // TODO
        return null;
    }
}
