package de.rccookie.css.functions;

import java.util.List;

import de.rccookie.util.Arguments;

public final class Url extends CSSFunction {

    private final String url;

    public Url(String url) {
        this.url = Arguments.checkNull(url, "url");
    }

    @Override
    public String getFunctionName() {
        return "url";
    }

    @Override
    public List<?> getParams() {
        return List.of(url);
    }

    public String getUrl() {
        return url;
    }
}
