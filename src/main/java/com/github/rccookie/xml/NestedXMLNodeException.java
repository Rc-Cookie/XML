package com.github.rccookie.xml;

public class NestedXMLNodeException extends RuntimeException {

    public NestedXMLNodeException() {
        super("XML nodes cannot be nested");
    }
}
