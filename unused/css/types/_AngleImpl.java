package de.rccookie.css.types;

public final class _AngleImpl {

    public static final _AngleImpl ZERO = new _AngleImpl(0);

    private final double turns;

    private _AngleImpl(double turns) {
        this.turns = turns;
    }

    @Override
    public String toString() {
        if((long) (turns * 1000) == 0) return "0";
        return NumberUtils.toString(turns)+"turn";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof _AngleImpl && Double.compare(turns, ((_AngleImpl) obj).turns) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(turns);
    }

    public double getRadians() {
        return turns * 2 * Math.PI;
    }

    public double getDegrees() {
        return turns * 360;
    }

    public double getGradians() {
        return turns * 400;
    }

    public double getTurns() {
        return turns;
    }

    public static _AngleImpl fromRadians(double radians) {
        return new _AngleImpl(radians / (2 * Math.PI));
    }

    public static _AngleImpl fromDegrees(double degrees) {
        return new _AngleImpl(degrees / 360);
    }

    public static _AngleImpl fromGradians(double gradians) {
        return new _AngleImpl(gradians / 400);
    }

    public static _AngleImpl fromTurns(double turns) {
        return new _AngleImpl(turns);
    }

    @SuppressWarnings("DuplicateExpressions")
    public static _AngleImpl parse(String css) {
        if(css.endsWith("rad"))
            return fromRadians(Double.parseDouble(css.substring(0, css.length() - 3)));
        if(css.endsWith("deg"))
            return fromDegrees(Double.parseDouble(css.substring(0, css.length() - 3)));
        if(css.endsWith("grad"))
            return fromGradians(Double.parseDouble(css.substring(0, css.length() - 4)));
        if(css.endsWith("turn"))
            return fromTurns(Double.parseDouble(css.substring(0, css.length() - 4)));
        if(NumberUtils.isZero(css))
            return ZERO;
        throw new NumberFormatException(css);
    }

    public static _AngleImpl parseAnglePercentage(String css) {
        if(css.endsWith("%"))
            return fromTurns(Double.parseDouble(css.substring(0, css.length() - 1)));
        return parse(css);
    }
}
