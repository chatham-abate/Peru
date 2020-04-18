package org.perudevteam.fa;

import io.vavr.Function1;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import io.vavr.control.Option;
import java.util.Objects;

public abstract class DFA<I, IC, O> extends FA<I, IC, O> {
    public static <I, IC, O> DFA<I, IC, O> dfa(int numberOfStates, Set<IC> ia,
                                               Function1<? super I, ? extends IC> getInputClass) {
        return dfa(HashMap.empty(), ia, Array.fill(numberOfStates, HashMap.empty()), getInputClass);
    }

    public static <I, IC, O> DFA<I, IC, O> dfa(Map<? extends Integer, O> as, Set<IC> ia,
                                               Array<? extends Map<IC, ? extends Integer>> tt,
                                               Function1<? super I, ? extends IC> getInputClass) {
        Objects.requireNonNull(getInputClass);
        return new DFA<I, IC, O>(as, ia, tt, true) {
            @Override
            protected IC getInputClassUnchecked(I input) {
                return getInputClass.apply(input);
            }
        };
    }

    private final Array<Map<IC, Integer>> transitionTable;

    public DFA(Map<? extends Integer, O> as, Set<IC> ia,
                Array<? extends Map<IC, ? extends Integer>> tt, boolean withCheck) {
        super(as, ia, withCheck);

        if (withCheck) {
            Objects.requireNonNull(tt);
            if (tt.isEmpty()) {
                throw new IllegalArgumentException("Transition table needs at least 1 row.");
            }

            for (Map<IC, ? extends Integer> row: tt) {
                Objects.requireNonNull(row);

                for (Tuple2<IC, ? extends Integer> cell: row) {
                    IC inputClass = cell._1;
                    Integer transitionState = cell._2;

                    if (!getInputAlphabet().contains(inputClass)) {
                        throw new IllegalArgumentException("Unknown input class found in transition table" +
                                inputClass.toString() + ".");
                    }

                    if (transitionState < 0 || tt.length() <= transitionState) {
                        throw new IndexOutOfBoundsException("Bad transition state found.");
                    }
                }
            }
        }

        transitionTable = Array.narrow(tt.map(Map::narrow));

        // Now that the transition table is set, we can validate the accepting states map.
        if (withCheck) {
            getAcceptingStates().keySet().forEach(this::validateState);
        }
    }

    @Override
    protected int getNumberOfStates() {
        return transitionTable.length();
    }

    public boolean hasTransition(int from, I input) {
        IC inputClass = getInputClass(input);
        validateState(from);
        return transitionTable.get(from).containsKey(inputClass);
    }

    public boolean hasTransitionFromClass(int from, IC inputClass) {
        validateInputClass(inputClass);
        validateState(from);
        return transitionTable.get(from).containsKey(inputClass);
    }

    public int getTransition(int from, I input) {
        IC inputClass = getInputClass(input);
        validateState(from);
        return transitionTable.get(from).get(inputClass).get();
    }

    public Option<Integer> getTransitionAsOption(int from, I input) {
        IC inputClass = getInputClass(input);
        validateState(from);
        return transitionTable.get(from).get(inputClass);
    }

    public int getTransitionFromClass(int from, IC inputClass) {
        validateInputClass(inputClass);
        validateState(from);
        return transitionTable.get(from).get(inputClass).get();
    }

    public Option<Integer> getTransitionFromClassAsOption(int from, IC inputClass) {
        validateInputClass(inputClass);
        validateState(from);
        return transitionTable.get(from).get(inputClass);
    }

    @Override
    public DFA<I, IC, O> withSingleTransition(int from, int to, IC inputClass) {
        validateState(from);
        validateState(to);
        validateInputClass(inputClass);

        final DFA<I, IC, O> thisDFA = this;

        return new DFA<I, IC, O>(getAcceptingStates(), getInputAlphabet(),
                transitionTable.update(from, row -> row.put(inputClass, to)), false) {
            @Override
            protected IC getInputClassUnchecked(I input) {
                return thisDFA.getInputClassUnchecked(input);
            }
        };
    }

    @Override
    public DFA<I, IC, O> withAcceptingState(int state, O output) {
        validateState(state);
        Objects.requireNonNull(output);

        final DFA<I, IC, O> thisDFA = this;

        return new DFA<I, IC, O>(getAcceptingStates().put(state, output), getInputAlphabet(),
                transitionTable, false) {
            @Override
            protected IC getInputClassUnchecked(I input) {
                return thisDFA.getInputClassUnchecked(input);
            }
        };
    }
}
