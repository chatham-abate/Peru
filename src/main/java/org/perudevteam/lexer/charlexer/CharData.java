package org.perudevteam.lexer.charlexer;

import org.perudevteam.misc.Typed;

import java.util.Objects;

public class CharData<T extends Enum<T>> implements Typed<T> {
    private T type;

    private int line;
    private int linePosition;

    public CharData(T t, CharSimpleContext context) {
        this(t, context.getLine().getStarting(), context.getLinePosition().getStarting());
    }

    public CharData(T t, int l, int lp) {
        Objects.requireNonNull(t);

        type = t;

        line = l;
        linePosition = lp;
    }

    @Override
    public T getType() {
        return type;
    }

    public int getLine() {
        return line;
    }

    public int getLinePosition() {
        return linePosition;
    }

    public <OT extends Enum<OT>> CharData<OT> withType(OT t) {
        return new CharData<>(t, line, linePosition);
    }

    public CharData<T> withLine(int l) {
        return new CharData<>(type, l, linePosition);
    }

    public CharData<T> withLinePosition(int lp) {
        return new CharData<>(type, line, linePosition);
    }

    @Override
    public String toString() {
        return "[" + line + " : " + linePosition + " : " + type.name() + "]";
    }

    /*
     * Generated hashCode and equals.
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharData<?> charData = (CharData<?>) o;
        return line == charData.line &&
                linePosition == charData.linePosition &&
                type.equals(charData.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, line, linePosition);
    }
}
