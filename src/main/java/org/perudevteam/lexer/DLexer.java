package org.perudevteam.lexer;

import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vavr.control.Try;
import org.perudevteam.misc.Builder;
import org.perudevteam.statemachine.DStateMachine;

import java.util.Objects;

/**
 * This Lexer will construct tokens as Tuple2s. (Lexeme, Try of Data)
 * NOTE, given a a sequence of inputs, a lexeme should always be able to be created.
 * However the data may not be able to be created.
 * Think reading characters to form a lexeme of type String... this should never cause an error.
 * However the type of this lexeme may be undefined by this Lexer... this should return a failure.
 *
 * As a deterministic lexer, this will traverse some deterministic state machine
 * to find the largest valid Lexeme.
 *
 * @param <I> Input Type.
 * @param <CL> Input Class Type.
 * @param <L> Lexeme Type.
 * @param <D> Data Type.
 * @param <C> Context Type.
 */
public abstract class DLexer<I, CL, L, D, C> implements Builder<I, C, Tuple2<L, Try<D>>> {
    private L initialLexeme;

    // (context) -> (data).
    private DStateMachine<CL, Function1<C, D>> dsm;

    @SuppressWarnings("unchecked")
    public DLexer(L initLex,
                  DStateMachine<? super CL, ? extends Function1<? super C, ? extends D>> d) {
        Objects.requireNonNull(initLex);
        Objects.requireNonNull(d);

        initialLexeme = initLex;
        dsm = (DStateMachine<CL, Function1<C, D>>) d;
    }

    public Stream<Tuple2<L, D>> buildSuccessfulTokenStream(Seq<I> input, C context) {
        return buildStream(input, context).map(tuple -> tuple.map2(Try::get));
    }

    public Stream<Tuple2<L, D>> buildOnlySuccessfulTokenStream(Seq<I> input, C context) {
        return buildStream(input, context).filter(tuple -> tuple._2.isSuccess())
                .map(tuple -> tuple.map2(Try::get));
    }

    protected L getInitialLexeme() {
        return initialLexeme;
    }

    protected DStateMachine<CL, Function1<C, D>> getDSM() {
        return dsm;
    }

    protected abstract C readInput(I input, C context);

    protected abstract L combineInput(L lexeme, I input);

    protected abstract CL inputClass(I input);

    protected abstract C onToken(L lexeme, D data, C context);

    protected abstract Throwable makeError(L lexeme, C context);

    protected abstract C onError(L lexeme, C context);

    protected abstract C onSuccess(L lexeme, D data, C context);
}
