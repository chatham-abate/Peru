package org.perudevteam.type.operator;

import io.vavr.control.Try;
import org.perudevteam.type.Tagged;

import java.util.Objects;

public abstract class UnaryOperator<OT extends Enum<OT>, DT extends Enum<DT>, DC extends Tagged<DT>>
        extends Operator<OT, DT, DC> {
    public UnaryOperator(OT tag, DT oTag) {
        super(tag, oTag);
    }

    public abstract DC applyUnchecked(DC i) throws Throwable;

    public DC apply(DC i) throws Throwable {
        Objects.requireNonNull(i);
        DC result = applyUnchecked(i);
        validateOutput(result);
        return result;
    }

    public Try<DC> tryApply(DC i) {
        return Try.of(() -> apply(i));
    }

    public Try<DC> tryApply(Try<DC> tryI) {
        return tryI.mapTry(this::apply);
    }
}
