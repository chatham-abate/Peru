package org.perudevteam.peru.ast;

import io.vavr.Tuple2;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import org.perudevteam.misc.LineException;
import org.perudevteam.peru.base.BaseValue;

import java.util.Objects;

@FunctionalInterface
public interface AST  {
    Try<Tuple2<Map<String, BaseValue>, ASTResult>> tryExecuteUnchecked(Map<String, BaseValue> env);

    default Try<Tuple2<Map<String, BaseValue>, ASTResult>> tryExecute(Map<? extends String, ? extends BaseValue> env) {
        Objects.requireNonNull(env);
        env.values().forEach(Objects::requireNonNull);

        return tryExecuteUnchecked(Map.narrow(env));
    }
}
