package de.rccookie.css.types;

public class _LengthImpl {

    public static final _LengthImpl ZERO = new _LengthImpl(0, Length.Unit.PX);

    private final double value;
    private final Length.Unit unit;

    public _LengthImpl(double value, Length.Unit unit) {
        this.value = value;
        this.unit = unit;
    }

    @Override
    public String toString() {
        if((long) (value * 1000) == 0) return "0";
        return value+""+unit;
    }

    public double getValue() {
        return value;
    }

    public Length.Unit getUnit() {
        return unit;
    }
}
