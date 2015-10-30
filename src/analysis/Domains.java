package analysis;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import ir.IRStmt;

import java.math.BigInteger;

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

    public static class Domain {}
    public static final Domain DNum = new Domain();
    public static final Domain DBool = new Domain();
    public static final Domain DStr = new Domain();
    public static final Domain DAddr = new Domain();
    public static final Domain DNull = new Domain();
    public static final Domain DUndef = new Domain();

    public static abstract class Value {}

    public static class BValue extends Value {
        public Num n;
        public Bool b;
        public Str s;
        public ImmutableSet<AddressSpace.Address> as;
        public Null nil;
        public Undef undef;
        public ImmutableSet<Domain> types;

        public BValue(Num n, Bool b, Str s, ImmutableSet<AddressSpace.Address> as, Null nil, Undef undef) {
            this.n = n;
            this.b = b;
            this.s = s;
            this.as = as;
            this.nil = nil;
            this.undef = undef;

            ImmutableSet.Builder<Domain> builder = ImmutableSet.<Domain>builder();
            if (!n.equals(Num.Bot)) builder = builder.add(DNum);
            if (!b.equals(Bool.Bot)) builder = builder.add(DBool);
            if (!s.equals(Str.Bot)) builder = builder.add(DStr);
            if (!as.isEmpty()) builder = builder.add(DAddr);
            if (!nil.equals(Null.Bot)) builder = builder.add(DNull);
            if (!undef.equals(Undef.Bot)) builder = builder.add(DUndef);
            this.types = builder.build();
        }

        public BValue merge(BValue bv) {
            return new BValue(
                    n.merge(bv.n),
                    b.merge(bv.b),
                    s.merge(bv.s),
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

        // TODO: add rest of operations

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
        public boolean equals(Object obj) {
            return (obj instanceof NBot);
        }
    }

    public static class NTop extends Num {
        @Override
        public boolean equals(Object obj) {
            return (obj instanceof NTop);
        }
    }

    public static class NReal extends Num {
        @Override
        public boolean equals(Object obj) {
            return (obj instanceof NReal);
        }
    }

    public static class NConst extends Num {
        public Double d;

        public NConst(Double d) {
            this.d = d;
        }

        @Override
        public boolean equals(Object obj) {
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
        public boolean equals(Object obj) {
            return (obj instanceof BBot);
        }
    }

    public static class BTrue extends Bool {
        @Override
        public boolean equals(Object obj) {
            return (obj instanceof BTrue);
        }
    }

    public static class BFalse extends Bool {
        @Override
        public boolean equals(Object obj) {
            return (obj instanceof BFalse);
        }
    }

    public static class BTop extends Bool {
        @Override
        public boolean equals(Object obj) {
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
        public boolean equals(Object obj) {
            return (obj instanceof SBot);
        }
    }

    public static class STop extends Str {
        @Override
        public boolean equals(Object obj) {
            return (obj instanceof STop);
        }
    }

    public static class SNum extends Str {
        @Override
        public boolean equals(Object obj) {
            return (obj instanceof SNum);
        }
    }

    public static class SNotNum extends Str {
        @Override
        public boolean equals(Object obj) {
            return (obj instanceof SNotNum);
        }
    }

    public static class SSpl extends Str {
        @Override
        public boolean equals(Object obj) {
            return (obj instanceof SSpl);
        }
    }

    public static class SNotSplNorNum extends Str {
        @Override
        public boolean equals(Object obj) {
            return (obj instanceof SNotSplNorNum);
        }
    }

    public static class SNotSpl extends Str {
        @Override
        public boolean equals(Object obj) {
            return (obj instanceof SNotSpl);
        }
    }

    public static class SConstNum extends Str {
        public String str;

        public SConstNum(String str) {
            this.str = str;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof SConstNum && ((SConstNum) obj).str.equals(str));
        }
    }

    public static class SConstNotSplNorNum extends Str {
        public String str;

        public SConstNotSplNorNum(String str) {
            this.str = str;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof SConstNotSplNorNum && ((SConstNotSplNorNum) obj).str.equals(str));
        }
    }

    public static class SConstSpl extends Str {
        public String str;

        public SConstSpl(String str) {
            this.str = str;
        }

        @Override
        public boolean equals(Object obj) {
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
        public boolean equals(Object obj) {
            return (obj instanceof MaybeNull);
        }
    }

    public static class NotNull extends Null {
        @Override
        public boolean equals(Object obj) {
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
        public boolean equals(Object obj) {
            return (obj instanceof MaybeUndef);
        }
    }

    public static class NotUndef extends Undef {
        @Override
        public boolean equals(Object obj) {
            return (obj instanceof NotUndef);
        }
    }
}
