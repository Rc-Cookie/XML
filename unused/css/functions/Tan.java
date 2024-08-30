package de.rccookie.css.functions;

import java.util.List;

import de.rccookie.css.types.Angle;

public final class Tan extends CSSFunction {

    private final Angle param;

    public Tan(Angle param) {
        this.param = param;
    }

    public Tan(double radians) {
        this(Angle.ofRadians(radians));
    }

    @Override
    public String getFunctionName() {
        return "tan";
    }

    @Override
    public List<?> getParams() {
        return List.of(param);
    }

    public Angle getParam() {
        return param;
    }
}
