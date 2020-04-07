package org.perudevteam.lexer.charlexer;

import io.vavr.Function1;
import io.vavr.Tuple2;
import org.perudevteam.lexer.SimpleDLexer;
import org.perudevteam.misc.LineException;
import org.perudevteam.statemachine.DStateMachine;

public abstract class CharSimpleDLexer<CL, T extends Enum<T>> extends
        SimpleDLexer<Character, CL, String, CharData<T>, CharSimpleContext> {

    public CharSimpleDLexer(DStateMachine<? super CL,
            ? extends Function1<? super CharSimpleContext, ? extends CharData<T>>> d) {
        super("", d);
    }

    @Override
    protected CharSimpleContext readInput(Character input, CharSimpleContext context) {
        // New Line Character means current line increments and current line pos goes to one..
        // Otherwise line stays the same, line position increments.
        return input == '\n'
                ? context.map(l -> l.withCurrent(l.getCurrent() + 1), lp -> lp.withCurrent(1))
                : context.mapLinePosition(lp -> lp.withCurrent(lp.getCurrent() + 1));
    }

    @Override
    protected String combineInput(String lexeme, Character input) {
        return lexeme + input;
    }

    @Override
    protected CharSimpleContext onToken(Tuple2<String, CharData<T>> token, CharSimpleContext context) {
        // Here current line becomes ending line, and current position becomes ending position.
        return context.map(l -> l.withEnding(l.getCurrent()), lp -> lp.withEnding(lp.getCurrent()));
    }

    @Override
    protected Throwable onError(String lexeme, CharSimpleContext context) {
        return new LineException(context.getLine().getStarting(),
                context.getLinePosition().getStarting(), "Lexeme cannot be lexed : " + lexeme);
    }

    @Override
    protected CharSimpleContext onSuccess(Tuple2<String, CharData<T>> token, CharSimpleContext context) {
        // Here we restart the context to whatever comes directly after the successful token.
        return context.map(l -> l.withCurrent(l.getEnding()).withStarting(l.getEnding()),
                lp -> lp.withCurrent(lp.getEnding()).withStarting(lp.getEnding()));
    }
}
