package org.perudevteam.lexer;

import io.vavr.Function1;
import io.vavr.control.Option;
import io.vavr.collection.Array;

public interface StateMachine<I, O> {

    // Get the next state as an option... it may not exist.
    Option<Integer> nextState(int state, I input);

    // Get the outcome of a given state... it too may not exist.
    Option<O> getOutcome(int state);
}
