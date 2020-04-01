package org.perudevteam.lexer;

import io.vavr.collection.Map;

public interface LinearContext<C> {
    int getAbsolutePosition();

    C withAbsolutePosition(int absPosition);

    C dropPreErrorsBefore(int absPosition);

    boolean isPreError(int absPosition, int state);

    C withPreErrors(Map<Integer, ? extends Integer> preErrors);
}
