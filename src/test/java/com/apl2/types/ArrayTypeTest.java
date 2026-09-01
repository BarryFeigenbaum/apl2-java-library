package com.apl2.types;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

public class ArrayTypeTest {

    @Test
    public void testCreate1DArray() {
        List<APLType> elements = Arrays.asList(
            new IntegerType(1),
            new IntegerType(2),
            new IntegerType(3)
        );
        ArrayType array = new ArrayType(elements);
        
        assertThat(array.getRank()).isEqualTo(1);
        assertThat(array.getShape()).isEqualTo(new int[]{3});
        assertThat(array.size()).isEqualTo(3);
    }

    @Test
    public void testCreate2DArray() {
        List<APLType> elements = Arrays.asList(
            new IntegerType(1), new IntegerType(2),
            new IntegerType(3), new IntegerType(4)
        );
        ArrayType array = new ArrayType(elements, 2, 2);
        
        assertThat(array.getRank()).isEqualTo(2);
        assertThat(array.getShape()).isEqualTo(new int[]{2, 2});
    }

    @Test
    public void testGetElement() {
        List<APLType> elements = Arrays.asList(
            new IntegerType(10),
            new IntegerType(20),
            new IntegerType(30)
        );
        ArrayType array = new ArrayType(elements);
        
        assertThat(((IntegerType) array.getElement(0)).getValue()).isEqualTo(10);
        assertThat(((IntegerType) array.getElement(1)).getValue()).isEqualTo(20);
    }

    @Test
    public void testReshape() {
        List<APLType> elements = Arrays.asList(
            new IntegerType(1), new IntegerType(2),
            new IntegerType(3), new IntegerType(4)
        );
        ArrayType array = new ArrayType(elements);
        ArrayType reshaped = array.reshape(2, 2);
        
        assertThat(reshaped.getRank()).isEqualTo(2);
        assertThat(reshaped.getShape()).isEqualTo(new int[]{2, 2});
        assertThat(reshaped.size()).isEqualTo(4);
    }

    @Test
    public void testFlatten() {
        List<APLType> elements = Arrays.asList(
            new IntegerType(1), new IntegerType(2),
            new IntegerType(3), new IntegerType(4)
        );
        ArrayType array = new ArrayType(elements, 2, 2);
        ArrayType flattened = array.flatten();
        
        assertThat(flattened.getRank()).isEqualTo(1);
        assertThat(flattened.size()).isEqualTo(4);
    }

    @Test
    public void testTranspose() {
        List<APLType> elements = Arrays.asList(
            new IntegerType(1), new IntegerType(2),
            new IntegerType(3), new IntegerType(4)
        );
        ArrayType array = new ArrayType(elements, 2, 2);
        ArrayType transposed = array.transpose();
        
        assertThat(transposed.getShape()).isEqualTo(new int[]{2, 2});
    }

    @Test
    public void testArrayBuilder() {
        ArrayType array = new ArrayBuilder()
            .add(1L)
            .add(2L)
            .add(3L)
            .build();
        
        assertThat(array.size()).isEqualTo(3);
        assertThat(array.getRank()).isEqualTo(1);
    }
}
