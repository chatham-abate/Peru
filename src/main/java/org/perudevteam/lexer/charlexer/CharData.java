package org.perudevteam.lexer.charlexer;

import java.util.Objects;

public class CharData {
    private Enum type;
    private int lineNumber;

    public CharData(Enum t, int l) {
        type = t;
        lineNumber = l;
    }

    public Enum getType() {
        return type;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public CharData withType(Enum t) {
        return new CharData(t, lineNumber);
    }

    public CharData withLineNumber(int ln) {
        return new CharData(type, ln);
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
