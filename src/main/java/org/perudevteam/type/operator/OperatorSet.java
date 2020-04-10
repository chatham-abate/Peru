package org.perudevteam.type.operator;

import io.vavr.collection.Map;
import io.vavr.control.Try;
import org.perudevteam.type.Tagged;

import java.util.Objects;

public class OperatorSet<OT extends Enum<OT>, DT extends Enum<DT>, DC extends Tagged<DT>> {
    private Map<OT, Map<DT, UnaryOperator<OT, DT, DC>>> unaries;
    private Map<OT, Map<DT, Map<DT, BinaryOperator<OT, DT, DC>>>> binaries;

    public OperatorSet(Map<OT, ? extends Map<DT, ? extends UnaryOperator<OT, DT, DC>>> uns,
                       Map<OT, ? extends Map<DT, ? extends Map<DT, BinaryOperator<OT, DT, DC>>>> bins) {
        this(Map.narrow(uns.mapValues(Map::narrow)), Map.narrow(bins.mapValues(Map::narrow)), true);
    }

    private OperatorSet(Map<OT, Map<DT, UnaryOperator<OT, DT, DC>>> uns,
                        Map<OT, Map<DT, Map<DT, BinaryOperator<OT, DT, DC>>>> bins, boolean check) {
        if (check) {
            Objects.requireNonNull(uns);
            uns.values().forEach(Objects::requireNonNull);

            Objects.requireNonNull(bins);
            bins.values().forEach(v -> {
                Objects.requireNonNull(v);
                v.values().forEach(Objects::requireNonNull);
            });
        }

        unaries = uns;
        binaries = bins;
    }

    public Try<DC> applyUnary(OT unOpTag, DC i) {
        if (!unaries.containsKey(unOpTag)) {
            return Try.failure(new Exception("Operator set does not contain unary operator : "
                    + unOpTag.name() + "."));
        }

        Map<DT, UnaryOperator<OT, DT, DC>> overloads = unaries.get(unOpTag).get();
        DT dataTag = i.getTag();

        if (!overloads.containsKey(dataTag)) {
            return Try.failure(new Exception("No overload for " + unOpTag.name() + " given " + dataTag.name() + "."));
        }

        return overloads.get(dataTag).get().apply(i);
    }

    public Try<DC> applyBinary(OT binOpTag, DC i1, DC i2) {
        if (!binaries.containsKey(binOpTag)) {
            return Try.failure(new Exception("Operator set does nto contain binary operator : "
                    + binOpTag.name() + "."));
        }

        Map<DT, Map<DT, BinaryOperator<OT, DT, DC>>> overloads1 = binaries.get(binOpTag).get();
        DT dataTag1 = i1.getTag();
        DT dataTag2 = i2.getTag();

        if (overloads1.containsKey(dataTag1)) {
            Map<DT, BinaryOperator<OT, DT, DC>> overloads2 = overloads1.get(dataTag1).get();
            if (overloads2.containsKey(dataTag2)) {
                return overloads2.get(dataTag2).get().apply(i1, i2);
            }
        }

        return Try.failure(new Exception("No overload for " + binOpTag.name() + " given "  + dataTag1.name()
                + " and " + dataTag2.name() + "."));
    }

    public OperatorSet<OT, DT, DC> withUnaryOperator(DT dataTag, UnaryOperator<OT, DT, DC> op) {
//        Objects.requireNonNull(dataTag);
//        Objects.requireNonNull(op);
//
//        OT operatorTag = op.getTag();
//
//        if (!unaries.containsKey(operatorTag))
//
//        return new OperatorSet<>()
        return null;
    }
}
