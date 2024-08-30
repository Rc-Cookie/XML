package de.rccookie.css.types;

import de.rccookie.util.Arguments;

public final class _PercentageImpl extends Number {

    private final OldNumber number;

    private _PercentageImpl(OldNumber value) {
        number = Arguments.checkNull(value, "value");
    }

    @Override
    public String toString() {
        return OldNumber.toString(getValue()) + "%";
    }

    @Override
    public double getValue() {
        return number.getValue() / 100;
    }

    public double getPercent() {
        return number.getValue();
    }

    public static _PercentageImpl zero() {
        return new _PercentageImpl(OldNumber.zero());
    }

    public static _PercentageImpl fromPercentage(double percentage) {
        return new _PercentageImpl(new OldNumber(percentage));
    }

    public static _PercentageImpl fromPercentage(Number percentage) {
        return fromPercentage(percentage.getValue());
    }

    public static _PercentageImpl fromValue(double value) {
        return new _PercentageImpl(new OldNumber(value * 100));
    }

    public static _PercentageImpl fromValue(Number value) {
        return fromValue(value.getValue());
    }

    public static _PercentageImpl parse(String css) {
        if(!css.endsWith("%")) {
            if(css.equals("0") || css.equals("0.0") || css.equals("0.") || css.equals(".0"))
                return new _PercentageImpl(new OldNumber(0));
            throw new NumberFormatException(css);
        }
        return new _PercentageImpl(OldNumber.parse(css.substring(0, css.length() - 1)));
    }
}
