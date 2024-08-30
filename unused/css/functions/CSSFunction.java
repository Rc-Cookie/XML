package de.rccookie.css.functions;

import java.util.List;
import java.util.Objects;

public abstract class CSSFunction {

    public abstract String getFunctionName();

    public abstract List<?> getParams();

    @Override
    public String toString() {
        return FunctionUtils.toString(getFunctionName(), getParams());
    }

    @Override
    public boolean equals(Object obj) {
        return getClass() == obj.getClass() && getFunctionName().equals(((CSSFunction) obj).getFunctionName()) &&
               getParams().equals(((CSSFunction) obj).getParams());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFunctionName(), getParams());
    }
}
