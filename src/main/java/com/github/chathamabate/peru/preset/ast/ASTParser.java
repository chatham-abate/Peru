package com.github.chathamabate.peru.preset.ast;

import com.github.chathamabate.peru.charpos.EnumCharPos;
import com.github.chathamabate.peru.parser.lrone.LROneParser;
import io.vavr.Function0;
import io.vavr.Function1;
import io.vavr.Tuple2;

public abstract class ASTParser<NT extends Enum<NT>, T extends Enum<T>>
        extends LROneParser<NT, T, String, EnumCharPos<T>, AST> {

    public static <NT extends Enum<NT>, T extends Enum<T>> ASTParser<NT, T> astParser(
            ASTCFGrammar<NT, T> g,
            Function1<? super Tuple2<String, EnumCharPos<T>>, ? extends Throwable> getMidError,
            Function0<? extends Throwable> getEofError
    ) {
        return new ASTParser<NT, T>(g) {
            @Override
            protected Throwable onError(Tuple2<String, EnumCharPos<T>> lookAhead) {
                return getMidError.apply(lookAhead);
            }

            @Override
            protected Throwable onError() {
                return getEofError.apply();
            }
        };
    }

    private ASTParser(ASTCFGrammar<NT, T> g) {
        super(g);
    }
}
