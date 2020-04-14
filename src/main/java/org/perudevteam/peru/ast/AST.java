package org.perudevteam.peru.ast;

import io.vavr.Tuple2;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import org.perudevteam.misc.LineException;
import org.perudevteam.peru.base.BaseValue;

import java.util.Objects;

@FunctionalInterface
public interface AST  {
    Tuple2<Map<String, BaseValue>, BaseResult> executeUnchecked(Map<String, BaseValue> env) throws LineException;

    default Try<Tuple2<Map<String, BaseValue>, BaseResult>> tryExecuteUnchecked(Map<String, BaseValue> env) {
        return Try.of(() -> executeUnchecked(env));
    }

    default Tuple2<Map<String, BaseValue>, BaseResult> execute(Map<? extends String, ? extends BaseValue> env)
            throws LineException {
        Objects.requireNonNull(env);
        env.values().forEach(Objects::requireNonNull);

        return executeUnchecked(Map.narrow(env));
    }

    default Try<Tuple2<Map<String, BaseValue>, BaseResult>> tryExecute(Map<? extends String, ? extends BaseValue> env) {
        return Try.of(() -> execute(env));
    }


}
