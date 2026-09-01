package com.apl2.operations;

import com.apl2.types.*;
import com.apl2.types.specialized.*;
import java.math.BigDecimal;
import java.math.BigInteger;

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
            return c.negate();
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
     * Helper method to convert any scalar to numeric value.
     */
    private static double toNumeric(APLType value) {
        if (value instanceof Scalar s) {
            return s.toNumeric();
        }
        throw new IllegalArgumentException("Cannot convert non-scalar to numeric");
    }
}
