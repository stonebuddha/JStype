package analysis;

import fj.data.Option;
import fj.data.Set;
import fj.P2;
import fj.P3;
import ir.*;

/**
 * Created by wayne on 15/11/2.
 */
public class Utils {

    public static class Recursive<I> {
        public I func;
    }

    public static class Errors {
        public static final Domains.EValue typeError = new Domains.EValue(Domains.Str.inject(Domains.Str.alpha("TypeError")));
        public static final Domains.EValue rangeError = new Domains.EValue(Domains.Str.inject(Domains.Str.alpha("RangeError")));
        public static final Domains.EValue uriError = new Domains.EValue(Domains.Str.inject(Domains.Str.alpha("URIError")));
        public static final Domains.EValue syntaxError = new Domains.EValue(Domains.Str.inject(Domains.Str.alpha("SyntaxError")));
    }

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

    public static class Filters {
        public static class BVFilter {}
        public static final BVFilter IsFunc = new BVFilter();
        public static final BVFilter IsUndefNull = new BVFilter();
    }

    public static Domains.Store allocFun(Domains.Closure clo, Domains.BValue n, Domains.AddressSpace.Address a, Domains.Store store) {
        // TODO
        return null;
    }

    public static P2<Domains.Store, Domains.BValue> allocObj(Domains.BValue bv, Domains.AddressSpace.Address a, Domains.Store store, Trace trace) {
        // TODO
        return null;
    }

    public static Set<Interpreter.State> applyClo(Domains.BValue bv1, Domains.BValue bv2, Domains.BValue bv3, IRVar x, Domains.Env env, Domains.Store store, Domains.Scratchpad pad, Domains.KontStack ks, Trace trace) {
        // TODO
        return null;
    }

    public static P2<Option<P2<Domains.Store, Domains.Scratchpad>>, Option<Domains.EValue>> delete(Domains.BValue bv1, Domains.BValue bv2, IRScratch x, Domains.Env env, Domains.Store store, Domains.Scratchpad pad) {
        // TODO
        return null;
    }

    public static Domains.BValue lookup(Set<Domains.AddressSpace.Address> as, Domains.Str str, Domains.Store store) {
        return null; // TODO
    }

    public static Set<Interpreter.State> objAllKeys(Domains.BValue bv, Domains.Store store) {
        // TODO
        return null;
    }

    public static Domains.Store setConstr(Domains.Store store, Domains.BValue bv) {
        // TODO
        return null;
    }

    public static P2<Option<P3<Domains.BValue, Domains.Store, Domains.Scratchpad>>, Option<Domains.EValue>> toObj(Domains.BValue bv, IRVar x, Domains.Env env, Domains.Store store, Domains.Scratchpad pad, Trace trace) {
        // TODO
        return null;
    }

    public static P2<Option<P2<Domains.BValue, Domains.Store>>, Option<Domains.EValue>> updateObj(Domains.BValue bv1, Domains.BValue bv2, Domains.BValue bv3, Domains.Store store) {
        // TODO
        return null;
    }

        public static P2<Domains.Store, Domains.Scratchpad> refineExc(IRExp e, Domains.Store store, Domains.Env env, Domains.Scratchpad pad, Filters.BVFilter bvf) {
        // TODO
        return null;
    }
}
