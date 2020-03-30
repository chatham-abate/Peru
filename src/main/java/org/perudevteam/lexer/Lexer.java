package org.perudevteam.lexer;

import io.vavr.*;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.perudevteam.dynamic.Builder;
import org.perudevteam.dynamic.Dynamic;
import static org.perudevteam.dynamic.Dynamic.*;

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
     *            NOTE, the DFA's accepting states are functions...
     *            in the form (lexeme, context) -> token.
     *            This will generate a token from the current lexeme and context given
     *            we have hit a certain accepting state.
     *
     * @param reader This is a sort of post processor, the DFA will take the top
     *                element from the input sequence and try to calculate the next state.
     *                If the state is valid, the algorithm continues. The input element will be
     *                passed to this reader to make any necessary changes to the context.
     *                Should be in the form (inputElement, context) -> context'.
     *                (Think reading new line characters).
     *
     *               NOTE, The reader additionally has the ability to signal the algorithm
     *               when some pre error is reached. Thus, we can stop searching the input
     *               without needing to wait for an error state.
     *               This is why the reader is a checked function.
     *
     * @param combiner After an input is counted, it will then be combined with the current lexeme.
     *                 This function should have nothing to do with the context.
     *                 Should be in the form (input, lexeme) -> lexeme'.
     *
     * @param initialLex The starting lexeme to use.
     *
     * @param onToken Function to calculate new context upon reading a token.
     *                Should be in the form...
     *                (token, context) -> context'
     *                NOTE, this is called every time a token is found...
     *                Not just the final token returned... that is what finisher is for.
     *
     * @param onError This is function will called in the situation of an error.
     *                An error is only thrown when no context is found.
     *              Should be in the form (lexeme, context) -> detailedError.
     *
     * @param finisher This function is called to finalize the context after finishing lexing.
     *                 Should be in the form (finalToken, context) -> context'.
     */
    public static <I> Builder<I, Dynamic> tableLexer(
            StateMachine<? super I, ? extends Function2<? super Dynamic, ? super Dynamic, ? extends Dynamic>> dfa,
            CheckedFunction2<? super I, ? super Dynamic, ? extends Dynamic> reader,
            Function2<? super I, ? super Dynamic, ? extends Dynamic> combiner,
            Dynamic initialLex,
            Function2<? super Dynamic, ? super Dynamic, ? extends Dynamic> onToken,
            Function2<? super Dynamic, ? super Dynamic, ? extends Throwable> onError,
            Function2<? super Dynamic, ? super Dynamic, ? extends Dynamic> finisher) {

        return (input, context) -> {
            // This will save the input state whenever a token is found.
            Seq<I> lastCutOff = input;
            Dynamic lastContext = context;

            Dynamic lastToken = null; // This will be plain null until a real token is found.

            Seq<I> symbolsLeft = input;
            Dynamic algoContext = context;

            Option<Integer> stateOp = Option.of(0);
            Dynamic lexeme = initialLex;

            // While we are not on error.
            while (!stateOp.isEmpty()) {
                int state = stateOp.get();

                Option<? extends Function2<? super Dynamic, ? super Dynamic, ? extends Dynamic>> output =
                        dfa.getOutcome(state);
                if (!output.isEmpty()) {
                    Function2<? super Dynamic, ? super Dynamic, ? extends Dynamic> tokenGenerator = output.get();

                    lastToken = tokenGenerator.apply(lexeme, algoContext);
                    lastCutOff = symbolsLeft;
                    algoContext = onToken.apply(lastToken, algoContext);
                    lastContext = algoContext;
                }

                // We know we are on a valid state, it was either just determined to be
                // an accepting state, or just some normal state.
                // Now, we continue to the next state.

                Option<I> nextSymbolOpt = symbolsLeft.headOption();
                if (nextSymbolOpt.isEmpty()) {
                    break; // If there are no more symbols to read, we exit.
                }

                I nextSymbol = nextSymbolOpt.get();
                symbolsLeft = symbolsLeft.tail();

                final Dynamic c = algoContext;
                Try<Dynamic> nextContextTry = Try.of(() -> reader.apply(nextSymbol, c));
                if (nextContextTry.isFailure()) {
                    break; // If there is a pre error detected by the reader, exit the loop.
                }

                algoContext = nextContextTry.get(); // Otherwise set the new context.
                lexeme = combiner.apply(nextSymbol, lexeme);
                stateOp = dfa.nextState(state, nextSymbol);
            }

            if (lastToken == null) {
                throw onError.apply(lexeme, algoContext);
            }

            return Tuple.of(lastToken, finisher.apply(lastToken, lastContext), lastCutOff);
        };
    }

    /*
     * Below lie presets for different types of lexers.
     *
     * First is the Classic String Lexer...
     * This lexer reads in characters and combines them into Strings.
     * The lexer has the following context format.
     *
     * Context is a DynaMap containing two entries.
     * CurrentLine : DynaInt
     * LineSinceLastToken : DynaInt
     *
     * These entries help the lexer position the tokens it lexes with line numbers.
     * CurrentLine will be the current line of the context.
     * LineSinceLastToken will be the line number the last lexed token starts on.
     */

    public static final CheckedFunction2<Character, Dynamic, Dynamic> CLASSIC_READER = (input, context) -> {
        if (input == '\n') {
            int linesRead = context.asMap().get("CurrentLine").get().asInt();
            return ofMap(context.asMap().put("CurrentLine", ofInt(linesRead + 1)));
        }

        return context;
    };

    public static final Function2<Character, Dynamic, Dynamic> CLASSIC_COMBINER =
            (input, lexeme) -> ofString(lexeme.asString() + input);

    public static final Dynamic CLASSIC_INIT_LEXEME = ofString("");

    public static final Function2<Dynamic, Dynamic, Dynamic> CLASSIC_ON_TOKEN = (token, context) -> context;

    public static final Function2<Dynamic, Dynamic, Throwable> CLASSIC_ON_ERROR = (lexeme, context) -> {
        int lineSinceLast = context.asMap().get("LineSinceLastToken").get().asInt();
        String lex = lexeme.asString();

        return new IllegalArgumentException("[" + lineSinceLast + "] Invalid Lexeme : " + lex);
    };

    public static final Function2<Dynamic, Dynamic, Dynamic> CLASSIC_FINISHER = (token, context) -> {
        Dynamic currentLine = context.asMap().get("CurrentLine").get();
        return ofMap(context.asMap().put("LineSinceLastToken", currentLine));
    };

    public static Builder<Character, Dynamic> classicTableLexer(
            StateMachine<? super Character,
                    ? extends Function2<? super Dynamic, ? super Dynamic, ? extends Dynamic>> dfa) {
        return tableLexer(dfa, CLASSIC_READER, CLASSIC_COMBINER, CLASSIC_INIT_LEXEME, CLASSIC_ON_TOKEN,
                CLASSIC_ON_ERROR, CLASSIC_FINISHER);
    }
}
