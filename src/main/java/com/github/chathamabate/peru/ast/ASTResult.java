package com.github.chathamabate.peru.ast;

import io.vavr.control.Option;
import com.github.chathamabate.charpos.CharPos;
import com.github.chathamabate.charpos.MappableCharPos;
import com.github.chathamabate.charpos.SimpleCharPos;
import com.github.chathamabate.peru.base.BaseValue;

import java.util.Objects;

public class ASTResult implements MappableCharPos<ASTResult> {

    static ASTResult empty() {
        return EMPTY;
    }

    static ASTResult position(int l, int lp) {
        return EMPTY.withPosition(l, lp);
    }

    static ASTResult position(CharPos d) {
        return EMPTY.withPosition(d);
    }

    static ASTResult value(BaseValue val) {
        return EMPTY.withValue(val);
    }

    static ASTResult fullResult(int l, int lp, BaseValue val) {
        Objects.requireNonNull(val);
        return new ASTResult(Option.of(SimpleCharPos.simpleCharPos(l, lp)), Option.of(val));
    }

    static ASTResult fullResult(CharPos d, BaseValue val) {
        Objects.requireNonNull(d);
        Objects.requireNonNull(val);

        return new ASTResult(Option.of(d), Option.of(val));
    }

    static String buildASTResultString(ASTResult r) {
        String internal = "";

        if (r.isPositioned()) internal += "[" + r.getLine() + " : " + r.getLinePosition() + "]";
        if (!r.isEmpty()) internal += " (" + r.getValue().toString() + ")";

        return internal;
    }

    private static final ASTResult EMPTY = new ASTResult(Option.none(), Option.none());

    private final Option<CharPos> positionOption;
    private final Option<BaseValue> valueOption;

    protected ASTResult(Option<CharPos> p, Option<BaseValue> v) {
        positionOption = p;
        valueOption = v;
    }

    public boolean isEmpty() {
        return valueOption.isEmpty();
    }

    public BaseValue getValue() {
        return valueOption.get();
    }

    public ASTResult withValue(BaseValue v) {
        Objects.requireNonNull(v);
        return new ASTResult(positionOption, Option.of(v));
    }

    public boolean isPositioned() {
        return !positionOption.isEmpty();
    }

    @Override
    public int getLine() {
        return positionOption.get().getLine();
    }

    @Override
    public int getLinePosition() {
        return positionOption.get().getLinePosition();
    }

    @Override
    public ASTResult withLine(int l) {
        SimpleCharPos newPos = positionOption.isEmpty()
                ? SimpleCharPos.simpleCharPos(l, 0)
                : SimpleCharPos.simpleCharPos(l, positionOption.get().getLinePosition());

        return new ASTResult(Option.of(newPos), valueOption);
    }

    @Override
    public ASTResult withLinePosition(int lp) {
        SimpleCharPos newPos = positionOption.isEmpty()
                ? SimpleCharPos.simpleCharPos(0, lp)
                : SimpleCharPos.simpleCharPos(positionOption.get().getLine(), lp);

        return new ASTResult(Option.of(newPos), valueOption);
    }

    @Override
    public ASTResult withPosition(int l, int lp) {
        return new ASTResult(Option.of(SimpleCharPos.simpleCharPos(l, lp)), valueOption);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ASTResult astResult = (ASTResult) o;
        return positionOption.equals(astResult.positionOption) &&
                valueOption.equals(astResult.valueOption);
    }

    @Override
    public int hashCode() {
        return Objects.hash(positionOption, valueOption);
    }

    @Override
    public String toString() {
        return buildASTResultString(this);
    }
}
