package org.perudevteam.type.base;

import io.vavr.Function1;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import org.perudevteam.type.Tagged;

public abstract class BaseValue extends Tagged<BaseType> {
    public static NullPointerException castError(BaseType from, BaseType to) {
        return new NullPointerException("Base Type " + from.name() + " cannot be converted to " + to.name() + ".");
    }

    public BaseValue(BaseType tag) {
        super(tag);
    }

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

    public Enum toEnum() {
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

    public Function1<Seq<BaseValue>, BaseValue> toFunction() {
        throw castError(getTag(), BaseType.FUNCTION);
    }



    /*
     *  Container Classes.
     */



    private static class BaseByte  extends BaseValue {
        private byte value;

        public BaseByte(byte v) {
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
    }

    private static class BaseShort extends BaseValue {
        private short value;

        public BaseShort(short v) {
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
    }

    private static class BaseInt extends BaseValue{
        private int value;

        public BaseInt(int v) {
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
    }

    private static class BaseLong extends BaseValue {
        private long value;

        public BaseLong(long v) {
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
    }

    private static class BaseFloat extends BaseValue {
        private float value;
        public BaseFloat(float v) {
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
    }

    private static class BaseDouble extends BaseValue {
        private double value;

        public BaseDouble(double v) {
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
    }

    private static class BaseCharacter extends BaseValue {
        private char value;
        public BaseCharacter(char v) {
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
    }

    private static class BaseEnum  extends BaseValue {
        private Enum value;
        public BaseEnum(Enum v) {
            super(BaseType.ENUM);
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
    }

    private static class BaseBoolean extends BaseValue {
        private boolean value;
        public BaseBoolean(boolean v) {
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
    }

    private static class BaseString extends BaseValue {
        private String value;
        public BaseString(String v) {
            super(BaseType.STRING);
            value = v;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    private static class BaseMap {

    }

    private static class BaseSequence {

    }

    private static class BaseFunction {

    }
}
