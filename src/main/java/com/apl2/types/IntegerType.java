package com.apl2.types;

import java.util.Objects;

/**
 * Represents an APL2 Integer value.
 */
public final class IntegerType implements Scalar {
    private final long value;

    public IntegerType(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String getTypeName() {
        return "Integer";
    }

    @Override
    public APLType deepCopy() {
        return new IntegerType(value);
    }

    @Override
    public double toNumeric() {
        return (double) value;
    }

    @Override
    public boolean toBoolean() {
        return value != 0;
    }

    @Override
    public char toCharacter() {
        return (char) value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntegerType that = (IntegerType) o;
        return value == that.value;
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
