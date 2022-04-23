package com.github.rccookie.xml;

import java.util.NoSuchElementException;

public class XMLParseException extends NoSuchElementException {

    public XMLParseException(String message) {
        super(message);
    }

    XMLParseException(String message, XMLReader xml) {
        super(message + " (at " + xml.getPosition() + ')');
    }

    XMLParseException(Object expected, Object found, XMLReader xml) {
        this("Expected '" + expected + "', found '" + found + "'", xml);
    }
}
