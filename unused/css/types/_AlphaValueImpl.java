package de.rccookie.css.types;

import de.rccookie.util.Arguments;

public final class _AlphaValueImpl extends Number {

    private final double value;

    public _AlphaValueImpl(double value) {
        this.value = Arguments.checkInclusive(value, 0.0, 1.0);
    }

    @Override
    public String toString() {
        return NumberUtils.toString(value);
    }

    @Override
    public double getValue() {
        return value;
    }
}
