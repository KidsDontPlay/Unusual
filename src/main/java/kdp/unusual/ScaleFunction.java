package kdp.unusual;

import java.util.function.DoubleUnaryOperator;

public enum ScaleFunction {
    LINEAR(DoubleUnaryOperator.identity()),//
    EASE_IN(d -> Math.pow(d, 2.5)),//
    EASE_OUT(d -> Math.pow(d, 0.4)),//
    EASE_IN_OUT(d -> Math.pow(d, 2.5) / (Math.pow(d, 2.5) + Math.pow(1 - d, 2.5)));

    public final DoubleUnaryOperator function;

    ScaleFunction(DoubleUnaryOperator function) {
        this.function = function;
    }

}
