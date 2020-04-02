package org.perudevteam.lexer;

import io.vavr.Function1;
import io.vavr.Tuple2;
import org.perudevteam.misc.Builder;
import org.perudevteam.statemachine.DStateMachine;

import java.util.Objects;

/**
 * @param <I> Input Type.
 * @param <CL> Input Class Type.
 * @param <L> Lexeme Type.
 * @param <D> Data Type.
 * @param <C> Context Type.
 *
 * This Lexer will construct tokens as Tuple2s. (Lexeme, Data).
 *
 * As a deterministic lexer, this will traverse some deterministic state machine
 * to find the largest valid Lexeme.
 */
public abstract class DLexer<I, CL, L, D, C> implements Builder<I, C, Tuple2<L, D>> {
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

    protected L getInitialLexeme() {
        return initialLexeme;
    }

    protected DStateMachine<CL, Function1<C, D>> getDSM() {
        return dsm;
    }

    protected abstract C readInput(I input, C context);

    protected abstract L combineInput(L lexeme, I input);

    protected abstract CL inputClass(I input);

    protected abstract C onToken(Tuple2<L, D> token, C context);

    protected abstract Throwable onError(L lexeme, C context);

    protected abstract C onSuccess(Tuple2<L, D> token, C context);
}
