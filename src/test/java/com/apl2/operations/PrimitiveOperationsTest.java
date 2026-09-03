package com.apl2.operations;

import com.apl2.types.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

public class PrimitiveOperationsTest {

    @Test
    public void testConjugateReciprocalAndLogarithm() {
        ComplexType complex = new ComplexType(2.0, 3.0);
        APLType conjugate = MathOperations.conjugate(complex);
        assertThat(((ComplexType) conjugate).getReal()).isEqualTo(2.0);
        assertThat(((ComplexType) conjugate).getImaginary()).isEqualTo(-3.0);

        APLType reciprocal = MathOperations.reciprocal(new IntegerType(4));
        assertThat(((FloatingPointType) reciprocal).getValue()).isEqualTo(0.25);

        APLType logBase2Of8 = MathOperations.logarithm(new IntegerType(2), new IntegerType(8));
        assertThat(((FloatingPointType) logBase2Of8).getValue()).isEqualTo(3.0);
    }

    @Test
    public void testFactorialAndBinomial() {
        APLType factorial = MathOperations.factorial(new IntegerType(5));
        assertThat(((IntegerType) factorial).getValue()).isEqualTo(120);

        APLType binomial = MathOperations.binomial(new IntegerType(5), new IntegerType(2));
        assertThat(((IntegerType) binomial).getValue()).isEqualTo(10);
    }

    @Test
    public void testComparisonAndLogical() {
        assertThat(((BooleanType) MathOperations.equal(new IntegerType(7), new IntegerType(7))).getValue()).isTrue();
        assertThat(((BooleanType) MathOperations.greaterEqual(new IntegerType(7), new IntegerType(3))).getValue()).isTrue();
        assertThat(((BooleanType) PrimitiveOperations.logicalAnd(new BooleanType(true), new BooleanType(false))).getValue()).isFalse();
        assertThat(((BooleanType) PrimitiveOperations.logicalNor(new BooleanType(false), new BooleanType(false))).getValue()).isTrue();
    }

    @Test
    public void testIndexingAndSelection() {
        ArrayType source = new ArrayType(Arrays.asList(new IntegerType(10), new IntegerType(20), new IntegerType(30)));
        ArrayType query = new ArrayType(Arrays.asList(new IntegerType(20), new IntegerType(40)));
        ArrayType indexOf = (ArrayType) PrimitiveOperations.indexOf(source, query);
        assertThat(((IntegerType) indexOf.getElement(0)).getValue()).isEqualTo(1);
        assertThat(((IntegerType) indexOf.getElement(1)).getValue()).isEqualTo(3);

        ArrayType mask = new ArrayType(Arrays.asList(new BooleanType(false), new BooleanType(true), new BooleanType(true)));
        ArrayType where = (ArrayType) PrimitiveOperations.where(mask, source);
        assertThat(where.size()).isEqualTo(2);
        assertThat(((IntegerType) where.getElement(0)).getValue()).isEqualTo(20);
        assertThat(((IntegerType) where.getElement(1)).getValue()).isEqualTo(30);
    }

    @Test
    public void testArrayMembershipAndGrading() {
        ArrayType left = new ArrayType(Arrays.asList(new IntegerType(2), new IntegerType(5), new IntegerType(1)));
        ArrayType right = new ArrayType(Arrays.asList(new IntegerType(1), new IntegerType(2), new IntegerType(3)));
        ArrayType membership = ArrayOperations.membership(left, right);
        assertThat(((BooleanType) membership.getElement(0)).getValue()).isTrue();
        assertThat(((BooleanType) membership.getElement(1)).getValue()).isFalse();

        ArrayType gradeUp = ArrayOperations.gradeUp(left);
        assertThat(((IntegerType) gradeUp.getElement(0)).getValue()).isEqualTo(2);
        assertThat(((IntegerType) gradeUp.getElement(1)).getValue()).isEqualTo(0);
        assertThat(((IntegerType) gradeUp.getElement(2)).getValue()).isEqualTo(1);
    }

    @Test
    public void testRandomAndFormat() {
        int roll = (int) ((IntegerType) PrimitiveOperations.roll(new IntegerType(6))).getValue();
        assertThat(roll).isBetween(1, 6);

        ArrayType dealt = (ArrayType) PrimitiveOperations.deal(new IntegerType(4), new IntegerType(8));
        assertThat(dealt.size()).isEqualTo(4);
        Set<Long> unique = new HashSet<>();
        for (APLType value : dealt.getElements()) {
            long n = ((IntegerType) value).getValue();
            assertThat(n).isBetween(1L, 8L);
            unique.add(n);
        }
        assertThat(unique).hasSize(4);

        StringType formatted = (StringType) PrimitiveOperations.formatWithPattern(new StringType("0.00"), new FloatingPointType(3.14159));
        assertThat(formatted.getValue()).isEqualTo("3.14");
    }

    @Test
    public void testCircleAndIotaAndWithout() {
        APLType sinHalfPi = MathOperations.circle(new IntegerType(1), new FloatingPointType(Math.PI / 2));
        assertThat(((FloatingPointType) sinHalfPi).getValue()).isCloseTo(1.0, within(1e-9));

        ArrayType iota = (ArrayType) PrimitiveOperations.iota(new IntegerType(4));
        assertThat(((IntegerType) iota.getElement(0)).getValue()).isEqualTo(0);
        assertThat(((IntegerType) iota.getElement(3)).getValue()).isEqualTo(3);

        ArrayType left = new ArrayType(List.of(new IntegerType(1), new IntegerType(2), new IntegerType(3)));
        ArrayType right = new ArrayType(List.of(new IntegerType(2)));
        ArrayType without = (ArrayType) PrimitiveOperations.without(left, right);
        assertThat(without.size()).isEqualTo(2);
        assertThat(((IntegerType) without.getElement(0)).getValue()).isEqualTo(1);
        assertThat(((IntegerType) without.getElement(1)).getValue()).isEqualTo(3);
    }
}
