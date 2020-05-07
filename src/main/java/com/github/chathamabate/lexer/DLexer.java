package com.github.chathamabate.lexer;

import io.vavr.Function1;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vavr.control.Try;
import com.github.chathamabate.fa.DFAutomaton;
import com.github.chathamabate.misc.Builder;

import java.util.Objects;

/**
 * This Lexer will read in some sequence of inputs and build a token.
 * A token is a <b>Tuple2</b> containing a lexeme of type <b>L</b> and a <b>Try</b> of
 * type <b>D</b>.
 * We assume given a sequence of inputs, a lexeme can always be created.
 * While processing that lexeme may throw an error.
 * <br>
 * Consider the example of reading in <b>Characters</b>.
 * The lexeme constructed will be a <b>String</b> containing the characters read.
 * The data will be some category of the string (Ex, <i>INTEGER</i> or <i>ID</i>)...
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
 * @param <L> Lexeme Type.
 * @param <D> Data Type.
 * @param <C> Context Type.
 */
public abstract class DLexer<I, L, D, C> implements Builder<I, C, Tuple2<L, Try<D>>> {

    /**
     * The initial lexeme to start every token's lexeme.
     * (Usually just an empty string)
     */
    private final L initialLexeme;

    /**
     * The deterministic finite automaton to lex with. The accepting states of this dfa
     * will mark when a lexeme can be successfully turned into a token. This is done using a function
     * which takes the lexer's current context and returns the data to be used in the newly found token.
     */
    private final DFAutomaton<I, ?, Function1<C, D>> dfa;

    /**
     * DLexer Constructor. This requires an initial lexeme as well as a deterministic state
     * machine.
     *
     * @param initLex The initial Lexeme to start every lex with.
     * @param d The <b>DStateMachine</b> to use while lexing.
     */
    @SuppressWarnings("unchecked")
    public DLexer(L initLex,
                  DFAutomaton<? super I, ?, ? extends Function1<? super C, ? extends D>> d) {
        Objects.requireNonNull(initLex);
        Objects.requireNonNull(d);

        initialLexeme = initLex;
        dfa = (DFAutomaton<I, ?, Function1<C, D>>) d;
    }

    /**
     * Given a sequence of inputs and a context, this function attempts to lex the entire input sequence.
     * This function assumes the given input sequence is valid. Any errors lexing the input will be thrown.
     *
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
     *
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
     *
     * @return The initial lexeme of type <b>L</b>.
     */
    protected L getInitialLexeme() {
        return initialLexeme;
    }

    /**
     * Get this lexer's underlying deterministic finite automaton.
     *
     * @return The <b>DFAutomaton</b> used by this lexer.
     */
    protected DFAutomaton<I, ?, Function1<C, D>> getDFA() {
        return dfa;
    }

    /**
     * Create a new context given some input was read.
     *
     * @param input The input of type <b>I</b>.
     * @param context The current context.
     * @return The new context.
     */
    protected abstract C readInput(I input, C context);

    /**
     * Create a new lexeme from some initial lexeme and input.
     *
     * @param lexeme The initial lexeme.
     * @param input The input.
     * @return The new lexeme of type <b>L</b>.
     */
    protected abstract L combineInput(L lexeme, I input);

    /**
     * This function is called when the lexer finds a successful token while lexing.
     * When this happens, the current context may need to be changed. This function will take in
     * the lexeme and data of the newly found token and the current context. It will return a new
     * context which has acknowledged a successful token has been found.
     *
     * @param lexeme The lexeme of the newly found token.
     * @param data The data of the newly found token.
     * @param context The current context.
     * @return The new context.
     */
    protected abstract C onToken(L lexeme, D data, C context);

    /**
     * When a given lexeme cannot be turned into a successful token, an error must be returned
     * in a <b>Try</b>. This function is used to build that error. After attempting to lex the lexeme and failing,
     * this function will be called with the invalid lexeme and the current context.
     *
     * @param lexeme The invalid lexeme.
     * @param context The context of the lexer after trying to lex the invalid lexeme.
     * @return The error associated with the invalid lexeme.
     */
    protected abstract Throwable makeError(L lexeme, C context);

    /**
     * When a lexeme cannot be lexed, the context may need to be changed.
     * This function will take in the invalid lexeme and the current context.
     * It will return some new context which has acknowledged a failure while lexing.
     *
     * @param lexeme The invalid lexeme.
     * @param context The current context.
     * @return The new lexeme.
     */
    protected abstract C onError(L lexeme, C context);

    /**
     * When a lexeme can be turned into a successful token, the context may need to be
     * changed accordingly. This function will take in the lexeme and data of the successful token and
     * the current context. It will return some new context which has acknowledged the successful token.
     *
     * @param lexeme The valid lexeme.
     * @param data The lexemes associated data.
     * @param context The current context.
     * @return The new context.
     */
    protected abstract C onSuccess(L lexeme, D data, C context);
}
