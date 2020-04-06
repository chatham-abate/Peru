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
        // If we read a new line, the current line should be incremented, current line position should be set to 1.
        // Otherwise, just increment line position.
        return input == '\n'
                ? context.map(l -> l.withCurrent(l.getCurrent() + 1), lp -> lp.withCurrent(1))
                : context.mapLinePosition(lp -> lp.withCurrent(lp.getCurrent() + 1));
    }

    @Override
    protected String combineInput(String lexeme, Character input) {
        return lexeme + input;
    }

    @Override
    protected CharLinearContext onToken(Tuple2<String, CharData> token, CharLinearContext context) {
        // On token shift ending line and line position to current line and line position.
        return context.map(l -> l.withEnding(l.getCurrent()), lp -> lp.withEnding(lp.getCurrent()));
    }

    @Override
    protected Throwable onError(String lexeme, CharLinearContext context) {
        return new LineException(context.getLine().getStarting(),
                context.getLinePosition().getStarting(), "Lexeme cannot be lexed : " + lexeme);
    }

    @Override
    protected CharLinearContext onSuccess(Tuple2<String, CharData> token, CharLinearContext context) {
        // Shift current back to ending, and starting up to ending.

        return context.map(l -> l.withStarting(l.getEnding()).withCurrent(l.getEnding()),
                lp -> lp.withStarting(lp.getEnding()).withCurrent(lp.getEnding()));
    }
}
