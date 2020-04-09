package org.perudevteam.statemachine;

import io.vavr.control.Option;
import io.vavr.collection.*;

import java.util.Objects;

// Deterministic Finite State Machine.
public class DFStateMachine<I, O> implements DStateMachine<I, O>, FStateMachine<O> {

    public static <I, O> DFStateMachine<I,O> emptyDFSM(int states) {
        if (states < 0) {
            throw new IllegalArgumentException("States must be non-negative.");
        }

        return new DFStateMachine<>(Array.fill(states, HashMap.empty()), HashMap.empty());
    }

    private Array<Map<I, Integer>> transitionTable;
    private Map<Integer, O> acceptingStates;

    private DFStateMachine(Array<? extends Map<I, ? extends Integer>> tt, Map<Integer, O> as) {
        transitionTable = Array.narrow(tt.map(Map::narrow));
        acceptingStates = as;
    }

    // Deterministic State Machine Functions.

    @Override
    public Option<Integer> getNextStateUnsafe(int st, I in) {
        return transitionTable.get(st).get(in);
    }

    @Override
    public DFStateMachine<I, O> withEdge(int from, int to, I in) {
        validateState(from);
        validateState(to);
        Objects.requireNonNull(in);

        return new DFStateMachine<>(transitionTable.update(from, m -> m.put(in, to)), acceptingStates);
    }

    private DFStateMachine<I, O> withEdgeUnsafe(int from, int to, I in) {
        return new DFStateMachine<>(transitionTable.update(from, m -> m.put(in, to)), acceptingStates);
    }

    @Override
    public DFStateMachine<I, O> withEdges(Seq<Integer> froms, int to, Seq<I> ins) {
        Objects.requireNonNull(ins);
        ins.forEach(Objects::requireNonNull);

        Objects.requireNonNull(froms);
        froms.forEach(i -> {
            Objects.requireNonNull(i);
            validateState(i);
        });

        DFStateMachine<I, O> dsm = this;

        for (int from: froms) {
            for (I in: ins) {
                dsm = dsm.withEdgeUnsafe(from, to, in);
            }
        }

        return dsm;
    }

    // Finite State Machine Function.

    @Override
    public int getNumberOfStates() {
        return transitionTable.length();
    }

    // Plain State Machine Functions.

    @Override
    public Option<O> getOutputUnsafe(int st) {
        return acceptingStates.get(st);
    }

    @Override
    public boolean isAcceptingUnsafe(int st) {
        return acceptingStates.containsKey(st);
    }

    @Override
    public DFStateMachine<I, O> withAcceptingState(int st, O output) {
        validateState(st);
        return new DFStateMachine<>(transitionTable, acceptingStates.put(st, output));
    }
}
