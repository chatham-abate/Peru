package org.perudevteam.type.base;

import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import org.perudevteam.type.Tagged;

public abstract class BaseValue extends Tagged<BaseType> {
    public static NullPointerException castError(BaseType from, BaseType to) {
        return new NullPointerException("Base Type " + from.name() + " cannot be converted to " + to.name() + ".");
    }

    public BaseValue(BaseType tag) {
        super(tag);
    }

    public int castToInt() {
        throw castError(getTag(), BaseType.INT);
    }

    public long castToLong() {
        throw castError(getTag(), BaseType.LONG);
    }

    public float castToFloat() {
        throw castError(getTag(), BaseType.FLOAT);
    }

    public double castToDouble() {
        throw castError(getTag(), BaseType.DOUBLE);
    }

    public Enum castToEnum() {
        throw castError(getTag(), BaseType.ENUM);
    }

    public boolean castToBoolean() {
        throw castError(getTag(), BaseType.BOOLEAN);
    }

    public Character castToCharacter() {
        throw castError(getTag(), BaseType.CHARACTER);
    }

    public String castToString() {
        throw castError(getTag(), BaseType.STRING);
    }

    public Map<String, BaseValue> castToMap() {
        throw castError(getTag(), BaseType.MAP);
    }

    public Seq<BaseValue> castToSeq() {
        throw castError(getTag(), BaseType.SEQ);
    }
}
