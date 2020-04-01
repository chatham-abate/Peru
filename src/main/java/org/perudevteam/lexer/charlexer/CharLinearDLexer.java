package org.perudevteam.lexer.charlexer;

import io.vavr.Function1;
import io.vavr.Tuple2;
import org.perudevteam.lexer.LinearDLexer;
import org.perudevteam.misc.LineException;
import org.perudevteam.statemachine.DStateMachine;

public abstract class CharLinearDLexer<CL> extends LinearDLexer<Character, CL, String, CharData, CharLinearContext> {
    public CharLinearDLexer(int mra,
            DStateMachine<? super CL, ? extends Function1<? super CharLinearContext, ? extends CharData>> d) {
        super(mra, "", d);
    }

    public CharLinearDLexer(
            DStateMachine<? super CL, ? extends Function1<? super CharLinearContext, ? extends CharData>> d) {
        super("", d);
    }

    @Override
    protected CharLinearContext readInput(Character input, CharLinearContext context) {
        // Increment Line number if necessary.
        return input == '\n'
                ? context.withCurrentLine(context.getCurrentLine() + 1)
                : context;
    }

    @Override
    protected String combineInput(String lexeme, Character input) {
        return lexeme + input;
    }

    @Override
    protected CharLinearContext onToken(Tuple2<String, CharData> token, CharLinearContext context) {
        return context.withEndingLine(context.getCurrentLine());
    }

    @Override
    protected Throwable onError(String lexeme, CharLinearContext context) {
        return new LineException(context.getStartingLine(), "Lexeme cannot be lexed : " + lexeme);
    }

    @Override
    protected CharLinearContext onSuccess(Tuple2<String, CharData> token, CharLinearContext context) {
        return context.withStartingLine(context.getEndingLine())
                .withCurrentLine(context.getEndingLine());
    }
}
