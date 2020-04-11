package org.perudevteam.type.operator;

import io.vavr.control.Try;
import org.perudevteam.type.Tagged;

import java.util.Objects;

public abstract class BinaryOperator<OT extends Enum<OT>, DT extends Enum<DT>, DC extends Tagged<DT>>
        extends Operator<OT, DT, DC> {
    public BinaryOperator(OT tag, DT oTag) {
        super(tag, oTag);
    }

    protected abstract DC applyUnchecked(DC i1, DC i2) throws Throwable;

    public DC apply(DC i1, DC i2) throws Throwable {
        // Check inputs are given.
        Objects.requireNonNull(i1);
        Objects.requireNonNull(i2);

        // Calculate result.
        DC result = applyUnchecked(i1, i2);

        // Check result is of correct type.
        validateOutput(result);
        return result;
    }

    public Try<DC> tryApply(DC i1, DC i2) {
        return Try.of(() -> apply(i1, i2));
    }

    public Try<DC> tryApply(Try<DC> tryI1, Try<DC> tryI2) {
        return tryI1.flatMap(i1 -> tryI2.mapTry(i2 -> apply(i1, i2)));
    }
}
