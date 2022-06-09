package com.github.rccookie.xml;

import java.io.StringReader;

public final class XML {

    private XML() {
        throw new UnsupportedOperationException();
    }


    public static XMLParser getParser(String xml) {
        return getParser(xml, 0);
    }

    public static XMLParser getParser(String xml, long options) {
        return new XMLParser(new StringReader(xml), options);
    }
}
