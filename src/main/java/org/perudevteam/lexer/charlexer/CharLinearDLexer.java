package org.perudevteam.lexer.charlexer;

import io.vavr.Function1;
import org.perudevteam.charpos.EnumCharPos;
import org.perudevteam.fa.DFAutomaton;
import org.perudevteam.lexer.LinearDLexer;
import org.perudevteam.misc.LineException;

/**
 * Lexer for lexing characters into categorized strings. This lexer uses the optimised algorithm from
 * {@link org.perudevteam.lexer.LinearDLexer}.
 *
 * @param <T> The <b>Enum</b> category type.
 */
public class CharLinearDLexer<T extends Enum<T>>
        extends LinearDLexer<Character, String, EnumCharPos<T>, CharLinearContext> {

    /**
     * Build a linear character lexer with a max rollback amount.
     *
     * @param mra The max rollback amount.
     * @param d The automaton of the lexer.
     */
    protected CharLinearDLexer(int mra, DFAutomaton<? super Character, ?,
            ? extends Function1<? super CharLinearContext, ? extends EnumCharPos<T>>> d) {
        super(mra, "", d);
    }

    /**
     * Build a linear character lexer with the preset max rollback amount.
     *
     * @param d The automaton of the lexer.
     */
    protected CharLinearDLexer(DFAutomaton<? super Character, ?,
            ? extends Function1<? super CharLinearContext, ? extends EnumCharPos<T>>> d) {
        super("", d);
    }

    @Override
    protected CharLinearContext readInput(Character input, CharLinearContext context) {
        // If we read a new line, the current line should be incremented, current line position should be set to 1.
        // Otherwise, just increment line position.
        return input == '\n'
                ? context.map(l -> l.withCurrent(l.getCurrent() + 1), lp -> lp.withCurrent(0))
                : context.mapLinePositionData(lp -> lp.withCurrent(lp.getCurrent() + 1));
    }

    @Override
    protected String combineInput(String lexeme, Character input) {
        return lexeme + input;
    }

    @Override
    protected CharLinearContext onToken(String lexeme, EnumCharPos<T> data, CharLinearContext context) {
        // On token shift ending line and line position to current line and line position.
        return context.map(l -> l.withEnding(l.getCurrent()), lp -> lp.withEnding(lp.getCurrent()));
    }

    @Override
    protected Throwable makeError(String lexeme, CharLinearContext context) {
        return LineException.lineEx(context.getLineData().getStarting(),
                context.getLinePositionData().getStarting(), "Lexeme cannot be lexed." + lexeme);
    }

    @Override
    protected CharLinearContext onError(String lexeme, CharLinearContext context) {
        return context.map(l -> l.withStarting(l.getCurrent()).withEnding(l.getCurrent()),
                lp -> lp.withStarting(lp.getCurrent()).withEnding(lp.getCurrent()));
    }

    @Override
    protected CharLinearContext onSuccess(String lexeme, EnumCharPos<T> data, CharLinearContext context) {
        // Shift current back to ending, and starting up to ending.

        return context.map(l -> l.withStarting(l.getEnding()).withCurrent(l.getEnding()),
                lp -> lp.withStarting(lp.getEnding()).withCurrent(lp.getEnding()));
    }
}
