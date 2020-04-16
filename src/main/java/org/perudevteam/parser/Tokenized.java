package org.perudevteam.parser;

public interface Tokenized<T extends Enum<T>> {
    static <T extends Enum<T>> Tokenized<T> token(T tokenType) {
        return new Tokenized<T>() {
            @Override
            public T getTokenType() {
                return tokenType;
            }

            @Override
            public String toString() {
                return tokenType.name();
            }
        };
    }

    T getTokenType();
}
