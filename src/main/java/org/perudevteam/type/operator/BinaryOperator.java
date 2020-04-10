package org.perudevteam.type.operator;

import io.vavr.control.Try;
import org.perudevteam.type.Tagged;

public abstract class BinaryOperator<OT extends Enum<OT>, DT extends Enum<DT>, DC extends Tagged<DT>>
        extends Operator<OT, DT, DC> {
    public BinaryOperator(OT tag) {
        super(tag);
    }

    public abstract Try<DC> apply(DC i1, DC i2);
}
