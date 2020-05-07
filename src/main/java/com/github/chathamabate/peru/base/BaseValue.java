package com.github.chathamabate.peru.base;

import com.github.chathamabate.type.Tagged;
import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.collection.Array;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.control.Try;
import com.github.chathamabate.misc.MiscHelpers;

import java.util.Objects;
import java.util.function.Function;


public abstract class BaseValue extends Tagged<BaseType> {
    public static ClassCastException castError(BaseType from, BaseType to) {
        return new ClassCastException("Base Type " + from.name() + " cannot be converted to " + to.name() + ".");
    }

    private static final Map<BaseType, BaseValue> PLACEHOLDERS = HashMap.ofEntries(Array.of(BaseType.values())
            .map(v -> Tuple.of(v, new BaseValue(v) {
                // Place Holder Base Value... Only holds a tag... no value.
            })));

    private BaseValue(BaseType tag) {
        super(tag);
    }


    /*
     * Casting Functions.
     */


    public byte toByte() {
        throw castError(getTag(), BaseType.BYTE);
    }

    public short toShort() {
        throw castError(getTag(), BaseType.SHORT);
    }

    public int toInt() {
        throw castError(getTag(), BaseType.INT);
    }

    public long toLong() {
        throw castError(getTag(), BaseType.LONG);
    }

    public float toFloat() {
        throw castError(getTag(), BaseType.FLOAT);
    }

    public double toDouble() {
        throw castError(getTag(), BaseType.DOUBLE);
    }

    // Characters are special, they can be widened to everything below and including int.
    // Nothing can be widened to Character since char is unsigned.
    public char toCharacter() {
        throw castError(getTag(), BaseType.CHARACTER);
    }

    /*
     * The above order refers to java widening rules.
     * The below casts have no widening or narrowing.
     * Ex, a Base Value holding an enum can only be cast to an enum... nothing else.
     *
     * (Except for String, everything can be cast to a String)
     */

    public Enum<?> toEnum() {
        throw castError(getTag(), BaseType.ENUM);
    }

    public boolean toBoolean() {
        throw castError(getTag(), BaseType.BOOLEAN);
    }

    @Override
    public String toString() {
        throw castError(getTag(), BaseType.STRING);
    }

    public Map<String, BaseValue> toMap() {
        throw castError(getTag(), BaseType.MAP);
    }

    public Seq<BaseValue> toSequence() {
        throw castError(getTag(), BaseType.SEQUENCE);
    }

    public Function1<Seq<BaseValue>, Try<BaseValue>> toFunction() {
        throw castError(getTag(), BaseType.FUNCTION);
    }


    /*
     * Static Constructors.
     */


    public static BaseValue ofByte(byte v) {
        return new BaseByte(v);
    }

    public static BaseValue ofShort(short v) {
        return new BaseShort(v);
    }

    public static BaseValue ofInt(int v) {
        return new BaseInt(v);
    }

    public static BaseValue ofLong(long v) {
        return new BaseLong(v);
    }

    public static BaseValue ofFloat(float v) {
        return new BaseFloat(v);
    }

    public static BaseValue ofDouble(double v) {
        return new BaseDouble(v);
    }

    public static BaseValue ofCharacter(char v) {
        return new BaseCharacter(v);
    }

    public static BaseValue ofEnum(Enum<?> v) {
        Objects.requireNonNull(v);
        return new BaseEnum(v);
    }

    public static BaseValue ofBoolean(boolean v) {
        return v ? BaseBoolean.TRUE : BaseBoolean.FALSE;
    }

    public static BaseValue ofString(String v) {
        Objects.requireNonNull(v);
        return new BaseString(v);
    }

    public static BaseValue ofMap(Map<? extends String, ? extends BaseValue> v) {
        MiscHelpers.requireNonNullMap(v);
        return new BaseMap(v);
    }

    public static BaseValue ofSequence(Seq<? extends BaseValue> v) {
        Objects.requireNonNull(v);
        v.forEach(Objects::requireNonNull);
        return new BaseSequence(v);
    }

    public static BaseValue ofSequence(BaseValue... v) {
        Objects.requireNonNull(v);
        Array<BaseValue> values = Array.of(v);
        values.forEach(Objects::requireNonNull);
        return new BaseSequence(values);
    }

    public static BaseValue ofFunction(Function1<? super Seq<BaseValue>, ? extends Try<BaseValue>> v) {
        Objects.requireNonNull(v);
        return new BaseFunction(v);
    }

    public static BaseValue placeHolder(BaseType t) {
        Objects.requireNonNull(t);
        return PLACEHOLDERS.get(t).get();
    }

    /*
     * Map Functions.
     */

    public BaseValue mapByte(Function1<? super Byte, ? extends Byte> f) {
        return new BaseByte(f.apply(toByte()));
    }

    public BaseValue mapShort(Function1<? super Short, ? extends Short> f) {
        return new BaseShort(f.apply(toShort()));
    }

    public BaseValue mapInt(Function1<? super Integer, ? extends Integer> f) {
        return new BaseInt(f.apply(toInt()));
    }

    public BaseValue mapLong(Function1<? super Long, ? extends Long> f) {
        return new BaseLong(f.apply(toLong()));
    }

    public BaseValue mapFloat(Function<? super Float, ? extends Float> f) {
        return new BaseFloat(f.apply(toFloat()));
    }

    public BaseValue mapDouble(Function1<? super Double, ? extends Double> f) {
        return new BaseDouble(f.apply(toDouble()));
    }

    public BaseValue mapCharacter(Function1<? super Character, ? extends Character> f) {
        return new BaseCharacter(f.apply(toCharacter()));
    }

    public BaseValue mapEnum(Function1<? super Enum<?>, ? extends Enum<?>> f) {
        return new BaseEnum(f.apply(toEnum()));
    }

    public BaseValue mapBoolean(Function1<? super Boolean, ? extends Boolean> f) {
        return ofBoolean(f.apply(toBoolean()));
    }

    public BaseValue mapString(Function1<? super String, ? extends String> f) {
        return new BaseString(f.apply(toString()));
    }

    public BaseValue mapMap(Function1<? super Map<String, BaseValue>, ? extends Map<String, BaseValue>> f) {
        return new BaseMap(f.apply(toMap()));
    }

    public BaseValue mapSequence(Function1<? super Seq<BaseValue>, ? extends Seq<BaseValue>> f) {
        return new BaseSequence(f.apply(toSequence()));
    }

    public BaseValue mapFunction(Function1<? super Function1<Seq<BaseValue>, Try<BaseValue>>,
            ? extends Function1<Seq<BaseValue>, Try<BaseValue>>> f) {
        return new BaseFunction(f.apply(toFunction()));
    }

    /**
     * Base Byte Class.
     */
    private static class BaseByte  extends BaseValue {
        private final byte value;

        private BaseByte(byte v) {
            super(BaseType.BYTE);
            value = v;
        }

        @Override
        public byte toByte() {
            return value;
        }

        @Override
        public short toShort() {
            return value;
        }

        @Override
        public int toInt() {
            return value;
        }

        @Override
        public long toLong() {
            return value;
        }

        @Override
        public float toFloat() {
            return value;
        }

        @Override
        public double toDouble() {
            return value;
        }

        @Override
        public String toString() {
            return Byte.toString(value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            BaseByte baseByte = (BaseByte) o;
            return value == baseByte.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), value);
        }
    }


    /**
     * Base Short Class.
     */
    private static class BaseShort extends BaseValue {
        private final short value;

        private BaseShort(short v) {
            super(BaseType.SHORT);
            value = v;
        }

        @Override
        public short toShort() {
            return value;
        }

        @Override
        public int toInt() {
            return value;
        }

        @Override
        public long toLong() {
            return value;
        }

        @Override
        public float toFloat() {
            return value;
        }

        @Override
        public double toDouble() {
            return value;
        }

        @Override
        public String toString() {
            return Short.toString(value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            BaseShort baseShort = (BaseShort) o;
            return value == baseShort.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), value);
        }
    }

    /**
     * Base Int Class.
     */
    private static class BaseInt extends BaseValue{
        private final int value;

        private BaseInt(int v) {
            super(BaseType.INT);
            value = v;
        }

        @Override
        public int toInt() {
            return value;
        }

        @Override
        public long toLong() {
            return value;
        }

        @Override
        public float toFloat() {
            return value;
        }

        @Override
        public double toDouble() {
            return value;
        }

        @Override
        public String toString() {
            return Integer.toString(value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            BaseInt baseInt = (BaseInt) o;
            return value == baseInt.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), value);
        }
    }


    /**
     * Base Long Class.
     */
    private static class BaseLong extends BaseValue {
        private final long value;

        private BaseLong(long v) {
            super(BaseType.LONG);
            value = v;
        }

        @Override
        public long toLong() {
            return value;
        }

        @Override
        public float toFloat() {
            return value;
        }

        @Override
        public double toDouble() {
            return value;
        }

        @Override
        public String toString() {
            return Long.toString(value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            BaseLong baseLong = (BaseLong) o;
            return value == baseLong.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), value);
        }
    }

    /**
     * Base Float Class.
     */
    private static class BaseFloat extends BaseValue {
        private final float value;
        private BaseFloat(float v) {
            super(BaseType.FLOAT);
            value = v;
        }

        @Override
        public float toFloat() {
            return value;
        }

        @Override
        public double toDouble() {
            return value;
        }

        @Override
        public String toString() {
            return Float.toString(value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            BaseFloat baseFloat = (BaseFloat) o;
            return Float.compare(baseFloat.value, value) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), value);
        }
    }


    /**
     * Base Double Class.
     */
    private static class BaseDouble extends BaseValue {
        private final double value;
        private BaseDouble(double v) {
            super(BaseType.DOUBLE);
            value = v;
        }

        @Override
        public double toDouble() {
            return value;
        }

        @Override
        public String toString() {
            return Double.toString(value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            BaseDouble that = (BaseDouble) o;
            return Double.compare(that.value, value) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), value);
        }
    }


    /**
     * Base Character Class.
     */
    private static class BaseCharacter extends BaseValue {
        private final char value;
        private BaseCharacter(char v) {
            super(BaseType.CHARACTER);
            value = v;
        }

        @Override
        public int toInt() {
            return value;
        }

        @Override
        public long toLong() {
            return value;
        }

        @Override
        public float toFloat() {
            return value;
        }

        @Override
        public double toDouble() {
            return value;
        }

        @Override
        public char toCharacter() {
            return value;
        }

        @Override
        public String toString() {
            return Character.toString(value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            BaseCharacter that = (BaseCharacter) o;
            return value == that.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), value);
        }
    }


    /**
     * Base Enum Class.
     */
    private static class BaseEnum  extends BaseValue {
        private final Enum<?> value;
        private BaseEnum(Enum<?> v) {
            super(BaseType.ENUM);
            Objects.requireNonNull(v);
            value = v;
        }

        @Override
        public Enum<?> toEnum() {
            return value;
        }

        @Override
        public String toString() {
            return value.name();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            BaseEnum baseEnum = (BaseEnum) o;
            return Objects.equals(value, baseEnum.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), value);
        }
    }


    /**
     * Base Boolean Class.
     */
    private static class BaseBoolean extends BaseValue {
        static final BaseBoolean TRUE = new BaseBoolean(true);
        static final BaseBoolean FALSE = new BaseBoolean(false);

        private final boolean value;
        private BaseBoolean(boolean v) {
            super(BaseType.BOOLEAN);
            value = v;
        }

        @Override
        public boolean toBoolean() {
            return value;
        }

        @Override
        public String toString() {
            return Boolean.toString(value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            BaseBoolean that = (BaseBoolean) o;
            return value == that.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), value);
        }
    }


    /**
     * Base String Class.
     */
    private static class BaseString extends BaseValue {
        private final String value;
        private BaseString(String v) {
            super(BaseType.STRING);
            Objects.requireNonNull(v);
            value = v;
        }

        @Override
        public String toString() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            BaseString that = (BaseString) o;
            return value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), value);
        }
    }


    /**
     * Base Map Class.
     */
    private static class BaseMap extends BaseValue {
        private final Map<String, BaseValue> value;
        private BaseMap(Map<? extends String, ? extends BaseValue> v) {
            super(BaseType.MAP);
            Objects.requireNonNull(v);
            value = Map.narrow(v);
        }

        @Override
        public Map<String, BaseValue> toMap() {
            return value;
        }

        @Override
        public String toString() {
            return value.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            BaseMap baseMap = (BaseMap) o;
            return value.equals(baseMap.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), value);
        }
    }


    /**
     * Base Sequence Class.
     */
    private static class BaseSequence extends BaseValue {
        private final Seq<BaseValue> value;
        private BaseSequence(Seq<? extends BaseValue> v) {
            super(BaseType.SEQUENCE);
            Objects.requireNonNull(v);
            v.forEach(Objects::requireNonNull);
            value = Seq.narrow(v);
        }

        @Override
        public Seq<BaseValue> toSequence() {
            return value;
        }

        @Override
        public String toString() {
            return value.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            BaseSequence that = (BaseSequence) o;
            return value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), value);
        }
    }


    /**
     * Base Function Class.
     */
    private static class BaseFunction extends BaseValue {
        private final Function1<Seq<BaseValue>, Try<BaseValue>> value;
        private BaseFunction(Function1<? super Seq<BaseValue>, ? extends Try<BaseValue>> v) {
            super(BaseType.FUNCTION);
            Objects.requireNonNull(v);
            value = Function1.narrow(v);
        }

        @Override
        public Function1<Seq<BaseValue>, Try<BaseValue>> toFunction() {
            return value;
        }

        @Override
        public String toString() {
            return value.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            BaseFunction that = (BaseFunction) o;
            return value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), value);
        }
    }
}
