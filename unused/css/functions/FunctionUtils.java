package de.rccookie.css.functions;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import de.rccookie.util.Utils;

final class FunctionUtils {

    private FunctionUtils() { }

    static String toString(String fnName, Object param1, List<?> moreParams) {
        return toString(fnName, Utils.view(List.of(param1), moreParams));
    }

    static String toString(String fnName, List<?> params) {
        return fnName + "(" + params.stream().map(Objects::toString).collect(Collectors.joining(", ")) + ")";
    }
}
