package org.perudevteam.lexer;

import io.vavr.Function1;
import io.vavr.collection.Array;
import io.vavr.control.Option;

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
