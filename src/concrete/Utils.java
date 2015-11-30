package concrete;

import concrete.init.Init;
import fj.*;
import fj.data.List;
import fj.data.Option;
import fj.data.Set;
import fj.data.TreeMap;
import ir.*;

/**
 * Created by wayne on 15/10/28.
 */
public class Utils {

    public static class Recursive<I> {
        public I func;
    }

    public static final class Errors {
        public static final Domains.EValue typeError = new Domains.EValue(new Domains.Str("TypeError"));
        public static final Domains.EValue rangeError = new Domains.EValue(new Domains.Str("RangeError"));
    }

    public static final class Fields {
        public static final Domains.Str proto = new Domains.Str("proto");
        public static final Domains.Str classname = new Domains.Str("class");
        public static final Domains.Str code = new Domains.Str("code");
        public static final Domains.Str prototype = new Domains.Str("prototype");
        public static final Domains.Str length = new Domains.Str("length");
        public static final Domains.Str value = new Domains.Str("value");
        public static final Domains.Str message = new Domains.Str("message");
        public static final Domains.Str constructor = new Domains.Str("constructor");
    }

    public static P2<Domains.Store, List<Domains.Address>> alloc(final Domains.Store store, final List<Domains.BValue> bvs) {
        final List<Domains.Address> as = bvs.map(bv -> Domains.Address.generate());
        final Domains.Store store1 = store.extendAll(as.zip(bvs));
        return P.p(store1, as);
    }

    public static P2<Domains.Store, Domains.Address> allocFun(final Domains.Closure clo, final Domains.BValue n, final Domains.Store store) {
        final Domains.Address a1 = Domains.Address.generate();
        final TreeMap<Domains.Str, Object> intern = TreeMap.treeMap(Ord.hashEqualsOrd(),
                P.p(Fields.proto, Init.Function_prototype_Addr),
                P.p(Fields.classname, JSClass.CFunction),
                P.p(Fields.code, clo));
        final TreeMap<Domains.Str, Domains.BValue> extern = TreeMap.treeMap(Ord.hashEqualsOrd(), P.p(Fields.length, n));
        return P.p(store.putObj(a1, new Domains.Object(extern, intern)), a1);
    }

    public static P2<Domains.Store, Domains.Address> allocObj(final Domains.Address a, final Domains.Store store) {
        final JSClass c = Init.classFromAddress.get(a).orSome(JSClass.CObject);
        final Domains.Address a1 = Domains.Address.generate();
        final Domains.Address a2;
        final Option<Domains.BValue> tmp = store.getObj(a).apply(Fields.prototype);
        if (tmp.isSome() && tmp.some() instanceof Domains.Address) {
            a2 = (Domains.Address)tmp.some();
        } else {
            a2 = Init.Object_prototype_Addr;
        }
        final TreeMap<Domains.Str, Object> intern = TreeMap.treeMap(Ord.hashEqualsOrd(),
                P.p(Fields.proto, a2),
                P.p(Fields.classname, c));
        final Domains.Store store1 = store.putObj(a1, new Domains.Object(TreeMap.empty(Ord.hashEqualsOrd()), intern));
        return P.p(store1, a1);
    }

    public static Interpreter.State applyClo(final Domains.BValue bv1, final Domains.BValue bv2, final Domains.BValue bv3, final IRVar x, final Domains.Env env, final Domains.Store store, final Domains.Scratchpad pad, final Domains.KontStack ks) {
        if (bv1 instanceof Domains.Address && bv2 instanceof Domains.Address && bv3 instanceof Domains.Address) {
            final Domains.Address a1 = (Domains.Address)bv1;
            final Domains.Address a2 = (Domains.Address)bv2;
            final Domains.Address a3 = (Domains.Address)bv3;
            final Domains.Object o = store.getObj(a1);
            final Boolean isCtor = store.getObj(a3).calledAsCtor();

            final Option<Domains.Closure> tmp = o.getCode();
            if (tmp.isNone()) {
                return new Interpreter.State(new Domains.ValueTerm(Errors.typeError), env, store, pad, ks);
            } else if (tmp.some() instanceof Domains.Clo) {
                final Domains.Clo tmp2 = (Domains.Clo)tmp.some();
                final Domains.Env envc = tmp2.env;
                final IRMethod m = tmp2.m;
                final IRPVar self = m.self;
                final IRPVar args = m.args;
                final IRStmt s = m.s;
                final P2<Domains.Store, List<Domains.Address>> tmp3 = alloc(store, List.list(a2, a3));
                final Domains.Store store1 = tmp3._1();
                final List<Domains.Address> as = tmp3._2();
                final Domains.Env envc1 = envc.extendAll(List.list(self, args).zip(as));
                return new Interpreter.State(new Domains.StmtTerm(s), envc1, store1, Domains.Scratchpad.apply(0), ks.push(new Domains.RetKont(x, env, isCtor, pad)));
            } else {
                final Domains.Native tmp2 = (Domains.Native)tmp.some();
                return tmp2.f.f(a2, a3, x, env, store, pad, ks);
            }
        } else {
            return new Interpreter.State(new Domains.ValueTerm(Errors.typeError), env, store, pad, ks);
        }
    }

    public static P3<Domains.Value, Domains.Store, Domains.Scratchpad> delete(final Domains.BValue bv1, final Domains.BValue bv2, final IRScratch x, final Domains.Env env, final Domains.Store store, final Domains.Scratchpad pad) {
        if (bv1.equals(Domains.Null) || bv1.equals(Domains.Undef)) {
            return P.p(Errors.typeError, store, pad);
        } else if (bv1 instanceof Domains.Address && bv2 instanceof Domains.Str) {
            final Domains.Address a = (Domains.Address)bv1;
            final Domains.Str str = (Domains.Str)bv2;
            final P2<Domains.Object, Boolean> tmp = store.getObj(a).delete(str);
            final Domains.Object o1 = tmp._1();
            final Boolean del = tmp._2();
            if (del) {
                return P.p(Domains.Undef, store.putObj(a, o1), pad.update(x, Domains.Bool.True));
            } else {
                return P.p(Domains.Undef, store, pad.update(x, Domains.Bool.False));
            }
        } else {
            return P.p(Domains.Undef, store, pad.update(x, Domains.Bool.True));
        }
    }

    public static Domains.BValue lookup(Domains.Object o, final Domains.Str str, final Domains.Store store) {
        while (true) {
            final Option<Domains.BValue> tmp = o.apply(str);
            if (tmp.isNone()) {
                final Domains.BValue tmp1 = o.getProto();
                if (tmp1 instanceof Domains.Address) {
                    final Domains.Address a = (Domains.Address)tmp1;
                    o = store.getObj(a);
                }
                else {
                    return Domains.Undef;
                }
            } else {
                return tmp.some();
            }
        }
    }

    public static List<Domains.Str> objAllKeys(final Domains.BValue bv, final Domains.Store store) {
        final Recursive<F<Domains.Address, Set<Domains.Str>>> recur = new Recursive<>();
        recur.func = a -> {
            final Domains.Object o = store.getObj(a);
            final Set<Domains.Str> flds = o.fields();
            final Set<Domains.Str> pflds;
            final Object sth = o.intern.get(Fields.proto).some();
            if (sth instanceof Domains.Address) {
                pflds = recur.func.f((Domains.Address)sth);
            } else {
                pflds = Set.empty(Ord.hashEqualsOrd());
            }
            return flds.union(pflds);
        };
        if (bv instanceof Domains.Address) {
            return recur.func.f((Domains.Address)bv).toList();
        } else {
            return List.list();
        }
    }

    public static Domains.Store setConstr(final Domains.Store store, final Domains.Address a) {
        final Domains.Object o = store.getObj(a);
        return store.putObj(a, new Domains.Object(o.extern, o.intern.set(Fields.constructor, true)));
    }

    public static P3<Domains.Value, Domains.Store, Domains.Scratchpad> toObj(final Domains.BValue bv, final IRVar x, final Domains.Env env, final Domains.Store store, final Domains.Scratchpad pad) {
        if (bv.equals(Domains.Null) || bv.equals(Domains.Undef)) {
            return P.p(Errors.typeError, store, pad);
        } else if (bv instanceof Domains.Address) {
            if (x instanceof IRPVar) {
                final IRPVar pv = (IRPVar)x;
                return P.p(bv, store.extend(P.p(env.apply(pv), bv)), pad);
            } else {
                final IRScratch sc = (IRScratch)x;
                return P.p(bv, store, pad.update(sc, bv));
            }
        } else {
            final Domains.Address a;
            if (bv instanceof Domains.Num) {
                a = Init.Number_Addr;
            } else if (bv instanceof Domains.Bool) {
                a = Init.Boolean_Addr;
            } else if (bv instanceof Domains.Str) {
                a = Init.String_Addr;
            } else {
                throw new RuntimeException("implementation error");
            }
            final P2<Domains.Store, Domains.Address> tmp = allocObj(a, store);
            final Domains.Store store1 = tmp._1();
            final Domains.Address a1 = tmp._2();
            final Domains.Object o = store1.getObj(a1);
            final Domains.Object updatedO;
            if (bv instanceof Domains.Str) {
                final Domains.Str s = (Domains.Str)bv;
                final TreeMap<Domains.Str, Domains.BValue> init = TreeMap.treeMap(Ord.hashEqualsOrd(), P.p(Fields.length, new Domains.Num((double)s.str.length())));
                updatedO = new Domains.Object(o.extern.union(List.range(0, s.str.length()).foldLeft((acc, e) -> acc.set(new Domains.Str(e.toString()), new Domains.Str(s.str.substring(e, e + 1))), init)), o.intern);
            } else {
                updatedO = o;
            }
            final Domains.Store store2 = store1.putObj(a1, new Domains.Object(updatedO.extern, updatedO.intern.set(Fields.value, bv)));
            if (x instanceof IRPVar) {
                final IRPVar pv = (IRPVar)x;
                return P.p(a1, store2.extend(P.p(env.apply(pv), a1)), pad);
            } else {
                final IRScratch sc = (IRScratch)x;
                return P.p(a1, store2, pad.update(sc, a1));
            }
        }
    }

    public static P2<Domains.Value, Domains.Store> updateObj(final Domains.BValue bv1, final Domains.BValue bv2, final Domains.BValue bv3, final Domains.Store store) {
        if (bv1 instanceof Domains.Address && bv2 instanceof Domains.Str) {
            final Domains.Address a = (Domains.Address)bv1;
            final Domains.Str str = (Domains.Str)bv2;
            final Domains.Object o = store.getObj(a);
            final Domains.Object o1 = o.update(str, bv3);
            if (o1.getJSClass().equals(JSClass.CArray)) {
                final Domains.Num bv2num = bv2.toNum();
                final Domains.BValue bv3num;
                if (bv3 instanceof Domains.Address) {
                    bv3num = bv3;
                } else {
                    bv3num = bv3.toNum();
                }
                if (str.equals(Fields.length) && !Domains.Num.isU32(bv3num)) {
                    return P.p(Errors.rangeError, store);
                } else if (str.equals(Fields.length)) {
                    if (o.apply(Fields.length).some().lessEqual(bv3num).equals(Domains.Bool.True)) {
                        return P.p(bv3, store.putObj(a, o.update(str, bv3num)));
                    } else {
                        final Long n1, n2;
                        if (bv3num instanceof Domains.Num && o.apply(Fields.length).some() instanceof Domains.Num) {
                            n1 = ((Domains.Num)bv3num).n.longValue();
                            n2 = ((Domains.Num)o.apply(Fields.length).some()).n.longValue();
                        } else {
                            throw new RuntimeException("implementation error: inconceivable");
                        }
                        final Domains.Object o2 = List.range(n1.intValue(), n2.intValue()).foldLeft((acc, n) -> (acc.delete(new Domains.Str(n.toString())))._1(), o.update(str, bv3num));
                        return P.p(bv3, store.putObj(a, o2));
                    }
                } else if (Domains.Num.isU32(bv2num)) {
                    if ((bv2num.lessThan(o.apply(Fields.length).some())).equals(Domains.Bool.True) || bv2num.equals(new Domains.Num((double)Domains.Num.maxU32))) {
                        return P.p(bv3, store.putObj(a, o1));
                    } else {
                        final Domains.Object o2 = o1.update(Fields.length, bv2num.plus(new Domains.Num(1.0)));
                        return P.p(bv3, store.putObj(a, o2));
                    }
                } else {
                    return P.p(bv3, store.putObj(a, o1));
                }
            } else {
                return P.p(bv3, store.putObj(a, o1));
            }
        } else {
            return P.p(Errors.typeError, store);
        }
    }
}
