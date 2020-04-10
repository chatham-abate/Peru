package org.perudevteam.type.operator;

import org.perudevteam.type.Tagged;

/**
 * Abstract Operator.
 *
 * @param <OT> Operator Tag Type.
 * @param <DT> Data Tag Type.
 * @param <DC> Data Class Type.
 */
public abstract class Operator<OT extends Enum<OT>, DT extends Enum<DT>, DC extends Tagged<DT>>
        extends Tagged<OT>  {
    public Operator(OT tag) {
        super(tag);
    }
}
