package com.apl2.types;

import com.apl2.APLRuntime;

import java.util.Objects;

/**
 * Represents an APL2 Complex number (real + imaginary parts).
 */
public final class ComplexType implements Scalar {
    private final double real;
    private final double imaginary;
    private static final double EPSILON = 1e-15;

    public ComplexType(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public double getReal() {
        return real;
    }

    public double getImaginary() {
        return imaginary;
    }

    @Override
    public String getTypeName() {
        return "Complex";
    }

    @Override
    public APLType deepCopy() {
        return new ComplexType(real, imaginary);
    }

    @Override
    public double toNumeric() {
        return Math.sqrt(real * real + imaginary * imaginary);
    }

    @Override
    public boolean toBoolean() {
        return Math.abs(real) > EPSILON || Math.abs(imaginary) > EPSILON;
    }

    @Override
    public char toCharacter() {
        return (char) (long) real;
    }

    public ComplexType add(ComplexType other) {
        return new ComplexType(real + other.real, imaginary + other.imaginary);
    }

    public ComplexType subtract(ComplexType other) {
        return new ComplexType(real - other.real, imaginary - other.imaginary);
    }

    public ComplexType multiply(ComplexType other) {
        double newReal = real * other.real - imaginary * other.imaginary;
        double newImag = real * other.imaginary + imaginary * other.real;
        return new ComplexType(newReal, newImag);
    }

    public ComplexType divide(ComplexType other) {
        double denominator = other.real * other.real + other.imaginary * other.imaginary;
        double newReal = (real * other.real + imaginary * other.imaginary) / denominator;
        double newImag = (imaginary * other.real - real * other.imaginary) / denominator;
        return new ComplexType(newReal, newImag);
    }

    public ComplexType conjugate() {
        return new ComplexType(real, -imaginary);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplexType that = (ComplexType) o;
        APLRuntime runtime = APLRuntime.getInstance();
        return runtime.areClose(that.real, real) &&
               runtime.areClose(that.imaginary, imaginary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(real, imaginary);
    }

    @Override
    public String toString() {
        if (Math.abs(imaginary) < EPSILON) {
            return String.valueOf(real);
        }
        return String.format("%f%s%fi", real, imaginary >= 0 ? "+" : "", imaginary);
    }
}
