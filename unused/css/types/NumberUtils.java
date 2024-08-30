package de.rccookie.css.types;

import java.util.regex.Pattern;

final class NumberUtils {

    private NumberUtils() { }

    private static final Pattern ZERO_PATTERN = Pattern.compile("[+-]?(?:0|0\\.|0?\\.0)(?:e[+-]?\\d+)?");

    static String toString(double value) {
        long valueX1000 = (long) (value * 1000);
        if(valueX1000 == (valueX1000 / 1000) * 1000)
            return (valueX1000 / 1000) + "";
        long fraction = Math.abs(valueX1000 % 1000);
        while(fraction == (fraction / 10) * 10) fraction /= 10;
        return (valueX1000 / 1000)+"."+fraction;
    }

    static boolean isZero(String css) {
        return ZERO_PATTERN.matcher(css).matches();
    }
}
