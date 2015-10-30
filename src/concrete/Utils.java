package concrete;

import com.google.common.collect.ImmutableList;

import ir.IRScratch;
import ir.IRVar;

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

    public static Map.Entry<Domains.Store, Domains.Address> allocFun(Domains.Closure clo, Domains.BValue n, Domains.Store store) {
        // TODO
        return null;
    }

    public static Map.Entry<Domains.Store, Domains.Address> allocObj(Domains.Address a, Domains.Store store) {
        // TODO
        return null;
    }

    public static Interpreter.State applyClo(Domains.BValue bv1, Domains.BValue bv2, Domains.BValue bv3, IRVar x, Domains.Env env, Domains.Store store, Domains.Scratchpad pad, Domains.KontStack ks) {
        // TODO
        return null;
    }

    public static Map.Entry<Domains.Value, Map.Entry<Domains.Store, Domains.Scratchpad>> delete(Domains.BValue bv1, Domains.BValue bv2, IRScratch x, Domains.Env env, Domains.Store store, Domains.Scratchpad pad) {
        // TODO
        return null;
    }

    public static Domains.BValue lookup(Domains.Object o, Domains.Str str, Domains.Store store) {
        // TODO
        return null;
    }

    public static ImmutableList<Domains.Str> objAllKeys(Domains.BValue bv, Domains.Store store) {
        // TODO
        return null;
    }

    public static Domains.Store setConstr(Domains.Store store, Domains.Address a) {
        // TODO
        return null;
    }

    public static Map.Entry<Domains.Value, Map.Entry<Domains.Store, Domains.Scratchpad>> toObj(Domains.BValue bv, IRVar x, Domains.Env env, Domains.Store store, Domains.Scratchpad pad) {
        // TODO
        return null;
    }

    public static Map.Entry<Domains.Value, Domains.Store> updateObj(Domains.BValue bv1, Domains.BValue bv2, Domains.BValue bv3, Domains.Store store) {
        // TODO
        return null;
    }
}
