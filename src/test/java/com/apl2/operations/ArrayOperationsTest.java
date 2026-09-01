package com.apl2.operations;

import com.apl2.types.*;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

public class ArrayOperationsTest {

    @Test
    public void testSum() {
        List<APLType> elements = Arrays.asList(
            new IntegerType(1),
            new IntegerType(2),
            new IntegerType(3)
        );
        ArrayType array = new ArrayType(elements);
        APLType result = ArrayOperations.sum(array);
        
        assertThat(((IntegerType) result).getValue()).isEqualTo(6);
    }

    @Test
    public void testProduct() {
        List<APLType> elements = Arrays.asList(
            new IntegerType(2),
            new IntegerType(3),
            new IntegerType(4)
        );
        ArrayType array = new ArrayType(elements);
        APLType result = ArrayOperations.product(array);
        
        assertThat(((IntegerType) result).getValue()).isEqualTo(24);
    }

    @Test
    public void testMaximum() {
        List<APLType> elements = Arrays.asList(
            new IntegerType(3),
            new IntegerType(1),
            new IntegerType(4),
            new IntegerType(1),
            new IntegerType(5)
        );
        ArrayType array = new ArrayType(elements);
        APLType result = ArrayOperations.maximum(array);
        
        assertThat(((FloatingPointType) result).getValue()).isEqualTo(5.0);
    }

    @Test
    public void testMinimum() {
        List<APLType> elements = Arrays.asList(
            new IntegerType(3),
            new IntegerType(1),
            new IntegerType(4),
            new IntegerType(1),
            new IntegerType(5)
        );
        ArrayType array = new ArrayType(elements);
        APLType result = ArrayOperations.minimum(array);
        
        assertThat(((FloatingPointType) result).getValue()).isEqualTo(1.0);
    }

    @Test
    public void testReverse() {
        List<APLType> elements = Arrays.asList(
            new IntegerType(1),
            new IntegerType(2),
            new IntegerType(3)
        );
        ArrayType array = new ArrayType(elements);
        ArrayType reversed = ArrayOperations.reverse(array);
        
        assertThat(((IntegerType) reversed.getElement(0)).getValue()).isEqualTo(3);
        assertThat(((IntegerType) reversed.getElement(2)).getValue()).isEqualTo(1);
    }

    @Test
    public void testRotate() {
        List<APLType> elements = Arrays.asList(
            new IntegerType(1),
            new IntegerType(2),
            new IntegerType(3)
        );
        ArrayType array = new ArrayType(elements);
        ArrayType rotated = ArrayOperations.rotate(array, 1);
        
        assertThat(((IntegerType) rotated.getElement(0)).getValue()).isEqualTo(3);
        assertThat(((IntegerType) rotated.getElement(1)).getValue()).isEqualTo(1);
        assertThat(((IntegerType) rotated.getElement(2)).getValue()).isEqualTo(2);
    }

    @Test
    public void testCount() {
        List<APLType> elements = Arrays.asList(
            new BooleanType(true),
            new BooleanType(false),
            new BooleanType(true)
        );
        ArrayType array = new ArrayType(elements);
        long count = ArrayOperations.count(array);
        
        assertThat(count).isEqualTo(2);
    }
}
