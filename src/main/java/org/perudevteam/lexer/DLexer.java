package org.perudevteam.lexer;

import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vavr.control.Try;
import org.perudevteam.fa.DFA;
import org.perudevteam.misc.Builder;

import java.util.Objects;

/**
 * <h1>Deterministic Lexer</h1>
 * This Lexer will read in some sequence of inputs and build a token.
 * A token is a <b>Tuple2</b> containing a lexeme of type <b>L</b> and a <b>Try</b> of
 * type <b>D</b>.
 * We assume given a sequence of inputs, a lexeme can always be created.
 * While processing that lexeme may throw an error.
 * <br>
 * Consider the example of reading in <b>Characters</b>.
 * The lexeme constructed will be a <B>String</B> containing the characters read.
 * The data will be some category of the string (Ex, <i>Integer</i> or <i>ID</i>)...
 * If the string cannot be categorized the data will be a <b>Try</b> failure.
 * <br>
 * Lastly, this Lexer is completely functional. So, since it holds no state,
 * it must be provided a context of type <b>C</b> containing necessary information for
 * creating the data of a lexeme.
 * <br>
 * A context can be anything and hold anything. A useful context may hold the current
 * line number of how many tokens have been lexed so far.
 *
 * @param <I> Input Type.
 * @param <CL> Input Class Type.
 * @param <L> Lexeme Type.
 * @param <D> Data Type.
 * @param <C> Context Type.
 */
public abstract class DLexer<I, L, D, C> implements Builder<I, C, Tuple2<L, Try<D>>> {
    private final L initialLexeme;
    private final DFA<I, ?, Function1<C, D>> dfa;

    /**
     * DLexer Constructor. This requires an initial lexeme as well as a deterministic state
     * machine.
     *
     * @param initLex The initial Lexeme to start every lex with.
     * @param d The <b>DStateMachine</b> to use while lexing.
     */
    @SuppressWarnings("unchecked")
    public DLexer(L initLex,
                  DFA<? super I, ?, ? extends Function1<? super C, ? extends D>> d) {
        Objects.requireNonNull(initLex);
        Objects.requireNonNull(d);

        initialLexeme = initLex;
        dfa = (DFA<I, ?, Function1<C, D>>) d;
    }

    /**
     * Given a sequence of inputs and a context, this function attempts to lex the entire input sequence.
     * This function assumes the given input sequence is valid. Any errors lexing the input will be thrown.
     * @param input The input sequence.
     * @param context The context.
     * @return A <b>Stream</b> containing each token lexed from the input. Given the input sequence is valid,
     * each token <b>Tuple2</b> will contain a successful <b>Try</b>. For convenience, this function strips each token
     * of its <b>Try</b>. Thus, each token returned will be a <b>Tuple2</b> containing a lexeme of type <b>L</b>
     * and a data of type <b>D</b>.
     */
    public Stream<Tuple2<L, D>> buildSuccessfulTokenStream(Seq<? extends I> input, C context) {
        return buildStream(input, context).map(tuple -> tuple.map2(Try::get));
    }

    /**
     * Identical to {@link #buildSuccessfulTokenStream(Seq, Object)}  buildSuccessfulTokenStream} except with
     * error filter. All bad tokens are ignored. All successful tokens are returned.
     * @param input The input sequence.
     * @param context The context.
     * @return The <b>Stream</b> of tokens lexed.
     */
    public Stream<Tuple2<L, D>> buildOnlySuccessfulTokenStream(Seq<? extends I> input, C context) {
        return buildStream(input, context).filter(tuple -> tuple._2.isSuccess())
                .map(tuple -> tuple.map2(Try::get));
    }

    /**
     * Get the initial lexeme.
     * @return The initial lexeme of type <b>L</b>.
     */
    protected L getInitialLexeme() {
        return initialLexeme;
    }

    protected DFA<I, ?, Function1<C, D>> getDFA() {
        return dfa;
    }

    /**
     * Create a new context given some input was read.
     * @param input The input of type <b>I</b>.
     * @param context The current context.
     * @return The new context.
     */
    protected abstract C readInput(I input, C context);

    /**
     * Create a new lexeme from some initial lexeme and input.
     * @param lexeme The initial lexeme.
     * @param input The input.
     * @return The new lexeme of type <b>L</b>.
     */
    protected abstract L combineInput(L lexeme, I input);

    protected abstract C onToken(L lexeme, D data, C context);

    protected abstract Throwable makeError(L lexeme, C context);

    protected abstract C onError(L lexeme, C context);

    protected abstract C onSuccess(L lexeme, D data, C context);
}
