package com.apl2.types;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Represents an APL2 BigInteger value for arbitrary-precision integers.
 */
public final class BigIntegerType implements Scalar {
    private final BigInteger value;

    public BigIntegerType(BigInteger value) {
        this.value = Objects.requireNonNull(value, "BigInteger value cannot be null");
    }

    public BigIntegerType(String value) {
        this.value = new BigInteger(Objects.requireNonNull(value, "String value cannot be null"));
    }

    public BigIntegerType(long value) {
        this.value = BigInteger.valueOf(value);
    }

    public BigInteger getValue() {
        return value;
    }

    @Override
    public String getTypeName() {
        return "BigInteger";
    }

    @Override
    public APLType deepCopy() {
        return new BigIntegerType(value);
    }

    @Override
    public double toNumeric() {
        return value.doubleValue();
    }

    @Override
    public boolean toBoolean() {
        return !value.equals(BigInteger.ZERO);
    }

    @Override
    public char toCharacter() {
        return (char) value.longValue();
    }

    /**
     * Adds two BigInteger values.
     */
    public BigIntegerType add(BigIntegerType other) {
        return new BigIntegerType(value.add(other.value));
    }

    /**
     * Subtracts two BigInteger values.
     */
    public BigIntegerType subtract(BigIntegerType other) {
        return new BigIntegerType(value.subtract(other.value));
    }

    /**
     * Multiplies two BigInteger values.
     */
    public BigIntegerType multiply(BigIntegerType other) {
        return new BigIntegerType(value.multiply(other.value));
    }

    /**
     * Divides two BigInteger values (integer division).
     */
    public BigIntegerType divide(BigIntegerType other) {
        if (other.value.equals(BigInteger.ZERO)) {
            throw new ArithmeticException("Division by zero");
        }
        return new BigIntegerType(value.divide(other.value));
    }

    /**
     * Returns the modulo of two BigInteger values.
     */
    public BigIntegerType mod(BigIntegerType other) {
        if (other.value.equals(BigInteger.ZERO)) {
            throw new ArithmeticException("Modulo by zero");
        }
        return new BigIntegerType(value.mod(other.value));
    }

    /**
     * Returns the power of this BigInteger raised to an exponent.
     */
    public BigIntegerType pow(int exponent) {
        return new BigIntegerType(value.pow(exponent));
    }

    /**
     * Returns the absolute value.
     */
    public BigIntegerType abs() {
        return new BigIntegerType(value.abs());
    }

    /**
     * Returns the negation.
     */
    public BigIntegerType negate() {
        return new BigIntegerType(value.negate());
    }

    /**
     * Returns the greatest common divisor with another BigInteger.
     */
    public BigIntegerType gcd(BigIntegerType other) {
        return new BigIntegerType(value.gcd(other.value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BigIntegerType that = (BigIntegerType) o;
        return Objects.equals(value, that.value);
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
