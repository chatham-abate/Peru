package org.perudevteam.lexer;

import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.Function3;
import org.perudevteam.dynamic.Builder;
import org.perudevteam.dynamic.Dynamic;

/**
 * Lexer, while not an actual type is a function which reads in
 * input one at a time and attempts to string create a token.
 * Token's here will be Dynamic, so the structure what is lexed is
 * completely user defined.
 */
public class Lexer {

    /**
     * Build a table driven lexer.
     *
     * @param dfa The DFA table the algorithm will run on.
     * @param counter This is a sort of post processor, the DFA will take the top
     *                element from the input sequence and try to calculate the next state.
     *                If the state is valid, the algorithm continues. The input element will be
     *                passed to this counter to make any necessary changes to the context.
     *                Should be in the form (inputElement, context) -> context'.
     *                (Think reading new line characters).
     * @param combiner After an input is counted, it will then be combined with the current lexeme.
     *                 This function should have nothing to do with the context.
     *                 Should be in the form (input, lexeme) -> lexeme'.
     * @param initialLex The starting lexeme to use.
     * @param error This is function will called in the situation of an error.
     *              Should be in the form (msg, lexeme, context) -> detailedError.
     */
    public static <I, O> Builder<I, Dynamic> tableLexer(StateMachine<I, O> dfa,
                                                        Function2<I, Dynamic, Dynamic> counter,
                                                        Function2<I, Dynamic, Dynamic> combiner,
                                                        Dynamic initialLex,
                                                        Function3<String, Dynamic, Dynamic, Throwable> error) {
        return (input, context) -> {
            return null;
        };
    }

}
