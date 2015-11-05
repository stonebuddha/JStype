package analysis;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import ir.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;

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
    }

    public static class ValueTerm extends Term {
        public Value v;

        public ValueTerm(Value v) {
            this.v = v;
        }
    }

    public static class Env {
        public ImmutableMap<IRPVar, ImmutableSet<AddressSpace.Address>> env;

        public Env(ImmutableMap<IRPVar, ImmutableSet<AddressSpace.Address>> env) {
            this.env = env;
        }

        public Env merge(Env rho) {
            ImmutableMap.Builder<IRPVar, ImmutableSet<AddressSpace.Address>> builder = ImmutableMap.<IRPVar, ImmutableSet<AddressSpace.Address>>builder();
            for (Map.Entry<IRPVar, ImmutableSet<AddressSpace.Address>> entry : env.entrySet()) {
                builder = builder.put(
                        entry.getKey(),
                        ImmutableSet.<AddressSpace.Address>builder()
                                .addAll(entry.getValue())
                                .addAll(rho.env.get(entry.getKey())).build());
            }
            return new Env(builder.build());
        }

        public ImmutableSet<AddressSpace.Address> apply(IRPVar x) {
            return env.get(x);
        }

        public Env extendAll(ImmutableList<Map.Entry<IRPVar, AddressSpace.Address>> bind) {
            ImmutableMap.Builder<IRPVar, ImmutableSet<AddressSpace.Address>> builder = ImmutableMap.<IRPVar, ImmutableSet<AddressSpace.Address>>builder().putAll(env);
            for (Map.Entry<IRPVar, AddressSpace.Address> entry : bind) {
                builder = builder.put(entry.getKey(), ImmutableSet.of(entry.getValue()));
            }
            return new Env(builder.build());
        }

        public ImmutableSet<AddressSpace.Address> addrs() {
            ImmutableSet.Builder<AddressSpace.Address> builder = ImmutableSet.<AddressSpace.Address>builder();
            for (ImmutableSet<AddressSpace.Address> set : env.values()) {
                builder = builder.addAll(set);
            }
            return builder.build();
        }
    }

    public static class Store {
        public ImmutableMap<AddressSpace.Address, BValue> toValue;
        public ImmutableSet<AddressSpace.Address> weak;
    }

    public static class Scratchpad {
        public ImmutableList<BValue> mem;

        public Scratchpad(ImmutableList<BValue> mem) {
            this.mem = mem;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Scratchpad && mem.equals(((Scratchpad) obj).mem));
        }

        public Scratchpad merge(Scratchpad pad) {
            if (this.equals(pad)) {
                return this;
            } else {
                ImmutableList.Builder<BValue> builder = ImmutableList.<BValue>builder();
                for (int i = 0; i < mem.size(); i += 1) {
                    builder.add(mem.get(i).merge(pad.mem.get(i)));
                }
                return new Scratchpad(builder.build());
            }
        }

        public BValue apply(IRScratch x) {
            return mem.get(x.n);
        }

        public Scratchpad update(IRScratch x, BValue bv) {
            int sz = mem.size();
            return new Scratchpad(ImmutableList.<BValue>builder()
                    .addAll(mem.subList(0, x.n))
                    .add(bv).addAll(mem.subList(x.n + 1, sz))
                    .build()
            );
        }

        public ImmutableSet<AddressSpace.Address> addrs() {
            ImmutableSet.Builder<AddressSpace.Address> builder = ImmutableSet.<AddressSpace.Address>builder();
            for (BValue bv : mem) {
                builder = builder.addAll(bv.as);
            }
            return builder.build();
        }

        public static Scratchpad apply(Integer len) {
            ArrayList<BValue> bvs = new ArrayList<>(len);
            for (int i = 0; i < len; i += 1) {
                bvs.add(i, Undef.BV);
            }
            return new Scratchpad(ImmutableList.<BValue>builder().addAll(bvs).build());
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
    }

    public static class JValue extends Value {
        public String lbl;
        public BValue bv;

        public JValue(String lbl, BValue bv) {
            this.lbl = lbl;
            this.bv = bv;
        }
    }

    public static class BValue extends Value {
        public Num n;
        public Bool b;
        public Str str;
        public ImmutableSet<AddressSpace.Address> as;
        public Null nil;
        public Undef undef;
        public ImmutableSet<Domain> types;

        public BValue(Num n, Bool b, Str str, ImmutableSet<AddressSpace.Address> as, Null nil, Undef undef) {
            this.n = n;
            this.b = b;
            this.str = str;
            this.as = as;
            this.nil = nil;
            this.undef = undef;

            ImmutableSet.Builder<Domain> builder = ImmutableSet.<Domain>builder();
            if (!n.equals(Num.Bot)) builder = builder.add(DNum);
            if (!b.equals(Bool.Bot)) builder = builder.add(DBool);
            if (!str.equals(Str.Bot)) builder = builder.add(DStr);
            if (!as.isEmpty()) builder = builder.add(DAddr);
            if (!nil.equals(Null.Bot)) builder = builder.add(DNull);
            if (!undef.equals(Undef.Bot)) builder = builder.add(DUndef);
            this.types = builder.build();
        }

        public BValue merge(BValue bv) {
            return new BValue(
                    n.merge(bv.n),
                    b.merge(bv.b),
                    str.merge(bv.str),
                    ImmutableSet.<AddressSpace.Address>builder().addAll(as).addAll(bv.as).build(),
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
            Bool notaddr = Bool.alpha(!types.contains(DAddr));
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

        private BValue toNum() {
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

        public Boolean isBot() {
            return types.isEmpty();
        }

        public Boolean defNum() {
            return (types.size() == 1 && types.contains(DNum));
        }

        public Boolean defBool() {
            return (types.size() == 1 && types.contains(DBool));
        }

        public Boolean defStr() {
            return (types.size() == 1 && types.contains(DStr));
        }

        public Boolean defAddr() {
            return (types.size() == 1 && types.contains(DAddr));
        }

        public Boolean defNull() {
            return (types.size() == 1 && types.contains(DNull));
        }

        public Boolean defUndef() {
            return (types.size() == 1 && types.contains(DUndef));
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

        public static final BValue Bot = new BValue(Num.Bot, Bool.Bot, Str.Bot, ImmutableSet.<AddressSpace.Address>of(), Null.Bot, Undef.Bot);
    }

    public static abstract class Num {
        public Num merge(Num n) {
            if (this.equals(n)) {
                return this;
            } else if (this instanceof NBot) {
                return n;
            } else if (n instanceof NBot) {
                return this;
            } else if (this instanceof NTop || n instanceof NTop) {
                return Top;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return Top;
            } else if (this.equals(Inf) || n.equals(Inf)) {
                return Top;
            } else if (this.equals(NInf) || n.equals(NInf)) {
                return Top;
            } else {
                return new NReal();
            }
        }

        public Bool strictEqual(Num n) {
            if (this instanceof NBot || n instanceof NBot) {
                return Bool.Bot;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return Bool.False;
            } else if (this instanceof NTop || n instanceof NTop) {
                return Bool.Top;
            } else if (this instanceof NConst && n instanceof NConst) {
                return Bool.alpha(((NConst)this).d.equals(((NConst)n).d));
            } else if (this.equals(Inf) && n instanceof NReal) {
                return Bool.False;
            } else if (this instanceof NReal && n.equals(Inf)) {
                return Bool.False;
            } else if (this.equals(NInf) && n instanceof NReal) {
                return Bool.False;
            } else if (this instanceof NReal && n.equals(NInf)) {
                return Bool.False;
            } else {
                return Bool.Top;
            }
        }

        public Num plus(Num n) {
            if (this instanceof NBot || n instanceof NBot) {
                return Bot;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return NaN;
            } else if (this instanceof NTop || n instanceof NTop) {
                return Top;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst(((NConst)this).d + ((NConst)n).d);
            } else if (this instanceof NReal && n.equals(Inf)) {
                return Inf;
            } else if (this.equals(Inf) && n instanceof NReal) {
                return Inf;
            } else if (this instanceof NReal && n.equals(NInf)) {
                return NInf;
            } else if (this.equals(NInf) && n instanceof NReal) {
                return NInf;
            } else {
                return new NReal();
            }
        }

        public Num minus(Num n) {
            if (this instanceof NBot || n instanceof NBot) {
                return Bot;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return NaN;
            } else if (this instanceof NTop || n instanceof NTop) {
                return Top;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst(((NConst)this).d - ((NConst)n).d);
            } else if (this instanceof NReal && n.equals(Inf)) {
                return NInf;
            } else if (this.equals(NInf) && n instanceof NReal) {
                return NInf;
            } else if (this instanceof NReal && n.equals(NInf)) {
                return Inf;
            } else if (this.equals(Inf) && n instanceof NReal) {
                return Inf;
            } else {
                return new NReal();
            }
        }

        public Num times(Num n) {
            if (this instanceof NBot || n instanceof NBot) {
                return Bot;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return NaN;
            } else if (this instanceof NTop || n instanceof NTop) {
                return Top;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst(((NConst)this).d * ((NConst)n).d);
            } else if ((this instanceof NReal && n.equals(Inf)) || (this.equals(Inf) && n instanceof NReal)) {
                return Top;
            } else if ((this instanceof NReal && n.equals(NInf) || (this.equals(NInf) && n instanceof NReal))) {
                return Top;
            } else {
                return new NReal();
            }
        }

        public Num divide(Num n) {
            if (this instanceof NBot || n instanceof NBot) {
                return Bot;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return NaN;
            } else if (this instanceof NTop || n instanceof NTop) {
                return Top;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst(((NConst)this).d / ((NConst)n).d);
            } else if (this instanceof NReal && (n.equals(Inf) || n.equals(NInf))) {
                return new NConst(0.0);
            } else {
                return Top;
            }
        }

        public Num mod(Num n) {
            if (this instanceof NBot || n instanceof NBot) {
                return Bot;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return NaN;
            } else if (this instanceof NTop || n instanceof NTop) {
                return Top;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst(((NConst)this).d % ((NConst)n).d);
            } else if ((this.equals(Inf) || this.equals((NInf))) && (n instanceof NReal)) {
                return NaN;
            } else {
                return new NReal();
            }
        }

        public Num shl(Num n) {
            if (this instanceof NBot || n instanceof NBot) {
                return Bot;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst((double)(((NConst)this).d.longValue() << ((NConst)n).d.longValue()));
            } else {
                return new NReal();
            }
        }

        public Num sar(Num n) {
            if (this instanceof NBot || n instanceof NBot) {
                return Bot;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst((double)(((NConst)this).d.longValue() >> ((NConst)n).d.longValue()));
            } else {
                return new NReal();
            }
        }

        public Num shr(Num n) {
            if (this instanceof NBot || n instanceof NBot) {
                return Bot;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst((double)(((NConst)this).d.longValue() >>> ((NConst)n).d.longValue()));
            } else {
                return new NReal();
            }
        }

        public Bool lessThan(Num n) {
            if (this instanceof NBot || n instanceof NBot) {
                return Bool.Bot;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return Bool.False;
            } else if (this instanceof NTop || n instanceof NTop) {
                return Bool.Top;
            } else if (this instanceof NConst && n instanceof NConst) {
                return Bool.alpha(((NConst)this).d < ((NConst)n).d);
            } else if ((this instanceof NReal && n.equals(Inf))
                    || (this.equals(NInf) && n instanceof NReal)) {
                return Bool.True;
            } else if ((this.equals(Inf) && n instanceof NReal)
                    || (this instanceof NReal && n.equals(NInf))) {
                return Bool.False;
            } else {
                return Bool.Top;
            }
        }

        public Bool lessEqual(Num n) {
            if (this instanceof NBot || n instanceof NBot) {
                return Bool.Bot;
            } else if (this.equals(NaN) || n.equals(NaN)) {
                return Bool.False;
            } else if (this instanceof NTop || n instanceof NTop) {
                return Bool.Top;
            } else if (this instanceof NConst && n instanceof NConst) {
                return Bool.alpha(((NConst)this).d <= ((NConst)n).d);
            } else if ((this instanceof NReal && n.equals(Inf))
                    || (this.equals(NInf) && n instanceof NReal)) {
                return Bool.True;
            } else if ((this.equals(Inf) && n instanceof NReal)
                    || (this instanceof NReal && n.equals(NInf))) {
                return Bool.False;
            } else {
                return Bool.Top;
            }
        }

        public Num and(Num n) {
            if (this instanceof NBot || n instanceof NBot) {
                return Num.Bot;
            } else if (this.equals(NaN) || n.equals((NaN))) {
                return Num.Zero;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst((double)(((NConst)this).d.longValue() & ((NConst)n).d.longValue()));
            } else {
                return new NReal();
            }
        }

        public Num or(Num n) {
            if (this instanceof NBot || n instanceof NBot) {
                return Num.Bot;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst((double)(((NConst)this).d.longValue() | ((NConst)n).d.longValue()));
            } else {
                return new NReal();
            }
        }

        public Num xor(Num n) {
            if (this instanceof NBot || n instanceof NBot) {
                return Num.Bot;
            } else if (this instanceof NConst && n instanceof NConst) {
                return new NConst((double)(((NConst)this).d.longValue() ^ ((NConst)n).d.longValue()));
            } else {
                return new NReal();
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
            } else if (this instanceof NTop) {
                return Str.Top;
            } else if (this instanceof NReal) {
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

        public static final Num Top = new NTop();
        public static final Num Bot = new NBot();
        public static final Num Zero = new NConst(0.0);
        public static final Num NaN = new NConst(Double.NaN);
        public static final Num Inf = new NConst(Double.POSITIVE_INFINITY);
        public static final Num NInf = new NConst(Double.NEGATIVE_INFINITY);
        public static final Num U32 = new NReal();
        public static final Long maxU32 = 4294967295L;

        public static Num alpha(Double d) {
            return new NConst(d);
        }

        public static Boolean maybeU32(BValue bv) {
            Num n = bv.n;
            if (n instanceof NTop || n instanceof NReal) {
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
            return new BValue(n, Bool.Bot, Str.Bot, ImmutableSet.<AddressSpace.Address>of(), Null.Bot, Undef.Bot);
        }
    }

    public static class NBot extends Num {
        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof NBot);
        }
    }

    public static class NTop extends Num {
        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof NTop);
        }
    }

    public static class NReal extends Num {
        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof NReal);
        }
    }

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
    }

    public static abstract class Bool {
        public Bool merge(Bool b) {
            if (this.equals(b)) {
                return this;
            } else if (this instanceof BTop || b instanceof BTop || (this instanceof BTrue && b instanceof BFalse) || (this instanceof BFalse && b instanceof BTrue)) {
                return Top;
            } else if (this instanceof BBot) {
                return b;
            } else if (b instanceof BBot) {
                return this;
            } else {
                throw new RuntimeException("suppress false compiler warning");
            }
        }

        public Bool strictEqual(Bool b) {
            if (this instanceof BBot || b instanceof BBot) {
                return Bot;
            } else if (this instanceof BTop || b instanceof BTop) {
                return Top;
            } else if ((this instanceof BTrue && b instanceof BTrue)
                    || (this instanceof BFalse && b instanceof BFalse)) {
                return True;
            } else {
                return False;
            }
        }

        public Bool logicalAnd(Bool b) {
            if (this instanceof BBot || b instanceof BBot) {
                return Bot;
            } else if (this instanceof BFalse || b instanceof BFalse) {
                return False;
            } else if (this instanceof BTop || b instanceof BTop) {
                return Top;
            } else {
                return True;
            }
        }

        public Bool logicalOr(Bool b) {
            if (this instanceof BBot || b instanceof BBot) {
                return Bot;
            } else if (this instanceof BTrue || b instanceof BTrue) {
                return True;
            } else if (this instanceof BTop || b instanceof BTop) {
                return Top;
            } else {
                return False;
            }
        }

        public Bool logicalNot() {
            if (this instanceof BBot) {
                return Bot;
            } else if (this instanceof BTrue) {
                return False;
            } else if (this instanceof BFalse) {
                return True;
            } else {
                return Top;
            }
        }

        public Num toNum() {
            if (this instanceof BBot) {
                return Num.Bot;
            } else if (this instanceof BTrue) {
                return Num.alpha(1.0);
            } else if (this instanceof BFalse) {
                return Num.alpha(0.0);
            } else {
                return Num.Top;
            }
        }

        public Str toStr() {
            if (this instanceof BBot) {
                return Str.Bot;
            } else if (this instanceof BTrue) {
                return Str.alpha("true");
            } else if (this instanceof BFalse) {
                return Str.alpha("false");
            } else {
                return Str.Top;
            }
        }

        public static final Bool Top = new BTop();
        public static final Bool Bot = new BBot();
        public static final Bool True = new BTrue();
        public static final Bool False = new BFalse();

        public static Bool alpha(Boolean b) {
            if (b) {
                return True;
            } else {
                return False;
            }
        }

        public static BValue inject(Bool b) {
            return new BValue(Num.Bot, b, Str.Bot, ImmutableSet.<AddressSpace.Address>of(), Null.Bot, Undef.Bot);
        }
    }

    public static class BBot extends Bool {
        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof BBot);
        }
    }

    public static class BTrue extends Bool {
        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof BTrue);
        }
    }

    public static class BFalse extends Bool {
        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof BFalse);
        }
    }

    public static class BTop extends Bool {
        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof BTop);
        }
    }

    public static abstract class Str {
        public Str merge(Str str) {
            if (this.equals(str)) {
                return this;
            } else if (this instanceof SBot) {
                return str;
            } else if (str instanceof SBot) {
                return this;
            } else if (this instanceof SConstNum && str instanceof SConstNum) {
                return new SNum();
            } else if ((this instanceof SConstNum && str instanceof SConstNotSplNorNum)
                    || (this instanceof SConstNotSplNorNum && str instanceof SConstNum)) {
                return new SNotSpl();
            } else if ((this instanceof SConstNum && str instanceof SConstSpl)
                    || (this instanceof SConstSpl && str instanceof SConstNum)) {
                return Top;
            } else if ((this instanceof SConstNum && str instanceof SNotSplNorNum)
                    || (this instanceof SNotSplNorNum && str instanceof SConstNum)) {
                return new SNotSpl();
            } else if ((this instanceof SConstNum && str instanceof SSpl)
                    || (this instanceof SSpl && str instanceof SConstNum)) {
                return Top;
            } else if ((this instanceof SConstNum && str instanceof SNotNum)
                    || (this instanceof SNotNum && str instanceof SConstNum)) {
                return Top;
            } else if (this instanceof SConstNotSplNorNum && str instanceof SConstNotSplNorNum) {
                return new SNotSplNorNum();
            } else if ((this instanceof SConstNotSplNorNum && str instanceof SConstSpl)
                    || (this instanceof SConstSpl && str instanceof SConstNotSplNorNum)) {
                return new SNotNum();
            } else if ((this instanceof SConstNotSplNorNum && str instanceof SNum)
                    || (this instanceof SNum && str instanceof SConstNotSplNorNum)) {
                return new SNotSpl();
            } else if ((this instanceof SConstNotSplNorNum && str instanceof SSpl)
                    || (this instanceof SSpl && str instanceof SConstNotSplNorNum)) {
                return new SNotNum();
            } else if (this instanceof SConstSpl && str instanceof SConstSpl) {
                return new SSpl();
            } else if ((this instanceof SConstSpl && str instanceof SNum)
                    || (this instanceof SNum && str instanceof SConstSpl)) {
                return Top;
            } else if ((this instanceof SConstSpl && str instanceof SNotSplNorNum)
                    || (this instanceof SNotSplNorNum && str instanceof SConstSpl)) {
                return new SNotNum();
            } else if ((this instanceof SConstSpl && str instanceof SNotSpl)
                    || (this instanceof SNotSpl && str instanceof SConstSpl)) {
                return Top;
            } else if ((this instanceof SNum && str instanceof SNotSplNorNum)
                    || (this instanceof SNotSplNorNum && str instanceof SNum)) {
                return new SNotSpl();
            } else if ((this instanceof SNum && str instanceof SSpl)
                    || (this instanceof SSpl && str instanceof SNum)) {
                return Top;
            } else if ((this instanceof SNum && str instanceof SNotNum)
                    || (this instanceof SNotNum && str instanceof SNum)) {
                return Top;
            } else if ((this instanceof SNotSplNorNum && str instanceof SSpl)
                    || (this instanceof SNotSpl && str instanceof SNotSplNorNum)) {
                return new SNotNum();
            } else if ((this instanceof SSpl && str instanceof SNotSpl)
                    || (this instanceof SNotSpl && str instanceof SSpl)) {
                return Top;
            } else if ((this instanceof SNotNum && str instanceof SNotSpl)
                    || (this instanceof SNotSpl && str instanceof SNotNum)) {
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
            if (this instanceof SBot || str instanceof STop) {
                return true;
            } else if (this.equals(str)) {
                return true;
            } else if ((this instanceof SConstNum && str instanceof SNum)
                    || (this instanceof SConstNum && str instanceof SNotSpl)
                    || (this instanceof SConstNotSplNorNum && str instanceof SNotSplNorNum)
                    || (this instanceof SConstNotSplNorNum && str instanceof SNotSpl)
                    || (this instanceof SConstNotSplNorNum && str instanceof SNotNum)
                    || (this instanceof SConstSpl && str instanceof SSpl)
                    || (this instanceof SConstSpl && str instanceof SNotNum)
                    || (this instanceof SNum && str instanceof SNotSpl)
                    || (this instanceof SSpl && str instanceof SNotNum)
                    || (this instanceof SNotSplNorNum && str instanceof SNotNum)
                    || (this instanceof SNotSplNorNum && str instanceof SNotSpl)) {
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
            } else if ((this instanceof SNotSpl && str instanceof SNotNum)
                    || (this instanceof SNotNum && str instanceof SNotSpl)) {
                return Bool.Top;
            } else if (this.notPartialLessEqual(str) && str.notPartialLessEqual(this)) {
                return Bool.False;
            } else if (this instanceof SBot || str instanceof SBot) {
                return Bool.Bot;
            } else {
                return Bool.Top;
            }
        }

        public Str strConcat(Str str) {
            if (this instanceof SBot || str instanceof SBot) {
                return Bot;
            } else if (this instanceof STop && str instanceof SConstNum) {
                return new SNotSpl();
            } else if (this instanceof STop && str instanceof SConstSpl) {
                return new SNotNum();
            } else if (this instanceof STop && str instanceof SNum) {
                return new SNotSpl();
            } else if (this instanceof STop && str instanceof SSpl) {
                return new SNotNum();
            } else if (this instanceof STop) {
                return Top;
            } else if (this.equals(Str.Empty)) {
                return str;
            } else if (str.equals(Str.Empty)) {
                return this;
            } else if (Str.isExact(this) && Str.isExact(str)) {
                return Str.alpha(Str.getExact(this).get() + Str.getExact(str).get());
            } else if (this instanceof SConstNum) {
                if (str instanceof SNum) {
                    return new SNotSpl();
                } else if (str instanceof SNotSplNorNum) {
                    return new SNotSpl();
                } else if (str instanceof SSpl) {
                    return new SNotSpl();
                } else if (str instanceof SNotSpl) {
                    return new SNotSpl();
                } else if (str instanceof SNotNum) {
                    return new SNotSpl();
                } else if (str instanceof STop) {
                    return new SNotSpl();
                }
            } else if (this instanceof SConstNotSplNorNum) {
                if (str instanceof SNum) {
                    return new SNotSpl();
                } else if (str instanceof SNotSplNorNum) {
                    return new SNotNum();
                } else if (str instanceof SSpl) {
                    return new SNotNum();
                } else if (str instanceof SNotSpl) {
                    return Top;
                } else if (str instanceof SNotNum) {
                    return new SNotNum();
                } else if (str instanceof STop) {
                    return Top;
                }
            } else if (this instanceof SConstSpl) {
                if (str instanceof SNum) {
                    return new SNotSpl();
                } else if (str instanceof SNotSplNorNum) {
                    return new SNotNum();
                } else if (str instanceof SSpl) {
                    return new SNotSplNorNum();
                } else if (str instanceof SNotSpl) {
                    return new SNotNum();
                } else if (str instanceof SNotNum) {
                    return new SNotNum();
                } else if (str instanceof STop) {
                    return new SNotNum();
                }
            } else if (this instanceof SNum) {
                if (str instanceof SConstNum) {
                    return new SNotSpl();
                } else if (str instanceof SConstNotSplNorNum) {
                    return new SNotSpl();
                } else if (str instanceof SConstSpl) {
                    return new SNotSplNorNum();
                } else if (str instanceof SNum) {
                    return new SNotSpl();
                } else if (str instanceof SNotSplNorNum) {
                    return new SNotSpl();
                } else if (str instanceof SSpl) {
                    return new SNotSplNorNum();
                } else if (str instanceof SNotSpl) {
                    return new SNotSpl();
                } else if (str instanceof SNotNum) {
                    return new SNotSpl();
                } else if (str instanceof STop) {
                    return new SNotSpl();
                }
            } else if (this instanceof SNotSplNorNum) {
                if (str instanceof SConstNum) {
                    return new SNotSpl();
                } else if (str instanceof SConstNotSplNorNum) {
                    return new SNotNum();
                } else if (str instanceof SConstSpl) {
                    return new SNotNum();
                } else if (str instanceof SNum) {
                    return new SNotSpl();
                } else if (str instanceof SNotSplNorNum) {
                    return new SNotNum();
                } else if (str instanceof SSpl) {
                    return new SNotNum();
                } else if (str instanceof SNotSpl) {
                    return Top;
                } else if (str instanceof SNotNum) {
                    return new SNotNum();
                } else if (str instanceof STop) {
                    return Top;
                }
            } else if (this instanceof SSpl) {
                if (str instanceof SConstNum) {
                    return new SNotSplNorNum();
                } else if (str instanceof SConstNotSplNorNum) {
                    return new SNotNum();
                } else if (str instanceof SConstSpl) {
                    return new SNotSplNorNum();
                } else if (str instanceof SNum) {
                    return new SNotSplNorNum();
                } else if (str instanceof SNotSplNorNum) {
                    return new SNotNum();
                } else if (str instanceof SSpl) {
                    return new SNotSplNorNum();
                } else if (str instanceof SNotSpl) {
                    return new SNotNum();
                } else if (str instanceof SNotNum) {
                    return new SNotNum();
                } else if (str instanceof STop) {
                    return new SNotNum();
                }
            } else if (this instanceof SNotSpl) {
                if (str instanceof SConstNum) {
                    return new SNotSpl();
                } else if (str instanceof SConstNotSplNorNum) {
                    return Top;
                } else if (str instanceof SConstSpl) {
                    return new SNotNum();
                } else if (str instanceof SNum) {
                    return new SNotSpl();
                } else if (str instanceof SNotSplNorNum) {
                    return Top;
                } else if (str instanceof SSpl) {
                    return new SNotNum();
                } else if (str instanceof SNotSpl) {
                    return Top;
                } else if (str instanceof SNotNum) {
                    return Top;
                } else if (str instanceof STop) {
                    return Top;
                }
            } else if (this instanceof SNotNum) {
                if (str instanceof SConstNum) {
                    return new SNotSpl();
                } else if (str instanceof SConstNotSplNorNum) {
                    return new SNotNum();
                } else if (str instanceof SConstSpl) {
                    return new SNotNum();
                } else if (str instanceof SNum) {
                    return new SNotSpl();
                } else if (str instanceof SNotSplNorNum) {
                    return new SNotNum();
                } else if (str instanceof SSpl) {
                    return new SNotNum();
                } else if (str instanceof SNotSpl) {
                    return Top;
                } else if (str instanceof SNotNum) {
                    return new SNotNum();
                } else if (str instanceof STop) {
                    return Top;
                }
            }
            throw new RuntimeException("incorrect implementation of string lattice");
        }

        public Bool strLessThan(Str str) {
            if (this instanceof SBot || str instanceof SBot) {
                return Bool.Bot;
            } else if (Str.isExact(this) && Str.isExact(str)) {
                return Bool.alpha(Str.getExact(this).get().compareTo(Str.getExact(str).get()) < 0);
            } else if ((this instanceof SConstNum && str instanceof SConstSpl)
                    || (this instanceof SConstNum && str instanceof SSpl)
                    || (this instanceof SNum && str instanceof SConstSpl)
                    || (this instanceof SNum && str instanceof SSpl)) {
                return Bool.True;
            } else if ((this instanceof SConstSpl && str instanceof SConstNum)
                    || (this instanceof SConstSpl && str instanceof SNum)
                    || (this instanceof SSpl && str instanceof SConstNum)
                    || (this instanceof SSpl && str instanceof SNum)) {
                return Bool.False;
            } else {
                return Bool.Top;
            }
        }

        public Bool strLessEqual(Str str) {
            if (this instanceof SBot || str instanceof SBot) {
                return Bool.Bot;
            } else if (Str.isExact(this) && Str.isExact(str)) {
                return Bool.alpha(Str.getExact(this).get().compareTo(Str.getExact(str).get()) <= 0);
            } else if ((this instanceof SConstNum && str instanceof SConstSpl)
                    || (this instanceof SConstNum && str instanceof SSpl)
                    || (this instanceof SNum && str instanceof SConstSpl)
                    || (this instanceof SNum && str instanceof SSpl)) {
                return Bool.True;
            } else if ((this instanceof SConstSpl && str instanceof SConstNum)
                    || (this instanceof SConstSpl && str instanceof SNum)
                    || (this instanceof SSpl && str instanceof SConstNum)
                    || (this instanceof SSpl && str instanceof SNum)) {
                return Bool.False;
            } else {
                return Bool.Top;
            }
        }

        public Num toNum() {
            if (this instanceof SBot) {
                return Num.Bot;
            } else if (this instanceof SNotNum || this instanceof SSpl || this instanceof SNotSplNorNum) {
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
            return (this instanceof SBot || this instanceof SNum || this instanceof SConstNum ||
                    this instanceof SConstSpl || this instanceof SSpl ||
                    (this instanceof SConstNotSplNorNum && !((SConstNotSplNorNum)this).str.isEmpty()));
        }

        public static final Str Top = new STop();
        public static final Str Bot = new SBot();
        public static final Str U32 = new SNum();
        public static final Str NumStr = new SNum();
        public static final Str Empty = new SConstNotSplNorNum("");
        public static final Str SingleChar = new SNotSpl();
        public static final Str DateStr = new SNotSplNorNum();
        public static final Str FunctionStr = new SNotSplNorNum();
        public static final ImmutableSet<String> SplStrings = ImmutableSet.of(
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
            return new BValue(Num.Bot, Bool.Bot, str, ImmutableSet.<AddressSpace.Address>of(), Null.Bot, Undef.Bot);
        }

        public static Boolean isExact(Str str) {
            return (str instanceof SConstSpl || str instanceof SConstNum || str instanceof SConstNotSplNorNum);
        }

        public static Optional<String> getExact(Str str) {
            if (str instanceof SConstSpl) {
                return Optional.of(((SConstSpl) str).str);
            } else if (str instanceof SConstNum) {
                return Optional.of(((SConstNum) str).str);
            } else if (str instanceof SConstNotSplNorNum) {
                return Optional.of(((SConstNotSplNorNum) str).str);
            } else {
                return Optional.absent();
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
            return SplStrings.contains(str);
        }

        public static Boolean isExactNum(Str str) {
            return (str instanceof SConstNum);
        }

        public static Boolean isExactNotNum(Str str) {
            return (str instanceof SConstSpl || str instanceof SConstNotSplNorNum);
        }

        public static ImmutableSet<Str> minimize(ImmutableSet<Str> strs) {
            if (strs.contains(Top)) {
                return ImmutableSet.of(Top);
            } else {
                boolean hasSNum = strs.contains(new SNum());
                boolean hasSNotSpl = strs.contains(new SNotSpl());
                boolean hasSNotSplNorNum = strs.contains(new SNotSplNorNum());
                boolean hasSSpl = strs.contains(new SSpl());
                boolean hasSNotNum = strs.contains(new SNotNum());

                ImmutableSet.Builder<Str> builder = ImmutableSet.<Str>builder();
                for (Str str : strs) {
                    if (str instanceof SConstNum && (hasSNum || hasSNotSpl)) {
                    } else if (str instanceof SConstNotSplNorNum && (hasSNotSplNorNum || hasSNotSpl || hasSNotNum)) {
                    } else if (str instanceof SConstSpl && (hasSSpl || hasSNotNum)) {
                    } else if (str instanceof SNum && (hasSNotSpl)) {
                    } else if (str instanceof SNotSplNorNum && (hasSNotSpl || hasSNotNum)) {
                    } else if (str instanceof SSpl && (hasSNotNum)) {
                    } else if (str instanceof SBot) {
                    } else {
                        builder = builder.add(str);
                    }
                }
                return builder.build();
            }
        }
    }

    public static class SBot extends Str {
        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof SBot);
        }
    }

    public static class STop extends Str {
        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof STop);
        }
    }

    public static class SNum extends Str {
        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof SNum);
        }
    }

    public static class SNotNum extends Str {
        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof SNotNum);
        }
    }

    public static class SSpl extends Str {
        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof SSpl);
        }
    }

    public static class SNotSplNorNum extends Str {
        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof SNotSplNorNum);
        }
    }

    public static class SNotSpl extends Str {
        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof SNotSpl);
        }
    }

    public static class SConstNum extends Str {
        public String str;

        public SConstNum(String str) {
            this.str = str;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof SConstNum && ((SConstNum) obj).str.equals(str));
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
    }

    public static class AddressSpace {

        public static class Address {
            public BigInteger loc;

            public Address(BigInteger loc) {
                this.loc = loc;
            }

            public static Address apply(Integer x) {
                return new Address(BigInteger.valueOf(x));
            }

            public static BValue inject(Address a) {
                return new BValue(Num.Bot, Bool.Bot, Str.Bot, ImmutableSet.of(a), Null.Bot, Undef.Bot);
            }
        }

        public static class Addresses {

            public static ImmutableSet<Address> apply() {
                return ImmutableSet.of();
            }
            public static ImmutableSet<Address> apply(Address a) {
                return ImmutableSet.of(a);
            }

            public static BValue inject(ImmutableSet<Address> as) {
                return new BValue(Num.Bot, Bool.Bot, Str.Bot, as, Null.Bot, Undef.Bot);
            }
        }
    }

    public static abstract class Null {
        public Null merge(Null nil) {
            if (this instanceof MaybeNull || nil instanceof MaybeNull) {
                return Top;
            } else {
                return Bot;
            }
        }

        public static final Null Top = new MaybeNull();
        public static final Null Bot = new NotNull();

        public static final BValue BV = new BValue(Num.Bot, Bool.Bot, Str.Bot, ImmutableSet.<AddressSpace.Address>of(), Top, Undef.Bot);
    }


    public static class MaybeNull extends Null {
        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof MaybeNull);
        }
    }

    public static class NotNull extends Null {
        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof NotNull);
        }
    }

    public static abstract class Undef {
        public Undef merge(Undef undef) {
            if (this instanceof MaybeUndef || undef instanceof MaybeUndef) {
                return Top;
            } else {
                return Bot;
            }
        }

        public static final Undef Top = new MaybeUndef();
        public static final Undef Bot = new NotUndef();

        public static final BValue BV = new BValue(Num.Bot, Bool.Bot, Str.Bot, ImmutableSet.<AddressSpace.Address>of(), Null.Bot, Top);
    }

    public static class MaybeUndef extends Undef {
        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof MaybeUndef);
        }
    }

    public static class NotUndef extends Undef {
        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof NotUndef);
        }
    }

    public static abstract class Closure {}

    public static class Clo extends Closure {
        public Env rho;
        public IRMethod m;

        public Clo(Env rho, IRMethod m) {
            this.rho = rho;
            this.m = m;
        }
    }

    public static class Object {
        public ExternMap extern;
        public ImmutableMap<Str, java.lang.Object> intern;
        public ImmutableSet<Str> present;

        JSClass myClass;
        BValue myProto;

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Object && extern.equals(((Object) obj).extern) && intern.equals(((Object) obj).intern) && present.equals(((Object) obj).present));
        }

        public Object(ExternMap extern, ImmutableMap<Str, java.lang.Object> intern, ImmutableSet<Str> present) {
            this.extern = extern;
            this.intern = intern;
            this.present = present;
            myClass = (JSClass)intern.get(Utils.Fields.classname);
            myProto = (BValue)intern.get(Utils.Fields.proto);
        }
    }

    public static class ExternMap {
        public Optional<BValue> top;
        public Optional<BValue> notnum;
        public Optional<BValue> num;
        public ImmutableMap<Str, BValue> exactnotnum;
        public ImmutableMap<Str, BValue> exactnum;

        public ExternMap() {
            this.top = Optional.absent();
            this.notnum = Optional.absent();
            this.num = Optional.absent();
            this.exactnotnum = ImmutableMap.of();
            this.exactnum = ImmutableMap.of();
        }

        public ExternMap(Optional<BValue> top, Optional<BValue> notnum, Optional<BValue> num, ImmutableMap<Str, BValue> exactnotnum, ImmutableMap<Str, BValue> exactnum) {
            this.top = top;
            this.notnum = notnum;
            this.num = num;
            this.exactnotnum = exactnotnum;
            this.exactnum = exactnum;
        }

        public ExternMap merge(ExternMap ext) {
            Optional<BValue> top1;
            if (top.isPresent() && ext.top.isPresent()) {
                top1 = Optional.of(top.get().merge(ext.top.get()));
            } else if (top.isPresent()) {
                top1 = top;
            } else if (ext.top.isPresent()) {
                top1 = ext.top;
            } else {
                top1 = Optional.absent();
            }

            Optional<BValue> notnum1;
            if (notnum.isPresent() && ext.notnum.isPresent()) {
                notnum1 = Optional.of(notnum.get().merge(ext.notnum.get()));
            } else if (notnum.isPresent()) {
                notnum1 = notnum;
            } else if (ext.notnum.isPresent()) {
                notnum1 = ext.notnum;
            } else {
                notnum1 = Optional.absent();
            }

            Optional<BValue> num1;
            if (num.isPresent() && ext.num.isPresent()) {
                num1 = Optional.of(num.get().merge(ext.num.get()));
            } else if (num.isPresent()) {
                num1 = num;
            } else if (ext.num.isPresent()) {
                num1 = ext.num;
            } else {
                num1 = Optional.absent();
            }

            ImmutableMap<Str, BValue> _exactnotnum;
            if (exactnotnum.equals(ext.exactnotnum)) {
                _exactnotnum = exactnotnum;
            } else {
                ImmutableMap.Builder<Str, BValue> builder = ImmutableMap.<Str, BValue>builder();
                builder.putAll(ext.exactnotnum);
                for (Map.Entry<Str, BValue> entry : exactnotnum.entrySet()) {
                    Str k = entry.getKey();
                    BValue bv = entry.getValue();
                    BValue bv1 = ext.exactnotnum.get(k);
                    if (bv1 != null) {
                        builder = builder.put(k, bv.merge(bv1));
                    } else {
                        builder = builder.put(k, bv);
                    }
                }
                _exactnotnum = builder.build();
            }

            ImmutableMap<Str, BValue> _exactnum;
            if (exactnum.equals(ext.exactnum)) {
                _exactnum = exactnum;
            } else {
                ImmutableMap.Builder<Str, BValue> builder = ImmutableMap.<Str, BValue>builder();
                builder.putAll(ext.exactnum);
                for (Map.Entry<Str, BValue> entry : exactnum.entrySet()) {
                    Str k = entry.getKey();
                    BValue bv = entry.getValue();
                    BValue bv1 = ext.exactnum.get(k);
                    if (bv1 != null) {
                        builder = builder.put(k, bv.merge(bv1));
                    } else {
                        builder = builder.put(k, bv);
                    }
                }
                _exactnum = builder.build();
            }

            return new ExternMap(top1, notnum1, num1, _exactnotnum, _exactnum);
        }
    }

    public static abstract class Kont {}

    public static class HaltKont {
        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof HaltKont);
        }
    }

    public static class SeqKont {
        public ImmutableList<IRStmt> ss;

        public SeqKont(ImmutableList<IRStmt> ss) {
            this.ss = ss;
        }
    }

    public static class WhileKont {
        public IRExp e;
        public IRStmt s;

        public WhileKont(IRExp e, IRStmt s) {
            this.e = e;
            this.s = s;
        }
    }

    public static class ForKont {
        public BValue bv;
        public IRVar x;
        public IRStmt s;

        public ForKont(BValue bv, IRVar x, IRStmt s) {
            this.bv = bv;
            this.x = x;
            this.s = s;
        }
    }

    public static class RetKont {
        public IRVar x;
        public Env rho;
        public Boolean isctor;
        // TODO: trace

        public RetKont(IRVar x, Env rho, Boolean isctor) {
            this.x = x;
            this.rho = rho;
            this.isctor = isctor;
        }
    }

    public static class TryKont {
        public IRPVar x;
        public IRStmt sc;
        public IRStmt sf;

        public TryKont(IRPVar x, IRStmt sc, IRStmt sf) {
            this.x = x;
            this.sc = sc;
            this.sf = sf;
        }
    }

    public static class CatchKont {
        public IRStmt sf;

        public CatchKont(IRStmt sf) {
            this.sf = sf;
        }
    }

    public static class FinKont {
        public ImmutableSet<Value> vs;

        public FinKont(ImmutableSet<Value> vs) {
            this.vs = vs;
        }
    }

    public static class LblKont {
        public String lbl;

        public LblKont(String lbl) {
            this.lbl = lbl;
        }
    }

    public static class AddrKont {
        public AddressSpace.Address a;
        public IRMethod m;

        public AddrKont(AddressSpace.Address a, IRMethod m) {
            this.a = a;
            this.m = m;
        }
    }

    public static class KontStack {
        public ImmutableList<Kont> ks;
        public ImmutableList<Integer> exc;

        public KontStack(ImmutableList<Kont> ks, ImmutableList<Integer> exc) {
            this.ks = ks;
            this.exc = exc;
        }
    }
}
