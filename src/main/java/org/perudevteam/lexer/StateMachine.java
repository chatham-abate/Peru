package org.perudevteam.lexer;

import io.vavr.Function1;
import io.vavr.control.Option;
import io.vavr.collection.Array;

public interface StateMachine<I, O> {

    static <I, O> StateMachine<I, O> tableDFA(Function1<I, ? extends Integer> ic,
                       Array<? extends Option<O>> as,
                       Array<? extends Array<? extends Option<? extends Integer>>> tt) {
        return new TableDFA<>(ic, as, tt);
    }

    // Get the next state as an option... it may not exist.
    Option<Integer> nextState(int state, I input);

    // Get the outcome of a given state... it too may not exist.
    Option<O> getOutcome(int state);

    class TableDFA<I, O> implements StateMachine<I, O> {
        Function1<I, Integer> inputClass;
        Array<Option<O>> acceptingStates;
        Array<Array<Option<Integer>>> transTable;

        private TableDFA(Function1<I, ? extends Integer> ic,
                         Array<? extends Option<O>> as,
                         Array<? extends Array<? extends Option<? extends Integer>>> tt) {
            inputClass = Function1.narrow(ic);
            acceptingStates = Array.narrow(as);
            transTable = Array.narrow(tt.map(r -> Array.narrow(r.map(Option::narrow))));
        }

        @Override
        public Option<Integer> nextState(int state, I input) {
            return transTable.get(state).get(inputClass.apply(input));
        }

        @Override
        public Option<O> getOutcome(int state) {
            return acceptingStates.get(state);
        }
    }
}
