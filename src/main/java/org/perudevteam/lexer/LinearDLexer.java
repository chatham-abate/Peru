package org.perudevteam.lexer;

import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import org.perudevteam.statemachine.DStateMachine;

public abstract class LinearDLexer<I, CL, L, D, C extends LinearContext<C>>
        extends DLexer<I, CL, L, D, C> {

    public LinearDLexer(L initLex, DStateMachine<CL, Function1<C, D>> d) {
        super(initLex, d);
    }

    @Override
    public Tuple3<Tuple2<L, D>, C, Seq<I>> build(Seq<I> input, C context) throws Throwable {
        // Rollback stack in form (position, state).
        // Starting at the given position, and state 0.
        Seq<Tuple2<Integer, Integer>> rollbackStack = List.empty();

        Seq<I> tail = input;
        C algoContext = context;

        L lexeme = getInitialLexeme();
        Option<Integer> stateOp = Option.of(0);

        Tuple2<L, D> lastToken = null;
        Seq<I> lastTail = null;
        int lastAbsolutePosition = algoContext.getAbsolutePosition();

        DStateMachine<CL, Function1<C, D>> dsm = getDSM();

        // While not on an error state or pre error state and position, continue.
        int state;
        while (!stateOp.isEmpty() &&
                !algoContext.isPreError(algoContext.getAbsolutePosition(), state = stateOp.get())) {
            Option<Function1<C, D>> dataBuilderOp = dsm.getOutput(state);

            if (!dataBuilderOp.isEmpty()) {
                Function1<C, D> dataBuilder = dataBuilderOp.get();
                lastToken = Tuple.of(lexeme, dataBuilder.apply(algoContext));
                lastTail = tail;
                lastAbsolutePosition = algoContext.getAbsolutePosition();

                algoContext = onToken(lastToken, algoContext);

                // Finally, clear rollback stack.
                rollbackStack = List.empty();
            } else {
                rollbackStack = rollbackStack.prepend(Tuple.of(
                        algoContext.getAbsolutePosition(), state
                ));
            }

            // Now we advance to the next symbol...
            if (tail.isEmpty()) break;

            I symbol = tail.head();
            // Increment absolute position.
            algoContext = algoContext.withAbsolutePosition(algoContext.getAbsolutePosition() + 1);

            algoContext = readInput(symbol, algoContext);
            lexeme = combineInput(lexeme, symbol);

            tail = tail.tail();

            stateOp = dsm.getNextState(state, inputClass(symbol));
        }


        if (lastToken == null) {
            throw onError(lexeme, algoContext);
        }

        algoContext = algoContext.dropPreErrorsBefore(lastAbsolutePosition);

        // unroll rollback stack.
        while (!rollbackStack.isEmpty()) {
            Tuple2<Integer, Integer> error = rollbackStack.head();

            algoContext = algoContext.withPreError(error._1, error._2);

            rollbackStack = rollbackStack.tail();
        }

        // Success.
        return Tuple.of(lastToken, onSuccess(lastToken, algoContext), lastTail);
    }
}
