package org.perudevteam.lexer;

import io.vavr.Function1;
import io.vavr.collection.Array;
import io.vavr.control.Option;

/**
 * Table Driven DFA.
 * This class implements the State Machine Interface.
 * Accepting states are stored in an array of options.
 * Transition edges are stored in a matrix of options.
 * The big catch here is that the user must provide some input class calculator function.
 * Since the transitions are stored in an array, they can only be accessed with integer
 * indices. Given some input, your input class function should return an integer in the range
 * [0, |states| - 1].
 */
public abstract class TableDFA<I, O> implements StateMachine<I, O> {
    public static <I, O> TableDFA<I, O> tableDFA(Function1<I, ? extends Option<? extends Integer>> cl,
                    Array<? extends Option<O>> as,
                    Array<? extends Array<? extends Option<? extends Integer>>> tt) {
        return new TableDFA<I, O>(as, tt) {
            @Override
            public Option<Integer> getInputClass(I input) {
                return Option.narrow(cl.apply(input));
            }
        };
    }

    private Array<Option<O>> acceptingStates;
    private Array<Array<Option<Integer>>> transTable;

    private TableDFA(Array<? extends Option<O>> as,
                     Array<? extends Array<? extends Option<? extends Integer>>> tt) {
        acceptingStates = Array.narrow(as);
        transTable = Array.narrow(tt.map(r -> Array.narrow(r.map(Option::narrow))));
    }

    public abstract Option<Integer> getInputClass(I input);

    @Override
    public Option<Integer> nextState(int state, I input) {
        return getInputClass(input)
                .flatMap(cl -> transTable.get(state).get(cl));
    }

    @Override
    public Option<O> getOutcome(int state) {
        return acceptingStates.get(state);
    }
}
