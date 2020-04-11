package org.perudevteam.type.operator;

import io.vavr.control.Try;
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

    private DT outputTag;

    public Operator(OT tag, DT oTag) {
        super(tag);
        outputTag = oTag;
    }

    public DT getOutputTag() {
        return outputTag;
    }

    protected void validateOutput(DC output) throws Exception {
        if (!output.getTag().equals(outputTag)) {
            throw new Exception("Bad Operator, unexpected return type.");
        }
    }

}
