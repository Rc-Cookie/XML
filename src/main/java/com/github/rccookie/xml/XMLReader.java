package com.github.rccookie.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;

/**
 * Internal class to read from a {@link Reader} more conveniently.
 */
@SuppressWarnings({"UnusedReturnValue", "BooleanMethodIsAlwaysInverted", "SameParameterValue"})
class XMLReader implements AutoCloseable {

    /**
     * The underlying reader.
     */
    private final Reader reader;
    /**
     * Current position in the string.
     */
    private int line = 1, charIndex = 1;

    final boolean includeComments;
    final boolean trimWhitespaces;
    final boolean includeProcessors;
    final boolean allowEmptyAttr;
    final boolean tryFixErrors;
    final boolean html;


    /**
     * Creates a new xml reader using the given reader.
     *
     * @param reader The reader to use
     */
    XMLReader(Reader reader, long options) {
        this.reader = reader.markSupported() ? reader : new BufferedReader(reader);
        includeComments = (options & XML.INCLUDE_COMMENTS) != 0;
        trimWhitespaces = (options & XML.PRESERVE_WHITESPACES) == 0;
        includeProcessors = (options & XML.INCLUDE_PROCESSORS) != 0;
        allowEmptyAttr = (options & XML.ALLOW_EMPTY_ATTR) != 0;
        tryFixErrors = (options & XML.TRY_FIX_ERRORS) != 0;
        html = (options & XML.HTML) != 0;
    }



    @Override
    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Returns the first index of the given char, or {@code -1} if this reader
     * does not contain the specified character.
     *
     * @param c The character to search for
     * @return First index of the character or {@code -1}
     */
    int indexOf(char c) {
        try {
            for(int i=0;;i++) {
                reader.mark(i+1);
                reader.skip(i);
                int d = reader.read();
                reader.reset();
                if(d == c) return i;
                if(d == -1) return -1;
            }
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Returns the first index of the given string, or {@code -1} if this reader
     * does not contain the specified character.
     *
     * @param string The string to search for
     * @return First index of the string or {@code -1}
     */
    int indexOf(String string) {
        if(string.isEmpty()) return 0;
        try {
            iLoop: for(int i=0;;i++) {
                reader.mark(i+1);
                reader.skip(i);
                int d = reader.read();
                if(d == string.charAt(0)) {
                    for(int j=1; j<string.length(); j++) {
                        if(string.charAt(j) != reader.read()) {
                            reader.reset();
                            continue iLoop;
                        }
                    }
                    reader.reset();
                    return i;
                }
                reader.reset();
                if(d == -1) return -1;
            }
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }



    @Override
    public String toString() {
        return "XMLReader{" + reader + "} at " + getPosition();
    }

    /**
     * Returns a string with the current position in the reader, in the form
     * {@code line:char}.
     *
     * @return The current position in the reader
     */
    String getPosition() {
        return line + ":" + charIndex;
    }


    /**
     * Returns {@code true} if the underlying reader has no more characters.
     *
     * @return If no more characters are available
     */
    boolean isEmpty() {
        try {
            reader.mark(1);
            boolean empty = reader.read() == -1;
            reader.reset();
            return empty;
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Skips all whitespaces at the start of the reader as specified in
     * {@link Character#isWhitespace(char)}. If the {@link XML#PRESERVE_WHITESPACES}
     * option is enabled this does nothing.
     *
     * @return This xml reader
     */
    XMLReader skipWhitespaces(boolean force) {
        if(!force && !trimWhitespaces) return this;
        try {
            reader.mark(1);
            int c;
            while(Character.isWhitespace(c = reader.read())) {
                countRead((char)c);
                reader.mark(1);
            }
            reader.reset();
            return this;
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    XMLReader skipWhitespace() {
        char c = read();
        if(!Character.isWhitespace(c))
            throw new XMLParseException("<whitespace>", c, this);
        return this;
    }

    char peekNextNonWhitespace() {
        try {
            int c = peek();
            int i = 2;
            while(Character.isWhitespace(c)) {
                reader.mark(i);
                reader.skip(i / 2);
                for (int j = i / 2; j < i; j++) {
                    c = reader.read();
                    if(c == -1) {
                        reader.reset();
                        throw new XMLParseException("Reached end of file during parsing", this);
                    }
                    if(!Character.isWhitespace(c)) break;
                }
                reader.reset();
                i *= 2;
            }
            return (char) c;
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Skips the specified number of characters.
     *
     * @param count The number of characters to be skipped
     * @return This xml reader
     * @throws XMLParseException If the end of the reader is reached
     */
    XMLReader skip(int count) {
        for(int i=0; i<count; i++) skip();
        return this;
    }

    /**
     * Skips the next character.
     *
     * @return This xml reader
     * @throws XMLParseException If the end of the reader is reached
     */
    XMLReader skip() {
        read();
        return this;
    }

    /**
     * Skips the first character if it is the specified character.
     *
     * @param start The character to test as starting character
     * @return Whether the string started with the specified char
     */
    boolean skipIf(char start) {
        if(startsWith(start)) {
            skip();
            return true;
        }
        return false;
    }

    /**
     * Skips the length of the specified string if this string starts
     * with that string.
     *
     * @param start The string to test as starting string
     * @return Whether the string started with the specified string
     */
    boolean skipIf(String start) {
        if(startsWith(start)) {
            skip(start.length());
            return true;
        }
        return false;
    }

    /**
     * Skips the first char and throws a {@link XMLParseException} if it
     * does not match the expected character.
     *
     * @param expected The expected first char
     * @return This xml reader
     * @throws XMLParseException If the first character was not the expected
     *                            one or there are no more characters
     */
    XMLReader skipExpected(char expected) {
        char found = read();
        if(found != expected) throw new XMLParseException(expected, found, this);
        return this;
    }

    /**
     * Reads and removes the next character.
     *
     * @return The next character
     * @throws XMLParseException If the end of the reader is reached
     */
    char read() {
        try {
            int c = reader.read();
            if(c == -1) throw new XMLParseException("Reached end of file during parsing", this);
            countRead((char)c);
            return (char)c;
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Reads and removes the specified number of characters.
     *
     * @param count The number of characters to read
     * @return The next characters
     * @throws XMLParseException If the end of the reader is reached
     */
    String read(int count) {
        StringBuilder string = new StringBuilder(count);
        for(int i=0; i<count; i++) string.append(read());
        return string.toString();
    }

    /**
     * Skips whitespaces, comments and newlines. If the {@link XML#INCLUDE_COMMENTS}
     * option is set this will behave equivalently to {@link #skipWhitespaces(boolean)} called
     * with {@code false}.
     *
     * @return This xml reader
     * @throws XMLParseException If the end of the reader is reached
     */
    XMLReader skipToContent() {
        if(!includeComments && skipWhitespaces(false).startsWith("<!--")) {
            int index = indexOf("-->");
            if(index == -1) throw new XMLParseException("Reached end of file during comment", this);
            skip(4); // <!--
            if(indexOf("--") != index - 4) throw new XMLParseException("'--' not allowed in XML comment", this);
            skip(index - 1); // -4 for <!-- +3 for -->
            return skipToContent();
        }
        if(!includeProcessors && skipWhitespaces(false).startsWith("<?") && !startsWith("<?xml ") && !startsWith("<?xml?")) {
            int index = indexOf("?>");
            if(index == -1) throw new XMLParseException("Reached end of file during processor", this);
            skip(index + 2);
            return skipToContent();
        }
        return skipWhitespaces(false);
    }

    /**
     * Returns the next character without removing it.
     *
     * @return The next character
     * @throws XMLParseException If the end of the reader is reached
     */
    char peek() {
        try {
            reader.mark(1);
            int c = reader.read();
            reader.reset();
            if(c == -1) throw new XMLParseException("Reached end of file during parsing", this);
            return (char)c;
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Returns the next character replacing control characters with appropriate
     * descriptions.
     *
     * @return The next char description
     */
    String peekDescription() {
        char c = peek();
        switch(c) {
            case '\n':
            case '\r': return "<end of line>";
            case '\t':
            case '\f': return " ";
            case '\b': return "<backspace>";
            default: return c+"";
        }
    }

    String peekClosingTag() {
        try {
            StringBuilder tag = new StringBuilder();
            int c = '<';
            int i = 4; // Skip </
            while(c != '>') {
                reader.mark(i);
                reader.skip(i / 2);
                for (int j = i / 2; j < i; j++) {
                    c = reader.read();
                    if(c == -1) {
                        reader.reset();
                        throw new XMLParseException("Reached end of file during parsing", this);
                    }
                    if(c == '>') break;
                    else tag.append((char) c);
                }
                reader.reset();
                i *= 2;
            }
            return tag.toString();
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Determines whether the reader starts with the given string.
     *
     * @param string The string to test for starting
     * @return Whether the reader starts with the given string
     */
    boolean startsWith(String string) {
        try {
            reader.mark(string.length());
            for(int i=0; i<string.length(); i++) {
                if(reader.read() != string.charAt(i)) {
                    reader.reset();
                    return false;
                }
            }
            reader.reset();
            return true;
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Determines whether the reader starts with the given character
     *
     * @param c The char to test for starting
     * @return Whether the reader starts with the given char
     */
    boolean startsWith(char c) {
        return !isEmpty() && peek() == c;
    }

    boolean startsWithIgnoreCase(String string) {
        try {
            reader.mark(string.length());
            for(int i=0; i<string.length(); i++) {
                int c = reader.read();
                if(c == -1 || Character.toLowerCase((char)c) != Character.toLowerCase(string.charAt(i))) {
                    reader.reset();
                    return false;
                }
            }
            reader.reset();
            return true;
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Counts up the position counters according to the read character
     *
     * @param read The character that was read
     */
    private void countRead(char read) {
        if(read == '\n') {
            line++;
            charIndex = 1;
        }
        else charIndex++;
    }
}
