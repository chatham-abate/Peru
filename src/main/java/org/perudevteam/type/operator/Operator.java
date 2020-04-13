package org.perudevteam.type.operator;

import io.vavr.control.Try;
import org.perudevteam.type.Tagged;

import java.util.Objects;

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

        Objects.requireNonNull(oTag);
        outputTag = oTag;
    }

    public DT getOutputTag() {
        return outputTag;
    }

    protected void validateOutput(DC output) throws Exception {
        // Output of an operator can never be null.
        Objects.requireNonNull(output);

        if (!output.getTag().equals(outputTag)) {
            throw new ClassCastException("Bad Output, expected " + outputTag.name() + " but found "
                    + output.getTag().name() + ".");
        }
    }

}
