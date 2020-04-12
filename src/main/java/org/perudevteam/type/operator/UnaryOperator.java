package org.perudevteam.type.operator;

import io.vavr.CheckedFunction1;
import io.vavr.Function1;
import io.vavr.control.Try;
import org.perudevteam.type.Tagged;

import java.util.Objects;

public abstract class UnaryOperator<OT extends Enum<OT>, DT extends Enum<DT>, DC extends Tagged<DT>>
        extends Operator<OT, DT, DC> {
    public static <OT extends Enum<OT>, DT extends Enum<DT>, DC extends Tagged<DT>> UnaryOperator<OT, DT, DC>
    unop(OT operatorTag, DT outputTag, CheckedFunction1<? super DC, ? extends DC> op) {
        return new UnaryOperator<OT, DT, DC>(operatorTag, outputTag) {
            @Override
            protected DC applyUnchecked(DC i) throws Throwable {
                return op.apply(i);
            }
        };
    }

    protected UnaryOperator(OT tag, DT oTag) {
        super(tag, oTag);
    }

    protected abstract DC applyUnchecked(DC i) throws Throwable;

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

    public UnaryOperator<OT, DT, DC> withTag(OT tag) {
        final UnaryOperator<OT, DT, DC> This = this;

        return new UnaryOperator<OT, DT, DC>(tag, getOutputTag()) {
            @Override
            protected DC applyUnchecked(DC i) throws Throwable {
                return This.applyUnchecked(i);
            }
        };
    }

    public UnaryOperator<OT, DT, DC> withOutputTag(DT oTag) {
        final UnaryOperator<OT, DT, DC> This = this;

        return new UnaryOperator<OT, DT, DC>(getTag(), oTag) {
            @Override
            protected DC applyUnchecked(DC i) throws Throwable {
                return This.applyUnchecked(i);
            }
        };
    }
}
