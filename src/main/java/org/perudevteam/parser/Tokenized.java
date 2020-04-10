package org.perudevteam.parser;

public interface Tokenized<T extends Enum<T>> {
    T getTokenType();
}
