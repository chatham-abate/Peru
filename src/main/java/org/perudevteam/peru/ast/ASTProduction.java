package org.perudevteam.peru.ast;

import io.vavr.CheckedFunction1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.perudevteam.parser.grammar.SemanticProduction;
import org.perudevteam.peru.base.BaseValue;

import java.util.Objects;

public abstract class ASTProduction<NT extends Enum<NT>, T extends Enum<T>>
        extends SemanticProduction<NT, T, AST> {

    // Try of (env', seq of results)
    public static Try<Tuple2<Map<String, BaseValue>, Seq<ASTResult>>>
    tryExtractOrderedResultsUnchecked(Map<String, BaseValue> env, Seq<AST> children) {
        Map<String, BaseValue> envPrime = env;
        Seq<ASTResult> results = List.empty();

        for (int i = 0; i < children.length(); i++) {
            Try<Tuple2<Map<String, BaseValue>, ASTResult>> resultTupleTry =
                    children.get(i).tryExecuteUnchecked(envPrime);

            if (resultTupleTry.isFailure()) {
                return Try.failure(resultTupleTry.getCause());
            }

            Tuple2<Map<String, BaseValue>, ASTResult> resultTuple = resultTupleTry.get();

            envPrime = resultTuple._1;
            results = results.append(resultTuple._2);
        }

        return Try.success(Tuple.of(envPrime, Array.ofAll(results)));
    }

    public static Try<Tuple2<Map<String, BaseValue>, Seq<ASTResult>>>
    tryExtractOrderedResult(Map<? extends String, ? extends BaseValue> env, Seq<? extends AST> children) {
        Objects.requireNonNull(env);
        env.values().forEach(Objects::requireNonNull);

        Objects.requireNonNull(children);
        children.forEach(Objects::requireNonNull);

        return tryExtractOrderedResultsUnchecked(Map.narrow(env), Seq.narrow(children));
    }

    public static <NT extends Enum<NT>, T extends Enum<T>> ASTProduction<NT, T>
    astProduction(NT s, Seq<? extends Either<NT, T>> r,
                  CheckedFunction1<? super Seq<AST>, ? extends AST> astBuilder) {
        return new ASTProduction<NT, T>(s, r) {
            @Override
            protected AST buildResultUnchecked(Seq<AST> children) throws Throwable {
                return astBuilder.apply(children);
            }
        };
    }

    private ASTProduction(NT s, Seq<? extends Either<NT, T>> r) {
        super(s, r);
    }
}
