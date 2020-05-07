package com.github.chathamabate.peru.lexer;

import io.vavr.collection.Map;

/**
 * The interface describing how the context for a linear lexer should be formatted.
 *
 * @param <C> The context type.
 */
public interface LinearContext<C> {

    /**
     * A linear context must store the position in the input stream the lexer is at.
     * This function returns that position.
     *
     * @return The absolute position of the lexer.
     */
    int getAbsolutePosition();

    /**
     * Create a new linear context with the given absolute position.
     *
     * @param absPosition The absolute position of the new context.
     * @return The new context.
     */
    C withAbsolutePosition(int absPosition);

    /**
     * Drop all error states cached in the context before some given absolute position.
     *
     * @param absPosition The absolute position.
     * @return The new context.
     */
    C dropPreErrorsBefore(int absPosition);

    /**
     * Determine if being at a given absolute position in the input and a given state in the automaton will result
     * in an error later in the lexing process.
     *
     * @param absPosition The absolute position.
     * @param state The state in the lexer's automaton.
     * @return Whether or not an error state is to come.
     */
    boolean isPreError(int absPosition, int state);

    /**
     * Place found error resulting positions and states into this context's error cache.
     *
     * @param preErrors A map of absolute positions to states.
     * @return The new context.
     */
    C withPreErrors(Map<? extends Integer, ? extends Integer> preErrors);
}
