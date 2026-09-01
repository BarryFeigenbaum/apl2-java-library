package com.apl2.types;

import java.util.Objects;

/**
 * Represents an APL2 String (sequence of characters).
 * In APL2, a string is essentially a 1-D character array.
 */
public final class StringType implements Scalar {
    private final String value;

    public StringType(String value) {
        this.value = Objects.requireNonNull(value, "String value cannot be null");
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getTypeName() {
        return "String";
    }

    @Override
    public APLType deepCopy() {
        return new StringType(value);
    }

    @Override
    public double toNumeric() {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @Override
    public boolean toBoolean() {
        return !value.isEmpty();
    }

    @Override
    public char toCharacter() {
        return value.isEmpty() ? 0 : value.charAt(0);
    }

    public int length() {
        return value.length();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringType that = (StringType) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
