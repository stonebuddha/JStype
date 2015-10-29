package concrete;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.sun.istack.internal.Nullable;
import concrete.init.Init;
import ir.*;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by wayne on 15/10/27.
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
        public ImmutableMap<IRPVar, Address> env;

        public Env(ImmutableMap<IRPVar, Address> env) {
            this.env = env;
        }

        public Address apply(IRPVar x) {
            return env.get(x);
        }

        public Env extendAll(ImmutableList<Map.Entry<IRPVar, Address>> bind) {
            ImmutableMap.Builder<IRPVar, Address> builder = ImmutableMap.<IRPVar, Address>builder().putAll(env);
            for (Map.Entry<IRPVar, Address> entry : bind) {
                builder = builder.put(entry);
            }
            return new Env(builder.build());
        }
    }

    public static class Store {
        public ImmutableMap<Address, BValue> toValue;
        public ImmutableMap<Address, Object> toObject;

        public Store(ImmutableMap<Address, BValue> toValue, ImmutableMap<Address, Object> toObject) {
            this.toValue = toValue;
            this.toObject = toObject;
        }

        public BValue apply(Address a) {
            return toValue.get(a);
        }

        public Object getObj(Address a) {
            return toObject.get(a);
        }

        public Store extend(Map.Entry<Address, BValue> av) {
            return new Store(ImmutableMap.<Address, BValue>builder().putAll(toValue).put(av).build(), toObject);
        }

        public Store extendAll(ImmutableList<Map.Entry<Address, BValue>> avs) {
            ImmutableMap.Builder<Address, BValue> builder = ImmutableMap.<Address, BValue>builder().putAll(toValue);
            for (Map.Entry<Address, BValue> entry : avs) {
                builder = builder.put(entry);
            }
            return new Store(builder.build(), toObject);
        }

        public Store putObj(Address a, Object o) {
            return new Store(toValue, ImmutableMap.<Address, Object>builder().putAll(toObject).put(a, o).build());
        }
    }

    public static class Scratchpad {
        public ImmutableList<BValue> mem;

        public Scratchpad(ImmutableList<BValue> mem) {
            this.mem = mem;
        }

        public BValue apply(IRScratch x) {
            return mem.get(x.n);
        }

        public Scratchpad update(IRScratch x, BValue bv) {
            int sz = mem.size();
            return new Scratchpad(ImmutableList.<BValue>builder().
                    addAll(mem.subList(0, x.n)).
                    add(bv).addAll(mem.subList(x.n + 1, sz)).
                    build());
        }

        public static Scratchpad apply(Integer len) {
            ArrayList<BValue> bvs = new ArrayList<>(len);
            for (int i = 0; i < len; i += 1) {
                bvs.add(i, new Undef());
            }
            return new Scratchpad(ImmutableList.<BValue>builder().addAll(bvs).build());
        }
    }

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

    public interface BValueVisitor {
        java.lang.Object forNum(Num bNum);
        java.lang.Object forBool(Bool bBool);
        java.lang.Object forStr(Str bStr);
        java.lang.Object forNull(Null bNull);
        java.lang.Object forUndef(Undef bUndef);
        java.lang.Object forAddress(Address bAddress);
    }

    public static abstract class BValue extends Value {
        public BValue plus(BValue bv) { throw new RuntimeException("translator reneged"); }
        public BValue minus(BValue bv) { throw new RuntimeException("translator reneged"); }
        public BValue times(BValue bv) { throw new RuntimeException("translator reneged"); }
        public BValue divide(BValue bv) { throw new RuntimeException("translator reneged"); }
        public BValue mod(BValue bv) { throw new RuntimeException("translator reneged"); }
        public BValue shl(BValue bv) { throw new RuntimeException("translator reneged"); }
        public BValue sar(BValue bv) { throw new RuntimeException("translator reneged"); }
        public BValue shr(BValue bv) { throw new RuntimeException("translator reneged"); }
        public BValue lessThan(BValue bv) { throw new RuntimeException("translator reneged"); }
        public BValue lessEqual(BValue bv) { throw new RuntimeException("translator reneged"); }
        public BValue and(BValue bv) { throw new RuntimeException("translator reneged"); }
        public BValue or(BValue bv) { throw new RuntimeException("translator reneged"); }
        public BValue xor(BValue bv) { throw new RuntimeException("translator reneged"); }
        public BValue logicalAnd(BValue bv) { throw new RuntimeException("translator reneged"); }
        public BValue logicalOr(BValue bv) { throw new RuntimeException("translator reneged"); }
        public BValue strConcat(BValue bv) { throw new RuntimeException("translator reneged"); }
        public BValue strLessThan(BValue bv) { throw new RuntimeException("translator reneged"); }
        public BValue strLessEqual(BValue bv) { throw new RuntimeException("translator reneged"); }

        public BValue strictEqual(BValue bv) {
            return new Bool(this.equals(bv));
        }

        public BValue nonStrictEqual(BValue bv) {
            BValue bv1 = this.strictEqual(bv);
            BValue bv2 = Bool.False;
            if (this instanceof Undef && bv instanceof Null) {
                bv2 = Bool.True;
            } else if (this instanceof Null && bv instanceof Undef) {
                bv2 = Bool.True;
            } else if (this instanceof Num && bv instanceof Str) {
                return this.strictEqual(bv.toNum());
            } else if (this instanceof Str && bv instanceof Num) {
                return this.toNum().strictEqual(bv);
            }
            return bv1.or(bv2);
        }

        public BValue negate() { throw new RuntimeException("translator reneged"); }
        public BValue not() { throw new RuntimeException("translator reneged"); }
        public BValue logicalNot() { throw new RuntimeException("translator reneged"); }

        public BValue isPrim() {
            return Bool.True;
        }
        public abstract Bool toBool();
        public abstract Str toStr();
        public abstract Num toNum();

        public abstract java.lang.Object accept(BValueVisitor ask);
    }

    public static class Num extends BValue {
        public Double n;

        public Num(Double n) {
            this.n = n;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Num && n.equals(((Num) obj).n));
        }

        @Override
        public BValue plus(BValue bv) {
            if (bv instanceof Num) {
                return new Num(n + ((Num) bv).n);
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue minus(BValue bv) {
            if (bv instanceof Num) {
                return new Num(n - ((Num) bv).n);
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue times(BValue bv) {
            if (bv instanceof Num) {
                return new Num(n * ((Num) bv).n);
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue divide(BValue bv) {
            if (bv instanceof Num) {
                return new Num(n / ((Num) bv).n);
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue mod(BValue bv) {
            if (bv instanceof Num) {
                return new Num(n % ((Num) bv).n);
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue shl(BValue bv) {
            if (bv instanceof Num) {
                return new Num((double)(n.longValue() << ((Num) bv).n.longValue()));
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue sar(BValue bv) {
            if (bv instanceof Num) {
                return new Num((double)(n.longValue() >> ((Num) bv).n.longValue()));
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue shr(BValue bv) {
            if (bv instanceof Num) {
                return new Num((double)(n.longValue() >>> ((Num) bv).n.longValue()));
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue lessThan(BValue bv) {
            if (bv instanceof Num) {
                return new Bool(n < ((Num) bv).n);
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue lessEqual(BValue bv) {
            if (bv instanceof Num) {
                return new Bool(n <= ((Num) bv).n);
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue and(BValue bv) {
            if (bv instanceof Num) {
                return new Num((double)(n.longValue() & ((Num) bv).n.longValue()));
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue or(BValue bv) {
            if (bv instanceof Num) {
                return new Num((double)(n.longValue() | ((Num) bv).n.longValue()));
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue xor(BValue bv) {
            if (bv instanceof Num) {
                return new Num((double)(n.longValue() ^ ((Num) bv).n.longValue()));
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue negate() {
            return new Num(-n);
        }

        @Override
        public BValue not() {
            return new Num((double)(~n.longValue()));
        }

        @Override
        public Bool toBool() {
            return new Bool(n != 0 && !n.isNaN());
        }
        @Override
        public Str toStr() {
            if (n.longValue() == n) {
                return new Str(Long.toString(n.longValue()));
            } else {
                return new Str(n.toString());
            }
        }
        @Override
        public Num toNum() {
            return this;
        }

        @Override
        public java.lang.Object accept(BValueVisitor ask) {
            return ask.forNum(this);
        }

        static final long maxU32 = 4294967295L;
        public static Boolean isU32(BValue bv) {
            if (bv instanceof Num) {
                Double n = ((Num) bv).n;
                return (n.longValue() == n && n >= 0 && n <= maxU32);
            } else {
                return false;
            }
        }
    }

    public static class Str extends BValue {
        public String str;

        public Str(String str) {
            this.str = str;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Str && str.equals(((Str) obj).str));
        }

        @Override
        public BValue strConcat(BValue bv) {
            if (bv instanceof Str) {
                return new Str(str + ((Str) bv).str);
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue strLessThan(BValue bv) {
            if (bv instanceof Str) {
                return new Bool(str.compareTo(((Str) bv).str) < 0);
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue strLessEqual(BValue bv) {
            if (bv instanceof Str) {
                return new Bool(str.compareTo(((Str) bv).str) <= 0);
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public Bool toBool() {
            return new Bool(!str.isEmpty());
        }
        @Override
        public Str toStr() {
            return this;
        }
        @Override
        public Num toNum() {
            try {
                return new Num(Double.valueOf(str));
            } catch (NumberFormatException e) {
                return new Num(Double.NaN);
            }
        }

        @Override
        public java.lang.Object accept(BValueVisitor ask) {
            return ask.forStr(this);
        }
    }

    public static class Bool extends BValue {
        public Boolean b;

        public Bool(Boolean b) {
            this.b = b;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Bool && b.equals(((Bool) obj).b));
        }

        @Override
        public BValue logicalAnd(BValue bv) {
            if (!b) {
                return this;
            } else {
                return bv;
            }
        }

        @Override
        public BValue logicalOr(BValue bv) {
            if (b) {
                return this;
            } else {
                return bv;
            }
        }

        @Override
        public BValue logicalNot() {
            return new Bool(!b);
        }

        @Override
        public java.lang.Object accept(BValueVisitor ask) {
            return ask.forBool(this);
        }

        @Override
        public Bool toBool() {
            return this;
        }
        @Override
        public Str toStr() {
            return new Str(b.toString());
        }
        @Override
        public Num toNum() {
            if (b) {
                return new Num(1.0);
            } else {
                return new Num(0.0);
            }
        }

        public static final Bool True = new Bool(true);
        public static final Bool False = new Bool(false);
    }

    public static class Address extends BValue {
        public Integer a;

        public Address(Integer a) {
            this.a = a;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Address && a.equals(((Address) obj).a));
        }


        @Override
        public BValue isPrim() {
            return Bool.False;
        }

        @Override
        public Bool toBool() {
            return Bool.True;
        }
        @Override
        public Str toStr() {
            throw new RuntimeException("translator reneged");
        }
        @Override
        public Num toNum() {
            throw new RuntimeException("translator reneged");
        }

        @Override
        public java.lang.Object accept(BValueVisitor ask) {
            return ask.forAddress(this);
        }

        static int count = 0;
        public static Address generate() {
            count += 1;
            return new Address(count);
        }
    }

    public static class Undef extends BValue {
        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Undef);
        }

        @Override
        public Bool toBool() {
            return Bool.False;
        }
        @Override
        public Str toStr() {
            return new Str("undefined");
        }
        @Override
        public Num toNum() {
            return new Num(Double.NaN);
        }

        @Override
        public java.lang.Object accept(BValueVisitor ask) {
            return ask.forUndef(this);
        }
    }

    public static class Null extends BValue {
        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Null);
        }

        @Override
        public Bool toBool() {
            return Bool.False;
        }
        @Override
        public Str toStr() {
            return new Str("null");
        }
        @Override
        public Num toNum() {
            return new Num(0.0);
        }

        @Override
        public java.lang.Object accept(BValueVisitor ask) {
            return ask.forNull(this);
        }
    }

    public static abstract class Closure {}

    public static class Clo extends Closure {
        public Env env;
        public IRMethod m;

        public Clo(Env env, IRMethod m) {
            this.env = env;
            this.m = m;
        }
    }

    public static class Object {
        public ImmutableMap<Str, BValue> extern;
        public ImmutableMap<Str, java.lang.Object> intern;

        JSClass myClass;
        BValue myProto;

        public Object(ImmutableMap<Str, BValue> extern, ImmutableMap<Str, java.lang.Object> intern) {
            this.extern = extern;
            this.intern = intern;
            myClass = (JSClass)intern.get(Utils.Fields.classname);
            myProto = (BValue)intern.get(Utils.Fields.proto);
        }

        @Nullable
        public BValue apply(Str str) {
            return extern.get(str);
        }

        public Object update(Str str, BValue bv) {
            if (Init.noupdate.getOrDefault(myClass, ImmutableSet.<Str>of()).contains(str)) {
                return this;
            } else {
                return new Object(ImmutableMap.<Str, BValue>builder().putAll(extern).put(str, bv).build(), intern);
            }
        }

        public Map.Entry<Object, Boolean> delete(Str str) {
            if ((Init.nodelete.getOrDefault(myClass, ImmutableSet.<Str>of()).contains(str)) || !(extern.containsKey(str))) {
                return new AbstractMap.SimpleImmutableEntry<>(this, false);
            } else {
                // TODO: time complexity needs to be refined
                ImmutableMap.Builder<Str, BValue> builder = ImmutableMap.<Str, BValue>builder();
                for (Map.Entry<Str, BValue> entry : extern.entrySet()) {
                    if (!entry.getKey().equals(str)) {
                        builder = builder.put(entry);
                    }
                }
                return new AbstractMap.SimpleImmutableEntry<>(
                        new Object(builder.build(), intern),
                        true
                );
            }
        }

        public ImmutableSet<Str> fields() {
            // TODO: time complexity needs to be refined
            ImmutableSet.Builder<Str> builder = ImmutableSet.<Str>builder();
            ImmutableSet<Str> noenum = Init.noenum.getOrDefault(myClass, ImmutableSet.<Str>of());
            for (Str str : extern.keySet()) {
                if (!noenum.contains(str)) {
                    builder = builder.add(str);
                }
            }
            return builder.build();
        }

        public JSClass getJSClass() {
            return myClass;
        }

        public BValue getProto() {
            return myProto;
        }

        public Boolean calledAsCtor() {
            return intern.containsKey(Utils.Fields.constructor);
        }

        @Nullable
        public Closure getCode() {
            java.lang.Object code = intern.get(Utils.Fields.code);
            if (code != null) {
                return (Closure)code;
            } else {
                return null;
            }
        }

        @Nullable
        public BValue getValue() {
            java.lang.Object value = intern.get(Utils.Fields.value);
            if (value != null) {
                return (BValue)value;
            } else {
                return null;
            }
        }
    }

    public static abstract class Kont {}

    public static class HaltKont extends Kont {
        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof HaltKont);
        }
    }

    public static class SeqKont extends Kont {
        public ImmutableList<IRStmt> ss;

        public SeqKont(ImmutableList<IRStmt> ss) {
            this.ss = ss;
        }
    }

    public static class WhileKont extends Kont {
        public IRExp e;
        public IRStmt s;

        public WhileKont(IRExp e, IRStmt s) {
            this.e = e;
            this.s = s;
        }
    }

    public static class ForKont extends Kont {
        public ImmutableList<Str> strs;
        public IRVar x;
        public IRStmt s;

        public ForKont(ImmutableList<Str> strs, IRVar x, IRStmt s) {
            this.strs = strs;
            this.x = x;
            this.s = s;
        }
    }

    public static class RetKont extends Kont {
        public IRVar x;
        public Env env;
        public Boolean isctor;
        public Scratchpad pad;

        public RetKont(IRVar x, Env env, Boolean isctor, Scratchpad pad) {
            this.x = x;
            this.env = env;
            this.isctor = isctor;
            this.pad = pad;
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
    }

    public static class CatchKont extends Kont {
        public IRStmt sf;

        public CatchKont(IRStmt sf) {
            this.sf = sf;
        }
    }

    public static class FinKont extends Kont {
        public Value v;

        public FinKont(Value v) {
            this.v = v;
        }
    }

    public static class LblKont extends Kont {
        public String lbl;

        public LblKont(String lbl) {
            this.lbl = lbl;
        }
    }

    public static class KontStack {
        public ImmutableList<Kont> ks;

        public KontStack(ImmutableList<Kont> ks) {
            this.ks = ks;
        }

        public KontStack push(Kont k) {
            return new KontStack(ImmutableList.<Kont>builder().add(k).addAll(ks).build());
        }

        public KontStack pop() {
            return new KontStack(ks.subList(1, ks.size()));
        }

        public KontStack repl(Kont k) {
            return new KontStack(ImmutableList.<Kont>builder().add(k).addAll(ks.subList(1, ks.size())).build());
        }

        public Kont top() {
            return ks.get(0);
        }

        public KontStack dropWhile(Predicate<Kont> f) {
            for (int i = 0; i < ks.size(); i += 1) {
                if (!f.test(ks.get(i))) {
                    return new KontStack(ks.subList(i, ks.size()));
                }
            }
            return new KontStack(ImmutableList.<Kont>of());
        }
    }
}
