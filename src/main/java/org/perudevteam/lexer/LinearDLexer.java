package org.perudevteam.lexer;

import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.perudevteam.fa.DFAutomaton;

public abstract class LinearDLexer<I, L, D, C extends LinearContext<C>>
        extends DLexer<I, L, D, C> {

    private static final int MAX_ROLLBACK_SIZE = 35;
    private final int maxRollbackAmount;

    public LinearDLexer(L initLex, DFAutomaton<? super I, ?, ? extends Function1<? super C, ? extends D>> d) {
        this(MAX_ROLLBACK_SIZE, initLex, d);
    }

    public LinearDLexer(int mra, L initLex,
                        DFAutomaton<? super I, ?, ? extends Function1<? super C, ? extends D>> d) {
        super(initLex, d);

        if (mra < 0) {
            throw new IllegalArgumentException("Rollback Amount cannot be negative.");
        }

        maxRollbackAmount = mra;
    }

    @Override
    public Tuple3<Tuple2<L, Try<D>>, C, Seq<I>> buildUnchecked(Seq<? extends I> input, C context) {
        // Rollback stack in form (position, state).
        // Starting at the given position, and state 0.
        Map<Integer, Integer> rollbackStack = HashMap.empty();

        Seq<I> tail = Seq.narrow(input);
        C algoContext = context;

        L lexeme = getInitialLexeme();
        Option<Integer> stateOp = Option.of(0);

        Tuple2<L, Try<D>> lastToken = null;
        Seq<I> lastTail = null;
        int lastAbsolutePosition = algoContext.getAbsolutePosition();

        DFAutomaton<I, ?, Function1<C, D>> dfa = getDFA();

        // While not on an error state or pre error state and position, continue.
        int state;
        while (!stateOp.isEmpty() &&
                !algoContext.isPreError(algoContext.getAbsolutePosition(), state = stateOp.get())) {
            if (dfa.isAccepting(state)) {
                Function1<C, D> dataBuilder = dfa.getOutput(state);
                D data = dataBuilder.apply(algoContext);
                lastToken = Tuple.of(lexeme, Try.success(data));
                lastTail = tail;
                lastAbsolutePosition = algoContext.getAbsolutePosition();

                algoContext = onToken(lexeme, data, algoContext);

                // Finally, clear rollback stack.
                rollbackStack = HashMap.empty();
            } else if (rollbackStack.size() < maxRollbackAmount) {
                rollbackStack = rollbackStack.put(algoContext.getAbsolutePosition(), state);
            }

            // Now we advance to the next symbol...
            if (tail.isEmpty()) break;

            I symbol = tail.head();

            // Increment absolute position.
            algoContext = algoContext.withAbsolutePosition(algoContext.getAbsolutePosition() + 1);

            algoContext = readInput(symbol, algoContext);
            lexeme = combineInput(lexeme, symbol);

            tail = tail.tail();

            stateOp = dfa.getTransitionAsOption(state, symbol);
        }

        /*
         * In an error situation, we want recovery to be an option.
         * So, we accept the full unrecognizable lexeme, and return its
         * data type as a failure.
         */
        if (lastToken == null) {
            algoContext = algoContext.dropPreErrorsBefore(algoContext.getAbsolutePosition());
            Try<D> errorData = Try.failure(makeError(lexeme, algoContext));

            // Error.
            return Tuple.of(Tuple.of(lexeme, errorData), onError(lexeme, algoContext), tail);
        }

        algoContext = algoContext.dropPreErrorsBefore(lastAbsolutePosition);
        algoContext = algoContext.withPreErrors(rollbackStack);
        algoContext = algoContext.withAbsolutePosition(lastAbsolutePosition);

        // Success.
        return Tuple.of(lastToken, onSuccess(lastToken._1, lastToken._2.get(), algoContext), lastTail);
    }
}
