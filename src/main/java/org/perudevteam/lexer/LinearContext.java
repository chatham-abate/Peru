package org.perudevteam.lexer;

public interface LinearContext<C> {
    int getAbsolutePosition();

    C withAbsolutePosition(int absPosition);

    C dropPreErrorsBefore(int absPosition);

    boolean isPreError(int absPosition, int state);

    C withPreError(int errorPosition, int errorState);
}
