package org.perudevteam.parser.lrone;

import io.vavr.Function1;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import io.vavr.control.Either;
import org.perudevteam.dynamic.Dynamic;
import org.perudevteam.misc.Typed;
import org.perudevteam.parser.ASTParser;
import org.perudevteam.parser.grammar.AttrCFGrammar;
import org.perudevteam.parser.grammar.AttrProduction;

import static io.vavr.control.Either.*;

import java.util.Objects;

public class LROneASTParser<NT extends Enum<NT>, T extends Enum<T>, L, D extends Typed<T>>
        implements ASTParser<L, D> {

    private Map<NT, Integer> nonTerminalIndex;
    private Map<T, Integer> terminalIndex;

    public LROneASTParser(AttrCFGrammar<NT, T, ? extends AttrProduction<NT, T>, Tuple2<L, D>> grammar) {

    }

    @Override
    public Function1<Dynamic, Dynamic> buildASTUnsafe(Seq<Tuple2<L, D>> tokens) throws Throwable {
        return null;
    }
}
