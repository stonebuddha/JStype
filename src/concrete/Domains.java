package concrete;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import ir.*;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by wayne on 15/10/27.
 */
public class Domains {

    public static abstract class Term {
        public static Term fromStmt(IRStmt s) {
            return new StmtTerm(s);
        }
        public static Term fromValue(Value v) {
            return new ValueTerm(v);
        }
    }

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

        public Env extendAll(ArrayList<Map.Entry<IRPVar, Address>> bind) {
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

        public Store extendAll(ArrayList<Map.Entry<Address, BValue>> avs) {
            ImmutableMap.Builder<Address, BValue> builder = ImmutableMap.<Address, BValue>builder().putAll(toValue);
            for (Map.Entry<Address, BValue> entry : avs) {
                builder = builder.put(entry);
            }
            return new Store(builder.build(), toObject);
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
                    addAll(mem.subList(0, x.n - 1)).
                    add(bv).addAll(mem.subList(x.n + 1, sz - 1)).
                    build());
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

        }
    }

    public static class Num extends BValue {
        public Double n;

        public Num(Double n) {
            this.n = n;
        }
    }

    public static class Str extends BValue {
        public String str;

        public Str(String str) {
            this.str = str;
        }
    }

    public static class Bool extends BValue {
        public Boolean b;

        public Bool(Boolean b) {
            this.b = b;
        }
    }

    public static class Address extends BValue {
        public Integer a;

        public Address(Integer a) {
            this.a = a;
        }

        static int count = 0;
        public static Address generate() {
            count += 1;
            return new Address(count);
        }
    }

    public static class Undef extends BValue {

    }

    public static class Null extends BValue {

    }

    public static class Object {
    }

    public static abstract class Kont {
    }

    public static class HaltKont extends Kont {
    }

    public static class SeqKont extends Kont {
        public ArrayList<IRStmt> ss;

        public SeqKont(ArrayList<IRStmt> ss) {
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
        public ArrayList<Str> strs;
        public IRVar x;
        public IRStmt s;

        public ForKont(ArrayList<Str> strs, IRVar x, IRStmt s) {
            this.strs = strs;
            this.x = x;
            this.s = s;
        }
    }
}
