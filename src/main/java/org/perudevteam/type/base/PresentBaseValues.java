package org.perudevteam.type.base;

import io.vavr.Function1;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;

import java.util.Objects;

public class PresentBaseValues {
    /**
     * Base Byte Class.
     */
    static class BaseByte  extends BaseValue {
        private byte value;

        BaseByte(byte v) {
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
    static class BaseShort extends BaseValue {
        private short value;

        BaseShort(short v) {
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
    static class BaseInt extends BaseValue{
        private int value;

        BaseInt(int v) {
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
    static class BaseLong extends BaseValue {
        private long value;

        BaseLong(long v) {
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
    static class BaseFloat extends BaseValue {
        private float value;
        BaseFloat(float v) {
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
    static class BaseDouble extends BaseValue {
        private double value;
        BaseDouble(double v) {
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
    static class BaseCharacter extends BaseValue {
        private char value;
        BaseCharacter(char v) {
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
    static class BaseEnum  extends BaseValue {
        private Enum value;
        BaseEnum(Enum v) {
            super(BaseType.ENUM);
            Objects.requireNonNull(v);
            value = v;
        }

        @Override
        public Enum toEnum() {
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
    static class BaseBoolean extends BaseValue {
        static final BaseBoolean TRUE = new BaseBoolean(true);
        static final BaseBoolean FALSE = new BaseBoolean(false);

        private boolean value;
        BaseBoolean(boolean v) {
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
    static class BaseString extends BaseValue {
        private String value;
        BaseString(String v) {
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
    static class BaseMap extends BaseValue {
        private Map<String, BaseValue> value;
        BaseMap(Map<? extends String, ? extends BaseValue> v) {
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
    static class BaseSequence extends BaseValue {
        private Seq<BaseValue> value;
        BaseSequence(Seq<? extends BaseValue> v) {
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
    static class BaseFunction extends BaseValue {
        private Function1<Seq<BaseValue>, BaseValue> value;
        BaseFunction(Function1<? super Seq<BaseValue>, ? extends BaseValue> v) {
            super(BaseType.FUNCTION);
            Objects.requireNonNull(v);
            value = Function1.narrow(v);
        }

        @Override
        public Function1<Seq<BaseValue>, BaseValue> toFunction() {
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
