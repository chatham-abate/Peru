package com.github.chathamabate.peru.ast;

import com.github.chathamabate.peru.base.BaseValue;
import io.vavr.Tuple2;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import com.github.chathamabate.misc.MiscHelpers;

@FunctionalInterface
public interface AST  {
    Try<Tuple2<Map<String, BaseValue>, ASTResult>> tryExecuteUnchecked(Map<String, BaseValue> env);

    default Try<Tuple2<Map<String, BaseValue>, ASTResult>> tryExecute(Map<? extends String, ? extends BaseValue> env) {
        MiscHelpers.requireNonNullMap(env);
        return tryExecuteUnchecked(Map.narrow(env));
    }
}
