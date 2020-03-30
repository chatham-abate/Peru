package org.perudevteam.dynamic;

import io.vavr.CheckedFunction1;
import io.vavr.Function1;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import io.vavr.control.Try;


import java.util.Objects;

/**
 * Functional Dynamic Value interface and implementations.
 * This represents a value with a "Dynamic" type.
 *
 * Types include : int, double, String, boolean, Enum, Map, Sequence, Reference.
 */
public interface Dynamic {

    Dynamic NULL = new Dynamic() {
        // Null Dynamic
    };

    Dynamic TRUE = new DynaBoolean(true);
    Dynamic FALSE = new DynaBoolean(false);

    static Dynamic ofNull() {
        return NULL;
    }

    static Dynamic ofInt(int v) {
        return new DynaInt(v);
    }

    static Dynamic ofDouble(double v) {
        return new DynaDouble(v);
    }

    static Dynamic ofString(String v) {
        return new DynaString(v);
    }

    static Dynamic ofTrue() {
        return TRUE;
    }

    static Dynamic ofFalse() {
        return FALSE;
    }

    static Dynamic ofEnum(Enum v) {
        return new DynaEnum(v);
    }

    static Dynamic ofSequence(Seq<? extends Dynamic> v) {
        return new DynaSeq(v);
    }

    static Dynamic ofMap(Map<? super String, ? extends Dynamic> v) {
        return new DynaMap(v);
    }

    static Dynamic ofReference(Object v) {
        return new DynaRef(v);
    }

    default int asInt() {
        throw new NullPointerException("Dynamic Value contains no Integer value.");
    }

    default boolean isInt() {
        return false;
    }

    default Dynamic mapInt(Function1<? super Integer, ? extends Integer> f) {
        throw new NullPointerException("Dynamic Value contains no Integer value.");
    }

    default double asDouble() {
        throw new NullPointerException("Dynamic Value contains no Double value.");
    }

    default boolean isDouble() {
        return false;
    }

    default Dynamic mapDouble(Function1<? super Double, ? extends Double> f) {
        throw new NullPointerException("Dynamic Value contains no Double value.");
    }


    default String asString() {
        throw new NullPointerException("Dynamic Value contains no String value.");
    }

    default boolean isString() {
        return false;
    }

    default Dynamic mapString(Function1<? super String, ? extends String> f) {
        throw new NullPointerException("Dynamic Value contains no String value.");
    }

    default boolean asBoolean() {
        throw new NullPointerException("Dynamic Value contains no Boolean value.");
    }

    default boolean isBoolean() {
        return false;
    }

    default Dynamic mapBoolean(Function1<? super Boolean, ? extends Boolean> f) {
        throw new NullPointerException("Dynamic Value contains no Boolean value.");
    }

    default Enum asEnum() {
        throw new NullPointerException("Dynamic Value contains no Enum value.");
    }

    default boolean isEnum() {
        return false;
    }

    default Dynamic mapEnum(Function1<? super Enum, ? extends Enum>  f) {
        throw new NullPointerException("Dynamic Value contains no Enum value.");
    }

    default Seq<Dynamic> asSequence() {
        throw new NullPointerException("Dynamic Value contains no Sequence value.");
    }

    default boolean isSequence() {
        return false;
    }

    default Dynamic mapSequence(Function1<? super Seq<? extends Dynamic>, ? extends Seq<? extends Dynamic>> f) {
        throw new NullPointerException("Dynamic Value contains no Sequence value.");
    }

    default Map<? super String, Dynamic> asMap() {
        throw new NullPointerException("Dynamic Value contains no Map value.");
    }

    default boolean isMap() {
        return false;
    }

    default Dynamic mapMap(Function1<? super Map<? super String, ? extends Dynamic>,
            ? extends Map<? super String, ? extends Dynamic>> f) {
        throw new NullPointerException("Dynamic Value contains no Map value.");
    }

    default Object asReference() {
        throw new NullPointerException("Dynamic Value contains no Reference value.");
    }

    default Dynamic mapReference(Function1<Object, Object> f) {
        throw new NullPointerException("Dynamic Value contains no Reference value.");
    }

    default boolean isNull() {
        return Try.of(this::asReference).isFailure();
    }

    default Dynamic map(Function1<? super Dynamic, ? extends Dynamic> mapper) {
        Objects.requireNonNull(mapper, "Given Map function is null.");
        return mapper.apply(this);
    }

    default Try<Dynamic> tryMap(CheckedFunction1<? super Dynamic, ? extends Dynamic> mapper) {
        Objects.requireNonNull(mapper, "Given Map function is null.");
        return Try.of(() -> mapper.apply(this));
    }

    default Option<Dynamic> optionMap(CheckedFunction1<? super Dynamic, ? extends Dynamic> mapper) {
        Objects.requireNonNull(mapper, "Given Map function is null.");
        return tryMap(mapper).toOption();
    }

    class DynaInt implements Dynamic {
        private int value;
        private DynaInt(int v) {
            value = v;
        }

        @Override
        public int asInt() {
            return value;
        }

        @Override
        public boolean isInt() {
            return true;
        }

        @Override
        public Dynamic mapInt(Function1<? super Integer, ? extends Integer> f) {
            return new DynaInt(f.apply(value));
        }

        @Override
        public Object asReference() {
            return value;
        }

        @Override
        public String toString() {
            return "" + value;
        }

        @Override
        public boolean equals(Object o) {
            if (o.getClass() != this.getClass()) return false;
            DynaInt other = (DynaInt)o;
            return value == other.asInt();
        }
    }

    class DynaDouble implements Dynamic {
        private double value;
        private DynaDouble(double v) {
            value = v;
        }

        @Override
        public double asDouble() {
            return value;
        }

        @Override
        public boolean isDouble() {
            return true;
        }

        @Override
        public Dynamic mapDouble(Function1<? super Double, ? extends Double> f) {
            return new DynaDouble(f.apply(value));
        }

        @Override
        public Object asReference() {
            return value;
        }

        @Override
        public String toString() {
            return "" + value;
        }

        @Override
        public boolean equals(Object o) {
            if (o.getClass() != this.getClass()) return false;
            DynaDouble other = (DynaDouble)o;
            return value == other.asDouble();
        }
    }

    class DynaString implements Dynamic {
        private String value;
        private DynaString(String v) {
            value = v;
        }

        @Override
        public String asString() {
            return value;
        }

        @Override
        public boolean isString() {
            return true;
        }

        @Override
        public Dynamic mapString(Function1<? super String, ? extends String> f) {
            return new DynaString(f.apply(value));
        }

        @Override
        public Object asReference() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (o.getClass() != this.getClass()) return false;
            DynaString other = (DynaString)o;
            return value.equals(other.asString());
        }
    }

    class DynaBoolean implements Dynamic {
        private boolean value;
        private DynaBoolean(boolean v) {
            value = v;
        }

        @Override
        public boolean asBoolean() {
            return value;
        }

        @Override
        public boolean isBoolean() {
            return true;
        }

        @Override
        public Dynamic mapBoolean(Function1<? super Boolean, ? extends Boolean> f) {
            return f.apply(value) ? TRUE : FALSE;
        }

        @Override
        public Object asReference() {
            return value;
        }

        @Override
        public String toString() {
            return "" + value;
        }

        @Override
        public boolean equals(Object o) {
            if (o.getClass() != this.getClass()) return false;
            DynaBoolean other = (DynaBoolean)o;
            return value == other.asBoolean();
        }
    }

    class DynaEnum implements Dynamic {
        private Enum value;
        private DynaEnum(Enum v) {
            value = v;
        }

        @Override
        public Enum asEnum() {
            return value;
        }

        @Override
        public boolean isEnum() {
            return true;
        }

        @Override
        public Dynamic mapEnum(Function1<? super Enum, ? extends Enum> f) {
            return new DynaEnum(f.apply(value));
        }

        @Override
        public Object asReference() {
            return value;
        }

        @Override
        public String toString() {
            return value.name();
        }

        @Override
        public boolean equals(Object o) {
            if (o.getClass() != this.getClass()) return false;
            DynaEnum other = (DynaEnum)o;
            return value == other.asEnum();
        }
    }

    class DynaSeq implements Dynamic {
        private Seq<Dynamic> value;
        private DynaSeq(Seq<? extends Dynamic> v) {
            value = Seq.narrow(v);
        }

        @Override
        public Seq<Dynamic> asSequence() {
            return value;
        }

        @Override
        public boolean isSequence() {
            return true;
        }

        @Override
        public Dynamic mapSequence(Function1<? super Seq<? extends Dynamic>, ? extends Seq<? extends Dynamic>> f) {
            return new DynaSeq(f.apply(value));
        }

        @Override
        public Object asReference() {
            return value;
        }

        @Override
        public String toString() {
            return value.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (o.getClass() != this.getClass()) return false;
            DynaSeq other = (DynaSeq)o;
            return value.equals(other.asSequence());
        }
    }

    class DynaMap implements Dynamic {
        private Map<? super String, Dynamic> value;
        @SuppressWarnings("unchecked")
        private DynaMap(Map<? super String, ? extends Dynamic> v) {
            value = (Map<? super String, Dynamic>) v;
        }

        @Override
        public Map<? super String, Dynamic> asMap() {
            return value;
        }

        @Override
        public boolean isMap() {
            return true;
        }

        @Override
        public Dynamic mapMap(Function1<? super Map<? super String, ? extends Dynamic>,
                ? extends Map<? super String, ? extends Dynamic>> f) {
            return new DynaMap(f.apply(value));
        }

        @Override
        public Object asReference() {
            return value;
        }

        @Override
        public String toString() {
            return value.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (o.getClass() != this.getClass()) return false;
            DynaMap other = (DynaMap)o;
            return value.equals(other.asMap());
        }
    }

    class DynaRef implements Dynamic {
        private Object value;
        private DynaRef(Object v) {
            value = v;
        }

        @Override
        public Object asReference() {
            return value;
        }

        @Override
        public Dynamic mapReference(Function1<Object, Object> f) {
            return new DynaRef(f.apply(value));
        }

        @Override
        public boolean equals(Object o) {
            if (o.getClass() != this.getClass()) return false;
            DynaRef other = (DynaRef)o;
            return value.equals(other.asReference());
        }
    }
}
