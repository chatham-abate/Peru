package org.perudevteam.type.operator;

import org.perudevteam.type.Tagged;

public abstract class BinaryOperator<OT extends Enum<OT>, DT extends Enum<DT>, DC extends Tagged<DT>>
        extends Operator<OT, DT, DC> {
    public BinaryOperator(OT tag) {
        super(tag);
    }

    public abstract DT apply(DT i1, DT i2);
}
