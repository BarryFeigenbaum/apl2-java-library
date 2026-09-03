package com.apl2.operations;

import com.apl2.types.APLType;
import com.apl2.types.ArrayType;
import com.apl2.types.IntegerType;
import com.apl2.functions.DyadicFunction;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

public class AxisOperationsTest {

    @Test
    public void testReverseAndRotateWithAxis() {
        ArrayType matrix = new ArrayType(Arrays.asList(
            new IntegerType(1), new IntegerType(2), new IntegerType(3),
            new IntegerType(4), new IntegerType(5), new IntegerType(6)
        ), 2, 3);

        ArrayType reverseLastAxis = ArrayOperations.reverse(matrix, null);
        assertThat(((IntegerType) reverseLastAxis.getElement(0)).getValue()).isEqualTo(3);
        assertThat(((IntegerType) reverseLastAxis.getElement(2)).getValue()).isEqualTo(1);
        assertThat(((IntegerType) reverseLastAxis.getElement(3)).getValue()).isEqualTo(6);

        ArrayType reverseFirstAxis = ArrayOperations.reverse(matrix, 0);
        assertThat(((IntegerType) reverseFirstAxis.getElement(0)).getValue()).isEqualTo(4);
        assertThat(((IntegerType) reverseFirstAxis.getElement(5)).getValue()).isEqualTo(3);

        ArrayType rotateLastAxis = ArrayOperations.rotate(matrix, 1, -1);
        assertThat(((IntegerType) rotateLastAxis.getElement(0)).getValue()).isEqualTo(3);
        assertThat(((IntegerType) rotateLastAxis.getElement(1)).getValue()).isEqualTo(1);
    }

    @Test
    public void testReduceWithAxis() {
        ArrayType matrix = new ArrayType(Arrays.asList(
            new IntegerType(1), new IntegerType(2), new IntegerType(3),
            new IntegerType(4), new IntegerType(5), new IntegerType(6)
        ), 2, 3);

        DyadicFunction add = MathOperations::add;
        APLType reducedRows = ReductionOperations.reduce(matrix, add, null);
        assertThat(((ArrayType) reducedRows).getShape()).containsExactly(2);
        assertThat(((IntegerType) ((ArrayType) reducedRows).getElement(0)).getValue()).isEqualTo(6);
        assertThat(((IntegerType) ((ArrayType) reducedRows).getElement(1)).getValue()).isEqualTo(15);

        APLType reducedCols = ReductionOperations.reduce(matrix, add, 0);
        assertThat(((ArrayType) reducedCols).getShape()).containsExactly(3);
        assertThat(((IntegerType) ((ArrayType) reducedCols).getElement(0)).getValue()).isEqualTo(5);
        assertThat(((IntegerType) ((ArrayType) reducedCols).getElement(2)).getValue()).isEqualTo(9);
    }

    @Test
    public void testScanWithAxis() {
        ArrayType matrix = new ArrayType(Arrays.asList(
            new IntegerType(1), new IntegerType(2), new IntegerType(3),
            new IntegerType(4), new IntegerType(5), new IntegerType(6)
        ), 2, 3);

        DyadicFunction add = MathOperations::add;
        ArrayType scanRows = (ArrayType) ScanOperations.scan(matrix, add, 1);
        assertThat(scanRows.getShape()).containsExactly(2, 3);
        assertThat(((IntegerType) scanRows.getElement(0)).getValue()).isEqualTo(1);
        assertThat(((IntegerType) scanRows.getElement(1)).getValue()).isEqualTo(3);
        assertThat(((IntegerType) scanRows.getElement(2)).getValue()).isEqualTo(6);
        assertThat(((IntegerType) scanRows.getElement(5)).getValue()).isEqualTo(15);

        ArrayType scanCols = (ArrayType) ScanOperations.scan(matrix, add, 0);
        assertThat(((IntegerType) scanCols.getElement(3)).getValue()).isEqualTo(5);
        assertThat(((IntegerType) scanCols.getElement(4)).getValue()).isEqualTo(7);
        assertThat(((IntegerType) scanCols.getElement(5)).getValue()).isEqualTo(9);
    }

    @Test
    public void testTransposeAxesAndConcatenateAxis() {
        ArrayType matrix = new ArrayType(Arrays.asList(
            new IntegerType(1), new IntegerType(2), new IntegerType(3),
            new IntegerType(4), new IntegerType(5), new IntegerType(6)
        ), 2, 3);
        ArrayType transposed = ArrayOperations.transposeAxes(matrix, 1, 0);
        assertThat(transposed.getShape()).containsExactly(3, 2);
        assertThat(((IntegerType) transposed.getElement(0)).getValue()).isEqualTo(1);
        assertThat(((IntegerType) transposed.getElement(1)).getValue()).isEqualTo(4);

        ArrayType appended = ArrayOperations.concatenate(matrix, matrix, 0);
        assertThat(appended.getShape()).containsExactly(4, 3);
        assertThat(((IntegerType) appended.getElement(6)).getValue()).isEqualTo(1);

        ArrayType appendedLastAxis = ArrayOperations.concatenate(matrix, matrix, null);
        assertThat(appendedLastAxis.getShape()).containsExactly(2, 6);
        assertThat(((IntegerType) appendedLastAxis.getElement(0)).getValue()).isEqualTo(1);
        assertThat(((IntegerType) appendedLastAxis.getElement(3)).getValue()).isEqualTo(1);
    }

    @Test
    public void testFirstAndSplitOnMatrix() {
        ArrayType matrix = new ArrayType(Arrays.asList(
            new IntegerType(1), new IntegerType(2), new IntegerType(3),
            new IntegerType(4), new IntegerType(5), new IntegerType(6)
        ), 2, 3);

        ArrayType firstRow = (ArrayType) ArrayOperations.first(matrix);
        assertThat(firstRow.getShape()).containsExactly(3);
        assertThat(((IntegerType) firstRow.getElement(0)).getValue()).isEqualTo(1);
        assertThat(((IntegerType) firstRow.getElement(2)).getValue()).isEqualTo(3);

        ArrayType split = ArrayOperations.split(matrix);
        assertThat(split.getShape()).containsExactly(1, 3);
        assertThat(((IntegerType) split.getElement(0)).getValue()).isEqualTo(4);
    }
}
