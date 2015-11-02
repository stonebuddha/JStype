package analysis;

/**
 * Created by wayne on 15/11/2.
 */
public class Utils {

    public static class Fields {
        public static final Domains.Str proto = Domains.Str.alpha("proto");
        public static final Domains.Str classname = Domains.Str.alpha("class");
        public static final Domains.Str code = Domains.Str.alpha("code");
        public static final Domains.Str prototype = Domains.Str.alpha("prototype");
        public static final Domains.Str length = Domains.Str.alpha("length");
        public static final Domains.Str value = Domains.Str.alpha("value");
        public static final Domains.Str message = Domains.Str.alpha("message");
        public static final Domains.Str constructor = Domains.Str.alpha("constructor");
    }
}
