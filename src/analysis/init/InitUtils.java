package analysis.init;

import analysis.Domains;
import analysis.Interpreter;
import analysis.Traces;
import analysis.Utils;
import fj.*;
import fj.data.List;
import fj.data.Option;
import fj.function.Effect1;
import immutable.FHashMap;
import immutable.FHashSet;
import ir.IRPVar;
import ir.IRScratch;
import ir.IRVar;
import ir.JSClass;

import javax.rmi.CORBA.Util;

/**
 * Created by BenZ on 15/11/24.
 */
public class InitUtils {
    //Might be unsuitable
    public static abstract class ConversionHint {};
    public static final ConversionHint NoConversion = new ConversionHint() {};

    public static abstract class PrimHint extends ConversionHint{};
    public static final PrimHint StringHint = new PrimHint() {};
    public static final PrimHint NumberHint = new PrimHint() {};

    private static F<Domains.AddressSpace.Address, P2<Domains.BValue, Boolean>> convObj(PrimHint hint, Domains.Store store) {
        return a-> {
            Domains.BValue valueOf = Utils.lookup(Domains.AddressSpace.Addresses.apply(a), Domains.Str.alpha("valueOf"), store);
            Domains.BValue toString = Utils.lookup(Domains.AddressSpace.Addresses.apply(a), Domains.Str.alpha("toString"), store);
            Domains.Object o = store.getObj(a);

            FHashSet<Domains.BValue> values = valueOf.as.map(av-> {
                if (av.equals(Init.Number_prototype_toString_Addr)) {
                    return o.getValue();
                } else if (av.equals(Init.String_prototype_toString_Addr)) {
                    return o.getValue();
                } else if (av.equals(Init.Boolean_prototype_toString_Addr)) {
                    return o.getValue();
                } else if (av.equals(Init.Date_prototype_toString_Addr)) {
                    return Domains.Num.inject(Domains.Num.NReal);
                } else if (av.equals(Init.Object_prototype_toString_Addr)) {
                    return Domains.AddressSpace.Address.inject(a);
                } else if (!store.getObj(av).getCode().isEmpty()) {
                    return Domains.BValue.Bot; // TODO: print a warning
                } else {
                    return Domains.BValue.Bot;
                }
            });
            FHashSet<Domains.BValue> strings = toString.as.map(av-> {
                if (av.equals(Init.Number_prototype_toString_Addr)) {
                    return o.getValue().toStr();
                } else if (av.equals(Init.String_prototype_toString_Addr)) {
                    return o.getValue();
                } else if (av.equals(Init.Boolean_prototype_toString_Addr)) {
                    return o.getValue().toStr();
                } else if (av.equals(Init.Object_prototype_toString_Addr)) {
                    return Domains.Str.inject(Domains.Str.SNotNum).toStr(); //TODO: can be more precise
                } else if (av.equals(Init.Array_prototype_toString_Addr)) {
                    convBValue(StringHint, Utils.lookup(Domains.AddressSpace.Addresses.apply(a), Domains.Str.SNum, store), store);
                    return Domains.Str.inject(Domains.Str.STop).toStr();
                } else if (av.equals(Init.Date_prototype_toString_Addr)) {
                    return Domains.Str.inject(Domains.Str.SNotNum).toStr();
                } else if (av.equals(Init.Function_prototype_toString_Addr)) {
                    return Domains.Str.inject(Domains.Str.SNotNum).toStr();
                } else if (!store.getObj(av).getCode().isEmpty()) {
                    return Domains.BValue.Bot; // TODO: print a warning
                } else {
                    return Domains.BValue.Bot;
                }
            });
//                P2<Domains.BValue, FHashSet<Domains.BValue>> numconv = P.p(valueOf, values);
//                P2<Domains.BValue, FHashSet<Domains.BValue>> strconv = P.p(toString, strings);
            Domains.BValue primaryConversion;
            FHashSet<Domains.BValue> primaryConverted;
            if (hint.equals(NumberHint)) {
                primaryConversion = valueOf;
                primaryConverted = values;
            } else {
                primaryConversion = toString;
                primaryConverted = strings;
            }
            Domains.BValue secondaryConversion;
            FHashSet<Domains.BValue> secondaryConverted;
            if (hint.equals(NumberHint)) {
                secondaryConversion = toString;
                secondaryConverted = strings;
            } else {
                secondaryConversion = valueOf;
                secondaryConverted = values;
            }

            F<FHashSet<Domains.BValue>, Domains.BValue> convPrims = bvs-> bvs.map(convPrim(hint)).foldLeft((acc, bv) -> acc.merge(bv), Domains.BValue.Bot);

            Domains.BValue tmp;
            if (primaryConversion.defAddr()) {
                tmp = Domains.BValue.Bot;
            } else {
                tmp = convPrims.f(secondaryConverted);
            }
            return P.p(convPrims.f(primaryConverted).merge(tmp), !secondaryConversion.defAddr());
        };
    }

    private static P2<Domains.BValue, Boolean> convBValue(PrimHint hint, Domains.BValue bv, Domains.Store store) {
        return bv.as.map(convObj(hint, store)).foldLeft((a, b) -> P.p(a._1().merge(b._1()), a._2() || b._2()), P.p(convPrim(hint).f(bv), false));
    }

    private static F<Domains.BValue, Domains.BValue> convPrim(PrimHint hint) {
        if (hint.equals(NumberHint)) {
            return bv->bv.toNum();
        } else {
            return bv->bv.toStr();
        }
    }

    //TODO
    public static FHashSet<Interpreter.State> Convert(
            List<P2<Domains.BValue, ConversionHint>> l,
            F7<List<Domains.BValue>, IRVar, Domains.Env, Domains.Store, Domains.Scratchpad, Domains.KontStack, Traces.Trace, FHashSet<Interpreter.State>> f,
            IRVar x, Domains.Env env, Domains.Store store, Domains.Scratchpad pad, Domains.KontStack ks, Traces.Trace trace) {
        List<P2<Domains.BValue, Boolean>> prims = l.map(p2-> {
            Domains.BValue bv = p2._1();
            ConversionHint hint = p2._2();
            if (hint.equals(NoConversion)) {
                return P.p(bv, false);
            } else {
                return convBValue((PrimHint) hint, bv, store);
            }
        });

        FHashSet<Interpreter.State> tmp;
        if (prims.map(any-> any._2()).exists(any-> any)) {
            tmp = makeState(Utils.Errors.typeError, x, env, store, pad, ks, trace);
        } else {
            tmp = FHashSet.empty();
        }
        return f.f(prims.map(any-> any._1()), x, env, store, pad, ks, trace).union(tmp);
    }

    public static FHashSet<Interpreter.State> ToNumber(List<Domains.BValue> l, F7<List<Domains.BValue>, IRVar, Domains.Env, Domains.Store, Domains.Scratchpad, Domains.KontStack, Traces.Trace, FHashSet<Interpreter.State>> f, IRVar x, Domains.Env env, Domains.Store store, Domains.Scratchpad pad, Domains.KontStack ks, Traces.Trace trace) {
        return Convert(l.map(e-> P.p(e, NumberHint)), f, x, env, store, pad, ks, trace);
    }

    public static FHashSet<Interpreter.State> ToString(List<Domains.BValue> l, F7<List<Domains.BValue>, IRVar, Domains.Env, Domains.Store, Domains.Scratchpad, Domains.KontStack, Traces.Trace, FHashSet<Interpreter.State>> f, IRVar x, Domains.Env env, Domains.Store store, Domains.Scratchpad pad, Domains.KontStack ks, Traces.Trace trace) {
        return Convert(l.map(e-> P.p(e, StringHint)), f, x, env, store, pad, ks, trace);
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

        Sig(ConversionHint selfHint, List<ConversionHint> argHints, Integer lengthProperty) {
            this.selfHint = selfHint;
            this.argHints = argHints;
            this.lengthProperty = lengthProperty;
        }
    }

    public static class VarSig extends UsualSignature {
        ConversionHint selfHint;
        ConversionHint argsHint;

        VarSig(ConversionHint selfHint, ConversionHint argsHint, Integer lengthProperty) {
            this.selfHint = selfHint;
            this.argsHint = argsHint;
            this.lengthProperty = lengthProperty;
        }
    }

    public static Domains.Object sigFunctionObj(UsualSignature sig, F7<List<Domains.BValue>, IRVar, Domains.Env, Domains.Store, Domains.Scratchpad, Domains.KontStack, Traces.Trace, FHashSet<Interpreter.State>> f) {
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
                    VarSig tmpVarSig = (VarSig)sig;
                    ConversionHint selfHint = tmpVarSig.selfHint;
                    ConversionHint argsHint = tmpVarSig.argsHint;
                    argsList = List.list(P.p(selfAddr, selfHint), P.p(argsArray.apply(Domains.Str.SNum).orSome(Domains.Undef.BV), argsHint));
                }
                return Convert(argsList, f, x, env, store, pad, ks, trace);
            }
        }), FHashMap.build("length", Domains.Num.inject(Domains.Num.alpha(sig.lengthProperty.doubleValue()))));
    }

    public static Domains.Object usualFunctionObj(UsualSignature sig, F3<List<Domains.BValue>, Domains.Store, Traces.Trace, FHashSet<P2<Domains.Value, Domains.Store> > > f) {
        return sigFunctionObj(sig, (bvs, x, env, store, pad, ks, trace)->
            f.f(bvs, store, trace).map(p2-> makeState(p2._1(), x, env, p2._2(), pad, ks, trace)).foldLeft((acc, set) -> acc.union(set), FHashSet.empty())
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

    public static F7<List<Domains.BValue>, IRVar, Domains.Env, Domains.Store, Domains.Scratchpad, Domains.KontStack, Traces.Trace, FHashSet<Interpreter.State>> genValueObjConstructor(String cname, F<Domains.BValue, Domains.BValue> bvtrans) {
        return (bvs, x, env, store, pad, ks, trace)-> {
            assert bvs.length() >= 2 : cname + " constructor: should have `self` address plus 1 argument by this point";
            Domains.BValue selfAddr_bv = bvs.index(0), arg_value = bvs.index(1);
            assert selfAddr_bv.as.size() == 1 : "String constructor: `self` address set should be singleton";
            Domains.AddressSpace.Address selfAddr = selfAddr_bv.as.head();
            Domains.BValue final_value = bvtrans.f(arg_value);
            Domains.Object old_self = store.getObj(selfAddr);
            Domains.Object new_self = new Domains.Object(old_self.extern.strongUpdate(Utils.Fields.value, final_value), old_self.intern, old_self.present);
            Domains.Object newer_self;
            if (cname.equals("String")) {
                Option<String> exactStr = Domains.Str.getExact(final_value.str);
                Domains.ExternMap extern;
                if (exactStr.isSome()) {
                    String s = exactStr.some();
                    extern = List.range(0, s.length()).foldLeft(
                            (acc, e)-> acc.strongUpdate(Domains.Str.alpha(e.toString()), Domains.Str.inject(Domains.Str.alpha(s.substring(e, e + 1)))),
                            new_self.extern.strongUpdate(Utils.Fields.length, Domains.Num.inject(Domains.Num.alpha((double)s.length()))));
                } else {
                    extern = new_self.extern.weakUpdate(Utils.Fields.length, Domains.Num.inject(Domains.Num.NReal)).weakUpdate(Domains.Str.SNum, Domains.Str.inject(Domains.Str.SingleChar));
                }
                newer_self = new Domains.Object(extern, new_self.intern, new_self.present.insert(Utils.Fields.length));
            } else {
                newer_self = new_self;
            }
            return InitUtils.makeState(selfAddr_bv, x, env, store.putObj(selfAddr, newer_self), pad, ks, trace);
        };
    }

    public static F7<List<Domains.BValue>, IRVar, Domains.Env, Domains.Store, Domains.Scratchpad, Domains.KontStack, Traces.Trace, FHashSet<Interpreter.State>> valueObjConstructor(String cname, Effect1<Domains.BValue> verify) {
        return genValueObjConstructor(cname, bv-> {
            verify.f(bv);
            return bv;
        });
    }

    public static <A> P2<FHashSet<A>, FHashSet<Domains.EValue>> toPrimeHelper(Domains.BValue selfAddr, Domains.Store store, F<JSClass, Boolean> goodClass, F<Domains.BValue, A> conv, A bottom, F<P2<A, A>, A> join) {
        assert selfAddr.defAddr() : "Assuming selfAddr is always addresses";
        FHashSet<Domains.Object> selves = selfAddr.as.map(addr-> store.getObj(addr));
        FHashSet<Domains.Object> goods = selves.filter(self-> goodClass.f(self.getJSClass()));
        FHashSet<Domains.Object> bads = selves.filter(self-> !goodClass.f(self.getJSClass()));
        A goodv = goods.foldLeft((acc, self)-> join.f(P.p(acc, conv.f(self.getValue()))), bottom);
        FHashSet<A> good_res;
        if (goodv.equals(bottom)) {
            good_res = FHashSet.empty();
        } else {
            good_res = FHashSet.build(goodv);
        }
        FHashSet<Domains.EValue> err_res;
        if (bads.isEmpty()){
            err_res = FHashSet.empty();
        } else {
            err_res = FHashSet.build(Utils.Errors.typeError);
        }
        return P.p(good_res, err_res);
    }

    public static <A> Domains.Object usualToPrim(F<JSClass, Boolean> goodClass, F<Domains.BValue, A> conv, A bottom, F<A, Domains.Value> inject, F<P2<A, A>, A> join) {
        return usualFunctionObj(ezSig(NoConversion, List.list()),
                (list, store, trace)-> {
                    if (list.length() == 1) {
                        Domains.BValue selfAddr = list.head();
                        P2<FHashSet<A>, FHashSet<Domains.EValue>> tmpP = toPrimeHelper(selfAddr, store, goodClass, conv, bottom, join);
                        FHashSet<A> goods = tmpP._1();
                        FHashSet<Domains.EValue> errs = tmpP._2();
                        return goods.map(inject).union(errs.map(ev -> (Domains.Value) ev)).map(any -> P.p(any, store));
                    } else {
                        throw new RuntimeException("usualToPrimHelper: signature nonconformance");
                    }
                });
    }

    public static FHashMap<String, Domains.BValue> dangleMap(FHashMap<String, Domains.BValue> m) {
        //notJS.Mutable.dangle
        if (Interpreter.Mutable.dangle) {
            return m;
        } else {
            return FHashMap.empty();
        }
    }

    public static Domains.Object unimplemented(String name)  {
        return createInitFunctionObj(new Domains.Native((selfAddr, argArrayAddr, x, env, store, pad, ks, trace)-> {
            throw new RuntimeException(name + ": Not implemented");
        }), FHashMap.build("length", Domains.Num.inject(Domains.Num.alpha(0.0))));
    }

    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}
