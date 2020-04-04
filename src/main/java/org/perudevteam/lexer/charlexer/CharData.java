package org.perudevteam.lexer.charlexer;

import org.perudevteam.misc.Typed;

import java.util.Objects;

public class CharData<T extends Enum<T>> implements Typed<T> {
    private T type;
    private int lineNumber;

    public CharData(T t, int l) {
        Objects.requireNonNull(t);

        type = t;
        lineNumber = l;
    }

    @Override
    public T getType() {
        return type;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public <OT extends Enum<OT>> CharData<OT> withType(OT t) {
        return new CharData<>(t, lineNumber);
    }

    public CharData<T> withLineNumber(int ln) {
        return new CharData<>(type, ln);
    }

    @Override
    public String toString() {
        return "[" + type.name() + ", " + lineNumber + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharData charData = (CharData) o;
        return lineNumber == charData.lineNumber &&
                type.equals(charData.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, lineNumber);
    }
}
