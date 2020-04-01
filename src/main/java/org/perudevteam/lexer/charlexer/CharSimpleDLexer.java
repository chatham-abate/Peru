package org.perudevteam.lexer.charlexer;

import io.vavr.Function1;
import io.vavr.Tuple2;
import org.perudevteam.lexer.SimpleDLexer;
import org.perudevteam.misc.LineException;
import org.perudevteam.statemachine.DStateMachine;

public abstract class CharSimpleDLexer<CL> extends
        SimpleDLexer<Character, CL, String, CharData, CharSimpleContext> {

    public CharSimpleDLexer(DStateMachine<? super CL,
            ? extends Function1<? super CharSimpleContext, ? extends CharData>> d) {
        super("", d);
    }

    @Override
    protected CharSimpleContext readInput(Character input, CharSimpleContext context) {
        return input == '\n' ? context.withCurrentLine(context.getCurrentLine() + 1) : context;
    }

    @Override
    protected String combineInput(String lexeme, Character input) {
        return lexeme + input;
    }

    @Override
    protected CharSimpleContext onToken(Tuple2<String, CharData> token, CharSimpleContext context) {
        return context.withEndingLine(context.getCurrentLine());
    }

    @Override
    protected Throwable onError(String lexeme, CharSimpleContext context) {
        return new LineException(context.getStartingLine(), "Lexeme cannot be lexed : " + lexeme);
    }

    @Override
    protected CharSimpleContext onSuccess(Tuple2<String, CharData> token, CharSimpleContext context) {
        return context.withStartingLine(context.getEndingLine())
                .withCurrentLine(context.getEndingLine());
    }
}
