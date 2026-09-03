package com.apl2.operations;

import com.apl2.types.*;
import com.apl2.types.specialized.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Core APL2 mathematical operations.
 * Supports all basic arithmetic operations with type promotion and error handling.
 */
public class MathOperations {

    /**
     * Addition (dyadic +)
     */
    public static APLType add(APLType left, APLType right) {
        if (left instanceof IntegerType l && right instanceof IntegerType r) {
            return new IntegerType(l.getValue() + r.getValue());
        }
        if (left instanceof FloatingPointType l && right instanceof FloatingPointType r) {
            return new FloatingPointType(l.getValue() + r.getValue());
        }
        if (left instanceof ComplexType l && right instanceof ComplexType r) {
            return l.add(r);
        }
        if (left instanceof BigIntegerType l && right instanceof BigIntegerType r) {
            return l.add(r);
        }
        if (left instanceof BigDecimalType l && right instanceof BigDecimalType r) {
            return l.add(r);
        }
        // Type promotion: promote integers to floating point
        double lVal = toNumeric(left);
        double rVal = toNumeric(right);
        return new FloatingPointType(lVal + rVal);
    }

    /**
     * Subtraction (dyadic -)
     */
    public static APLType subtract(APLType left, APLType right) {
        if (left instanceof IntegerType l && right instanceof IntegerType r) {
            return new IntegerType(l.getValue() - r.getValue());
        }
        if (left instanceof FloatingPointType l && right instanceof FloatingPointType r) {
            return new FloatingPointType(l.getValue() - r.getValue());
        }
        if (left instanceof ComplexType l && right instanceof ComplexType r) {
            return l.subtract(r);
        }
        if (left instanceof BigIntegerType l && right instanceof BigIntegerType r) {
            return l.subtract(r);
        }
        if (left instanceof BigDecimalType l && right instanceof BigDecimalType r) {
            return l.subtract(r);
        }
        double lVal = toNumeric(left);
        double rVal = toNumeric(right);
        return new FloatingPointType(lVal - rVal);
    }

    /**
     * Multiplication (dyadic ×)
     */
    public static APLType multiply(APLType left, APLType right) {
        if (left instanceof IntegerType l && right instanceof IntegerType r) {
            return new IntegerType(l.getValue() * r.getValue());
        }
        if (left instanceof FloatingPointType l && right instanceof FloatingPointType r) {
            return new FloatingPointType(l.getValue() * r.getValue());
        }
        if (left instanceof ComplexType l && right instanceof ComplexType r) {
            return l.multiply(r);
        }
        if (left instanceof BigIntegerType l && right instanceof BigIntegerType r) {
            return l.multiply(r);
        }
        if (left instanceof BigDecimalType l && right instanceof BigDecimalType r) {
            return l.multiply(r);
        }
        double lVal = toNumeric(left);
        double rVal = toNumeric(right);
        return new FloatingPointType(lVal * rVal);
    }

    /**
     * Division (dyadic ÷)
     */
    public static APLType divide(APLType left, APLType right) {
        double rVal = toNumeric(right);
        if (Math.abs(rVal) < 1e-15) {
            throw new ArithmeticException("Division by zero");
        }
        if (left instanceof IntegerType l && right instanceof IntegerType r) {
            return new FloatingPointType((double) l.getValue() / r.getValue());
        }
        if (left instanceof FloatingPointType l && right instanceof FloatingPointType r) {
            return new FloatingPointType(l.getValue() / r.getValue());
        }
        if (left instanceof ComplexType l && right instanceof ComplexType r) {
            return l.divide(r);
        }
        if (left instanceof BigDecimalType l && right instanceof BigDecimalType r) {
            return l.divide(r);
        }
        double lVal = toNumeric(left);
        return new FloatingPointType(lVal / rVal);
    }

    /**
     * Modulo/Remainder (dyadic |)
     */
    public static APLType modulo(APLType left, APLType right) {
        if (left instanceof IntegerType l && right instanceof IntegerType r) {
            if (r.getValue() == 0) throw new ArithmeticException("Modulo by zero");
            return new IntegerType(l.getValue() % r.getValue());
        }
        if (left instanceof BigIntegerType l && right instanceof BigIntegerType r) {
            return l.mod(r);
        }
        if (left instanceof BigDecimalType l && right instanceof BigDecimalType r) {
            return l.remainder(r);
        }
        double rVal = toNumeric(right);
        if (Math.abs(rVal) < 1e-15) {
            throw new ArithmeticException("Modulo by zero");
        }
        double lVal = toNumeric(left);
        return new FloatingPointType(lVal % rVal);
    }

    /**
     * Power/Exponentiation (dyadic *)
     */
    public static APLType power(APLType left, APLType right) {
        if (left instanceof BigIntegerType l && right instanceof IntegerType r) {
            return l.pow((int) r.getValue());
        }
        double base = toNumeric(left);
        double exponent = toNumeric(right);
        return new FloatingPointType(Math.pow(base, exponent));
    }

    /**
     * Negation (monadic -)
     */
    public static APLType negate(APLType operand) {
        if (operand instanceof IntegerType i) {
            return new IntegerType(-i.getValue());
        }
        if (operand instanceof FloatingPointType f) {
            return new FloatingPointType(-f.getValue());
        }
        if (operand instanceof ComplexType c) {
            return new ComplexType(-c.getReal(), -c.getImaginary());
        }
        if (operand instanceof BigIntegerType b) {
            return b.negate();
        }
        if (operand instanceof BigDecimalType b) {
            return b.negate();
        }
        return new FloatingPointType(-toNumeric(operand));
    }

    /**
     * Absolute value (monadic |)
     */
    public static APLType abs(APLType operand) {
        if (operand instanceof IntegerType i) {
            return new IntegerType(Math.abs(i.getValue()));
        }
        if (operand instanceof FloatingPointType f) {
            return new FloatingPointType(Math.abs(f.getValue()));
        }
        if (operand instanceof ComplexType c) {
            return new FloatingPointType(c.toNumeric());
        }
        if (operand instanceof BigIntegerType b) {
            return b.abs();
        }
        if (operand instanceof BigDecimalType b) {
            return b.abs();
        }
        return new FloatingPointType(Math.abs(toNumeric(operand)));
    }

    /**
     * Square root (monadic *)
     */
    public static APLType sqrt(APLType operand) {
        double value = toNumeric(operand);
        if (value < 0) {
            // Return complex number for negative values
            return new ComplexType(0, Math.sqrt(-value));
        }
        return new FloatingPointType(Math.sqrt(value));
    }

    /**
     * Ceiling (monadic ⌈)
     */
    public static APLType ceiling(APLType operand) {
        if (operand instanceof IntegerType i) {
            return i;
        }
        if (operand instanceof FloatingPointType f) {
            return new IntegerType((long) Math.ceil(f.getValue()));
        }
        return new IntegerType((long) Math.ceil(toNumeric(operand)));
    }

    /**
     * Floor (monadic ⌊)
     */
    public static APLType floor(APLType operand) {
        if (operand instanceof IntegerType i) {
            return i;
        }
        if (operand instanceof FloatingPointType f) {
            return new IntegerType((long) Math.floor(f.getValue()));
        }
        return new IntegerType((long) Math.floor(toNumeric(operand)));
    }

    /**
     * Sign function (monadic ×)
     */
    public static APLType sign(APLType operand) {
        double value = toNumeric(operand);
        long result = value > 0 ? 1 : (value < 0 ? -1 : 0);
        return new IntegerType(result);
    }

    /**
     * Conjugate/identity (monadic +)
     */
    public static APLType conjugate(APLType operand) {
        if (operand instanceof ComplexType c) {
            return c.conjugate();
        }
        return operand.deepCopy();
    }

    /**
     * Signum (monadic ×)
     */
    public static APLType signum(APLType operand) {
        return sign(operand);
    }

    /**
     * Reciprocal (monadic ÷)
     */
    public static APLType reciprocal(APLType operand) {
        return divide(new IntegerType(1), operand);
    }

    /**
     * Residue (dyadic |)
     */
    public static APLType residue(APLType left, APLType right) {
        return modulo(left, right);
    }

    /**
     * Magnitude (monadic |)
     */
    public static APLType magnitude(APLType operand) {
        return abs(operand);
    }

    /**
     * Natural logarithm (monadic ⍟)
     */
    public static APLType log(APLType operand) {
        double value = toNumeric(operand);
        if (value <= 0) {
            throw new ArithmeticException("Logarithm of non-positive number");
        }
        return new FloatingPointType(Math.log(value));
    }

    /**
     * Exponential (monadic *)
     */
    public static APLType exp(APLType operand) {
        return new FloatingPointType(Math.exp(toNumeric(operand)));
    }

    /**
     * Natural logarithm (monadic ⍟)
     */
    public static APLType naturalLog(APLType operand) {
        return log(operand);
    }

    /**
     * Exponential (monadic *)
     */
    public static APLType exponential(APLType operand) {
        return exp(operand);
    }

    /**
     * Logarithm with base (dyadic ⍟): log base left of right
     */
    public static APLType logarithm(APLType left, APLType right) {
        double base = toNumeric(left);
        double value = toNumeric(right);
        if (base <= 0 || Math.abs(base - 1.0) < 1e-15 || value <= 0) {
            throw new ArithmeticException("Invalid arguments for logarithm");
        }
        return new FloatingPointType(Math.log(value) / Math.log(base));
    }

    /**
     * Factorial (monadic !)
     */
    public static APLType factorial(APLType operand) {
        double value = toNumeric(operand);
        if (value < 0 || value != Math.floor(value)) {
            throw new ArithmeticException("Factorial requires a non-negative integer");
        }
        long n = (long) value;
        BigInteger result = BigInteger.ONE;
        for (long i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        if (result.bitLength() < 63) {
            return new IntegerType(result.longValue());
        }
        return new BigIntegerType(result);
    }

    /**
     * Binomial (dyadic !): left choose right
     */
    public static APLType binomial(APLType left, APLType right) {
        long n = toNonNegativeInteger(left, "Binomial left argument");
        long k = toNonNegativeInteger(right, "Binomial right argument");
        if (k > n) {
            return new IntegerType(0);
        }
        long m = Math.min(k, n - k);
        BigInteger result = BigInteger.ONE;
        for (long i = 1; i <= m; i++) {
            result = result.multiply(BigInteger.valueOf(n - m + i))
                           .divide(BigInteger.valueOf(i));
        }
        if (result.bitLength() < 63) {
            return new IntegerType(result.longValue());
        }
        return new BigIntegerType(result);
    }

    /**
     * Sine (monadic ○)
     */
    public static APLType sin(APLType operand) {
        return new FloatingPointType(Math.sin(toNumeric(operand)));
    }

    /**
     * Cosine (monadic ○)
     */
    public static APLType cos(APLType operand) {
        return new FloatingPointType(Math.cos(toNumeric(operand)));
    }

    /**
     * Tangent (monadic ○)
     */
    public static APLType tan(APLType operand) {
        return new FloatingPointType(Math.tan(toNumeric(operand)));
    }

    /**
     * Circle functions (dyadic ∘) using APL function codes.
     */
    public static APLType circle(APLType left, APLType right) {
        int fn = (int) Math.round(toNumeric(left));
        double x = toNumeric(right);
        return switch (fn) {
            case 1 -> new FloatingPointType(Math.sin(x));
            case 2 -> new FloatingPointType(Math.cos(x));
            case 3 -> new FloatingPointType(Math.tan(x));
            case -1 -> new FloatingPointType(Math.asin(x));
            case -2 -> new FloatingPointType(Math.acos(x));
            case -3 -> new FloatingPointType(Math.atan(x));
            case 5 -> new FloatingPointType(Math.sinh(x));
            case 6 -> new FloatingPointType(Math.cosh(x));
            case 7 -> new FloatingPointType(Math.tanh(x));
            case -5 -> new FloatingPointType(Math.log(x + Math.sqrt(x * x + 1)));
            case -6 -> new FloatingPointType(Math.log(x + Math.sqrt(x * x - 1)));
            case -7 -> new FloatingPointType(0.5 * Math.log((1 + x) / (1 - x)));
            default -> throw new IllegalArgumentException("Unsupported circle function code: " + fn);
        };
    }

    /**
     * Circle functions alias.
     */
    public static APLType circleFunctions(APLType left, APLType right) {
        return circle(left, right);
    }

    /**
     * Maximum (dyadic ⌈)
     */
    public static APLType max(APLType left, APLType right) {
        double lVal = toNumeric(left);
        double rVal = toNumeric(right);
        return new FloatingPointType(Math.max(lVal, rVal));
    }

    /**
     * Minimum (dyadic ⌊)
     */
    public static APLType min(APLType left, APLType right) {
        double lVal = toNumeric(left);
        double rVal = toNumeric(right);
        return new FloatingPointType(Math.min(lVal, rVal));
    }

    /**
     * Equals (=)
     */
    public static APLType equal(APLType left, APLType right) {
        if (left instanceof ArrayType lArr && right instanceof ArrayType rArr) {
            return new BooleanType(lArr.equals(rArr));
        }
        if (left instanceof Scalar && right instanceof Scalar) {
            return new BooleanType(Math.abs(toNumeric(left) - toNumeric(right)) < 1e-15);
        }
        return new BooleanType(left.equals(right));
    }

    /**
     * Not equals (≠)
     */
    public static APLType notEqual(APLType left, APLType right) {
        return new BooleanType(!((BooleanType) equal(left, right)).getValue());
    }

    /**
     * Less than (<)
     */
    public static APLType lessThan(APLType left, APLType right) {
        return new BooleanType(toNumeric(left) < toNumeric(right));
    }

    /**
     * Less or equal (≤)
     */
    public static APLType lessEqual(APLType left, APLType right) {
        return new BooleanType(toNumeric(left) <= toNumeric(right));
    }

    /**
     * Greater than (>)
     */
    public static APLType greaterThan(APLType left, APLType right) {
        return new BooleanType(toNumeric(left) > toNumeric(right));
    }

    /**
     * Greater or equal (≥)
     */
    public static APLType greaterEqual(APLType left, APLType right) {
        return new BooleanType(toNumeric(left) >= toNumeric(right));
    }

    /**
     * Monadic iota (⍳)
     */
    public static APLType iota(APLType operand) {
        long n = toNonNegativeInteger(operand, "Iota argument");
        List<APLType> elements = new ArrayList<>();
        for (long i = 0; i < n; i++) {
            elements.add(new IntegerType(i));
        }
        return new ArrayType(elements);
    }

    /**
     * Helper method to convert any scalar to numeric value.
     */
    private static double toNumeric(APLType value) {
        if (value instanceof Scalar s) {
            return s.toNumeric();
        }
        throw new IllegalArgumentException("Cannot convert non-scalar to numeric");
    }

    private static long toNonNegativeInteger(APLType value, String name) {
        double numeric = toNumeric(value);
        if (numeric < 0 || numeric != Math.floor(numeric)) {
            throw new ArithmeticException(name + " must be a non-negative integer");
        }
        return (long) numeric;
    }
}
