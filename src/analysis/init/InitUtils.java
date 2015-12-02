package analysis.init;

import analysis.Domains;
import analysis.Interpreter;
import analysis.Traces;
import analysis.Utils;
import fj.*;
import fj.data.List;
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
    //Might be unsuitable
    public static abstract class ConversionHint {};
    public static class NoConversion extends ConversionHint {};

    public static abstract class PrimHint {};
    public static class StringHint extends PrimHint {};
    public static class NumberHint extends PrimHint {};

    //TODO
    public static FHashSet<Interpreter.State> Convert(
            List<P2<Domains.BValue, ConversionHint>> l,
            F7<List<Domains.BValue>, IRVar, Domains.Env, Domains.Store, Domains.Scratchpad, Domains.KontStack, Traces.Trace, FHashSet<Interpreter.State>> f,
            IRVar x, Domains.Env env, Domains.Store store, Domains.Scratchpad pad, Domains.KontStack ks, Traces.Trace trace) {
        return null;
    }

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
        internalFieldMap = internal.union(FHashMap.<Domains.Str, Object>build(
                Utils.Fields.proto, Domains.AddressSpace.Address.inject(Init.Function_prototype_Addr),
                Utils.Fields.code, FHashSet.<Domains.Closure>build(clo),
                Utils.Fields.classname, cls));
        return createInitObjCore(fields, internalFieldMap);
    }

    public static Domains.Object createInitFunctionObj(Domains.Native clo, FHashMap<String, Domains.BValue> fields) {
        FHashMap<Domains.Str, Object> internal = FHashMap.empty();
        JSClass cls = JSClass.CFunction;
        final FHashMap<Domains.Str, Object> internalFieldMap;
        internalFieldMap = internal.union(FHashMap.<Domains.Str, Object>build(
                Utils.Fields.proto, Domains.AddressSpace.Address.inject(Init.Function_prototype_Addr),
                Utils.Fields.code, FHashSet.<Domains.Closure>build(clo),
                Utils.Fields.classname, cls));
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

    public static Boolean isNumberClass(JSClass j) {
        return new Boolean(j.equals(JSClass.CNumber));
    }

    public static Boolean isStringClass(JSClass j) {
        return new Boolean(j.equals(JSClass.CString));
    }

    public static abstract class UsualSignature {
        Integer lengthProperty;
    }

    public static class Sig extends UsualSignature {
        ConversionHint selfHint;
        List<ConversionHint> argHints;
        Integer lengthProperty;

        Sig(ConversionHint selfHint, List<ConversionHint> argHints, Integer lengthProperty) {
            this.selfHint = selfHint;
            this.argHints = argHints;
            this.lengthProperty = lengthProperty;
        }
    }

    public static class VarSig extends UsualSignature {
        ConversionHint selfHint;
        ConversionHint argsHint;
        Integer lengthProperty;

        VarSig(ConversionHint selfHint, ConversionHint argsHint, Integer lengthProperty) {
            this.selfHint = selfHint;
            this.argsHint = argsHint;
            this.lengthProperty = lengthProperty;
        }
    }

    public static Domains.Object sigFunctionObj(UsualSignature sig, F7<List<Domains.BValue>, IRVar, Domains.Env, Domains.Store, Domains.Scratchpad, Domains.KontStack, Traces.Trace, FHashSet<Interpreter.State> > f) {
        return createInitFunctionObj(new Domains.Native((selfAddr, argArrayAddr, x, env, store, pad, ks, trace) -> {
            assert argArrayAddr.defAddr() : "Internal function object: argument array address must be an address";
            assert argArrayAddr.as.size() == 1 : "Internal function object: argument array address set size must be 1";
            Domains.Object argsArray = store.getObj(argArrayAddr.as.head());
            Boolean construct = (Boolean)argsArray.intern.get(Utils.Fields.constructor).orSome(false);
            if (construct) {
                return makeState(Utils.Errors.typeError, x, env, store, pad, ks, trace);
            } else {
                List<P2<Domains.BValue, ConversionHint> > argsList;
                if (sig instanceof Sig) {
                    Sig tmpSig = (Sig)sig;
                    ConversionHint selfHint = tmpSig.selfHint;
                    List<ConversionHint> argHints = tmpSig.argHints;
                    argsList = List.range(0, argHints.length()).zip(argHints).map(p2 -> P.p(argsArray.apply(Domains.Str.alpha(p2._1().toString())).orSome(Domains.Undef.BV), p2._2())).cons(P.p(selfAddr, selfHint));
                } else {
                    VarSig tmpSig = (VarSig)sig;
                    ConversionHint selfHint = tmpSig.selfHint;
                    ConversionHint argsHint = tmpSig.argsHint;
                    argsList = List.list(P.p(selfAddr, selfHint), P.p(argsArray.apply(Domains.SNum).orSome(Domains.Undef.BV), argsHint));
                }
                return Convert(argsList, f, x, env, store, pad, ks, trace);
            }
        }), FHashMap.build("length", Domains.Num.inject(Domains.Num.alpha(sig.lengthProperty.doubleValue()))));
    }

    public static Domains.Object usualFunctionObj(UsualSignature sig, F3<List<Domains.BValue>, Domains.Store, Traces.Trace, FHashSet<P2<Domains.Value, Domains.Store> > > f) {
        return sigFunctionObj(sig, (List<Domains.BValue> bvs, IRVar x, Domains.Env env, Domains.Store store, Domains.Scratchpad pad, Domains.KontStack ks, Traces.Trace trace)->
            f.f(bvs, store, trace).map(p2 -> makeState(p2._1(), x, env, p2._2(), pad, ks, trace)).foldLeft((acc, set) -> acc.union(set), FHashSet.empty())
        );
    }

    public static Domains.Object pureFunctionObj(UsualSignature sig, F<List<Domains.BValue>, FHashSet<Domains.Value>> f) {
        return usualFunctionObj(sig, (bvs, store, trace)-> f.f(bvs).map(bv-> P.p(bv, store)));
    }

    public static Domains.Object constFunctionObj(UsualSignature sig, Domains.Value v) {
        return pureFunctionObj(sig, any -> FHashSet.build(v));
    }

    public static Sig ezSig(ConversionHint selfHint, List<ConversionHint> argHints) {
        return new Sig(selfHint, argHints, argHints.length());
    }
}
