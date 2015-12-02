package analysis.init;

import analysis.Domains;
import analysis.Interpreter;
import analysis.Traces;
import analysis.Utils;
import fj.data.Option;
import immutable.FHashMap;
import immutable.FHashSet;
import ir.IRPVar;
import ir.IRScratch;
import ir.IRVar;
import ir.JSClass;

/**
 * Created by BenZ on 15/11/24.
 */
public class InitUtils {
    public static Domains.Object createInitObj(final FHashMap<String, Domains.BValue> fields, final FHashMap<Domains.Str, Object> internal) {
        FHashMap<Domains.Str, Object> internalFieldMap;
        if (!internal.contains(Utils.Fields.proto)) {
            internalFieldMap = internal.set(Utils.Fields.proto, Domains.AddressSpace.Address.inject(Init.Object_prototype_Addr));
        } else {
            internalFieldMap = internal;
        }
        if (!internal.contains(Utils.Fields.classname)) {
            internalFieldMap = internalFieldMap.set(Utils.Fields.classname, JSClass.CObject);
        }
        return createInitObjCore(fields, internalFieldMap);
    }

    public static Domains.Object createInitObj(final FHashMap<String, Domains.BValue> fields) {
        return createInitObj(fields, FHashMap.empty());
    }

    public static Domains.Object createInitObjCore(final FHashMap<String, Domains.BValue> fields, final FHashMap<Domains.Str, Object> internal) {
        final FHashMap<Domains.Str, Domains.BValue> exactnotnum =
        fields.keys().filter(s -> !Domains.Str.isNum(s)).foldLeft((acc, cur) -> {
            return acc.set(Domains.Str.alpha(cur), fields.get(cur).some());
        }, FHashMap.empty());
        final FHashMap<Domains.Str, Domains.BValue> exactnum =
        fields.keys().filter(s -> Domains.Str.isNum(s)).foldLeft((acc, cur) -> {
            return acc.set(Domains.Str.alpha(cur), fields.get(cur).some());
        }, FHashMap.empty());
        final Domains.ExternMap external = new Domains.ExternMap(Option.none(), Option.none(), Option.none(), exactnotnum, exactnum);
        return new Domains.Object(external, internal, FHashSet.build(exactnotnum.keys()));
    }

    public static Domains.Object createObj(final Domains.ExternMap external, final FHashMap<Domains.Str, Object> internal, final FHashSet<Domains.Str> present) {
        FHashMap<Domains.Str, Object> internalFieldMap;
        if (!internal.contains(Utils.Fields.proto)) {
            internalFieldMap = internal.set(Utils.Fields.proto, Domains.AddressSpace.Address.inject(Init.Object_prototype_Addr));
        } else {
            internalFieldMap = internal;
        }
        if (!internal.contains(Utils.Fields.classname)) {
            internalFieldMap = internalFieldMap.set(Utils.Fields.classname, JSClass.CObject);
        }
        FHashSet<Domains.Str> present1 = (external.exactnotnum.keys()).append(external.exactnum.keys()).foldLeft((acc, cur) -> acc.insert(cur), present);
        return new Domains.Object(external, internalFieldMap, present1);
    }

    public static Domains.Object createObj() {
        return createObj(new Domains.ExternMap(), FHashMap.empty(), FHashSet.empty());
    }

    public static Domains.Object createInitFunctionObj(Domains.Native clo, FHashMap<String, Domains.BValue> fields, FHashMap<Domains.Str, Object> internal, JSClass cls) {
        final FHashMap<Domains.Str, Object> internalFieldMap;
        internalFieldMap = internal.union(FHashMap.build(
                Utils.Fields.proto, Domains.AddressSpace.Address.inject(Init.Function_prototype_Addr),
                Utils.Fields.code, FHashSet.<Domains.Closure>build(clo),
                Utils.Fields.classname, cls
        ));
        return createInitObjCore(fields, internalFieldMap);
    }

    public static FHashSet<Interpreter.State> makeState(final Domains.Value v, final IRVar x, final Domains.Env env, final Domains.Store store, final Domains.Scratchpad pad, final Domains.KontStack ks, final Traces.Trace tr) {
        if (v instanceof Domains.BValue) {
            Domains.BValue bv = (Domains.BValue)v;
            if (x instanceof IRPVar) {
                IRPVar pv = (IRPVar)x;
                return FHashSet.build(new Interpreter.State(new Domains.ValueTerm(v), env, store.extend(env.apply(pv).some(), bv), pad, ks, tr));
            } else {
                IRScratch sc = (IRScratch)x;
                return FHashSet.build(new Interpreter.State(new Domains.ValueTerm(v), env, store, pad.update(sc, bv), ks, tr));
            }
        } else {
            return FHashSet.build(new Interpreter.State(new Domains.ValueTerm(v), env, store, pad, ks, tr));
        }
    }
}
