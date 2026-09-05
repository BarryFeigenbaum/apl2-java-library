package com.apl2.types;

import java.util.Objects;

/**
 * Represents an APL2 Floating Point value.
 */
public final class FloatingPointType implements Scalar {
    private final double value;
    private static final double EPSILON = 1e-15;

    public FloatingPointType(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String getTypeName() {
        return "FloatingPoint";
    }

    @Override
    public APLType deepCopy() {
        return new FloatingPointType(value);
    }

    @Override
    public double toNumeric() {
        return value;
    }

    @Override
    public boolean toBoolean() {
        return Math.abs(value) > EPSILON;
    }

    @Override
    public char toCharacter() {
        return (char) (long) value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FloatingPointType that = (FloatingPointType) o;
        return Math.abs(that.value - value) < EPSILON;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
