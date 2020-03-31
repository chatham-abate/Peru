package org.perudevteam.lexer;

import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import org.perudevteam.statemachine.DStateMachine;

public abstract class SimpleDLexer<I, CL, L, D, C> extends DLexer<I, CL, L, D, C> {
    public SimpleDLexer(L initLex, DStateMachine<CL, Function1<C, D>> d) {
        super(initLex, d);
    }

    @Override
    public Tuple3<Tuple2<L, D>, C, Seq<I>> build(Seq<I> input, C context) throws Throwable {
        C algoContext = context;
        Seq<I> tail = input;

        // Initial State and Lexeme.
        L lexeme = getInitialLexeme();
        Option<Integer> stateOp = Option.of(0);

        Tuple2<L, D> lastToken = null;
        Seq<I> lastTail = null;

        DStateMachine<CL, Function1<C, D>> dsm = getDSM();

        while(!stateOp.isEmpty()) {
            int state = stateOp.get();
            Option<Function1<C, D>> dataBuilderOp = dsm.getOutput(state);

            // If we are on an accepting state.
            if (!dataBuilderOp.isEmpty()) {
                // Build data for token.
                D data = dataBuilderOp.get().apply(algoContext);

                lastToken = Tuple.of(lexeme, data);
                lastTail = tail;    // Save tail position.

                // Signal Context.
                algoContext = onToken(lastToken, algoContext);
            }

            // Now to read next input...
            if (tail.isEmpty()) {
                break; // Out of inputs to read.
            }

            I next = tail.head();

            algoContext = readInput(next, algoContext);
            lexeme = combineInput(lexeme, next);

            tail = tail.tail(); // Advance through input.

            // Calc next State.
            stateOp = dsm.getNextState(state, inputClass(next));
        }

        if (lastToken == null) {
            throw onError(lexeme, algoContext);
        }

        algoContext = onSuccess(lastToken, algoContext);

        return Tuple.of(lastToken, algoContext, lastTail);
    }
}
