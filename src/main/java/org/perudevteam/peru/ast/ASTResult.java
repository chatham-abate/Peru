package org.perudevteam.peru.ast;

import io.vavr.Function1;
import org.perudevteam.misc.CharPosition;
import org.perudevteam.peru.base.BaseValue;

import java.util.Objects;

public interface ASTResult extends CharPosition {

    static ASTResult empty() {
        return EMPTY;
    }

    static ASTResult position(int l, int lp) {
        return EMPTY.withPosition(l, lp);
    }

    static ASTResult position(CharPosition d) {
        return EMPTY.withPosition(d);
    }

    static ASTResult value(BaseValue val) {
        return EMPTY.withValue(val);
    }

    static ASTResult fullResult(int l, int lp, BaseValue val) {
        return value(val).withPosition(l, lp);
    }

    static ASTResult fullResult(CharPosition d, BaseValue val) {
        return value(val).withPosition(d.getLine(), d.getLinePosition());
    }

    static boolean samePositions(ASTResult r1, ASTResult r2) {
        if (!r1.isPositioned() && !r2.isPositioned()) {
            return true;
        }

        if (r1.isPositioned() && r2.isPositioned()) {
            return r1.getLine() == r2.getLine() && r1.getLinePosition() == r2.getLinePosition();
        }

        return false;
    }

    static boolean sameValues(ASTResult r1, ASTResult r2) {
        if (r1.isEmpty() && r2.isEmpty()) {
            return true;
        }

        if (!r1.isEmpty() && !r2.isEmpty()) {
            return r1.getValue().equals(r2.getValue());
        }

        return false;
    }

    static boolean sameASTResults(ASTResult r1, ASTResult r2) {
        return samePositions(r1, r2) && sameValues(r1, r2);
    }

    static boolean equalsASTResult(ASTResult r, Object o) {
        Objects.requireNonNull(r);

        if (o == null) return false;
        if (o.getClass() != r.getClass()) return false;

        ASTResult that = (ASTResult) o;

        return sameASTResults(r, that);
    }

    static String buildASTResultString(ASTResult r) {
        String internal = "";

        if (r.isPositioned()) internal += "[" + r.getLine() + " : " + r.getLinePosition() + "]";
        if (!r.isEmpty()) internal += " " + r.getValue().toString();

        return "(" + internal + ")";
    }

    ASTResult EMPTY = new ASTResult() {
        @Override
        @SuppressWarnings("all")
        public boolean equals(Object obj) {
            return ASTResult.equalsASTResult(this, obj);
        }

        @Override
        public String toString() {
            return ASTResult.buildASTResultString(this);
        }
    };

    default BaseValue getValue() {
        throw new NullPointerException("AST Result holds no value.");
    }

    default boolean isEmpty() {
        return true;
    }

    default ASTResult withValue(BaseValue val) {
        Objects.requireNonNull(val);
        final ASTResult This = this;

        return new ASTResult() {
            @Override
            public BaseValue getValue() {
                return val;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public int getLine() {
                return This.getLine();
            }

            @Override
            public int getLinePosition() {
                return This.getLinePosition();
            }

            @Override
            public boolean isPositioned() {
                return This.isPositioned();
            }

            @Override
            public int hashCode() {
                if (this.isPositioned()) {
                    return Objects.hash(val, this.getLine(), this.getLinePosition());
                }

                return Objects.hashCode(val);
            }

            @Override
            @SuppressWarnings("all")
            public boolean equals(Object obj) {
                return ASTResult.equalsASTResult(this, obj);
            }

            @Override
            public String toString() {
                return ASTResult.buildASTResultString(this);
            }
        };
    }

    default boolean isPositioned() {
        return false;
    }

    @Override
    default int getLine() {
        throw new NullPointerException("AST Result holds no line.");
    }

    @Override
    default int getLinePosition() {
        throw new NullPointerException("AST Result holds no line position.");
    }

    @Override
    default ASTResult withLine(int l) {
        return withPosition(l, 0);
    }

    @Override
    default ASTResult withLinePosition(int lp) {
        return withPosition(0, lp);
    }

    @Override
    default ASTResult withPosition(CharPosition d) {
        Objects.requireNonNull(d);
        return withPosition(d.getLine(), d.getLinePosition());
    }

    default ASTResult withPosition(int l, int lp) {
        final ASTResult This = this;

        return new ASTResult() {
            @Override
            public BaseValue getValue() {
                return This.getValue();
            }

            @Override
            public boolean isEmpty() {
                return This.isEmpty();
            }

            @Override
            public int getLine() {
                return l;
            }

            @Override
            public int getLinePosition() {
                return lp;
            }

            @Override
            public ASTResult withLine(int l) {
                return withPosition(l, lp);
            }

            @Override
            public ASTResult withLinePosition(int lp) {
                return withPosition(l, lp);
            }

            @Override
            public ASTResult mapLine(Function1<? super Integer, ? extends Integer> f) {
                Objects.requireNonNull(f);
                return withLine(f.apply(l));
            }

            @Override
            public ASTResult mapLinePosition(Function1<? super Integer, ? extends Integer> f) {
                Objects.requireNonNull(f);
                return withLinePosition(f.apply(lp));
            }

            @Override
            public ASTResult mapPosition(Function1<? super CharPosition, ? extends CharPosition> f) {
                Objects.requireNonNull(f);
                return withPosition(f.apply(this));
            }

            @Override
            public boolean isPositioned() {
                return true;
            }

            @Override
            public int hashCode() {
                if (!this.isEmpty()) {
                    return Objects.hash(this.getValue(), l, lp);
                }

                return Objects.hash(l, lp);
            }

            @Override
            @SuppressWarnings("all")
            public boolean equals(Object obj) {
                return ASTResult.equalsASTResult(this, obj);
            }

            @Override
            public String toString() {
                return ASTResult.buildASTResultString(this);
            }
        };
    }

    @Override
    default ASTResult mapLine(Function1<? super Integer, ? extends Integer> f) {
        throw new NullPointerException("AST Result holds no line.");
    }

    @Override
    default ASTResult mapLinePosition(Function1<? super Integer, ? extends Integer> f) {
        throw new NullPointerException("AST Result holds no line position.");
    }

    @Override
    default ASTResult mapPosition(Function1<? super CharPosition, ? extends CharPosition> f) {
        throw new NullPointerException("AST Result holds no position");
    }
}
