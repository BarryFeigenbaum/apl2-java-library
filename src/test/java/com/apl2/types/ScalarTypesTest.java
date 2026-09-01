package com.apl2.types;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class ScalarTypesTest {

    @Test
    public void testBooleanType() {
        BooleanType trueVal = new BooleanType(true);
        BooleanType falseVal = new BooleanType(false);
        
        assertThat(trueVal.getValue()).isTrue();
        assertThat(falseVal.getValue()).isFalse();
        assertThat(trueVal.toNumeric()).isEqualTo(1.0);
        assertThat(falseVal.toNumeric()).isEqualTo(0.0);
        assertThat(trueVal.toBoolean()).isTrue();
        assertThat(trueVal.toCharacter()).isEqualTo('1');
    }

    @Test
    public void testIntegerType() {
        IntegerType num = new IntegerType(42);
        assertThat(num.getValue()).isEqualTo(42);
        assertThat(num.toNumeric()).isEqualTo(42.0);
        assertThat(num.toBoolean()).isTrue();
        assertThat(new IntegerType(0).toBoolean()).isFalse();
    }

    @Test
    public void testFloatingPointType() {
        FloatingPointType num = new FloatingPointType(3.14);
        assertThat(num.getValue()).isEqualTo(3.14);
        assertThat(num.toNumeric()).isEqualTo(3.14);
        assertThat(num.toBoolean()).isTrue();
        assertThat(new FloatingPointType(0.0).toBoolean()).isFalse();
    }

    @Test
    public void testComplexType() {
        ComplexType complex = new ComplexType(3.0, 4.0);
        assertThat(complex.getReal()).isEqualTo(3.0);
        assertThat(complex.getImaginary()).isEqualTo(4.0);
        assertThat(complex.toNumeric()).isEqualTo(5.0); // magnitude
        
        ComplexType sum = complex.add(new ComplexType(1.0, 2.0));
        assertThat(sum.getReal()).isEqualTo(4.0);
        assertThat(sum.getImaginary()).isEqualTo(6.0);
    }

    @Test
    public void testBigIntegerType() {
        BigIntegerType big = new BigIntegerType("999999999999999999999999999");
        assertThat(big.getValue().toString()).isEqualTo("999999999999999999999999999");
        assertThat(big.toBoolean()).isTrue();
        
        BigIntegerType sum = big.add(new BigIntegerType(1));
        assertThat(sum.getValue().toString()).isEqualTo("1000000000000000000000000000");
    }

    @Test
    public void testBigDecimalType() {
        BigDecimalType big = new BigDecimalType("123.456789123456789");
        assertThat(big.getValue().toString()).contains("123.456789");
        assertThat(big.toBoolean()).isTrue();
    }

    @Test
    public void testStringType() {
        StringType str = new StringType("hello");
        assertThat(str.getValue()).isEqualTo("hello");
        assertThat(str.toCharacter()).isEqualTo('h');
        assertThat(str.length()).isEqualTo(5);
    }
}
