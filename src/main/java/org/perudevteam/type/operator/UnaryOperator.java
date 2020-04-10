package org.perudevteam.type.operator;

import org.perudevteam.type.Tagged;

public abstract class UnaryOperator<OT extends Enum<OT>, DT extends Enum<DT>, DC extends Tagged<DT>>
        extends Operator<OT, DT, DC> {
    public UnaryOperator(OT tag) {
        super(tag);
    }

    public abstract DT apply(DT i);
}
