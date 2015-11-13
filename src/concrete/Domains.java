package concrete;

import concrete.init.Init;
import fj.*;
import fj.data.*;
import ir.*;

import java.util.ArrayList;

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
        public TreeMap<IRPVar, Address> env;

        public Env(TreeMap<IRPVar, Address> env) {
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

        public Address apply(IRPVar x) {
            return env.get(x).some();
        }

        public Env extendAll(List<P2<IRPVar, Address>> bind) {
            return new Env(env.union(bind));
        }

        public Env filter(F<IRPVar, Boolean> f) {
            return new Env(TreeMap.treeMap(Ord.hashEqualsOrd(), env.keys().filter(f).map(x -> P.p(x, env.get(x).some()))));
        }
    }

    public static class Store {
        public TreeMap<Address, BValue> toValue;
        public TreeMap<Address, Object> toObject;

        public Store(TreeMap<Address, BValue> toValue, TreeMap<Address, Object> toObject) {
            this.toValue = toValue;
            this.toObject = toObject;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Store && toValue.equals(((Store) obj).toValue) && toObject.equals(((Store) obj).toObject));
        }

        @Override
        public int hashCode() {
            return P.p(toValue, toObject).hashCode();
        }

        public BValue apply(Address a) {
            return toValue.get(a).some();
        }

        public Object getObj(Address a) {
            return toObject.get(a).some();
        }

        public Store extend(P2<Address, BValue> av) {
            return new Store(toValue.set(av._1(), av._2()), toObject);
        }

        public Store extendAll(List<P2<Address, BValue>> avs) {
            return new Store(toValue.union(avs), toObject);
        }

        public Store putObj(Address a, Object o) {
            return new Store(toValue, toObject.set(a, o));
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

        public BValue apply(IRScratch x) {
            return mem.index(x.n);
        }

        public Scratchpad update(IRScratch x, BValue bv) {
            return new Scratchpad(mem.update(x.n, bv));
        }

        public static Scratchpad apply(Integer len) {
            ArrayList<BValue> bvs = new ArrayList<>(len);
            for (int i = 0; i < len; i += 1) {
                bvs.add(i, Undef);
            }
            return new Scratchpad(Seq.seq(List.list(bvs)));
        }
    }

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

    public interface BValueVisitor<T> {
        T forNum(Num bNum);
        T forBool(Bool bBool);
        T forStr(Str bStr);
        T forNull(BValue bNull);
        T forUndef(BValue bUndef);
        T forAddress(Address bAddress);
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
            if (this.equals(Undef) && bv.equals(Null)) {
                bv2 = Bool.True;
            } else if (this.equals(Null) && bv.equals(Undef)) {
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

        public abstract <T> T accept(BValueVisitor<T> ask);
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
        public int hashCode() {
            return P.p(n).hashCode();
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
        public <T> T accept(BValueVisitor<T> ask) {
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
        public int hashCode() {
            return P.p(str).hashCode();
        }

        @Override
        public Str strConcat(BValue bv) {
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
        public <T> T accept(BValueVisitor<T> ask) {
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
        public int hashCode() {
            return P.p(b).hashCode();
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
        public <T> T accept(BValueVisitor<T> ask) {
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

        public Address() {}
        public Address(Integer a) {
            this.a = a;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Address && a.equals(((Address) obj).a));
        }

        @Override
        public int hashCode() {
            return P.p(a).hashCode();
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
        public <T> T accept(BValueVisitor<T> ask) {
            return ask.forAddress(this);
        }

        static int count = 0;
        public static Address generate() {
            count += 1;
            return new Address(count);
        }
    }

    public static final BValue Undef = new BValue() {
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
        public <T> T accept(BValueVisitor<T> ask) {
            return ask.forUndef(this);
        }
    };

    public static final BValue Null = new BValue() {
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
        public <T> T accept(BValueVisitor<T> ask) {
            return ask.forNull(this);
        }
    };

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
        public F7<Address, Address, IRVar, Env, Store, Scratchpad, KontStack, Interpreter.State> f;

        public Native(F7<Address, Address, IRVar, Env, Store, Scratchpad, KontStack, Interpreter.State> f) {
            this.f = f;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Native && f.equals(((Native) obj).f));
        }

        @Override
        public int hashCode() {
            return P.p(f).hashCode();
        }
    }

    public static class Object {
        public TreeMap<Str, BValue> extern;
        public TreeMap<Str, java.lang.Object> intern;

        JSClass myClass;
        BValue myProto;

        public Object(TreeMap<Str, BValue> extern, TreeMap<Str, java.lang.Object> intern) {
            this.extern = extern;
            this.intern = intern;
            myClass = (JSClass)intern.get(Utils.Fields.classname).some();
            myProto = (BValue)intern.get(Utils.Fields.proto).some();
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Object && extern.equals(((Object) obj).extern) && intern.equals(((Object) obj).intern));
        }

        @Override
        public int hashCode() {
            return P.p(extern, intern).hashCode();
        }

        public Option<BValue> apply(Str str) {
            return extern.get(str);
        }

        public Object update(Str str, BValue bv) {
            if (Init.noupdate.get(myClass).orSome(Set.empty(Ord.hashEqualsOrd())).member(str)) {
                return this;
            } else {
                return new Object(extern.set(str, bv), intern);
            }
        }

        public P2<Object, Boolean> delete(Str str) {
            if (Init.nodelete.get(myClass).orSome(Set.empty(Ord.hashEqualsOrd())).member(str) || !(extern.contains(str))) {
                return P.p(this, false);
            } else {
                return P.p(new Object(extern.delete(str), intern), true);
            }
        }

        public Set<Str> fields() {
            return Set.set(Ord.hashEqualsOrd(), extern.keys()).minus(Init.noenum.get(myClass).orSome(Set.empty(Ord.hashEqualsOrd())));
        }

        public JSClass getJSClass() {
            return myClass;
        }

        public BValue getProto() {
            return myProto;
        }

        public Boolean calledAsCtor() {
            return intern.contains(Utils.Fields.constructor);
        }

        public Option<Closure> getCode() {
            Option<java.lang.Object> code = intern.get(Utils.Fields.code);
            if (code.isSome()) {
                return Option.some((Closure)code.some());
            } else {
                return Option.none();
            }
        }

        public Option<BValue> getValue() {
            Option<java.lang.Object> value = intern.get(Utils.Fields.value);
            if (value.isSome()) {
                return Option.some((BValue)value.some());
            } else {
                return Option.none();
            }
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
        public List<Str> strs;
        public IRVar x;
        public IRStmt s;

        public ForKont(List<Str> strs, IRVar x, IRStmt s) {
            this.strs = strs;
            this.x = x;
            this.s = s;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof ForKont && strs.equals(((ForKont) obj).strs) && x.equals(((ForKont) obj).x) && s.equals(((ForKont) obj).s));
        }

        @Override
        public int hashCode() {
            return P.p(strs, x, s).hashCode();
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

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof RetKont && x.equals(((RetKont) obj).x) && env.equals(((RetKont) obj).env) && isctor.equals(((RetKont) obj).isctor) && pad.equals(((RetKont) obj).pad));
        }

        @Override
        public int hashCode() {
            return P.p(x, env, isctor, pad).hashCode();
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
        public Value v;

        public FinKont(Value v) {
            this.v = v;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof FinKont && v.equals(((FinKont) obj).v));
        }

        @Override
        public int hashCode() {
            return P.p(v).hashCode();
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

    public static class KontStack {
        public List<Kont> ks;

        public KontStack(List<Kont> ks) {
            this.ks = ks;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof KontStack && ks.equals(((KontStack) obj).ks));
        }

        @Override
        public int hashCode() {
            return P.p(ks).hashCode();
        }

        public KontStack push(Kont k) {
            return new KontStack(ks.snoc(k));
        }

        public KontStack pop() {
            return new KontStack(ks.tail());
        }

        public KontStack repl(Kont k) {
            return new KontStack(ks.tail().snoc(k));
        }

        public Kont top() {
            return ks.head();
        }

        public KontStack dropWhile(F<Kont, Boolean> f) {
            return new KontStack(ks.dropWhile(f));
        }
    }
}
