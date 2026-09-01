package com.apl2.types;

import java.util.Objects;

/**
 * Represents an APL2 Boolean value (0 or 1).
 */
public final class BooleanType implements Scalar {
    private final boolean value;

    public BooleanType(boolean value) {
        this.value = value;
    }

    public static BooleanType TRUE = new BooleanType(true);
    public static BooleanType FALSE = new BooleanType(false);

    public boolean getValue() {
        return value;
    }

    @Override
    public String getTypeName() {
        return "Boolean";
    }

    @Override
    public APLType deepCopy() {
        return new BooleanType(value);
    }

    @Override
    public double toNumeric() {
        return value ? 1.0 : 0.0;
    }

    @Override
    public boolean toBoolean() {
        return value;
    }

    @Override
    public char toCharacter() {
        return value ? '1' : '0';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BooleanType that = (BooleanType) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value ? "1" : "0";
    }
}
