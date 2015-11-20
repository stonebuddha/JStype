package analysis;

import analysis.init.Init;
import fj.*;
import fj.data.*;
import ir.*;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * Created by wayne on 15/10/29.
 */
public class Domains {

    public static abstract class Term {}

    public static class StmtTerm extends Term {
        public IRStmt s;

        public StmtTerm(IRStmt s) {
            this.s = s;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof StmtTerm && s.equals(((StmtTerm) obj).s));
        }

        @Override
        public int hashCode() {
            return P.p(s).hashCode();
        }
    }

    public static class ValueTerm extends Term {
        public Value v;

        public ValueTerm(Value v) {
            this.v = v;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof ValueTerm && v.equals(((ValueTerm) obj).v));
        }

        @Override
        public int hashCode() {
            return P.p(v).hashCode();
        }
    }

    public static class Env {
        public TreeMap<IRPVar, Set<AddressSpace.Address>> env;

        public Env(TreeMap<IRPVar, Set<AddressSpace.Address>> env) {
            this.env = env;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Env && env.equals(((Env) obj).env));
        }

        @Override
        public int hashCode() {
            return P.p(env).hashCode();
        }

        public Env merge(Env rho) {
            if (this.equals(rho)) {
                return this;
            } else {
                ArrayList<P2<IRPVar, Set<AddressSpace.Address>>> list = new ArrayList<>();
                assert env.keys().equals(rho.env.keys());
                for (P2<IRPVar, Set<AddressSpace.Address>> p2 : env) {
                    list.add(P.p(p2._1(), p2._2().union(rho.env.get(p2._1()).some())));
                }
                return new Env(TreeMap.treeMap(Ord.hashEqualsOrd(), List.list(list)));
            }
        }

        public Option<Set<AddressSpace.Address>> apply(IRPVar x) {
            return env.get(x);
        }

        public Env extendAll(List<P2<IRPVar, AddressSpace.Address>> bind) {
            List<P2<IRPVar, Set<AddressSpace.Address>>> list = bind.map(p -> P.p(p._1(), AddressSpace.Addresses.apply(p._2())));
            return new Env(env.union(list));
        }

        public Env filter(F<IRPVar, Boolean> f) {
            return new Env(TreeMap.treeMap(Ord.hashEqualsOrd(), env.keys().filter(f).map(k -> P.p(k, env.get(k).some()))));
        }

        public Set<AddressSpace.Address> addrs() {
            return env.values().foldLeft(Set.union(), Set.empty(Ord.hashEqualsOrd()));
        }
    }

    public static class Store {
        public TreeMap<AddressSpace.Address, BValue> toValue;
        public TreeMap<AddressSpace.Address, Object> toObject;
        public TreeMap<AddressSpace.Address, Set<KontStack>> toKonts;
        public Set<AddressSpace.Address> weak;

        public Store(TreeMap<AddressSpace.Address, BValue> toValue, TreeMap<AddressSpace.Address, Object> toObject, TreeMap<AddressSpace.Address, Set<KontStack>> toKonts, Set<AddressSpace.Address> weak) {
            this.toValue = toValue;
            this.toObject = toObject;
            this.toKonts = toKonts;
            this.weak = weak;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            if (obj instanceof Store) {
                return (toValue.equals(((Store) obj).toValue) && toObject.equals(((Store) obj).toObject) && toKonts.equals(((Store) obj).toKonts) && weak.equals(((Store) obj).weak));
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return P.p(toValue, toObject, toKonts, weak).hashCode();
        }

        public Store merge(Store sigma) {
            TreeMap<AddressSpace.Address, BValue> _toValue;
            if (toValue.equals(sigma.toValue)) {
                _toValue = toValue;
            } else {
                _toValue = sigma.toValue.union(
                        toValue.keys().map(a -> {
                            Option<BValue> bv = sigma.toValue.get(a);
                            if (bv.isSome()) {
                                return P.p(a, toValue.get(a).some().merge(bv.some()));
                            } else {
                                return P.p(a, toValue.get(a).some());
                            }
                        })
                );
            }
            TreeMap<AddressSpace.Address, Object> _toObject;
            if (toObject.equals(sigma.toObject)) {
                _toObject = toObject;
            } else {
                _toObject = sigma.toObject.union(
                        toObject.keys().map(a -> {
                            Option<Object> o = sigma.toObject.get(a);
                            if (o.isSome()) {
                                return P.p(a, toObject.get(a).some().merge(o.some()));
                            } else {
                                return P.p(a, toObject.get(a).some());
                            }
                        })
                );
            }
            TreeMap<AddressSpace.Address, Set<KontStack>> _toKonts;
            if (toKonts.equals(sigma.toKonts)) {
                _toKonts = toKonts;
            } else {
                _toKonts = sigma.toKonts.union(
                        toKonts.keys().map(a -> {
                            Option<Set<KontStack>> ks = sigma.toKonts.get(a);
                            if (ks.isSome()) {
                                return P.p(a, toKonts.get(a).some().union(ks.some()));
                            } else {
                                return P.p(a, toKonts.get(a).some());
                            }
                        })
                );
            }
            return new Store(_toValue, _toObject, _toKonts, weak.union(sigma.weak));
        }

        public Store alloc(List<P2<AddressSpace.Address, BValue>> bind) {
            assert bind.isNotEmpty();
            P2<List<P2<AddressSpace.Address, BValue>>, List<P2<AddressSpace.Address, BValue>>> par = bind.partition(p -> toValue.contains(p._1()));
            List<P2<AddressSpace.Address, BValue>> bindw = par._1();
            List<P2<AddressSpace.Address, BValue>> bindn = par._2();
            List<P2<AddressSpace.Address, BValue>> wToValue = bindw.map(p -> P.p(p._1(), toValue.get(p._1()).some().merge(p._2())));
            return new Store(toValue.union(bindn).union(wToValue), toObject, toKonts, weak.union(Set.set(Ord.hashEqualsOrd(), List.unzip(bindw)._1())));
        }

        public Store alloc(AddressSpace.Address a, Object o) {
            Option<Object> oo = toObject.get(a);
            Object cod;
            Set<AddressSpace.Address> wk;
            if (oo.isSome()) {
                cod = oo.some().merge(o);
                wk = weak.insert(a);
            } else {
                cod = o;
                wk = weak;
            }
            return new Store(toValue, toObject.set(a, cod), toKonts, wk);
        }

        public Store alloc(AddressSpace.Address a, KontStack ks) {
            Option<Set<KontStack>> kss = toKonts.get(a);
            Set<KontStack> cod;
            if (kss.isSome()) {
                cod = kss.some().insert(ks);
            } else {
                cod = Set.set(Ord.hashEqualsOrd(), ks);
            }
            return new Store(toValue, toObject, toKonts.set(a, cod), weak);
        }

        public BValue applyAll(Set<AddressSpace.Address> as) {
            assert !as.isEmpty();
            if (as.size() == 1) {
                return apply(as.toList().head());
            } else {
                return as.toList().map(this::apply).foldLeft1(BValue::merge);
            }
        }

        public BValue apply(AddressSpace.Address a) {
            Option<BValue> obv = toValue.get(a);
            if (obv.isSome()) {
                return obv.some();
            } else {
                throw new RuntimeException("address not found");
            }
        }

        public Object getObj(AddressSpace.Address a) {
            Option<Object> oo = toObject.get(a);
            if (oo.isSome()) {
                return oo.some();
            } else {
                throw new RuntimeException("address not found");
            }
        }

        public Set<KontStack> getKont(AddressSpace.Address a) {
            return toKonts.get(a).some();
        }

        public Store extend(Set<AddressSpace.Address> as, BValue bv) {
            TreeMap<AddressSpace.Address, BValue> _toValue;
            if (as.size() == 1 && !weak.member(as.toList().head())) {
                _toValue = toValue.set(as.toList().head(), bv);
            } else {
                _toValue = as.toList().foldLeft((acc, a) -> acc.set(a, bv.merge(toValue.get(a).some())), toValue);
            }
            return new Store(_toValue, toObject, toKonts, weak);
        }

        public Store putObj(AddressSpace.Address a, Object o) {
            if (weak.member(a)) {
                return putObjWeak(a, o);
            } else {
                return putObjStrong(a, o);
            }
        }

        public Store putObjStrong(AddressSpace.Address a, Object o) {
            return new Store(toValue, toObject.set(a, o), toKonts, weak);
        }

        public Store putObjWeak(AddressSpace.Address a, Object o) {
            return new Store(toValue, toObject.set(a, o.merge(toObject.get(a).some())), toKonts, weak);
        }

        public Boolean isStrong(AddressSpace.Address a) {
            return !(weak.member(a));
        }

        public Boolean toValueContains(Set<AddressSpace.Address> as) {
            return !as.filter(a -> toValue.contains(a)).isEmpty();
        }

        public Store weaken(Set<Integer> valIDs, Set<Integer> objIDs) {
            Set<AddressSpace.Address> wkn = Set.set(Ord.hashEqualsOrd(),
                    toValue.keys().filter(a -> valIDs.member(Trace.getBase(a))).append(
                    toObject.keys().filter(a -> objIDs.member(Trace.getBase(a)))));
            return new Store(toValue, toObject, toKonts, weak.union(wkn));
        }

        public Store lightGC(Set<Integer> ids) {
            TreeMap<AddressSpace.Address, BValue> toKeep = TreeMap.treeMap(Ord.hashEqualsOrd(),
                    toValue.keys().filter(a -> !ids.member(Trace.getBase(a)) || weak.member(a)).map(a -> P.p(a, toValue.get(a).some())));
            return new Store(toKeep, toObject, toKonts, weak);
        }

        public Store fullGC(Set<AddressSpace.Address> vRoots, Set<AddressSpace.Address> oRoots, Set<AddressSpace.Address> kRoots) {
            return null; // TODO
        }

        public P2<Store, Store> prune(Set<AddressSpace.Address> vRoots, Set<AddressSpace.Address> oRoots) {
            return null; // TODO
        }
    }

    public static class Scratchpad {
        public Seq<BValue> mem;

        public Scratchpad(Seq<BValue> mem) {
            this.mem = mem;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Scratchpad && mem.equals(((Scratchpad) obj).mem));
        }

        @Override
        public int hashCode() {
            return P.p(mem).hashCode();
        }

        public Scratchpad merge(Scratchpad pad) {
            assert mem.length() == pad.mem.length();
            if (this.equals(pad)) {
                return this;
            } else {
                ArrayList<BValue> bvs = new ArrayList<>();
                for (int i = 0; i < mem.length(); i += 1) {
                    bvs.add(mem.index(i).merge(pad.mem.index(i)));
                }
                return new Scratchpad(Seq.seq(List.list(bvs)));
            }
        }

        public BValue apply(IRScratch x) {
            return mem.index(x.n);
        }

        public Scratchpad update(IRScratch x, BValue bv) {
            return new Scratchpad(mem.update(x.n, bv));
        }

        public Set<AddressSpace.Address> addrs() {
            return mem.foldLeft((sa, bv) -> sa.union(bv.as), Set.empty(Ord.hashEqualsOrd()));
        }

        public static Scratchpad apply(Integer len) {
            ArrayList<BValue> bvs = new ArrayList<>(len);
            for (int i = 0; i < len; i += 1) {
                bvs.add(i, Undef.BV);
            }
            return new Scratchpad(Seq.seq(List.list(bvs)));
        }
    }

    public static class Domain {}
    public static final Domain DNum = new Domain();
    public static final Domain DBool = new Domain();
    public static final Domain DStr = new Domain();
    public static final Domain DAddr = new Domain();
    public static final Domain DNull = new Domain();
    public static final Domain DUndef = new Domain();

    public static abstract class Value {}

    public static class EValue extends Value {
        public BValue bv;

        public EValue(BValue bv) {
            this.bv = bv;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof EValue && bv.equals(((EValue) obj).bv));
        }

        @Override
        public int hashCode() {
            return P.p(bv).hashCode();
        }
    }

    public static class JValue extends Value {
        public String lbl;
        public BValue bv;

        public JValue(String lbl, BValue bv) {
            this.lbl = lbl;
            this.bv = bv;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof JValue && lbl.equals(((JValue) obj).lbl) && bv.equals(((JValue) obj).bv));
        }

        @Override
        public int hashCode() {
            return P.p(lbl, bv).hashCode();
        }
    }

    public static class BValue extends Value {
        public Num n;
        public Bool b;
        public Str str;
        public Set<AddressSpace.Address> as;
        public Null nil;
        public Undef undef;

        Set<Domain> types;

        public BValue(Num n, Bool b, Str str, Set<AddressSpace.Address> as, Null nil, Undef undef) {
            this.n = n;
            this.b = b;
            this.str = str;
            this.as = as;
            this.nil = nil;
            this.undef = undef;

            ArrayList<Domain> doms = new ArrayList<>();
            if (!n.equals(Num.Bot)) doms.add(DNum);
            if (!b.equals(Bool.Bot)) doms.add(DBool);
            if (!str.equals(Str.Bot)) doms.add(DStr);
            if (!as.isEmpty()) doms.add(DAddr);
            if (!nil.equals(Null.Bot)) doms.add(DNull);
            if (!undef.equals(Undef.Bot)) doms.add(DUndef);
            this.types = Set.set(Ord.hashEqualsOrd(), List.list(doms));
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            if (obj instanceof BValue) {
                BValue bv = (BValue)obj;
                return (n.equals(bv.n) && b.equals(bv.b) && str.equals(bv.str) && as.equals(bv.as) && nil.equals(bv.nil) && undef.equals(bv.undef));
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return P.p(n, b, str, as, nil, undef).hashCode();
        }

        public BValue merge(BValue bv) {
            return new BValue(
                    n.merge(bv.n),
                    b.merge(bv.b),
                    str.merge(bv.str),
                    as.union(bv.as),
                    nil.merge(bv.nil),
                    undef.merge(bv.undef));
        }

        public BValue plus(BValue bv) {
            return Num.inject(n.plus(bv.n));
        }

        public BValue minus(BValue bv) {
            return Num.inject(n.minus(bv.n));
        }

        public BValue times(BValue bv) {
            return Num.inject(n.times(bv.n));
        }

        public BValue divide(BValue bv) {
            return Num.inject(n.divide(bv.n));
        }

        public BValue mod(BValue bv) {
            return Num.inject(n.mod(bv.n));
        }

        public BValue shl(BValue bv) {
            return Num.inject(n.shl(bv.n));
        }

        public BValue sar(BValue bv) {
            return Num.inject(n.sar(bv.n));
        }

        public BValue shr(BValue bv) {
            return Num.inject(n.shr(bv.n));
        }

        public BValue lessThan(BValue bv) {
            return Bool.inject(n.lessThan(bv.n));
        }

        public BValue lessEqual(BValue bv) {
            return Bool.inject(n.lessEqual(bv.n));
        }

        public BValue and(BValue bv) {
            return Num.inject(n.and(bv.n));
        }

        public BValue or(BValue bv) {
            return Num.inject(n.or(bv.n));
        }

        public BValue xor(BValue bv) {
            return Num.inject(n.xor(bv.n));
        }

        public BValue logicalAnd(BValue bv) {
            return Bool.inject(b.logicalAnd(bv.b));
        }

        public BValue logicalOr(BValue bv) {
            return Bool.inject(b.logicalOr(bv.b));
        }

        public BValue strConcat(BValue bv) {
            return Str.inject(str.strConcat(bv.str));
        }

        public BValue strLessThan(BValue bv) {
            return Bool.inject(str.strLessThan(bv.str));
        }

        public BValue strLessEqual(BValue bv) {
            return Bool.inject(str.strLessEqual(bv.str));
        }

        public BValue negate() {
            return Num.inject(n.negate());
        }

        public BValue not() {
            return Num.inject(n.not());
        }

        public BValue logicalNot() {
            return Bool.inject(b.logicalNot());
        }

        public BValue isPrim() {
            Bool notaddr = Bool.alpha(!types.member(DAddr));
            Bool hasprim = Bool.alpha(!defAddr());
            return Bool.inject(notaddr.merge(hasprim));
        }

        public BValue toBool() {
            Bool res = Bool.Bot;
            for (Domain dom : types) {
                if (!res.equals(Bool.Top)) {
                    Bool b1;
                    if (dom.equals(DNum)) {
                        if (n.defNaN() || n.def0()) {
                            b1 = Bool.False;
                        } else if (n.defNotNaN() && n.defNot0()) {
                            b1 = Bool.True;
                        } else {
                            b1 = Bool.Top;
                        }
                    } else if (dom.equals(DBool)) {
                        b1 = b;
                    } else if (dom.equals(DStr)) {
                        if (str.defEmpty()) {
                            b1 = Bool.False;
                        } else if (str.defNotEmpty()) {
                            b1 = Bool.True;
                        } else {
                            b1 = Bool.Top;
                        }
                    } else if (dom.equals(DAddr)) {
                        b1 = Bool.True;
                    } else {
                        b1 = Bool.False;
                    }
                    res = res.merge(b1);
                }
            }
            return Bool.inject(res);
        }

        public BValue toStr() {
            Str res = Str.Bot;
            for (Domain dom : types) {
                if (!res.equals(Str.Top)) {
                    Str str1;
                    if (dom.equals(DNum)) {
                        str1 = n.toStr();
                    } else if (dom.equals(DBool)) {
                        str1 = b.toStr();
                    } else if (dom.equals(DStr)) {
                        str1 = str;
                    } else if (dom.equals(DNull)) {
                        str1 = Str.alpha("null");
                    } else if (dom.equals(DUndef)) {
                        str1 = Str.alpha("undefined");
                    } else {
                        str1 = Str.Top;
                    }
                    res = res.merge(str1);
                }
            }
            return Str.inject(res);
        }

        public BValue toNum() {
            Num res = Num.Bot;
            for (Domain dom : types) {
                if (!res.equals(Num.Top)) {
                    Num n1;
                    if (dom.equals(DNum)) {
                        n1 = n;
                    } else if (dom.equals(DBool)) {
                        n1 = b.toNum();
                    } else if (dom.equals(DStr)) {
                        n1 = str.toNum();
                    } else if (dom.equals(DNull)) {
                        n1 = Num.alpha(0.0);
                    } else if (dom.equals(DUndef)) {
                        n1 = Num.alpha(Double.NaN);
                    } else {
                        n1 = Num.Top;
                    }
                    res = res.merge(n1);
                }
            }
            return Num.inject(res);
        }

        public final Set<Domain> sorts = Set.empty(Ord.<Domain>hashEqualsOrd()).
                union(n == Num.Bot ? Set.empty(Ord.<Domain>hashEqualsOrd()) : Set.single(Ord.<Domain>hashEqualsOrd(), DNum)).
                union(b == Bool.Bot ? Set.empty(Ord.<Domain>hashEqualsOrd()) : Set.single(Ord.<Domain>hashEqualsOrd(), DBool)).
                union(str == Str.Bot ? Set.empty(Ord.<Domain>hashEqualsOrd()) : Set.single(Ord.<Domain>hashEqualsOrd(), DStr)).
                union(as.isEmpty() ? Set.empty(Ord.<Domain>hashEqualsOrd()) : Set.single(Ord.<Domain>hashEqualsOrd(), DAddr)).
                union(nil == Null.Bot ? Set.empty(Ord.<Domain>hashEqualsOrd()) : Set.single(Ord.<Domain>hashEqualsOrd(), DNull)).
                union(undef == Undef.Bot ? Set.empty(Ord.<Domain>hashEqualsOrd()) : Set.single(Ord.<Domain>hashEqualsOrd(), DUndef));

        public Boolean isBot() {
            return types.isEmpty();
        }

        public Boolean defNum() {
            return (types.size() == 1 && types.member(DNum));
        }

        public Boolean defBool() {
            return (types.size() == 1 && types.member(DBool));
        }

        public Boolean defStr() {
            return (types.size() == 1 && types.member(DStr));
        }

        public Boolean defAddr() {
            return (types.size() == 1 && types.member(DAddr));
        }

        public Boolean defNull() {
            return (types.size() == 1 && types.member(DNull));
        }

        public Boolean defUndef() {
            return (types.size() == 1 && types.member(DUndef));
        }

        public BValue onlyNum() {
            return Num.inject(n);
        }

        public BValue onlyBool() {
            return Bool.inject(b);
        }

        public BValue onlyStr() {
            return Str.inject(str);
        }

        public BValue onlyAddr() {
            return AddressSpace.Addresses.inject(as);
        }

        public BValue removeNullAndUndef() {
            return new BValue(n, b, str, as, Null.Bot, Undef.Bot);
        }

        public P2<BValue, BValue> filterBy(Utils.Filters.BVFilter bvf, Store store) {
            if (bvf == Utils.Filters.IsFunc) {
                return P.p(onlyAddr(), this);
            }
            else if (bvf == Utils.Filters.IsUndefNull) {
                return P.p(removeNullAndUndef(), this);
            }
            return null;
        }

        public static final BValue Bot = new BValue(Num.Bot, Bool.Bot, Str.Bot, Set.empty(Ord.hashEqualsOrd()), Null.Bot, Undef.Bot);
    }

    public static abstract class Num {
        public Num merge(Num n) {
            if (this.equals(n)) {
                return this;
            } else if (this.equals(NBot)) {
                return n;
            } else if (n.equals(NBot)) {
                return this;
            } else if (this.equals(NTop) || n.equals(NTop)) {
                return Top;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return Top;
            } else if (this.equals(Inf) || n.equals(Inf)) {
                return Top;
            } else if (this.equals(NInf) || n.equals(NInf)) {
                return Top;
            } else {
                return NReal;
            }
        }

        public Bool strictEqual(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Bool.Bot;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return Bool.False;
            } else if (this.equals(NTop) || n.equals(NTop)) {
                return Bool.Top;
            } else if (this instanceof NConst && n instanceof NConst) {
                return Bool.alpha(((NConst)this).d.equals(((NConst)n).d));
            } else if (this.equals(Inf) && n.equals(NReal)) {
                return Bool.False;
            } else if (this.equals(NReal) && n.equals(Inf)) {
                return Bool.False;
            } else if (this.equals(NInf) && n.equals(NReal)) {
                return Bool.False;
            } else if (this.equals(NReal) && n.equals(NInf)) {
                return Bool.False;
            } else {
                return Bool.Top;
            }
        }

        public Num plus(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Bot;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return NaN;
            } else if (this.equals(NTop) || n.equals(NTop)) {
                return Top;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst(((NConst)this).d + ((NConst)n).d);
            } else if (this.equals(NReal) && n.equals(Inf)) {
                return Inf;
            } else if (this.equals(Inf) && n.equals(NReal)) {
                return Inf;
            } else if (this.equals(NReal) && n.equals(NInf)) {
                return NInf;
            } else if (this.equals(NInf) && n.equals(NReal)) {
                return NInf;
            } else {
                return NReal;
            }
        }

        public Num minus(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Bot;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return NaN;
            } else if (this.equals(NTop) || n.equals(NTop)) {
                return Top;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst(((NConst)this).d - ((NConst)n).d);
            } else if (this.equals(NReal) && n.equals(Inf)) {
                return NInf;
            } else if (this.equals(NInf) && n.equals(NReal)) {
                return NInf;
            } else if (this.equals(NReal) && n.equals(NInf)) {
                return Inf;
            } else if (this.equals(Inf) && n.equals(NReal)) {
                return Inf;
            } else {
                return NReal;
            }
        }

        public Num times(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Bot;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return NaN;
            } else if (this.equals(NTop) || n.equals(NTop)) {
                return Top;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst(((NConst)this).d * ((NConst)n).d);
            } else if ((this.equals(NReal) && n.equals(Inf)) || (this.equals(Inf) && n.equals(NReal))) {
                return Top;
            } else if ((this.equals(NReal) && n.equals(NInf) || (this.equals(NInf) && n.equals(NReal)))) {
                return Top;
            } else {
                return NReal;
            }
        }

        public Num divide(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Bot;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return NaN;
            } else if (this.equals(NTop) || n.equals(NTop)) {
                return Top;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst(((NConst)this).d / ((NConst)n).d);
            } else if (this.equals(NReal) && (n.equals(Inf) || n.equals(NInf))) {
                return new NConst(0.0);
            } else {
                return Top;
            }
        }

        public Num mod(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Bot;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return NaN;
            } else if (this.equals(NTop) || n.equals(NTop)) {
                return Top;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst(((NConst)this).d % ((NConst)n).d);
            } else if ((this.equals(Inf) || this.equals((NInf))) && (n.equals(NReal))) {
                return NaN;
            } else {
                return NReal;
            }
        }

        public Num shl(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Bot;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst((double)(((NConst)this).d.longValue() << ((NConst)n).d.longValue()));
            } else {
                return NReal;
            }
        }

        public Num sar(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Bot;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst((double)(((NConst)this).d.longValue() >> ((NConst)n).d.longValue()));
            } else {
                return NReal;
            }
        }

        public Num shr(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Bot;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst((double)(((NConst)this).d.longValue() >>> ((NConst)n).d.longValue()));
            } else {
                return NReal;
            }
        }

        public Bool lessThan(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Bool.Bot;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return Bool.False;
            } else if (this.equals(NTop) || n.equals(NTop)) {
                return Bool.Top;
            } else if (this instanceof NConst && n instanceof NConst) {
                return Bool.alpha(((NConst)this).d < ((NConst)n).d);
            } else if ((this.equals(NReal) && n.equals(Inf))
                    || (this.equals(NInf) && n.equals(NReal))) {
                return Bool.True;
            } else if ((this.equals(Inf) && n.equals(NReal))
                    || (this.equals(NReal) && n.equals(NInf))) {
                return Bool.False;
            } else {
                return Bool.Top;
            }
        }

        public Bool lessEqual(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Bool.Bot;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return Bool.False;
            } else if (this.equals(NTop) || n.equals(NTop)) {
                return Bool.Top;
            } else if (this instanceof NConst && n instanceof NConst) {
                return Bool.alpha(((NConst)this).d <= ((NConst)n).d);
            } else if ((this.equals(NReal) && n.equals(Inf))
                    || (this.equals(NInf) && n.equals(NReal))) {
                return Bool.True;
            } else if ((this.equals(Inf) && n.equals(NReal))
                    || (this.equals(NReal) && n.equals(NInf))) {
                return Bool.False;
            } else {
                return Bool.Top;
            }
        }

        public Num and(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Num.Bot;
            } else if (this.equals(NaN) || n.equals((NaN))) {
                return Num.Zero;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst((double)(((NConst)this).d.longValue() & ((NConst)n).d.longValue()));
            } else {
                return NReal;
            }
        }

        public Num or(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Num.Bot;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst((double)(((NConst)this).d.longValue() | ((NConst)n).d.longValue()));
            } else {
                return NReal;
            }
        }

        public Num xor(Num n) {
            if (this.equals(NBot) || n.equals(NBot)) {
                return Num.Bot;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst((double)(((NConst)this).d.longValue() ^ ((NConst)n).d.longValue()));
            } else {
                return NReal;
            }
        }

        public Num negate() {
            if (this instanceof NConst) {
                return new NConst(-((NConst)this).d);
            } else {
                return this;
            }
        }

        public Num not() {
            if (this instanceof NConst) {
                return new NConst((double)(~((NConst)this).d.longValue()));
            } else {
                return this;
            }
        }

        public Str toStr() {
            if (this instanceof NConst) {
                return Str.alpha(((NConst)this).d.toString());
            } else if (this.equals(NTop)) {
                return Str.Top;
            } else if (this.equals(NReal)) {
                return Str.NumStr;
            } else {
                return Str.Bot;
            }
        }

        public Boolean defNaN() {
            return this.equals(NaN);
        }

        public Boolean defNotNaN() {
            return (!this.equals(NaN) && !this.equals(Top));
        }

        public Boolean def0() {
            return this.equals(Zero);
        }

        public Boolean defNot0() {
            return (this.equals(Bot) || (this instanceof NConst && ((NConst)this).d != 0));
        }

        public static final Num Top = NTop;
        public static final Num Bot = NBot;
        public static final Num Zero = new NConst(0.0);
        public static final Num NaN = new NConst(Double.NaN);
        public static final Num Inf = new NConst(Double.POSITIVE_INFINITY);
        public static final Num NInf = new NConst(Double.NEGATIVE_INFINITY);
        public static final Num U32 = NReal;
        public static final Long maxU32 = 4294967295L;

        public static Num alpha(Double d) {
            return new NConst(d);
        }

        public static Boolean maybeU32(BValue bv) {
            Num n = bv.n;
            if (n.equals(NTop) || n.equals(NReal)) {
                return true;
            } else if (n instanceof NConst) {
                Double d = ((NConst) n).d;
                return (d.longValue() == d && d >= 0 && d <= maxU32);
            } else {
                return false;
            }
        }

        public static Boolean maybeNotU32(BValue bv) {
            if (!bv.defNum()) {
                return true;
            } else {
                Num n = bv.n;
                if (n instanceof NConst) {
                    Double d = ((NConst) n).d;
                    return !(d.longValue() == d && d >= 0 && d <= maxU32);
                } else {
                    return true;
                }
            }
        }

        public static BValue inject(Num n) {
            return new BValue(n, Bool.Bot, Str.Bot, Set.empty(Ord.hashEqualsOrd()), Null.Bot, Undef.Bot);
        }
    }

    public static final Num NBot = new Num() {};

    public static final Num NTop = new Num() {};

    public static final Num NReal = new Num() {};

    public static class NConst extends Num {
        public Double d;

        public NConst(Double d) {
            this.d = d;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            if (obj instanceof NConst) {
                return (d.equals(((NConst) obj).d) || (d.isNaN() && ((NConst) obj).d.isNaN()));
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return P.p(d).hashCode();
        }
    }

    public static abstract class Bool {
        public Bool merge(Bool b) {
            if (this.equals(b)) {
                return this;
            } else if (this.equals(BTop) || b.equals(BTop) || (this.equals(BTrue) && b.equals(BFalse)) || (this.equals(BFalse) && b.equals(BTrue))) {
                return Top;
            } else if (this.equals(BBot)) {
                return b;
            } else if (b.equals(BBot)) {
                return this;
            } else {
                throw new RuntimeException("suppress false compiler warning");
            }
        }

        public Bool strictEqual(Bool b) {
            if (this.equals(BBot) || b.equals(BBot)) {
                return Bot;
            } else if (this.equals(BTop) || b.equals(BTop)) {
                return Top;
            } else if ((this.equals(BTrue) && b.equals(BTrue))
                    || (this.equals(BFalse) && b.equals(BFalse))) {
                return True;
            } else {
                return False;
            }
        }

        public Bool logicalAnd(Bool b) {
            if (this.equals(BBot) || b.equals(BBot)) {
                return Bot;
            } else if (this.equals(BFalse) || b.equals(BFalse)) {
                return False;
            } else if (this.equals(BTop) || b.equals(BTop)) {
                return Top;
            } else {
                return True;
            }
        }

        public Bool logicalOr(Bool b) {
            if (this.equals(BBot) || b.equals(BBot)) {
                return Bot;
            } else if (this.equals(BTrue) || b.equals(BTrue)) {
                return True;
            } else if (this.equals(BTop) || b.equals(BTop)) {
                return Top;
            } else {
                return False;
            }
        }

        public Bool logicalNot() {
            if (this.equals(BBot)) {
                return Bot;
            } else if (this.equals(BTrue)) {
                return False;
            } else if (this.equals(BFalse)) {
                return True;
            } else {
                return Top;
            }
        }

        public Num toNum() {
            if (this.equals(BBot)) {
                return Num.Bot;
            } else if (this.equals(BTrue)) {
                return Num.alpha(1.0);
            } else if (this.equals(BFalse)) {
                return Num.alpha(0.0);
            } else {
                return Num.Top;
            }
        }

        public Str toStr() {
            if (this.equals(BBot)) {
                return Str.Bot;
            } else if (this.equals(BTrue)) {
                return Str.alpha("true");
            } else if (this.equals(BFalse)) {
                return Str.alpha("false");
            } else {
                return Str.Top;
            }
        }

        public static final Bool Top = BTop;
        public static final Bool Bot = BBot;
        public static final Bool True = BTrue;
        public static final Bool False = BFalse;
        public static final BValue TrueBV = inject(True);
        public static final BValue FalseBV = inject(False);
        public static final BValue TopBV = inject(Top);

        public static Bool alpha(Boolean b) {
            if (b) {
                return True;
            } else {
                return False;
            }
        }

        public static BValue inject(Bool b) {
            return new BValue(Num.Bot, b, Str.Bot, Set.empty(Ord.hashEqualsOrd()), Null.Bot, Undef.Bot);
        }
    }

    public static final Bool BBot = new Bool() {};

    public static final Bool BTrue = new Bool() {};

    public static final Bool BFalse = new Bool() {};

    public static final Bool BTop = new Bool() {};

    public static abstract class Str {
        public Str merge(Str str) {
            if (this.equals(str)) {
                return this;
            } else if (this.equals(SBot)) {
                return str;
            } else if (str.equals(SBot)) {
                return this;
            } else if (this instanceof SConstNum && str instanceof SConstNum) {
                return SNum;
            } else if ((this instanceof SConstNum && str instanceof SConstNotSplNorNum)
                    || (this instanceof SConstNotSplNorNum && str instanceof SConstNum)) {
                return SNotSpl;
            } else if ((this instanceof SConstNum && str instanceof SConstSpl)
                    || (this instanceof SConstSpl && str instanceof SConstNum)) {
                return Top;
            } else if ((this instanceof SConstNum && str.equals(SNotSplNorNum))
                    || (this.equals(SNotSplNorNum) && str instanceof SConstNum)) {
                return SNotSpl;
            } else if ((this instanceof SConstNum && str.equals(SSpl))
                    || (this.equals(SSpl) && str instanceof SConstNum)) {
                return Top;
            } else if ((this instanceof SConstNum && str.equals(SNotNum))
                    || (this.equals(SNotNum) && str instanceof SConstNum)) {
                return Top;
            } else if (this instanceof SConstNotSplNorNum && str instanceof SConstNotSplNorNum) {
                return SNotSplNorNum;
            } else if ((this instanceof SConstNotSplNorNum && str instanceof SConstSpl)
                    || (this instanceof SConstSpl && str instanceof SConstNotSplNorNum)) {
                return SNotNum;
            } else if ((this instanceof SConstNotSplNorNum && str.equals(SNum))
                    || (this.equals(SNum) && str instanceof SConstNotSplNorNum)) {
                return SNotSpl;
            } else if ((this instanceof SConstNotSplNorNum && str.equals(SSpl))
                    || (this.equals(SSpl) && str instanceof SConstNotSplNorNum)) {
                return SNotNum;
            } else if (this instanceof SConstSpl && str instanceof SConstSpl) {
                return SSpl;
            } else if ((this instanceof SConstSpl && str.equals(SNum))
                    || (this.equals(SNum) && str instanceof SConstSpl)) {
                return Top;
            } else if ((this instanceof SConstSpl && str.equals(SNotSplNorNum))
                    || (this.equals(SNotSplNorNum) && str instanceof SConstSpl)) {
                return SNotNum;
            } else if ((this instanceof SConstSpl && str.equals(SNotSpl))
                    || (this.equals(SNotSpl) && str instanceof SConstSpl)) {
                return Top;
            } else if ((this.equals(SNum) && str.equals(SNotSplNorNum))
                    || (this.equals(SNotSplNorNum) && str.equals(SNum))) {
                return SNotSpl;
            } else if ((this.equals(SNum) && str.equals(SSpl))
                    || (this.equals(SSpl) && str.equals(SNum))) {
                return Top;
            } else if ((this.equals(SNum) && str.equals(SNotNum))
                    || (this.equals(SNotNum) && str.equals(SNum))) {
                return Top;
            } else if ((this.equals(SNotSplNorNum) && str.equals(SSpl))
                    || (this.equals(SNotSpl) && str.equals(SNotSplNorNum))) {
                return SNotNum;
            } else if ((this.equals(SSpl) && str.equals(SNotSpl))
                    || (this.equals(SNotSpl) && str.equals(SSpl))) {
                return Top;
            } else if ((this.equals(SNotNum) && str.equals(SNotSpl))
                    || (this.equals(SNotSpl) && str.equals(SNotNum))) {
                return Top;
            } else if (this.partialLessEqual(str)) {
                return str;
            } else if (str.partialLessEqual(this)) {
                return str;
            } else {
                throw new RuntimeException("Incorrect implementation of string lattice");
            }
        }

        public Boolean partialLessEqual(Str str) {
            if (this.equals(SBot) || str.equals(STop)) {
                return true;
            } else if (this.equals(str)) {
                return true;
            } else if ((this instanceof SConstNum && str.equals(SNum))
                    || (this instanceof SConstNum && str.equals(SNotSpl))
                    || (this instanceof SConstNotSplNorNum && str.equals(SNotSplNorNum))
                    || (this instanceof SConstNotSplNorNum && str.equals(SNotSpl))
                    || (this instanceof SConstNotSplNorNum && str.equals(SNotNum))
                    || (this instanceof SConstSpl && str.equals(SSpl))
                    || (this instanceof SConstSpl && str.equals(SNotNum))
                    || (this.equals(SNum) && str.equals(SNotSpl))
                    || (this.equals(SSpl) && str.equals(SNotNum))
                    || (this.equals(SNotSplNorNum) && str.equals(SNotNum))
                    || (this.equals(SNotSplNorNum) && str.equals(SNotSpl))) {
                return true;
            } else {
                return false;
            }
        }

        public Boolean notPartialLessEqual(Str str) {
            return !(this.partialLessEqual(str));
        }

        public Bool strictEqual(Str str) {
            if (this instanceof SConstNum && str instanceof SConstNum) {
                return Bool.alpha(((SConstNum)this).str.equals(((SConstNum)str).str));
            } else if (this instanceof SConstNotSplNorNum && str instanceof SConstNotSplNorNum) {
                return Bool.alpha(((SConstNotSplNorNum)this).str.equals(((SConstNotSplNorNum)str).str));
            } else if (this instanceof SConstSpl && str instanceof SConstSpl) {
                return Bool.alpha(((SConstSpl)this).str.equals(((SConstSpl)str).str));
            } else if ((this.equals(SNotSpl) && str.equals(SNotNum))
                    || (this.equals(SNotNum) && str.equals(SNotSpl))) {
                return Bool.Top;
            } else if (this.notPartialLessEqual(str) && str.notPartialLessEqual(this)) {
                return Bool.False;
            } else if (this.equals(SBot) || str.equals(SBot)) {
                return Bool.Bot;
            } else {
                return Bool.Top;
            }
        }

        public Str strConcat(Str str) {
            if (this.equals(SBot) || str.equals(SBot)) {
                return Bot;
            } else if (this.equals(STop) && str instanceof SConstNum) {
                return SNotSpl;
            } else if (this.equals(STop) && str instanceof SConstSpl) {
                return SNotNum;
            } else if (this.equals(STop) && str.equals(SNum)) {
                return SNotSpl;
            } else if (this.equals(STop) && str.equals(SSpl)) {
                return SNotNum;
            } else if (this.equals(STop)) {
                return Top;
            } else if (this.equals(Str.Empty)) {
                return str;
            } else if (str.equals(Str.Empty)) {
                return this;
            } else if (Str.isExact(this) && Str.isExact(str)) {
                return Str.alpha(Str.getExact(this).some() + Str.getExact(str).some());
            } else if (this instanceof SConstNum) {
                if (str.equals(SNum)) {
                    return SNotSpl;
                } else if (str.equals(SNotSplNorNum)) {
                    return SNotSpl;
                } else if (str.equals(SSpl)) {
                    return SNotSpl;
                } else if (str.equals(SNotSpl)) {
                    return SNotSpl;
                } else if (str.equals(SNotNum)) {
                    return SNotSpl;
                } else if (str.equals(STop)) {
                    return SNotSpl;
                }
            } else if (this instanceof SConstNotSplNorNum) {
                if (str.equals(SNum)) {
                    return SNotSpl;
                } else if (str.equals(SNotSplNorNum)) {
                    return SNotNum;
                } else if (str.equals(SSpl)) {
                    return SNotNum;
                } else if (str.equals(SNotSpl)) {
                    return Top;
                } else if (str.equals(SNotNum)) {
                    return SNotNum;
                } else if (str.equals(STop)) {
                    return Top;
                }
            } else if (this instanceof SConstSpl) {
                if (str.equals(SNum)) {
                    return SNotSpl;
                } else if (str.equals(SNotSplNorNum)) {
                    return SNotNum;
                } else if (str.equals(SSpl)) {
                    return SNotSplNorNum;
                } else if (str.equals(SNotSpl)) {
                    return SNotNum;
                } else if (str.equals(SNotNum)) {
                    return SNotNum;
                } else if (str.equals(STop)) {
                    return SNotNum;
                }
            } else if (this.equals(SNum)) {
                if (str instanceof SConstNum) {
                    return SNotSpl;
                } else if (str instanceof SConstNotSplNorNum) {
                    return SNotSpl;
                } else if (str instanceof SConstSpl) {
                    return SNotSplNorNum;
                } else if (str.equals(SNum)) {
                    return SNotSpl;
                } else if (str.equals(SNotSplNorNum)) {
                    return SNotSpl;
                } else if (str.equals(SSpl)) {
                    return SNotSplNorNum;
                } else if (str.equals(SNotSpl)) {
                    return SNotSpl;
                } else if (str.equals(SNotNum)) {
                    return SNotSpl;
                } else if (str.equals(STop)) {
                    return SNotSpl;
                }
            } else if (this.equals(SNotSplNorNum)) {
                if (str instanceof SConstNum) {
                    return SNotSpl;
                } else if (str instanceof SConstNotSplNorNum) {
                    return SNotNum;
                } else if (str instanceof SConstSpl) {
                    return SNotNum;
                } else if (str.equals(SNum)) {
                    return SNotSpl;
                } else if (str.equals(SNotSplNorNum)) {
                    return SNotNum;
                } else if (str.equals(SSpl)) {
                    return SNotNum;
                } else if (str.equals(SNotSpl)) {
                    return Top;
                } else if (str.equals(SNotNum)) {
                    return SNotNum;
                } else if (str.equals(STop)) {
                    return Top;
                }
            } else if (this.equals(SSpl)) {
                if (str instanceof SConstNum) {
                    return SNotSplNorNum;
                } else if (str instanceof SConstNotSplNorNum) {
                    return SNotNum;
                } else if (str instanceof SConstSpl) {
                    return SNotSplNorNum;
                } else if (str.equals(SNum)) {
                    return SNotSplNorNum;
                } else if (str.equals(SNotSplNorNum)) {
                    return SNotNum;
                } else if (str.equals(SSpl)) {
                    return SNotSplNorNum;
                } else if (str.equals(SNotSpl)) {
                    return SNotNum;
                } else if (str.equals(SNotNum)) {
                    return SNotNum;
                } else if (str.equals(STop)) {
                    return SNotNum;
                }
            } else if (this.equals(SNotSpl)) {
                if (str instanceof SConstNum) {
                    return SNotSpl;
                } else if (str instanceof SConstNotSplNorNum) {
                    return Top;
                } else if (str instanceof SConstSpl) {
                    return SNotNum;
                } else if (str.equals(SNum)) {
                    return SNotSpl;
                } else if (str.equals(SNotSplNorNum)) {
                    return Top;
                } else if (str.equals(SSpl)) {
                    return SNotNum;
                } else if (str.equals(SNotSpl)) {
                    return Top;
                } else if (str.equals(SNotNum)) {
                    return Top;
                } else if (str.equals(STop)) {
                    return Top;
                }
            } else if (this.equals(SNotNum)) {
                if (str instanceof SConstNum) {
                    return SNotSpl;
                } else if (str instanceof SConstNotSplNorNum) {
                    return SNotNum;
                } else if (str instanceof SConstSpl) {
                    return SNotNum;
                } else if (str.equals(SNum)) {
                    return SNotSpl;
                } else if (str.equals(SNotSplNorNum)) {
                    return SNotNum;
                } else if (str.equals(SSpl)) {
                    return SNotNum;
                } else if (str.equals(SNotSpl)) {
                    return Top;
                } else if (str.equals(SNotNum)) {
                    return SNotNum;
                } else if (str.equals(STop)) {
                    return Top;
                }
            }
            throw new RuntimeException("incorrect implementation of string lattice");
        }

        public Bool strLessThan(Str str) {
            if (this.equals(SBot) || str.equals(SBot)) {
                return Bool.Bot;
            } else if (Str.isExact(this) && Str.isExact(str)) {
                return Bool.alpha(Str.getExact(this).some().compareTo(Str.getExact(str).some()) < 0);
            } else if ((this instanceof SConstNum && str instanceof SConstSpl)
                    || (this instanceof SConstNum && str.equals(SSpl))
                    || (this.equals(SNum) && str instanceof SConstSpl)
                    || (this.equals(SNum) && str.equals(SSpl))) {
                return Bool.True;
            } else if ((this instanceof SConstSpl && str instanceof SConstNum)
                    || (this instanceof SConstSpl && str.equals(SNum))
                    || (this.equals(SSpl) && str instanceof SConstNum)
                    || (this.equals(SSpl) && str.equals(SNum))) {
                return Bool.False;
            } else {
                return Bool.Top;
            }
        }

        public Bool strLessEqual(Str str) {
            if (this.equals(SBot) || str.equals(SBot)) {
                return Bool.Bot;
            } else if (Str.isExact(this) && Str.isExact(str)) {
                return Bool.alpha(Str.getExact(this).some().compareTo(Str.getExact(str).some()) <= 0);
            } else if ((this instanceof SConstNum && str instanceof SConstSpl)
                    || (this instanceof SConstNum && str.equals(SSpl))
                    || (this.equals(SNum) && str instanceof SConstSpl)
                    || (this.equals(SNum) && str.equals(SSpl))) {
                return Bool.True;
            } else if ((this instanceof SConstSpl && str instanceof SConstNum)
                    || (this instanceof SConstSpl && str.equals(SNum))
                    || (this.equals(SSpl) && str instanceof SConstNum)
                    || (this.equals(SSpl) && str.equals(SNum))) {
                return Bool.False;
            } else {
                return Bool.Top;
            }
        }

        public Num toNum() {
            if (this.equals(SBot)) {
                return Num.Bot;
            } else if (this.equals(SNotNum) || this.equals(SSpl) || this.equals(SNotSplNorNum)) {
                return Num.NaN;
            } else if (this instanceof SConstNotSplNorNum || this instanceof SConstSpl) {
                return Num.NaN;
            } else if (this instanceof SConstNum) {
                return Num.alpha(Double.valueOf(((SConstNum)this).str));
            } else {
                return Num.Top;
            }
        }

        public Boolean defEmpty() {
            return this.equals(Empty);
        }

        public Boolean defNotEmpty() {
            return (this.equals(SBot) || this.equals(SNum) || this instanceof SConstNum ||
                    this instanceof SConstSpl || this.equals(SSpl) ||
                    (this instanceof SConstNotSplNorNum && !((SConstNotSplNorNum)this).str.isEmpty()));
        }

        public static final Str Top = STop;
        public static final Str Bot = SBot;
        public static final Str U32 = SNum;
        public static final Str NumStr = SNum;
        public static final Str Empty = new SConstNotSplNorNum("");
        public static final Str SingleChar = SNotSpl;
        public static final Str DateStr = SNotSplNorNum;
        public static final Str FunctionStr = SNotSplNorNum;
        public static final Set<String> SplStrings = Set.set(Ord.stringOrd,
                "valueOf",
                "toString",
                "length",
                "constructor",
                "toLocaleString",
                "hasOwnProperty",
                "isPrototypeOf",
                "propertyIsEnumerable",
                "concat",
                "indexOf",
                "join",
                "lastIndexOf",
                "pop",
                "push",
                "reverse",
                "shift",
                "sort",
                "splice"
        );

        public static Str alpha(String str) {
            if (isNum(str)) {
                return new SConstNum(str);
            } else if (isSpl(str)) {
                return new SConstSpl(str);
            } else {
                return new SConstNotSplNorNum(str);
            }
        }

        public static BValue inject(Str str) {
            return new BValue(Num.Bot, Bool.Bot, str, Set.empty(Ord.hashEqualsOrd()), Null.Bot, Undef.Bot);
        }

        public static Boolean isExact(Str str) {
            return (str instanceof SConstSpl || str instanceof SConstNum || str instanceof SConstNotSplNorNum);
        }

        public static Option<String> getExact(Str str) {
            if (str instanceof SConstSpl) {
                return Option.fromString(((SConstSpl) str).str);
            } else if (str instanceof SConstNum) {
                return Option.fromString(((SConstNum) str).str);
            } else if (str instanceof SConstNotSplNorNum) {
                return Option.fromString(((SConstNotSplNorNum) str).str);
            } else {
                return Option.none();
            }
        }

        public static Boolean isNum(String str) {
            try {
                Double d = Double.valueOf(str);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        public static Boolean isSpl(String str) {
            return SplStrings.member(str);
        }

        public static Boolean isExactNum(Str str) {
            return (str instanceof SConstNum);
        }

        public static Boolean isExactNotNum(Str str) {
            return (str instanceof SConstSpl || str instanceof SConstNotSplNorNum);
        }

        public static Set<Str> minimize(Set<Str> strs) {
            assert !strs.member(Bot);
            if (strs.member(Top)) {
                return Set.set(Ord.hashEqualsOrd(), Top);
            } else {
                boolean hasSNum = strs.member(SNum);
                boolean hasSNotSpl = strs.member(SNotSpl);
                boolean hasSNotSplNorNum = strs.member(SNotSplNorNum);
                boolean hasSSpl = strs.member(SSpl);
                boolean hasSNotNum = strs.member(SNotNum);

                return strs.toList().foldLeft((acc, str) -> {
                    if (str instanceof SConstNum && (hasSNum || hasSNotSpl)) {
                        return acc;
                    } else if (str instanceof SConstNotSplNorNum && (hasSNotSplNorNum || hasSNotSpl || hasSNotNum)) {
                        return acc;
                    } else if (str instanceof SConstSpl && (hasSSpl || hasSNotNum)) {
                        return acc;
                    } else if (str.equals(SNum) && (hasSNotSpl)) {
                        return acc;
                    } else if (str.equals(SNotSplNorNum) && (hasSNotSpl || hasSNotNum)) {
                        return acc;
                    } else if (str.equals(SSpl) && (hasSNotNum)) {
                        return acc;
                    } else if (str.equals(SBot)) {
                        return acc;
                    } else {
                        return acc.insert(str);
                    }
                }, Set.empty(Ord.hashEqualsOrd()));
            }
        }
    }

    public static final Str SBot = new Str() {};

    public static final Str STop = new Str() {};

    public static final Str SNum = new Str() {};

    public static final Str SNotNum = new Str() {};

    public static final Str SSpl = new Str() {};

    public static final Str SNotSplNorNum = new Str() {};

    public static final Str SNotSpl = new Str() {};

    public static class SConstNum extends Str {
        public String str;

        public SConstNum(String str) {
            this.str = str;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof SConstNum && ((SConstNum) obj).str.equals(str));
        }

        @Override
        public int hashCode() {
            return P.p(str).hashCode();
        }
    }

    public static class SConstNotSplNorNum extends Str {
        public String str;

        public SConstNotSplNorNum(String str) {
            this.str = str;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof SConstNotSplNorNum && ((SConstNotSplNorNum) obj).str.equals(str));
        }

        @Override
        public int hashCode() {
            return P.p(str).hashCode();
        }
    }

    public static class SConstSpl extends Str {
        public String str;

        public SConstSpl(String str) {
            this.str = str;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof SConstSpl && ((SConstSpl) obj).str.equals(str));
        }

        @Override
        public int hashCode() {
            return P.p(str).hashCode();
        }
    }

    public static class AddressSpace {

        public static class Address {
            public BigInteger loc;

            public Address(BigInteger loc) {
                this.loc = loc;
            }

            @Override
            public boolean equals(java.lang.Object obj) {
                return (obj instanceof Address && loc.equals(((Address) obj).loc));
            }

            @Override
            public int hashCode() {
                return P.p(loc).hashCode();
            }

            public static Address apply(Integer x) {
                return new Address(BigInteger.valueOf(x));
            }

            public static BValue inject(Address a) {
                return new BValue(Num.Bot, Bool.Bot, Str.Bot, Set.set(Ord.hashEqualsOrd(), a), Null.Bot, Undef.Bot);
            }
        }

        public static class Addresses {

            public static Set<Address> apply() {
                return Set.empty(Ord.hashEqualsOrd());
            }
            public static Set<Address> apply(Address a) {
                return Set.set(Ord.hashEqualsOrd(), a);
            }

            public static BValue inject(Set<Address> as) {
                return new BValue(Num.Bot, Bool.Bot, Str.Bot, as, Null.Bot, Undef.Bot);
            }
        }
    }

    public static abstract class Null {
        public Null merge(Null nil) {
            if (this.equals(MaybeNull) || nil.equals(MaybeNull)) {
                return Top;
            } else {
                return Bot;
            }
        }

        public static final Null Top = MaybeNull;
        public static final Null Bot = NotNull;

        public static final BValue BV = new BValue(Num.Bot, Bool.Bot, Str.Bot, Set.empty(Ord.hashEqualsOrd()), Top, Undef.Bot);
    }

    public static final Null MaybeNull = new Null() {};

    public static final Null NotNull = new Null() {};

    public static abstract class Undef {
        public Undef merge(Undef undef) {
            if (this.equals(MaybeUndef) || undef.equals(MaybeUndef)) {
                return Top;
            } else {
                return Bot;
            }
        }

        public static final Undef Top = MaybeUndef;
        public static final Undef Bot = NotUndef;

        public static final BValue BV = new BValue(Num.Bot, Bool.Bot, Str.Bot, Set.empty(Ord.hashEqualsOrd()), Null.Bot, Top);
    }

    public static final Undef MaybeUndef = new Undef() {};

    public static final Undef NotUndef = new Undef() {};

    public static abstract class Closure {}

    public static class Clo extends Closure {
        public Env env;
        public IRMethod m;

        public Clo(Env env, IRMethod m) {
            this.env = env;
            this.m = m;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Clo && env.equals(((Clo) obj).env) && m.equals(((Clo) obj).m));
        }

        @Override
        public int hashCode() {
            return P.p(env, m).hashCode();
        }
    }

    public static class Native extends Closure {
        public F8<BValue, BValue, IRVar, Env, Store, Scratchpad, KontStack, Trace, Set<Interpreter.State>> f;

        Integer hash;

        public Native(F8<BValue, BValue, IRVar, Env, Store, Scratchpad, KontStack, Trace, Set<Interpreter.State>> f) {
            this.f = f;
            this.hash = Native.freshHash();
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Native && f.equals(((Native) obj).f));
        }

        @Override
        public int hashCode() {
            return hash;
        }

        private static Integer hashCounter = 0;
        public static Integer freshHash() {
            Integer retval = hashCounter;
            hashCounter += 1;
            return retval;
        }
    }

    public static class Object {
        public ExternMap extern;
        public TreeMap<Str, java.lang.Object> intern;
        public Set<Str> present;

        JSClass myClass;
        BValue myProto;

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Object && extern.equals(((Object) obj).extern) && intern.equals(((Object) obj).intern) && present.equals(((Object) obj).present));
        }

        @Override
        public int hashCode() {
            return P.p(extern, intern, present).hashCode();
        }

        public Object(ExternMap extern, TreeMap<Str, java.lang.Object> intern, Set<Str> present) {
            this.extern = extern;
            this.intern = intern;
            this.present = present;
            myClass = (JSClass)intern.get(Utils.Fields.classname).some();
            myProto = (BValue)intern.get(Utils.Fields.proto).some();
        }

        public Object merge(Object o) {
            if (this.equals(o)) {
                return this;
            } else {
                assert myClass.equals(o.myClass);

                ExternMap extern1 = extern.merge(o.extern);
                Set<Str> present1 = present.intersect(o.present);

                TreeMap<Str, java.lang.Object> intern1 = TreeMap.treeMap(Ord.hashEqualsOrd(), o.intern.keys().map(k -> {
                    if (k.equals(Utils.Fields.code)) {
                        Set<Closure> me = (Set<Closure>)intern.get(Utils.Fields.code).some();
                        Set<Closure> that = (Set<Closure>)o.intern.get(k).some();
                        return P.p(k, me.union(that));
                    } else if (k.equals(Utils.Fields.classname)) {
                        assert o.intern.get(k).some().equals(myClass);
                        return P.p(k, myClass);
                    } else if (k.equals(Utils.Fields.constructor)) {
                        return P.p(k, true);
                    } else {
                        BValue me;
                        if (intern.contains(k)) {
                            me = (BValue)intern.get(k).some();
                        } else {
                            me = BValue.Bot;
                        }
                        BValue that = (BValue)o.intern.get(k).some();
                        return P.p(k, me.merge(that));
                    }
                }));

                TreeMap<Str, java.lang.Object> intern2;
                if (intern.contains(Utils.Fields.constructor)) {
                    intern2 = intern1.set(Utils.Fields.constructor, true);
                } else {
                    intern2 = intern1;
                }

                return new Object(extern1, intern2, present1);
            }
        }

        public Option<BValue> apply(Str str) {
            return extern.apply(str);
        }

        public Object strongUpdate(Str str, BValue bv) {
            if (Str.isExact(str)) {
                if (Init.noupdate.get(myClass).orSome(Set.empty(Ord.hashEqualsOrd())).member(str)) {
                    return this;
                } else {
                    return new Object(extern.strongUpdate(str, bv), intern, present.insert(str));
                }
            } else {
                return new Object(extern.weakUpdate(str, bv), intern, present);
            }
        }

        public Object weakUpdate(Str str, BValue bv) {
            if (Str.isExact(str)) {
                if (Init.noupdate.get(myClass).orSome(Set.empty(Ord.hashEqualsOrd())).member(str)) {
                    return this;
                } else {
                    return new Object(extern.weakUpdate(str, bv), intern, present);
                }
            } else {
                return new Object(extern.weakUpdate(str, bv), intern, present);
            }
        }

        public Object strongDelete(Str str) {
            assert present.member(str) && !(Init.nodelete.get(myClass).orSome(Set.empty(Ord.hashEqualsOrd())).member(str));
            return new Object(extern.delete(str), intern, present.delete(str));
        }

        public Object weakDelete(Str str) {
            if (Str.isExact(str)) {
                if (!(Init.nodelete.get(myClass).orSome(Set.empty(Ord.hashEqualsOrd())).member(str))) {
                    return new Object(extern, intern, present.delete(str));
                } else {
                    return this;
                }
            } else {
                Set<Str> le = extern.exactLE(str).minus(Init.nodelete.get(myClass).orSome(Set.empty(Ord.hashEqualsOrd())));
                return new Object(extern, intern, present.minus(le));
            }
        }

        public Set<Str> fields() {
            return extern.reducedKeys().minus(Init.noenum.get(myClass).orSome(Set.empty(Ord.hashEqualsOrd())));
        }

        public JSClass getJSClass() {
            return myClass;
        }

        public BValue getProto() {
            return myProto;
        }

        public BValue getValue() {
            return (BValue)intern.get(Utils.Fields.value).some();
        }

        public Set<Closure> getCode() {
            Option<java.lang.Object> o = intern.get(Utils.Fields.code);
            if (o.isSome()) {
                return (Set<Closure>)o.some();
            } else {
                return Set.empty(Ord.hashEqualsOrd());
            }
        }

        public Boolean calledAsCtor() {
            return intern.contains(Utils.Fields.constructor);
        }

        public Boolean defField(Str str) {
            return present.member(str);
        }

        public Boolean defNotField(Str str) {
            return extern.notContains(str);
        }

        public Set<BValue> getAllValues() {
            return extern.getAllValues().union(Set.set(Ord.hashEqualsOrd(), intern.values().filter(x -> x instanceof BValue).map(x -> (BValue)x)));
        }
    }

    public static class ExternMap {
        public Option<BValue> top;
        public Option<BValue> notnum;
        public Option<BValue> num;
        public TreeMap<Str, BValue> exactnotnum;
        public TreeMap<Str, BValue> exactnum;

        public ExternMap() {
            this.top = Option.none();
            this.notnum = Option.none();
            this.num = Option.none();
            this.exactnotnum = TreeMap.empty(Ord.hashEqualsOrd());
            this.exactnum = TreeMap.empty(Ord.hashEqualsOrd());
        }

        public ExternMap(Option<BValue> top, Option<BValue> notnum, Option<BValue> num, TreeMap<Str, BValue> exactnotnum, TreeMap<Str, BValue> exactnum) {
            this.top = top;
            this.notnum = notnum;
            this.num = num;
            this.exactnotnum = exactnotnum;
            this.exactnum = exactnum;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            if (obj instanceof ExternMap) {
                ExternMap em = (ExternMap)obj;
                return (top.equals(em.top) && notnum.equals(em.notnum) && num.equals(em.num) && exactnotnum.equals(em.exactnotnum) && exactnum.equals(em.exactnum));
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return P.p(top, notnum, num, exactnotnum, exactnum).hashCode();
        }

        public ExternMap merge(ExternMap ext) {
            Option<BValue> top1;
            if (top.isSome() && ext.top.isSome()) {
                top1 = Option.some(top.some().merge(ext.top.some()));
            } else if (top.isSome()) {
                top1 = top;
            } else if (ext.top.isSome()) {
                top1 = ext.top;
            } else {
                top1 = Option.none();
            }

            Option<BValue> notnum1;
            if (notnum.isSome() && ext.notnum.isSome()) {
                notnum1 = Option.some(notnum.some().merge(ext.notnum.some()));
            } else if (notnum.isSome()) {
                notnum1 = notnum;
            } else if (ext.notnum.isSome()) {
                notnum1 = ext.notnum;
            } else {
                notnum1 = Option.none();
            }

            Option<BValue> num1;
            if (num.isSome() && ext.num.isSome()) {
                num1 = Option.some(num.some().merge(ext.num.some()));
            } else if (num.isSome()) {
                num1 = num;
            } else if (ext.num.isSome()) {
                num1 = ext.num;
            } else {
                num1 = Option.none();
            }

            TreeMap<Str, BValue> _exactnotnum;
            if (exactnotnum.equals(ext.exactnotnum)) {
                _exactnotnum = exactnotnum;
            } else {
                _exactnotnum = ext.exactnotnum.union(
                        exactnotnum.keys().map(k -> {
                            BValue bv = exactnotnum.get(k).some();
                            Option<BValue> bv1 = ext.exactnotnum.get(k);
                            if (bv1.isSome()) {
                                return P.p(k, bv.merge(bv1.some()));
                            } else {
                                return P.p(k, bv);
                            }
                        })
                );
            }

            TreeMap<Str, BValue> _exactnum;
            if (exactnum.equals(ext.exactnum)) {
                _exactnum = exactnum;
            } else {
                _exactnum = ext.exactnum.union(
                        exactnum.keys().map(k -> {
                            BValue bv = exactnum.get(k).some();
                            Option<BValue> bv1 = ext.exactnum.get(k);
                            if (bv1.isSome()) {
                                return P.p(k, bv.merge(bv1.some()));
                            } else {
                                return P.p(k, bv);
                            }
                        })
                );
            }

            return new ExternMap(top1, notnum1, num1, _exactnotnum, _exactnum);
        }

        public Option<BValue> apply(Str str) {
            List<BValue> splValues = exactnotnum.keys().filter(k -> Str.SplStrings.member(Str.getExact(k).some())).map(s -> exactnotnum.get(s).some());
            List<BValue> nonSplValues = exactnotnum.keys().filter(k -> !Str.SplStrings.member(Str.getExact(k).some())).map(s -> exactnotnum.get(s).some());

            List<BValue> bvs = List.nil();
            if (str instanceof SConstNotSplNorNum || str instanceof SConstSpl) {
                if (top.isSome()) bvs = bvs.snoc(top.some());
                if (notnum.isSome()) bvs = bvs.snoc(notnum.some());
                if (exactnotnum.contains(str)) bvs = bvs.snoc(exactnotnum.get(str).some());
            } else if (str instanceof SConstNum) {
                if (top.isSome()) bvs = bvs.snoc(top.some());
                if (num.isSome()) bvs = bvs.snoc(num.some());
                if (exactnum.contains(str)) bvs = bvs.snoc(exactnum.get(str).some());
            } else if (str.equals(SNum)) {
                if (top.isSome()) bvs = bvs.snoc(top.some());
                if (num.isSome()) bvs = bvs.snoc(num.some());
                bvs = bvs.append(exactnum.values());
            } else if (str.equals(SNotSplNorNum)) {
                if (top.isSome()) bvs = bvs.snoc(top.some());
                if (notnum.isSome()) bvs = bvs.snoc(notnum.some());
                bvs = bvs.append(nonSplValues);
            } else if (str.equals(SSpl)) {
                if (top.isSome()) bvs = bvs.snoc(top.some());
                if (notnum.isSome()) bvs = bvs.snoc(notnum.some());
                bvs = bvs.append(splValues);
            } else if (str.equals(SNotSpl)) {
                if (top.isSome()) bvs = bvs.snoc(top.some());
                if (num.isSome()) bvs = bvs.snoc(num.some());
                if (notnum.isSome()) bvs = bvs.snoc(notnum.some());
                bvs = bvs.append(exactnum.values());
                bvs = bvs.append(nonSplValues);
            } else if (str.equals(SNotNum)) {
                if (top.isSome()) bvs = bvs.snoc(top.some());
                if (notnum.isSome()) bvs = bvs.snoc(notnum.some());
                bvs = bvs.append(exactnotnum.values());
            } else if (str.equals(STop)) {
                if (top.isSome()) bvs = bvs.snoc(top.some());
                if (num.isSome()) bvs = bvs.snoc(num.some());
                if (notnum.isSome()) bvs = bvs.snoc(notnum.some());
                bvs = bvs.append(exactnotnum.values());
                bvs = bvs.append(exactnum.values());
            } else {
                throw new RuntimeException("used SBot with an object");
            }

            if (bvs.isEmpty()) {
                return Option.none();
            } else {
                return Option.some(bvs.foldLeft(BValue::merge, BValue.Bot));
            }
        }

        public ExternMap strongUpdate(Str str, BValue bv) {
            if (str instanceof SConstNotSplNorNum || str instanceof SConstSpl) {
                TreeMap<Str, BValue> exactnotnum1 = exactnotnum.set(str, bv);
                return new ExternMap(top, notnum, num, exactnotnum1, exactnum);
            } else if (str instanceof SConstNum) {
                TreeMap<Str, BValue> exactnum1 = exactnum.set(str, bv);
                return new ExternMap(top, notnum, num, exactnotnum, exactnum1);
            } else {
                throw new RuntimeException("strong updated with inexact string");
            }
        }

        public ExternMap weakUpdate(Str str, BValue bv) {
            if (str instanceof SConstNotSplNorNum || str instanceof SConstSpl) {
                Option<BValue> _bv = exactnotnum.get(str);
                BValue bv1;
                if (_bv.isSome()) {
                    bv1 = bv.merge(_bv.some());
                } else {
                    bv1 = bv;
                }
                return new ExternMap(top, notnum, num, exactnotnum.set(str, bv1), exactnum);
            } else if (str instanceof SConstNum) {
                Option<BValue> _bv = exactnum.get(str);
                BValue bv1;
                if (_bv.isSome()) {
                    bv1 = bv.merge(_bv.some());
                } else {
                    bv1 = bv;
                }
                return new ExternMap(top, notnum, num, exactnotnum, exactnum.set(str, bv1));
            } else if (str.equals(SNum)) {
                Option<BValue> num1;
                if (num.isSome()) {
                    num1 = Option.some(num.some().merge(bv));
                } else {
                    num1 = Option.some(bv);
                }
                return new ExternMap(top, notnum, num1, exactnotnum, exactnum);
            } else if (str.equals(SSpl) || str.equals(SNotSplNorNum) || str.equals(SNotNum)) {
                Option<BValue> notnum1;
                if (notnum.isSome()) {
                    notnum1 = Option.some(notnum.some().merge(bv));
                } else {
                    notnum1 = Option.some(bv);
                }
                return new ExternMap(top, notnum1, num, exactnotnum, exactnum);
            } else if (str.equals(SNotSpl) || str.equals(STop)) {
                Option<BValue> top1;
                if (top.isSome()) {
                    top1 = Option.some(top.some().merge(bv));
                } else {
                    top1 = Option.some(bv);
                }
                return new ExternMap(top1, notnum, num, exactnotnum, exactnum);
            } else {
                throw new RuntimeException("used SBot with an object");
            }
        }

        public ExternMap delete(Str str) {
            if (str instanceof SConstNotSplNorNum || str instanceof SConstSpl) {
                return new ExternMap(top, notnum, num, exactnotnum.delete(str), exactnum);
            } else if (str instanceof SConstNum) {
                return new ExternMap(top, notnum, num, exactnotnum, exactnum.delete(str));
            } else {
                throw new RuntimeException("tried to delete inexact string");
            }
        }

        public Boolean notContains(Str str) {
            List<BValue> splValues = exactnotnum.keys().filter(k -> Str.SplStrings.member(Str.getExact(k).some())).map(s -> exactnotnum.get(s).some());
            List<BValue> nonSplValues = exactnotnum.keys().filter(k -> !Str.SplStrings.member(Str.getExact(k).some())).map(s -> exactnotnum.get(s).some());

            if (str instanceof SConstNotSplNorNum || str instanceof SConstSpl) {
                return top.isNone() && notnum.isNone() && !(exactnotnum.contains(str));
            } else if (str instanceof SConstNum) {
                return top.isNone() && num.isNone() && !(exactnum.contains(str));
            } else if (str.equals(SNotSplNorNum)) {
                return top.isNone() && notnum.isNone() && nonSplValues.isEmpty();
            } else if (str.equals(SSpl)) {
                return top.isNone() && notnum.isNone() && splValues.isEmpty();
            } else if (str.equals(SNum)) {
                return top.isNone() && num.isNone() && exactnum.isEmpty();
            } else if (str.equals(SNotSpl)) {
                return top.isNone() && num.isNone() && notnum.isNone() && exactnum.isEmpty() && nonSplValues.isEmpty();
            } else if (str.equals(SNotNum)) {
                return top.isNone() && notnum.isNone() && exactnotnum.isEmpty();
            } else if (str.equals(STop)) {
                return top.isNone() && notnum.isNone() && num.isNone() && exactnotnum.isEmpty() && exactnum.isEmpty();
            } else {
                throw new RuntimeException("used SBot with an object");
            }
        }

        public Set<Str> exactLE(Str str) {
            List<Str> nonSplKeys = exactnotnum.keys().filter(k -> !(Str.SplStrings.member(Str.getExact(k).some())));
            if (str instanceof SConstNotSplNorNum || str instanceof SConstSpl) {
                if (exactnotnum.contains(str)) {
                    return Set.set(Ord.hashEqualsOrd(), str);
                } else {
                    return Set.empty(Ord.hashEqualsOrd());
                }
            } else if (str instanceof SConstNum) {
                if (exactnum.contains(str)) {
                    return Set.set(Ord.hashEqualsOrd(), str);
                } else {
                    return Set.empty(Ord.hashEqualsOrd());
                }
            } else if (str.equals(SNum)) {
                return Set.set(Ord.hashEqualsOrd(), exactnum.keys());
            } else if (str.equals(SNotSplNorNum)) {
                return Set.set(Ord.hashEqualsOrd(), nonSplKeys);
            } else if (str.equals(SSpl)) {
                return Set.set(Ord.hashEqualsOrd(), exactnotnum.keys().filter(k -> Str.SplStrings.member(Str.getExact(k).some())));
            } else if (str.equals(SNotSpl)) {
                return Set.set(Ord.hashEqualsOrd(), exactnum.keys().append(nonSplKeys));
            } else if (str.equals(SNotNum)) {
                return Set.set(Ord.hashEqualsOrd(), exactnotnum.keys());
            } else if (str.equals(STop)) {
                return Set.set(Ord.hashEqualsOrd(), exactnotnum.keys().append(exactnum.keys()));
            } else {
                throw new RuntimeException("used SBot with an object");
            }
        }

        public Set<Str> reducedKeys() {
            if (top.isSome()) {
                return Set.set(Ord.hashEqualsOrd(), STop);
            } else {
                List<Str> l1 = notnum.isSome() ? List.list(SNotNum) : exactnotnum.keys();
                List<Str> l2 = num.isSome() ? List.list(SNum) : exactnum.keys();
                return Set.set(Ord.hashEqualsOrd(), l1.append(l2));
            }
        }

        public Set<BValue> getAllValues() {
            List<BValue> list = List.list();
            if (top.isSome()) list = list.snoc(top.some());
            if (notnum.isSome()) list = list.snoc(notnum.some());
            if (num.isSome()) list = list.snoc(num.some());
            list = list.append(exactnotnum.values());
            list = list.append(exactnum.values());
            return Set.set(Ord.hashEqualsOrd(), list);
        }
    }

    public static abstract class Kont {}

    public static final Kont HaltKont = new Kont() {};

    public static class SeqKont extends Kont {
        public List<IRStmt> ss;

        public SeqKont(List<IRStmt> ss) {
            this.ss = ss;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof SeqKont && ss.equals(((SeqKont) obj).ss));
        }

        @Override
        public int hashCode() {
            return P.p(ss).hashCode();
        }
    }

    public static class WhileKont extends Kont {
        public IRExp e;
        public IRStmt s;

        public WhileKont(IRExp e, IRStmt s) {
            this.e = e;
            this.s = s;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof WhileKont && e.equals(((WhileKont) obj).e) && s.equals(((WhileKont) obj).s));
        }

        @Override
        public int hashCode() {
            return P.p(e, s).hashCode();
        }
    }

    public static class ForKont extends Kont {
        public BValue bv;
        public IRVar x;
        public IRStmt s;

        public ForKont(BValue bv, IRVar x, IRStmt s) {
            this.bv = bv;
            this.x = x;
            this.s = s;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof ForKont && bv.equals(((ForKont) obj).bv) && x.equals(((ForKont) obj).x) && s.equals(((ForKont) obj).s));
        }

        @Override
        public int hashCode() {
            return P.p(bv, x, s).hashCode();
        }
    }

    public static class RetKont extends Kont {
        public IRVar x;
        public Env env;
        public Boolean isctor;
        public Trace trace;

        public RetKont(IRVar x, Env env, Boolean isctor, Trace trace) {
            this.x = x;
            this.env = env;
            this.isctor = isctor;
            this.trace = trace;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof RetKont && x.equals(((RetKont) obj).x) && env.equals(((RetKont) obj).env) && isctor.equals(((RetKont) obj).isctor) && trace.equals(((RetKont) obj).trace));
        }

        @Override
        public int hashCode() {
            return P.p(x, env, isctor, trace).hashCode();
        }
    }

    public static class TryKont extends Kont {
        public IRPVar x;
        public IRStmt sc;
        public IRStmt sf;

        public TryKont(IRPVar x, IRStmt sc, IRStmt sf) {
            this.x = x;
            this.sc = sc;
            this.sf = sf;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof TryKont && x.equals(((TryKont) obj).x) && sc.equals(((TryKont) obj).sc) && sf.equals(((TryKont) obj).sf));
        }

        @Override
        public int hashCode() {
            return P.p(x, sc, sf).hashCode();
        }
    }

    public static class CatchKont extends Kont {
        public IRStmt sf;

        public CatchKont(IRStmt sf) {
            this.sf = sf;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof CatchKont && sf.equals(((CatchKont) obj).sf));
        }

        @Override
        public int hashCode() {
            return P.p(sf).hashCode();
        }
    }

    public static class FinKont extends Kont {
        public Set<Value> vs;

        public FinKont(Set<Value> vs) {
            this.vs = vs;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof FinKont && vs.equals(((FinKont) obj).vs));
        }

        @Override
        public int hashCode() {
            return P.p(vs).hashCode();
        }
    }

    public static class LblKont extends Kont {
        public String lbl;

        public LblKont(String lbl) {
            this.lbl = lbl;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof LblKont && lbl.equals(((LblKont) obj).lbl));
        }

        @Override
        public int hashCode() {
            return P.p(lbl).hashCode();
        }
    }

    public static class AddrKont extends Kont {
        public AddressSpace.Address a;
        public IRMethod m;

        public AddrKont(AddressSpace.Address a, IRMethod m) {
            this.a = a;
            this.m = m;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof AddrKont && a.equals(((AddrKont) obj).a) && m.equals(((AddrKont) obj).m));
        }

        @Override
        public int hashCode() {
            return P.p(a, m).hashCode();
        }
    }

    public static class KontStack {
        public List<Kont> ks;
        public List<Integer> exc;

        public KontStack(List<Kont> ks, List<Integer> exc) {
            this.ks = ks;
            this.exc = exc;
        }

        public KontStack(List<Kont> ks) {
            this.ks = ks;
            this.exc = List.list(0);
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof KontStack && ks.equals(((KontStack) obj).ks) && exc.equals(((KontStack) obj).exc));
        }

        @Override
        public int hashCode() {
            return P.p(ks, exc).hashCode();
        }

        public KontStack merge(KontStack rhs) {
            assert ks.length() == rhs.ks.length();
            ArrayList<Kont> list = new ArrayList<>();
            for (int i = 0; i < ks.length(); i += 1) {
                Kont k1 = ks.index(i);
                Kont k2 = rhs.ks.index(i);
                if (k1 instanceof FinKont && k2 instanceof FinKont) {
                    FinKont fk1 = (FinKont)k1, fk2 = (FinKont)k2;
                    Set<Value> bv;
                    Set<Value> bvs1 = fk1.vs.filter(v -> v instanceof BValue);
                    Set<Value> bvs2 = fk2.vs.filter(v -> v instanceof BValue);
                    if (bvs1.isEmpty() && bvs2.isEmpty()) {
                        bv = Set.empty(Ord.hashEqualsOrd());
                    } else if (!bvs1.isEmpty() && !bvs2.isEmpty()) {
                        bv = Set.set(Ord.hashEqualsOrd(), ((BValue)bvs1.toList().index(0)).merge((BValue)bvs2.toList().index(0)));
                    } else if (!bvs1.isEmpty()) {
                        bv = bvs1;
                    } else {
                        bv = bvs2;
                    }
                    Set<Value> ev;
                    Set<Value> evs1 = fk1.vs.filter(v -> v instanceof EValue);
                    Set<Value> evs2 = fk2.vs.filter(v -> v instanceof EValue);
                    if (evs1.isEmpty() && evs2.isEmpty()) {
                        ev = Set.empty(Ord.hashEqualsOrd());
                    } else if (!evs1.isEmpty() && !evs2.isEmpty()) {
                        ev = Set.set(Ord.hashEqualsOrd(), new EValue(((EValue)evs1.toList().index(0)).bv.merge(((EValue)evs2.toList().index(0)).bv)));
                    } else if (!evs1.isEmpty()) {
                        ev = evs1;
                    } else {
                        ev = evs2;
                    }
                    Set<Value> jv;
                    Set<Value> jvs1 = fk1.vs.filter(v -> v instanceof JValue);
                    Set<Value> jvs2 = fk2.vs.filter(v -> v instanceof JValue);
                    if (jvs1.isEmpty() && jvs2.isEmpty()) {
                        jv = Set.empty(Ord.hashEqualsOrd());
                    } else if (!jvs1.isEmpty() && !jvs2.isEmpty()) {
                        JValue jv1 = (JValue)jvs1.toList().index(0);
                        JValue jv2 = (JValue)jvs2.toList().index(0);
                        if (jv1.lbl.equals(jv2.lbl)) {
                            jv = Set.set(Ord.hashEqualsOrd(), new JValue(jv1.lbl, jv1.bv.merge(jv2.bv)));
                        } else {
                            jv = Set.set(Ord.hashEqualsOrd(), jv1, jv2);
                        }
                    } else if (!jvs1.isEmpty()) {
                        jv = jvs1;
                    } else {
                        jv = jvs2;
                    }
                    list.add(new FinKont(bv.union(ev).union(jv)));
                } else if (k1 instanceof ForKont && k2 instanceof ForKont) {
                    ForKont fk1 = (ForKont)k1, fk2 = (ForKont)k2;
                    assert fk1.s.equals(fk2.s) && fk1.x.equals(fk2.x);
                    list.add(new ForKont(fk1.bv.merge(fk2.bv), fk1.x, fk1.s));
                } else {
                    list.add(k1);
                }
            }
            List<Kont> newks = List.list(list);
            List<Integer> newexc;
            if (exc.last() < rhs.exc.last()) {
                newexc = rhs.exc;
            } else {
                newexc = exc;
            }
            return new KontStack(newks, newexc);
        }

        public KontStack push(Kont k) {
            List<Integer> newexc;
            if (k instanceof TryKont || k instanceof CatchKont) {
                newexc = exc.cons(2);
            } else {
                newexc = exc;
            }
            return new KontStack(ks.cons(k), newexc);
        }

        public KontStack pop() {
            List<Integer> newexc;
            if (ks.head() instanceof TryKont || ks.head() instanceof CatchKont) {
                newexc = exc.tail();
            } else {
                newexc = exc;
            }
            return new KontStack(ks.tail(), newexc);
        }

        public KontStack repl(Kont k) {
            List<Integer> newexc;
            if (ks.head() instanceof TryKont && k instanceof CatchKont) {
                newexc = exc;
            } else if (k instanceof TryKont) {
                newexc = exc.cons(2);
            } else if (ks.head() instanceof TryKont || ks.head() instanceof CatchKont) {
                newexc = exc.tail();
            } else {
                newexc = exc;
            }
            return new KontStack(ks.tail().cons(k), newexc);
        }

        public Kont top() {
            return ks.head();
        }

        public Kont last() {
            return ks.last();
        }

        public KontStack toHandler() {
            List<Kont> newks = ks.dropWhile(k -> !(k instanceof TryKont || k instanceof CatchKont));
            return new KontStack(newks, exc);
        }

        public KontStack toSpecial(String lbl) {
            List<Kont> newks = ks.dropWhile(k -> {
               if (k instanceof TryKont || k instanceof CatchKont || k.equals(HaltKont)) {
                   return false;
               } else if (k instanceof LblKont) {
                   return (!((LblKont)k).lbl.equals(lbl));
               } else {
                   return true;
               }
            });
            return new KontStack(newks, exc);
        }

        public static KontStack apply(Kont k) {
            return new KontStack(List.list(k));
        }

        public static KontStack apply(Kont k, Integer exc) {
            return new KontStack(List.list(k), List.list(exc));
        }
    }
}
