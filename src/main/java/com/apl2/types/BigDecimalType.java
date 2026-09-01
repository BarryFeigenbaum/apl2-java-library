package com.apl2.types;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Represents an APL2 BigDecimal value for arbitrary-precision decimal numbers.
 */
public final class BigDecimalType implements Scalar {
    private final BigDecimal value;
    private static final double EPSILON = 1e-15;

    public BigDecimalType(BigDecimal value) {
        this.value = Objects.requireNonNull(value, "BigDecimal value cannot be null");
    }

    public BigDecimalType(String value) {
        this.value = new BigDecimal(Objects.requireNonNull(value, "String value cannot be null"));
    }

    public BigDecimalType(double value) {
        this.value = BigDecimal.valueOf(value);
    }

    public BigDecimalType(long value) {
        this.value = BigDecimal.valueOf(value);
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public String getTypeName() {
        return "BigDecimal";
    }

    @Override
    public APLType deepCopy() {
        return new BigDecimalType(value);
    }

    @Override
    public double toNumeric() {
        return value.doubleValue();
    }

    @Override
    public boolean toBoolean() {
        return value.compareTo(BigDecimal.ZERO) != 0;
    }

    @Override
    public char toCharacter() {
        return (char) value.longValue();
    }

    /**
     * Adds two BigDecimal values.
     */
    public BigDecimalType add(BigDecimalType other) {
        return new BigDecimalType(value.add(other.value));
    }

    /**
     * Subtracts two BigDecimal values.
     */
    public BigDecimalType subtract(BigDecimalType other) {
        return new BigDecimalType(value.subtract(other.value));
    }

    /**
     * Multiplies two BigDecimal values.
     */
    public BigDecimalType multiply(BigDecimalType other) {
        return new BigDecimalType(value.multiply(other.value));
    }

    /**
     * Divides two BigDecimal values.
     */
    public BigDecimalType divide(BigDecimalType other) {
        if (other.value.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return new BigDecimalType(value.divide(other.value, 50, RoundingMode.HALF_UP));
    }

    /**
     * Returns the remainder of division.
     */
    public BigDecimalType remainder(BigDecimalType other) {
        if (other.value.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Remainder by zero");
        }
        return new BigDecimalType(value.remainder(other.value));
    }

    /**
     * Returns the absolute value.
     */
    public BigDecimalType abs() {
        return new BigDecimalType(value.abs());
    }

    /**
     * Returns the negation.
     */
    public BigDecimalType negate() {
        return new BigDecimalType(value.negate());
    }

    /**
     * Rounds to a specified number of decimal places.
     */
    public BigDecimalType round(int scale) {
        return new BigDecimalType(value.setScale(scale, RoundingMode.HALF_UP));
    }

    /**
     * Returns the maximum of two BigDecimal values.
     */
    public BigDecimalType max(BigDecimalType other) {
        return new BigDecimalType(value.max(other.value));
    }

    /**
     * Returns the minimum of two BigDecimal values.
     */
    public BigDecimalType min(BigDecimalType other) {
        return new BigDecimalType(value.min(other.value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BigDecimalType that = (BigDecimalType) o;
        return value.compareTo(that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
