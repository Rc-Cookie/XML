package de.rccookie.css.functions;

import java.util.List;

import de.rccookie.util.Arguments;
import de.rccookie.util.Utils;

public final class Var extends CSSFunction {
    private final String propertyName;
    private final List<? extends String> fallback;

    public Var(String propertyName, List<? extends String> fallback) {
        this.propertyName = Arguments.checkNull(propertyName, "propertyName");
        this.fallback = List.copyOf(Arguments.checkNull(fallback, "fallback"));
    }

    @Override
    public String getFunctionName() {
        return "var";
    }

    @Override
    public List<?> getParams() {
        return Utils.view(List.of(propertyName), fallback);
    }

    public String getPropertyName() {
        return propertyName;
    }

    public List<?> getFallback() {
        return fallback;
    }
}
