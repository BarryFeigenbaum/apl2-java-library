package com.apl2;

import com.apl2.types.APLType;
import com.apl2.types.ArrayType;
import com.apl2.types.BigDecimalType;
import com.apl2.types.ComplexType;
import com.apl2.types.FloatingPointType;
import com.apl2.types.Scalar;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * Thread-local runtime state and formatting helpers for APL evaluation.
 */
public final class APLRuntime {
    private static final APLRuntime INSTANCE = new APLRuntime();

    private final ThreadLocal<Deque<APLContext>> contexts = ThreadLocal.withInitial(ArrayDeque::new);

    private APLRuntime() {
    }

    public static APLRuntime getInstance() {
        return INSTANCE;
    }

    public APLContext createContext() {
        return APLContext.defaultContext();
    }

    public APLContext createContext(APLContext.Builder builder) {
        return Objects.requireNonNull(builder, "Builder cannot be null").build();
    }

    public void destroyContext() {
        contexts.remove();
    }

    public APLContext pushContext(APLContext context) {
        APLContext resolvedContext = Objects.requireNonNull(context, "Context cannot be null");
        contexts.get().push(resolvedContext);
        return resolvedContext;
    }

    public APLContext popContext() {
        Deque<APLContext> stack = contexts.get();
        if (stack.isEmpty()) {
            throw new IllegalStateException("No runtime context is currently active");
        }
        APLContext popped = stack.pop();
        if (stack.isEmpty()) {
            contexts.remove();
        }
        return popped;
    }

    public APLContext currentContext() {
        Deque<APLContext> stack = contexts.get();
        return stack.isEmpty() ? APLContext.defaultContext() : stack.peek();
    }

    public int toZeroBasedIndex(int index, int size) {
        int zeroBasedIndex = index - currentContext().getIndexOrigin();
        if (zeroBasedIndex < 0 || zeroBasedIndex >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return zeroBasedIndex;
    }

    public int[] toZeroBasedIndices(int[] indices, int[] shape) {
        int[] zeroBasedIndices = indices.clone();
        int indexOrigin = currentContext().getIndexOrigin();
        for (int i = 0; i < zeroBasedIndices.length; i++) {
            zeroBasedIndices[i] -= indexOrigin;
            if (zeroBasedIndices[i] < 0 || zeroBasedIndices[i] >= shape[i]) {
                throw new IndexOutOfBoundsException(
                    String.format("Index [%s] out of bounds for shape %s",
                        java.util.Arrays.toString(indices), java.util.Arrays.toString(shape))
                );
            }
        }
        return zeroBasedIndices;
    }

    public long toOriginIndex(long zeroBasedIndex) {
        return zeroBasedIndex + currentContext().getIndexOrigin();
    }

    public boolean areClose(double left, double right) {
        double tolerance = currentContext().getComparisonTolerance();
        return Math.abs(left - right) <= tolerance;
    }

    public String format(APLType value) {
        Objects.requireNonNull(value, "Value cannot be null");
        if (value instanceof ArrayType array) {
            StringBuilder builder = new StringBuilder("[");
            for (int i = 0; i < array.size(); i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(format(array.getRawElement(i)));
            }
            builder.append("]");
            return applyWidth(builder.toString());
        }
        if (value instanceof ComplexType complex) {
            return formatComplex(complex);
        }
        if (value instanceof FloatingPointType floatingPoint) {
            return applyWidth(formatDecimal(BigDecimal.valueOf(floatingPoint.getValue())));
        }
        if (value instanceof BigDecimalType bigDecimal) {
            return applyWidth(formatDecimal(bigDecimal.getValue()));
        }
        if (value instanceof Scalar) {
            return applyWidth(value.toString());
        }
        return applyWidth(value.toString());
    }

    private String formatComplex(ComplexType complex) {
        double tolerance = currentContext().getComparisonTolerance();
        if (Math.abs(complex.getImaginary()) <= tolerance) {
            return applyWidth(formatDecimal(BigDecimal.valueOf(complex.getReal())));
        }

        String real = formatDecimal(BigDecimal.valueOf(complex.getReal()));
        String imaginary = formatDecimal(BigDecimal.valueOf(Math.abs(complex.getImaginary())));
        String sign = complex.getImaginary() >= 0 ? "+" : "-";
        return applyWidth(real + sign + imaginary + "i");
    }

    private String formatDecimal(BigDecimal value) {
        int precision = currentContext().getPrintPrecision();
        if (precision >= 0) {
            value = value.setScale(precision, RoundingMode.HALF_UP);
        }
        value = value.stripTrailingZeros();
        if (value.scale() < 0) {
            value = value.setScale(0, RoundingMode.UNNECESSARY);
        }
        return value.toPlainString();
    }

    private String applyWidth(String value) {
        int width = currentContext().getPrintWidth();
        if (width <= value.length()) {
            return value;
        }
        return String.format("%" + width + "s", value);
    }
}
