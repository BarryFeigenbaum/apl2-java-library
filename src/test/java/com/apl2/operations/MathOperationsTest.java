package com.apl2.operations;

import com.apl2.types.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class MathOperationsTest {

    @Test
    public void testAdd() {
        IntegerType a = new IntegerType(5);
        IntegerType b = new IntegerType(3);
        APLType result = MathOperations.add(a, b);
        
        assertThat(((IntegerType) result).getValue()).isEqualTo(8);
    }

    @Test
    public void testSubtract() {
        IntegerType a = new IntegerType(10);
        IntegerType b = new IntegerType(3);
        APLType result = MathOperations.subtract(a, b);
        
        assertThat(((IntegerType) result).getValue()).isEqualTo(7);
    }

    @Test
    public void testMultiply() {
        IntegerType a = new IntegerType(6);
        IntegerType b = new IntegerType(7);
        APLType result = MathOperations.multiply(a, b);
        
        assertThat(((IntegerType) result).getValue()).isEqualTo(42);
    }

    @Test
    public void testDivide() {
        IntegerType a = new IntegerType(20);
        IntegerType b = new IntegerType(4);
        APLType result = MathOperations.divide(a, b);
        
        assertThat(((FloatingPointType) result).getValue()).isEqualTo(5.0);
    }

    @Test
    public void testDivideByZero() {
        IntegerType a = new IntegerType(5);
        IntegerType b = new IntegerType(0);
        
        assertThatThrownBy(() -> MathOperations.divide(a, b))
            .isInstanceOf(ArithmeticException.class);
    }

    @Test
    public void testModulo() {
        IntegerType a = new IntegerType(17);
        IntegerType b = new IntegerType(5);
        APLType result = MathOperations.modulo(a, b);
        
        assertThat(((IntegerType) result).getValue()).isEqualTo(2);
    }

    @Test
    public void testPower() {
        IntegerType a = new IntegerType(2);
        IntegerType b = new IntegerType(8);
        APLType result = MathOperations.power(a, b);
        
        assertThat(((FloatingPointType) result).getValue()).isEqualTo(256.0);
    }

    @Test
    public void testNegate() {
        IntegerType num = new IntegerType(42);
        APLType result = MathOperations.negate(num);
        
        assertThat(((IntegerType) result).getValue()).isEqualTo(-42);
    }

    @Test
    public void testAbs() {
        IntegerType num = new IntegerType(-42);
        APLType result = MathOperations.abs(num);
        
        assertThat(((IntegerType) result).getValue()).isEqualTo(42);
    }

    @Test
    public void testSqrt() {
        FloatingPointType num = new FloatingPointType(16.0);
        APLType result = MathOperations.sqrt(num);
        
        assertThat(((FloatingPointType) result).getValue()).isEqualTo(4.0);
    }

    @Test
    public void testSin() {
        FloatingPointType num = new FloatingPointType(0.0);
        APLType result = MathOperations.sin(num);
        
        assertThat(((FloatingPointType) result).getValue()).isCloseTo(0.0, within(0.0001));
    }

    @Test
    public void testCos() {
        FloatingPointType num = new FloatingPointType(0.0);
        APLType result = MathOperations.cos(num);
        
        assertThat(((FloatingPointType) result).getValue()).isCloseTo(1.0, within(0.0001));
    }

    @Test
    public void testMax() {
        IntegerType a = new IntegerType(5);
        IntegerType b = new IntegerType(10);
        APLType result = MathOperations.max(a, b);
        
        assertThat(((FloatingPointType) result).getValue()).isEqualTo(10.0);
    }

    @Test
    public void testMin() {
        IntegerType a = new IntegerType(5);
        IntegerType b = new IntegerType(10);
        APLType result = MathOperations.min(a, b);
        
        assertThat(((FloatingPointType) result).getValue()).isEqualTo(5.0);
    }
}
