package concrete;

import concrete.init.Init;
import fj.*;
import fj.data.*;
import immutable.FHashMap;
import immutable.FHashSet;
import immutable.FVector;
import ir.*;

/**
 * Created by wayne on 15/10/27.
 */
public class Domains {

    public static abstract class Term {}

    public static final class StmtTerm extends Term {
        public final IRStmt s;

        public StmtTerm(final IRStmt s) {
            this.s = s;
        }

        /*@Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof StmtTerm && s.equals(((StmtTerm) obj).s));
        }

        @Override
        public int hashCode() {
            return P.p(s).hashCode();
        }*/
    }

    public static final class ValueTerm extends Term {
        public final Value v;

        public ValueTerm(final Value v) {
            this.v = v;
        }

        /*@Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof ValueTerm && v.equals(((ValueTerm) obj).v));
        }

        @Override
        public int hashCode() {
            return P.p(v).hashCode();
        }*/
    }

    public static class Env {
        public final FHashMap<IRPVar, Address> env;

        public Env(final FHashMap<IRPVar, Address> env) {
            this.env = env;
        }

        /*@Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Env && env.equals(((Env) obj).env));
        }

        @Override
        public int hashCode() {
            return P.p(env).hashCode();
        }*/

        public Address apply(final IRPVar x) {
            return env.get(x).some();
        }

        public Env extendAll(final List<P2<IRPVar, Address>> bind) {
            return new Env(env.union(bind));
        }

        public Env filter(final F<IRPVar, Boolean> f) {
            return new Env(env.filter(f));
        }
    }

    public static final class Store {
        public final FHashMap<Address, BValue> toValue;
        public final FHashMap<Address, Object> toObject;

        public Store(final FHashMap<Address, BValue> toValue, final FHashMap<Address, Object> toObject) {
            this.toValue = toValue;
            this.toObject = toObject;
        }

        /*@Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Store && toValue.equals(((Store) obj).toValue) && toObject.equals(((Store) obj).toObject));
        }

        @Override
        public int hashCode() {
            return P.p(toValue, toObject).hashCode();
        }*/

        public BValue apply(final Address a) {
            return toValue.get(a).some();
        }

        public Object getObj(final Address a) {
            return toObject.get(a).some();
        }

        public Store extend(final P2<Address, BValue> av) {
            return new Store(toValue.set(av._1(), av._2()), toObject);
        }

        public Store extendAll(final List<P2<Address, BValue>> avs) {
            return new Store(toValue.union(avs), toObject);
        }

        public Store putObj(final Address a, final Object o) {
            return new Store(toValue, toObject.set(a, o));
        }
    }

    public static class Scratchpad {
        public final FVector<BValue> mem;

        public Scratchpad(final FVector<BValue> mem) {
            this.mem = mem;
        }

        /*@Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Scratchpad && mem.equals(((Scratchpad) obj).mem));
        }

        @Override
        public int hashCode() {
            return P.p(mem).hashCode();
        }*/

        public BValue apply(final IRScratch x) {
            return mem.index(x.n);
        }

        public Scratchpad update(final IRScratch x, final BValue bv) {
            return new Scratchpad(mem.update(x.n, bv));
        }

        public static Scratchpad apply(final int len) {
            return new Scratchpad(FVector.build(len, Undef));
        }
    }

    public static abstract class Value {}

    public static final class EValue extends Value {
        public final BValue bv;

        public EValue(final BValue bv) {
            this.bv = bv;
        }

        /*@Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof EValue && bv.equals(((EValue) obj).bv));
        }

        @Override
        public int hashCode() {
            return P.p(bv).hashCode();
        }*/
    }

    public static final class JValue extends Value {
        public final String lbl;
        public final BValue bv;

        public JValue(final String lbl, final BValue bv) {
            this.lbl = lbl;
            this.bv = bv;
        }

        /*@Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof JValue && lbl.equals(((JValue) obj).lbl) && bv.equals(((JValue) obj).bv));
        }

        @Override
        public int hashCode() {
            return P.p(lbl, bv).hashCode();
        }*/
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
            return Bool.apply(this.equals(bv));
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
            return bv1.logicalOr(bv2);
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

    public static final class Num extends BValue {
        public final double n;

        public Num(final double n) {
            this.n = n;
        }

        @Override
        public String toString() {
            return String.valueOf(n);
        }

        /*@Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Num && n == ((Num) obj).n);
        }

        @Override
        public int hashCode() {
            return P.p(n).hashCode();
        }*/

        @Override
        public BValue plus(final BValue bv) {
            if (bv instanceof Num) {
                return new Num(n + ((Num) bv).n);
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue minus(final BValue bv) {
            if (bv instanceof Num) {
                return new Num(n - ((Num) bv).n);
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue times(final BValue bv) {
            if (bv instanceof Num) {
                return new Num(n * ((Num) bv).n);
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue divide(final BValue bv) {
            if (bv instanceof Num) {
                return new Num(n / ((Num) bv).n);
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue mod(final BValue bv) {
            if (bv instanceof Num) {
                return new Num(n % ((Num) bv).n);
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue shl(final BValue bv) {
            if (bv instanceof Num) {
                return new Num((double)((long)n << (long)((Num) bv).n));
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue sar(final BValue bv) {
            if (bv instanceof Num) {
                return new Num((double)((long)n >> (long)((Num) bv).n));
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue shr(final BValue bv) {
            if (bv instanceof Num) {
                return new Num((double)((long)n >>> (long)((Num) bv).n));
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue lessThan(final BValue bv) {
            if (bv instanceof Num) {
                return Bool.apply(n < ((Num) bv).n);
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue lessEqual(final BValue bv) {
            if (bv instanceof Num) {
                return Bool.apply(n <= ((Num) bv).n);
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue and(final BValue bv) {
            if (bv instanceof Num) {
                return new Num((double)((long)n & (long)((Num) bv).n));
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue or(final BValue bv) {
            if (bv instanceof Num) {
                return new Num((double)((long)n | (long)((Num) bv).n));
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue xor(final BValue bv) {
            if (bv instanceof Num) {
                return new Num((double)((long)n ^ (long)((Num) bv).n));
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
            return new Num((double)(~(long)n));
        }

        @Override
        public Bool toBool() {
            return Bool.apply(n != 0 && !Double.isNaN(n));
        }
        @Override
        public Str toStr() {
            if ((long)n == n) {
                return new Str(Long.toString((long)n));
            } else {
                return new Str(String.valueOf(n));
            }
        }
        @Override
        public Num toNum() {
            return this;
        }

        @Override
        public <T> T accept(final BValueVisitor<T> ask) {
            return ask.forNum(this);
        }

        static final long maxU32 = 4294967295L;
        public static boolean isU32(final BValue bv) {
            if (bv instanceof Num) {
                double n = ((Num) bv).n;
                return ((long)n == n && n >= 0 && n <= maxU32);
            } else {
                return false;
            }
        }
    }

    public static final class Str extends BValue {
        public final String str;
        final int recordHash;

        @Override
        public String toString() {
            return str;
        }

        public Str(final String str) {
            this.str = str;
            this.recordHash = str.hashCode();
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Str && str.equals(((Str) obj).str));
        }

        @Override
        public int hashCode() {
            return recordHash;
        }

        @Override
        public Str strConcat(final BValue bv) {
            if (bv instanceof Str) {
                return new Str(str + ((Str) bv).str);
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue strLessThan(final BValue bv) {
            if (bv instanceof Str) {
                return Bool.apply(str.compareTo(((Str) bv).str) < 0);
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public BValue strLessEqual(final BValue bv) {
            if (bv instanceof Str) {
                return Bool.apply(str.compareTo(((Str) bv).str) <= 0);
            } else {
                throw new RuntimeException("translator reneged");
            }
        }

        @Override
        public Bool toBool() {
            return Bool.apply(!str.isEmpty());
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
        public <T> T accept(final BValueVisitor<T> ask) {
            return ask.forStr(this);
        }
    }

    public static class Bool extends BValue {
        @Override
        public String toString() {
            if (this.equals(True))
                return "true";
            else
                return "false";
        }

        @Override
        public BValue logicalAnd(BValue bv) {
            if (this.equals(False)) {
                return this;
            } else {
                return bv;
            }
        }

        @Override
        public BValue logicalOr(BValue bv) {
            if (this.equals(True)) {
                return this;
            } else {
                return bv;
            }
        }

        @Override
        public BValue logicalNot() {
            if (this.equals(True))
                return False;
            else
                return True;
        }

        @Override
        public <T> T accept(final BValueVisitor<T> ask) {
            return ask.forBool(this);
        }

        @Override
        public Bool toBool() {
            return this;
        }
        @Override
        public Str toStr() {
            if (this.equals(True))
                return new Str("true");
            else
                return new Str("false");
        }
        @Override
        public Num toNum() {
            if (this.equals(True)) {
                return new Num(1.0);
            } else {
                return new Num(0.0);
            }
        }

        public static final Bool True = new Bool();
        public static final Bool False = new Bool();

        public static Bool apply(final boolean b) {
            if (b) {
                return True;
            } else {
                return False;
            }
        }
    }

    public static final class Address extends BValue {
        public final int a;

        //public Address() {}
        public Address(final int a) {
            this.a = a;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Address && a == ((Address) obj).a);
        }

        @Override
        public int hashCode() {
            return a;
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
        public <T> T accept(final BValueVisitor<T> ask) {
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
        public String toString() {
            return "undefined";
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
        public <T> T accept(final BValueVisitor<T> ask) {
            return ask.forUndef(this);
        }
    };

    public static final BValue Null = new BValue() {
        @Override
        public String toString() {
            return "null";
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
        public <T> T accept(final BValueVisitor<T> ask) {
            return ask.forNull(this);
        }
    };

    public static abstract class Closure {}

    public static final class Clo extends Closure {
        public final Env env;
        public final IRMethod m;

        public Clo(final Env env, final IRMethod m) {
            this.env = env;
            this.m = m;
        }

        /*@Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Clo && env.equals(((Clo) obj).env) && m.equals(((Clo) obj).m));
        }

        @Override
        public int hashCode() {
            return P.p(env, m).hashCode();
        }*/
    }

    public static final class Native extends Closure {
        public final F7<Address, Address, IRVar, Env, Store, Scratchpad, KontStack, Interpreter.State> f;

        public Native(final F7<Address, Address, IRVar, Env, Store, Scratchpad, KontStack, Interpreter.State> f) {
            this.f = f;
        }

        /*@Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Native && f.equals(((Native) obj).f));
        }

        @Override
        public int hashCode() {
            return P.p(f).hashCode();
        }*/
    }

    public static final class Object {
        public final FHashMap<Str, BValue> extern;
        public final FHashMap<Str, java.lang.Object> intern;

        final JSClass myClass;
        final BValue myProto;

        public Object(final FHashMap<Str, BValue> extern, final FHashMap<Str, java.lang.Object> intern) {
            this.extern = extern;
            this.intern = intern;
            myClass = (JSClass)intern.get(Utils.Fields.classname).some();
            myProto = (BValue)intern.get(Utils.Fields.proto).some();
        }

        /*@Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof Object && extern.equals(((Object) obj).extern) && intern.equals(((Object) obj).intern));
        }

        @Override
        public int hashCode() {
            return P.p(extern, intern).hashCode();
        }*/

        public Option<BValue> apply(final Str str) {
            return extern.get(str);
        }

        public Object update(final Str str, final BValue bv) {
            if (Init.noupdate.get(myClass).orSome(FHashSet.empty()).member(str)) {
                return this;
            } else {
                return new Object(extern.set(str, bv), intern);
            }
        }

        public P2<Object, Boolean> delete(final Str str) {
            if (Init.nodelete.get(myClass).orSome(FHashSet.empty()).member(str) || !(extern.contains(str))) {
                return P.p(this, false);
            } else {
                return P.p(new Object(extern.delete(str), intern), true);
            }
        }

        public FHashSet<Str> fields() {
            return FHashSet.build(extern.keys()).minus(Init.noenum.get(myClass).orSome(FHashSet.empty()));
        }

        public JSClass getJSClass() {
            return myClass;
        }

        public BValue getProto() {
            return myProto;
        }

        public boolean calledAsCtor() {
            return intern.contains(Utils.Fields.constructor);
        }

        public Option<Closure> getCode() {
            final Option<java.lang.Object> code = intern.get(Utils.Fields.code);
            if (code.isSome()) {
                return Option.some((Closure)code.some());
            } else {
                return Option.none();
            }
        }

        public Option<BValue> getValue() {
            final Option<java.lang.Object> value = intern.get(Utils.Fields.value);
            if (value.isSome()) {
                return Option.some((BValue)value.some());
            } else {
                return Option.none();
            }
        }
    }

    public static abstract class Kont {}

    public static final Kont HaltKont = new Kont() {};

    public static final class SeqKont extends Kont {
        public final List<IRStmt> ss;

        public SeqKont(final List<IRStmt> ss) {
            this.ss = ss;
        }

        /*@Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof SeqKont && ss.equals(((SeqKont) obj).ss));
        }

        @Override
        public int hashCode() {
            return P.p(ss).hashCode();
        }*/
    }

    public static final class WhileKont extends Kont {
        public final IRExp e;
        public final IRStmt s;

        public WhileKont(final IRExp e, final IRStmt s) {
            this.e = e;
            this.s = s;
        }

        /*@Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof WhileKont && e.equals(((WhileKont) obj).e) && s.equals(((WhileKont) obj).s));
        }

        @Override
        public int hashCode() {
            return P.p(e, s).hashCode();
        }*/
    }

    public static final class ForKont extends Kont {
        public final List<Str> strs;
        public final IRVar x;
        public final IRStmt s;

        public ForKont(final List<Str> strs, final IRVar x, final IRStmt s) {
            this.strs = strs;
            this.x = x;
            this.s = s;
        }

        /*@Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof ForKont && strs.equals(((ForKont) obj).strs) && x.equals(((ForKont) obj).x) && s.equals(((ForKont) obj).s));
        }

        @Override
        public int hashCode() {
            return P.p(strs, x, s).hashCode();
        }*/
    }

    public static final class RetKont extends Kont {
        public final IRVar x;
        public final Env env;
        public final boolean isctor;
        public final Scratchpad pad;

        public RetKont(final IRVar x, final Env env, final boolean isctor, final Scratchpad pad) {
            this.x = x;
            this.env = env;
            this.isctor = isctor;
            this.pad = pad;
        }

        /*@Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof RetKont && x.equals(((RetKont) obj).x) && env.equals(((RetKont) obj).env) && isctor == ((RetKont) obj).isctor && pad.equals(((RetKont) obj).pad));
        }

        @Override
        public int hashCode() {
            return P.p(x, env, isctor, pad).hashCode();
        }*/
    }

    public static final class TryKont extends Kont {
        public final IRPVar x;
        public final IRStmt sc;
        public final IRStmt sf;

        public TryKont(final IRPVar x, final IRStmt sc, final IRStmt sf) {
            this.x = x;
            this.sc = sc;
            this.sf = sf;
        }

        /*@Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof TryKont && x.equals(((TryKont) obj).x) && sc.equals(((TryKont) obj).sc) && sf.equals(((TryKont) obj).sf));
        }

        @Override
        public int hashCode() {
            return P.p(x, sc, sf).hashCode();
        }*/
    }

    public static final class CatchKont extends Kont {
        public final IRStmt sf;

        public CatchKont(final IRStmt sf) {
            this.sf = sf;
        }

        /*@Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof CatchKont && sf.equals(((CatchKont) obj).sf));
        }

        @Override
        public int hashCode() {
            return P.p(sf).hashCode();
        }*/
    }

    public static final class FinKont extends Kont {
        public final Value v;

        public FinKont(final Value v) {
            this.v = v;
        }

        /*@Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof FinKont && v.equals(((FinKont) obj).v));
        }

        @Override
        public int hashCode() {
            return P.p(v).hashCode();
        }*/
    }

    public static final class LblKont extends Kont {
        public final String lbl;

        public LblKont(final String lbl) {
            this.lbl = lbl;
        }

        /*@Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof LblKont && lbl.equals(((LblKont) obj).lbl));
        }

        @Override
        public int hashCode() {
            return P.p(lbl).hashCode();
        }*/
    }

    public static final class KontStack {
        public final List<Kont> ks;

        public KontStack(final List<Kont> ks) {
            this.ks = ks;
        }

        /*@Override
        public boolean equals(java.lang.Object obj) {
            return (obj instanceof KontStack && ks.equals(((KontStack) obj).ks));
        }

        @Override
        public int hashCode() {
            return P.p(ks).hashCode();
        }*/

        public KontStack push(final Kont k) {
            return new KontStack(ks.cons(k));
        }

        public KontStack pop() {
            return new KontStack(ks.tail());
        }

        public KontStack repl(final Kont k) {
            return new KontStack(ks.tail().cons(k));
        }

        public Kont top() {
            return ks.head();
        }

        public KontStack dropWhile(final F<Kont, Boolean> f) {
            return new KontStack(ks.dropWhile(f));
        }
    }
}
