package de.rccookie.xml;

import java.util.NoSuchElementException;

/**
 * Thrown to indicate that the xml input was not valid xml syntax for
 * the specified parsing flags.
 */
public class XMLParseException extends NoSuchElementException {

    /**
     * Location of the parsing error.
     */
    private final int row, column;

    /**
     * Creates a new xml parsing exception.
     *
     * @param message The exception message
     */
    public XMLParseException(String message) {
        super(message);
        row = column = 0;
    }

    /**
     * Creates a new xml parsing exception for the given xml reader.
     *
     * @param message The error message
     * @param xml The xml parser that contains the error source
     */
    XMLParseException(String message, XMLReader xml) {
        super(message + " (at " + xml.getPosition() + ')');
        row = xml.line;
        column = xml.charIndex;
    }

    /**
     * Creates a new xml parsing exception for the given incorrect object.
     *
     * @param expected The object expected to be found
     * @param found What was actually found
     * @param xml The xml parser that contains the error source
     */
    XMLParseException(Object expected, Object found, XMLReader xml) {
        this("Expected '" + expected + "', found '" + found + "'", xml);
    }

    /**
     * Returns the row in the input source where the error occurred.
     * 1-indexed, a value of 0 indicates that the error position is not
     * available.
     *
     * @return The error row (line number)
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column in the input source where the error occurred.
     * 1-indexed, a value of 0 indicates that the error position is not
     * available.
     *
     * @return The error column (number of character)
     */
    public int getColumn() {
        return column;
    }

    /**
     * Returns a string in the form <code>row:column</code> describing
     * where the error occurred.1-indexed, a value of 0 indicates that
     * the error position is not available.
     *
     * @return The error position as string
     */
    public String getPosition() {
        return row + ":" + column;
    }
}
