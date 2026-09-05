package com.apl2;

/**
 * Immutable runtime context for APL evaluation.
 */
public final class APLContext {
    private static final int DEFAULT_INDEX_ORIGIN = 0;
    private static final int DEFAULT_PRINT_WIDTH = 0;
    private static final int DEFAULT_PRINT_PRECISION = -1;
    private static final double DEFAULT_COMPARISON_TOLERANCE = 1e-15;
    private static final APLContext DEFAULT_CONTEXT = new Builder().build();

    private final int indexOrigin;
    private final int printWidth;
    private final int printPrecision;
    private final double comparisonTolerance;

    private APLContext(Builder builder) {
        this.indexOrigin = builder.indexOrigin;
        this.printWidth = builder.printWidth;
        this.printPrecision = builder.printPrecision;
        this.comparisonTolerance = builder.comparisonTolerance;
    }

    public static APLContext defaultContext() {
        return DEFAULT_CONTEXT;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
            .indexOrigin(indexOrigin)
            .printWidth(printWidth)
            .printPrecision(printPrecision)
            .comparisonTolerance(comparisonTolerance);
    }

    public int getIndexOrigin() {
        return indexOrigin;
    }

    public int getPrintWidth() {
        return printWidth;
    }

    public int getPrintPrecision() {
        return printPrecision;
    }

    public double getComparisonTolerance() {
        return comparisonTolerance;
    }

    public static final class Builder {
        private int indexOrigin = DEFAULT_INDEX_ORIGIN;
        private int printWidth = DEFAULT_PRINT_WIDTH;
        private int printPrecision = DEFAULT_PRINT_PRECISION;
        private double comparisonTolerance = DEFAULT_COMPARISON_TOLERANCE;

        public Builder indexOrigin(int indexOrigin) {
            this.indexOrigin = indexOrigin;
            return this;
        }

        public Builder printWidth(int printWidth) {
            if (printWidth < 0) {
                throw new IllegalArgumentException("Print width must be non-negative");
            }
            this.printWidth = printWidth;
            return this;
        }

        public Builder printPrecision(int printPrecision) {
            if (printPrecision < -1) {
                throw new IllegalArgumentException("Print precision must be -1 or greater");
            }
            this.printPrecision = printPrecision;
            return this;
        }

        public Builder comparisonTolerance(double comparisonTolerance) {
            if (comparisonTolerance < 0) {
                throw new IllegalArgumentException("Comparison tolerance must be non-negative");
            }
            this.comparisonTolerance = comparisonTolerance;
            return this;
        }

        public APLContext build() {
            return new APLContext(this);
        }
    }
}
