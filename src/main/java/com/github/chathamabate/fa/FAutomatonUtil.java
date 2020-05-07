package com.github.chathamabate.fa;

import io.vavr.collection.*;

import java.util.Objects;

/**
 * Finite Automaton Static Helpers.
 */
public final class FAutomatonUtil {

    /**
     * Private constructor should never be used for this helper class.
     */
    private FAutomatonUtil() {

    }

    /**
     * Convert an <b>NFAutomaton</b> to a <b>DFAutomaton</b>.
     * Enforces precedence signal rules described by {@link NFAutomaton#toDFA(Seq)}.
     *
     * @param nfa The <b>NFAutomaton</b> to convert.
     * @param precSeq The Sequence of signal sets.
     * @param <I> The raw input type of the given automaton.
     * @param <IC> The translated input type of the given automaton.
     * @param <O> The output type of the given automaton.
     * @return The resulting <b>DFAutomaton</b>.
     * @throws Exception When the given <b>NFAutomaton</b> has output ambiguities.
     */
    static <I, IC, O> DFAutomaton<I, IC, O> convertNFAToDFA(
            NFAutomaton<? super I, ? extends IC, ? extends O> nfa,
            Seq<? extends Set<? extends O>> precSeq) throws Exception {
        Objects.requireNonNull(nfa);

        Map<O, Integer> precMap = precedenceMap(precSeq);

        // NFA Accepting states and Transition Table.
        Array<Map<IC, Set<Integer>>> nfaTT = nfa.getTransitionTable().map(Map::narrow);
        Map<Integer, O> nfaAS = Map.narrow(nfa.getAcceptingStates());

        // Array of All NFA States' e* transitions.
        Array<Set<Integer>> epsilonStar = reachableSets(nfa.getEpsilonTransitions());

        // This will map Set's of NFA states to their equivalent DFA states.
        Map<Set<Integer>, Integer> stateSetIndex = HashMap.empty();

        Seq<Map<IC, Integer>> dfaTT = Vector.empty();   // Starts as Vector.
        Map<Integer, O> dfaAS = HashMap.empty();

        Seq<Set<Integer>> workQueue = Queue.empty();    // Work stack for algorithm.

        // Start with an e closure on state 0 from the NFA.
        workQueue = workQueue.append(epsilonStar.get(0));
        stateSetIndex = stateSetIndex.put(epsilonStar.get(0), 0);   // Set position of First State.
        dfaTT = dfaTT.append(HashMap.empty());   // Add a rou for state 0 in the transition table.

        while (!workQueue.isEmpty()) {
            // Get next work item.
            Set<Integer> newStateSet = workQueue.head();
            workQueue = workQueue.tail();

            // Get its index.
            int newStateSetIndex = stateSetIndex.get(newStateSet).get();

            // Find stateSet's underlying outputs.
            Set<O> underlyingOutputs = HashSet.ofAll(newStateSet.filter(nfaAS::containsKey))
                    .map(acceptState -> nfaAS.get(acceptState).get());

            if (underlyingOutputs.length() > 0) {
                O output = getMostPrecedent(precMap, underlyingOutputs);
                dfaAS = dfaAS.put(newStateSetIndex, output);
            }

            // Flatten Transition Sets for all found inputs.
            Map<IC, Set<Integer>> transitionSets = HashMap.empty();

            for (int nfaState: newStateSet) {
                for (IC transitionInput: nfaTT.get(nfaState).keySet()) {
                    if (!transitionSets.containsKey(transitionInput)) {
                        transitionSets = transitionSets.put(transitionInput, HashSet.empty());
                    }

                    transitionSets = transitionSets.put(transitionInput,
                            transitionSets.get(transitionInput).get()
                                    .addAll(nfaTT.get(nfaState).get(transitionInput).get()));
                }
            }

            // Perform e* Closures.
            transitionSets = transitionSets.mapValues(ts -> epsilonClosure(ts, epsilonStar));

            Map<IC, Integer> dfaTTRow = HashMap.empty();

            // Now we must number each of our sets... and/or add them to the work queue and state index.
            for (IC input: transitionSets.keySet()) {
                Set<Integer> stateSet = transitionSets.get(input).get();

                if (!stateSetIndex.containsKey(stateSet)) {
                    int newStateIndex = dfaTT.length();
                    dfaTT = dfaTT.append(HashMap.empty());
                    stateSetIndex = stateSetIndex.put(stateSet, newStateIndex);
                    workQueue = workQueue.append(stateSet);
                }

                dfaTTRow = dfaTTRow.put(input, stateSetIndex.get(stateSet).get());
            }

            dfaTT = dfaTT.update(newStateSetIndex, dfaTTRow);
        }

        return new DFAutomaton<>(dfaAS, nfa.getInputAlphabet(), Array.ofAll(dfaTT),
                nfa.getGetInputClassUnchecked(), false);
    }

    /**
     * Given a map of elements to their precedences and a set of elements.
     * Return the element with the highest precedence in the given set.
     * <br>
     * Here precedence is represented by an integer. The lower the integer, the higher the precedence.
     * Basically and argmin function.
     *
     * @param precMap The map of elements to their precedence values.
     * @param outputs The set of elements.
     * @param <O> The type of the elements.
     * @return The element with the highest precedence.
     * @throws Exception When two or more elements in the given set share the highest precedence value.
     */
    static <O> O getMostPrecedent(Map<? extends O, ? extends Integer> precMap,
                                   Set<? extends O> outputs) throws Exception {

        Map<O, Integer> narrowPrecMap = Map.narrow(precMap);
        Set<O> mostPrecedent = HashSet.empty();
        int mostPrecedentRank = Integer.MAX_VALUE;    // Most Precedent Rank is 0.

        for (O output: outputs) {
            int rank = narrowPrecMap.containsKey(output)
                    ? narrowPrecMap.get(output).get()
                    : Integer.MAX_VALUE;

            if (rank == mostPrecedentRank) {
                mostPrecedent = mostPrecedent.add(output);
            } else if (rank < mostPrecedentRank) {
                mostPrecedentRank = rank;
                mostPrecedent = HashSet.of(output);
            }
        }

        if (mostPrecedent.length() > 1) {
            throw new Exception("Ambiguous Accepting State.");
        }

        return mostPrecedent.head();
    }

    /**
     * Given a sequence of precedence sets, create a precedence map. Basically map each element
     * to the index of its parent set in the given sequence.
     *
     * @param precSeq The sequence of precedence sets.
     * @param <O> The type of the elements.
     * @return A map mapping each element to its precedence.
     */
    static <O> Map<O, Integer> precedenceMap(Seq<? extends Set<? extends O>> precSeq) {
        // No NULLs anywhere.
        Objects.requireNonNull(precSeq);
        precSeq.forEach(Objects::requireNonNull);
        precSeq.forEach(s -> s.forEach(Objects::requireNonNull));

        Map<O, Integer> precMap = HashMap.empty();

        for (int i = 0; i < precSeq.length(); i++) {
            for (O output: precSeq.get(i)) {
                precMap = precMap.put(output, i);
            }
        }

        return precMap;
    }

    /**
     * Perform an epsilon closure on the given set of states.
     *
     * @param stateSet The set of states to perform the closure on.
     * @param epsilonStar The epsilon star sets for every state.
     * @return The set of states reachable from the input set using only epsilon transitions.
     */
    static Set<Integer> epsilonClosure(Set<Integer> stateSet, Array<Set<Integer>> epsilonStar) {
        Set<Integer> eClosure = HashSet.empty();

        for (int state: stateSet) {
            eClosure = eClosure.addAll(epsilonStar.get(state));
        }

        return eClosure;
    }

    /**
     * Given a directed graph, determine the set of reachable nodes for each node in the graph.
     *
     * @param graph The graph.
     * @return A sequence, of reachable sets.
     */
    static Array<Set<Integer>> reachableSets(Array<? extends Set<? extends Integer>> graph) {
        Array<Set<Integer>> reachableSets = Array.rangeBy(0, graph.length(), 1).map(HashSet::of);

        int oldSize = 0;
        int newSize = graph.length();

        while (newSize - oldSize > 0) {
            oldSize = newSize;
            newSize = 0;

            Array<Set<Integer>> tempReachableSets = Array.fill(graph.length(), HashSet.empty());

            for (int node = 0; node < graph.length(); node++) {
                Set<Integer> nodeTempReachableSet = reachableSets.get(node);
                for (int neighbor: graph.get(node)) {
                    nodeTempReachableSet = nodeTempReachableSet.addAll(reachableSets.get(neighbor));
                }

                newSize += nodeTempReachableSet.length();
                tempReachableSets = tempReachableSets.update(node, nodeTempReachableSet);
            }

            reachableSets = tempReachableSets;
        }

        return reachableSets;
    }
}
