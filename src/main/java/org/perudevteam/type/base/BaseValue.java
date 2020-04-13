package org.perudevteam.type.base;

import io.vavr.Function0;
import io.vavr.Function1;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import org.perudevteam.type.Tagged;

import java.util.Calendar;
import java.util.Objects;
import java.util.function.Function;

import static org.perudevteam.type.base.PresentBaseValues.*;

public abstract class BaseValue extends Tagged<BaseType> {
    public static ClassCastException castError(BaseType from, BaseType to) {
        return new ClassCastException("Base Type " + from.name() + " cannot be converted to " + to.name() + ".");
    }

    public BaseValue(BaseType tag) {
        super(tag);
    }


    /*
     * Casting Functions.
     */


    public byte toByte() {
        throw castError(getTag(), BaseType.BYTE);
    }

    public short toShort() {
        throw castError(getTag(), BaseType.SHORT);
    }

    public int toInt() {
        throw castError(getTag(), BaseType.INT);
    }

    public long toLong() {
        throw castError(getTag(), BaseType.LONG);
    }

    public float toFloat() {
        throw castError(getTag(), BaseType.FLOAT);
    }

    public double toDouble() {
        throw castError(getTag(), BaseType.DOUBLE);
    }

    // Characters are special, they can be widened to everything below and including int.
    // Nothing can be widened to Character since char is unsigned.
    public char toCharacter() {
        throw castError(getTag(), BaseType.CHARACTER);
    }

    /*
     * The above order refers to java widening rules.
     * The below casts have no widening or narrowing.
     * Ex, a Base Value holding an enum can only be cast to an enum... nothing else.
     *
     * (Except for String, everything can be cast to a String)
     */

    public Enum toEnum() {
        throw castError(getTag(), BaseType.ENUM);
    }

    public boolean toBoolean() {
        throw castError(getTag(), BaseType.BOOLEAN);
    }

    @Override
    public String toString() {
        throw castError(getTag(), BaseType.STRING);
    }

    public Map<String, BaseValue> toMap() {
        throw castError(getTag(), BaseType.MAP);
    }

    public Seq<BaseValue> toSequence() {
        throw castError(getTag(), BaseType.SEQUENCE);
    }

    public Function1<Seq<BaseValue>, BaseValue> toFunction() {
        throw castError(getTag(), BaseType.FUNCTION);
    }


    /*
     * Static Constructors.
     */


    public static BaseValue ofByte(byte v) {
        return new BaseByte(v);
    }

    public static BaseValue ofShort(short v) {
        return new BaseShort(v);
    }

    public static BaseValue ofInt(int v) {
        return new BaseInt(v);
    }

    public static BaseValue ofLong(long v) {
        return new BaseLong(v);
    }

    public static BaseValue ofFloat(float v) {
        return new BaseFloat(v);
    }

    public static BaseValue ofDouble(double v) {
        return new BaseDouble(v);
    }

    public static BaseValue ofCharacter(char v) {
        return new BaseCharacter(v);
    }

    public static BaseValue ofEnum(Enum v) {
        return new BaseEnum(v);
    }

    public static BaseValue ofBoolean(boolean v) {
        return v ? BaseBoolean.TRUE : BaseBoolean.FALSE;
    }

    public static BaseValue ofString(String v) {
        return new BaseString(v);
    }

    public static BaseValue ofMap(Map<? extends String, ? extends BaseValue> v) {
        return new BaseMap(v);
    }

    public static BaseValue ofSequence(Seq<? extends BaseValue> v) {
        return new BaseSequence(v);
    }

    public static BaseValue ofFunction(Function1<? super Seq<BaseValue>, ? extends BaseValue> v) {
        return new BaseFunction(v);
    }


    /*
     * Map Functions.
     */


    public BaseValue mapByte(Function1<? super Byte, ? extends Byte> f) {
        return new BaseByte(f.apply(toByte()));
    }

    public BaseValue mapShort(Function1<? super Short, ? extends Short> f) {
        return new BaseShort(f.apply(toShort()));
    }

    public BaseValue mapInt(Function1<? super Integer, ? extends Integer> f) {
        return new BaseInt(f.apply(toInt()));
    }

    public BaseValue mapLong(Function1<? super Long, ? extends Long> f) {
        return new BaseLong(f.apply(toLong()));
    }

    public BaseValue mapFloat(Function<? super Float, ? extends Float> f) {
        return new BaseFloat(f.apply(toFloat()));
    }

    public BaseValue mapDouble(Function1<? super Double, ? extends Double> f) {
        return new BaseDouble(f.apply(toDouble()));
    }

    public BaseValue mapCharacter(Function1<? super Character, ? extends Character> f) {
        return new BaseCharacter(f.apply(toCharacter()));
    }

    public BaseValue mapEnum(Function1<? super Enum, ? extends Enum> f) {
        return new BaseEnum(f.apply(toEnum()));
    }

    public BaseValue mapBoolean(Function1<? super Boolean, ? extends Boolean> f) {
        return ofBoolean(f.apply(toBoolean()));
    }

    public BaseValue mapString(Function1<? super String, ? extends String> f) {
        return new BaseString(f.apply(toString()));
    }

    public BaseValue mapMap(Function1<? super Map<String, BaseValue>, ? extends Map<String, BaseValue>> f) {
        return new BaseMap(f.apply(toMap()));
    }

    public BaseValue mapSeq(Function1<? super Seq<BaseValue>, ? extends Seq<BaseValue>> f) {
        return new BaseSequence(f.apply(toSequence()));
    }

    public BaseValue mapFunction(Function1<? super Function1<Seq<BaseValue>, BaseValue>,
            ? extends Function1<Seq<BaseValue>, BaseValue>> f) {
        return new BaseFunction(f.apply(toFunction()));
    }


}
