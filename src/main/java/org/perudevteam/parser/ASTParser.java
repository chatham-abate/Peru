package org.perudevteam.parser;

import io.vavr.Function1;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.control.Try;
import org.perudevteam.dynamic.Dynamic;

import java.util.Objects;

@FunctionalInterface
public interface ASTParser<L, D> {
    Function1<Dynamic, Dynamic> buildASTUnsafe(Seq<Tuple2<L, D>> tokens) throws Throwable;

    default Try<Function1<Dynamic, Dynamic>> buildAST(Seq<Tuple2<L, D>> tokens) {
        return Try.of(() -> {
            Objects.requireNonNull(tokens);
            return buildASTUnsafe(tokens);
        });
    }
}
