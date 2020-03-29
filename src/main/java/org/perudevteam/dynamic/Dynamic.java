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

    static Dynamic ofSequence(Seq<Dynamic> v) {
        return new DynaSeq(v);
    }

    static Dynamic ofMap(Map<? super String, Dynamic> v) {
        return new DynaMap(v);
    }

    static Dynamic ofReference(Object v) {
        return new DynaRef(v);
    }

    default int asInt() {
        throw new NullPointerException("Dynamic Value contains no Integer value.");
    }

    default double asDouble() {
        throw new NullPointerException("Dynamic Value contains no Double value.");
    }

    default String asString() {
        throw new NullPointerException("Dynamic Value contains no String value.");
    }

    default boolean asBoolean() {
        throw new NullPointerException("Dynamic Value contains no Boolean value.");
    }

    default Enum asEnum() {
        throw new NullPointerException("Dynamic Value contains no Enum value.");
    }

    default Seq<Dynamic> asSequence() {
        throw new NullPointerException("Dynamic Value contains no Sequence value.");
    }

    default Map<? super String, Dynamic> asMap() {
        throw new NullPointerException("Dynamic Value contains no Map value.");
    }

    default Object asReference() {
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
        public Object asReference() {
            return value;
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
        public Object asReference() {
            return value;
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
        public Object asReference() {
            return value;
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
        public Object asReference() {
            return value;
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
        public Object asReference() {
            return value;
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
        public Object asReference() {
            return value;
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
        public Object asReference() {
            return value;
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
    }
}