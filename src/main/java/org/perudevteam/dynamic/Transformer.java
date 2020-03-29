package org.perudevteam.dynamic;

import io.vavr.Function2;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import io.vavr.control.Try;

@FunctionalInterface
public interface Transformer<I, O> {
    Tuple2<O, Dynamic> transform(I inputEle, Dynamic context) throws Throwable;

    default Try<Tuple2<O, Dynamic>> tryTransform(I inputEle, Dynamic context) {
        return Try.of(() -> transform(inputEle, context));
    }

    default Option<Tuple2<O, Dynamic>> optionTransform(I inputEle, Dynamic context) {
        return tryTransform(inputEle, context).toOption();
    }

    default <D> Transformer<I, D> andThen(Transformer<O, D> t) {
        final Transformer<I, O> thisTransform = this;

        return ((inputEle, context) -> {
            Tuple2<O, Dynamic> firstOutput = thisTransform.transform(inputEle, context);
            return t.transform(firstOutput._1, firstOutput._2);
        });
    }
}
