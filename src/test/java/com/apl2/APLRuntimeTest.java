package com.apl2;

import com.apl2.operations.ArrayOperations;
import com.apl2.operations.MathOperations;
import com.apl2.operations.PrimitiveOperations;
import com.apl2.types.APLType;
import com.apl2.types.ArrayType;
import com.apl2.types.BigDecimalType;
import com.apl2.types.BooleanType;
import com.apl2.types.ComplexType;
import com.apl2.types.FloatingPointType;
import com.apl2.types.IntegerType;
import com.apl2.types.StringType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class APLRuntimeTest {
    private final APLRuntime runtime = APLRuntime.getInstance();

    @AfterEach
    void tearDown() {
        runtime.destroyContext();
    }

    @Test
    public void testContextStackLifecycle() {
        APLContext defaultContext = runtime.createContext();
        assertThat(defaultContext.getIndexOrigin()).isEqualTo(0);
        assertThat(defaultContext.getPrintWidth()).isEqualTo(0);
        assertThat(defaultContext.getPrintPrecision()).isEqualTo(-1);
        assertThat(defaultContext.getComparisonTolerance()).isEqualTo(1e-15);
        assertThat(runtime.currentContext()).isSameAs(APLContext.defaultContext());

        APLContext outer = APLContext.builder().indexOrigin(1).comparisonTolerance(0.01).build();
        APLContext inner = outer.toBuilder().printPrecision(3).build();

        runtime.pushContext(outer);
        assertThat(runtime.currentContext()).isSameAs(outer);

        runtime.pushContext(inner);
        assertThat(runtime.currentContext()).isSameAs(inner);

        assertThat(runtime.popContext()).isSameAs(inner);
        assertThat(runtime.currentContext()).isSameAs(outer);
        assertThat(runtime.popContext()).isSameAs(outer);
        assertThat(runtime.currentContext()).isSameAs(APLContext.defaultContext());

        assertThatThrownBy(runtime::popContext).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void testArrayIndexingRespectsRuntimeIndexOrigin() {
        runtime.pushContext(APLContext.builder().indexOrigin(1).build());

        ArrayType vector = new ArrayType(List.of(new IntegerType(10), new IntegerType(20), new IntegerType(30)));
        assertThat(((IntegerType) vector.getElement(1)).getValue()).isEqualTo(10);
        assertThat(((IntegerType) vector.getElement(3)).getValue()).isEqualTo(30);

        ArrayType matrix = new ArrayType(
            List.of(new IntegerType(1), new IntegerType(2), new IntegerType(3), new IntegerType(4)),
            2, 2
        );
        assertThat(((IntegerType) matrix.getElement(1, 2)).getValue()).isEqualTo(2);
        assertThat(((IntegerType) ArrayOperations.pick(new IntegerType(2), vector)).getValue()).isEqualTo(20);
    }

    @Test
    public void testIndexProducingOperationsRespectRuntimeIndexOrigin() {
        runtime.pushContext(APLContext.builder().indexOrigin(1).build());

        ArrayType iota = (ArrayType) PrimitiveOperations.iota(new IntegerType(4));
        assertThat(((IntegerType) iota.getRawElement(0)).getValue()).isEqualTo(1);
        assertThat(((IntegerType) iota.getRawElement(3)).getValue()).isEqualTo(4);

        ArrayType source = new ArrayType(List.of(new IntegerType(10), new IntegerType(20), new IntegerType(30)));
        ArrayType query = new ArrayType(List.of(new IntegerType(20), new IntegerType(40)));
        ArrayType indexOf = (ArrayType) PrimitiveOperations.indexOf(source, query);
        assertThat(((IntegerType) indexOf.getRawElement(0)).getValue()).isEqualTo(2);
        assertThat(((IntegerType) indexOf.getRawElement(1)).getValue()).isEqualTo(4);

        ArrayType indicesWhere = (ArrayType) PrimitiveOperations.indicesWhere(
            new ArrayType(List.of(new BooleanType(false), new BooleanType(true), new BooleanType(true)))
        );
        assertThat(((IntegerType) indicesWhere.getRawElement(0)).getValue()).isEqualTo(2);
        assertThat(((IntegerType) indicesWhere.getRawElement(1)).getValue()).isEqualTo(3);

        ArrayType gradeUp = ArrayOperations.gradeUp(
            new ArrayType(List.of(new IntegerType(2), new IntegerType(5), new IntegerType(1)))
        );
        assertThat(((IntegerType) gradeUp.getRawElement(0)).getValue()).isEqualTo(3);
        assertThat(((IntegerType) gradeUp.getRawElement(1)).getValue()).isEqualTo(1);
        assertThat(((IntegerType) gradeUp.getRawElement(2)).getValue()).isEqualTo(2);
    }

    @Test
    public void testFormattingRespectsPrecisionAndWidth() {
        runtime.pushContext(APLContext.builder().printPrecision(2).printWidth(6).build());

        StringType formattedFloating = (StringType) PrimitiveOperations.format(new FloatingPointType(3.14159));
        assertThat(formattedFloating.getValue()).isEqualTo("  3.14");

        StringType formattedComplex = (StringType) PrimitiveOperations.format(new ComplexType(2.345, -6.789));
        assertThat(formattedComplex.getValue()).isEqualTo("2.35-6.79i");

        StringType formattedBigDecimal = (StringType) PrimitiveOperations.format(new BigDecimalType(new BigDecimal("123.456")));
        assertThat(formattedBigDecimal.getValue()).isEqualTo("123.46");

        StringType formattedArray = (StringType) PrimitiveOperations.format(
            new ArrayType(List.of(new IntegerType(1), new FloatingPointType(2.345)))
        );
        assertThat(formattedArray.getValue()).isEqualTo("[     1,   2.35]");
    }

    @Test
    public void testEqualityRespectsComparisonTolerance() {
        APLType exactDefault = MathOperations.equal(new FloatingPointType(1.0), new FloatingPointType(1.005));
        assertThat(((BooleanType) exactDefault).getValue()).isFalse();

        runtime.pushContext(APLContext.builder().comparisonTolerance(0.01).build());

        APLType floatingEqual = MathOperations.equal(new FloatingPointType(1.0), new FloatingPointType(1.005));
        assertThat(((BooleanType) floatingEqual).getValue()).isTrue();

        APLType complexEqual = MathOperations.equal(
            new ComplexType(1.0, 2.0),
            new ComplexType(1.005, 1.995)
        );
        assertThat(((BooleanType) complexEqual).getValue()).isTrue();

        ArrayType left = new ArrayType(List.of(new FloatingPointType(1.0), new FloatingPointType(2.0)));
        ArrayType right = new ArrayType(List.of(new FloatingPointType(1.005), new FloatingPointType(1.995)));
        assertThat(((BooleanType) MathOperations.equal(left, right)).getValue()).isTrue();
    }
}
