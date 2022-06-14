package com.github.rccookie.xml;

/**
 * Thrown to indicate that an attempt was made to create a cycle reference in a node,
 * meaning that a node is its own child.
 */
public class NestedXMLNodeException extends RuntimeException {

    /**
     * Creates a new NestedXMLNodeException.
     */
    public NestedXMLNodeException() {
        super("XML nodes cannot be nested");
    }
}
