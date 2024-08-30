package de.rccookie.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;

/**
 * Internal class to read from a {@link Reader} more conveniently.
 */
@SuppressWarnings({"UnusedReturnValue", "BooleanMethodIsAlwaysInverted", "SameParameterValue", "resource"})
class XMLReader implements AutoCloseable {

    /**
     * The underlying reader.
     */
    private final Reader reader;
    /**
     * Current position in the string.
     */
    int line = 1;
    int charIndex = 1;

    final boolean includeComments;
    private int preserveWhitespaces;
    final boolean includeProcessors;
    boolean allowEmptyAttr;
    boolean tryFixErrors;
    boolean allowDoubleDashInComments;
    boolean html;
    boolean xhtml;
    boolean detectSyntax;


    /**
     * Creates a new xml reader using the given reader.
     *
     * @param reader The reader to use
     */
    XMLReader(Reader reader, long options) {
        this.reader = reader.markSupported() ? reader : new BufferedReader(reader);
        includeComments = (options & XML.INCLUDE_COMMENTS) != 0;
        preserveWhitespaces = (options & XML.PRESERVE_WHITESPACES) != 0 ? 1 : 0;
        includeProcessors = (options & XML.INCLUDE_PROCESSORS) != 0;
        tryFixErrors = (options & XML.TRY_FIX_ERRORS) != 0;
        allowEmptyAttr = (options & XML.ALLOW_EMPTY_ATTR) != 0;
        allowDoubleDashInComments = (options & XML.ALLOW_DOUBLE_DASH_IN_COMMENT) != 0;
        html = (options & XML.HTML_OPTION) != 0;
        xhtml = (options & XML.XHTML_OPTION) != 0;
        if(html && xhtml) throw new IllegalArgumentException("Illegal parsing flags: HTML and XHTML");
        detectSyntax = !html && !xhtml && (options & XML.AUTO_DETECT_SYNTAX) != 0;
    }

    XMLReader htmlDetected() {
        if(!detectSyntax) return this;
        detectSyntax = false;
        html = allowEmptyAttr = tryFixErrors = allowDoubleDashInComments = true;
        return this;
    }

    XMLReader xhtmlDetected() {
        if(!detectSyntax) return this;
        detectSyntax = false;
        xhtml = allowDoubleDashInComments = true;
        return this;
    }

    XMLReader xmlDetected() {
        detectSyntax = false;
        return this;
    }

    XMLReader pushPreserveWhitespaces() {
        preserveWhitespaces++;
        return this;
    }

    XMLReader popPreserveWhitespaces() {
        preserveWhitespaces = Math.max(0, preserveWhitespaces - 1);
        return this;
    }

    boolean trimWhitespaces() {
        return preserveWhitespaces == 0;
    }

    boolean preserveWhitespaces() {
        return preserveWhitespaces != 0;
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
            int i = 0;
            for(int buf=1024;; buf*=2) {
                reader.mark(buf);
                reader.skip(i);

                int d = 0;
                for(; i<buf; i++) {
                    d = reader.read();
                    if(d == c || d == -1)
                        break;
                }
                reader.reset();

                if(i == buf) continue;
                if(d == c) return i;
                else return -1;
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

        char[] str = string.toCharArray();

        // Precalculate shared prefix at offset (str and str[offset...])
        // All elements at index offset-1, because offset=0 is trivial and not used, and offset=length => 0 is final element
        int[] p = new int[str.length];
        for(int i=0; i<str.length; i++) {
            for(; p[i] < str.length - i - 1; p[i]++)
                if(str[p[i]] != str[i + 1 + p[i]])
                    break;
        }

        try {

            int i = 0, c = 0, o, r;
            for(int buf=256;; buf*=2) {
                reader.mark(buf + str.length);
                reader.skip(i);

                outer: for(o=1, r=0; i<buf; i++, o++, r--) {

                    if(p[o-1] < r - 1)
                        continue;
                    // Equivalent, not precalculated:
//                for(int j=0; j<r-1; j++)
//                    if(str[j] != str[j+o])
//                        continue outer;

                    if(r != 0 && str[r-1] != c)
                        continue;

                    while(r < str.length) {
                        c = reader.read();
                        if(c == str[r++]) continue;
                        if(c == -1) {
                            reader.reset();
                            return -1;
                        }
                        o = 0;
                        continue outer;
                    }

                    reader.reset();
                    return i;
                }
                reader.reset();
            }

        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private int simpleIndexOf(char[] str) throws IOException {
        iLoop: for(int i=0;;i++) {
            reader.mark(i + str.length);
            reader.skip(i);
            int d = reader.read();
            if(d == str[0]) {
                for(int j=1; j<str.length; j++) {
                    if(str[j] != reader.read()) {
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
     * option and not <code>force</code> is enabled only skips a newline if content
     * follows immediately after that.
     *
     * @return This xml reader
     */
    XMLReader skipWhitespaces(boolean force) {
        if(!force && preserveWhitespaces())
            return this;
        try {
//            if(!force && preserveWhitespaces()) {
//                reader.mark(3);
//                int c = reader.read();
//                if(c == '\n') {
//                    c = reader.read();
//                    reader.reset();
//                    if(!Character.isWhitespace(c))
//                        reader.skip(1);
//                    return this;
//                }
//                if(c == '\r') {
//                    c = reader.read();
//                    if(c == '\n') {
//                        c = reader.read();
//                        reader.reset();
//                        if(!Character.isWhitespace(c))
//                            reader.skip(2);
//                        return this;
//                    }
//                    reader.reset();
//                    if(!Character.isWhitespace(c))
//                        reader.skip(1);
//                    return this;
//                }
//                reader.reset();
//                return this;
//            }
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

    /**
     * Skips the next character asserting it is a whitespace.
     *
     * @return This xml reader
     */
    XMLReader skipWhitespace() {
        char c = read();
        if(!Character.isWhitespace(c))
            throw new XMLParseException("<whitespace>", c, this);
        return this;
    }

    /**
     * Returns the next non-whitespace character asserting there is one,
     * without consuming the reader.
     *
     * @return The next non-whitespace character
     */
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
            if(!allowDoubleDashInComments && indexOf("--") != index - 4)
                throw new XMLParseException("'--' not allowed in XML comment", this);
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
        return peek(0);
    }

    /**
     * Returns the character at the given offset without removing it. The offset
     * <code>0</code> represents the first character and is identical to {@link #peek()}.
     *
     * @param offset The offset from the next character to get (non-negative), 0 is the next character
     * @return The character at that offset
     * @throws XMLParseException If the end of the reader is reached
     */
    char peek(int offset) {
        try {
            reader.mark(offset + 1);
            if(offset != 0 && reader.skip(offset) != offset)
                throw new XMLParseException("Reached end of file during parsing", this);
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

    /**
     * Returns the tag name of the closing tag at the current start of
     * the reader, if there is a closing tag, otherwise <code>null</code>
     * is returned.
     *
     * @return The name of the next closing tag, or <code>null</code>
     */
    String peekClosingTag() {
        try {
            try {
                varMark(8);
                if(varRead() != '<' || varRead() != '/') return null;

                int c;
                //noinspection StatementWithEmptyBody
                while(Character.isWhitespace(c = varRead()));

                StringBuilder tag = new StringBuilder();
                while(c != '>' && !Character.isWhitespace(c)) {
                    if(c == -1) throw new XMLParseException("Reached end of file during parsing", this);
                    tag.append((char) c);
                    c = varRead();
                }

                while(Character.isWhitespace(c)) c = varRead();
                if(c != '>') throw new XMLParseException('>', c, this);

                return tag.toString();

            } finally {
                varReset(); // Effectively no reset after mark invalidated
            }
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

    /**
     * Determines whether the reader starts with the given characters,
     * ignoring their case.
     *
     * @param string The string to check for
     * @return Whether the reader starts with those characters, ignoring
     *         their case
     */
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
     * Skips the closing tag with the specified name. It will not be verified that what was
     * skipped was actually the specified closing tag.
     *
     * @param tag The name of the tag to skip. Only relevant for the length of the tag name
     * @return This reader
     */
    XMLReader skipClosingTag(String tag) {
        // </ \s* tag \* >
        return skip(2).skipWhitespaces(false).skip(tag.length()).skipWhitespaces(false).skip(1);
    }

    private int marked = 0, read;

    /**
     * Marks the current position in the reader as the position to possibly reset to later.
     * The specified buffer size will be initial read ahead limit, but it will grow as needed.
     * To function properly the {@link #varRead()} and {@link #varReset()} methods have to be
     * used.
     *
     * @param initialBuffer The initial buffer size, when becoming to small it will keep growing by a factor of 2
     */
    private void varMark(int initialBuffer) throws IOException {
        reader.mark(initialBuffer);
        marked = initialBuffer;
        read = 0;
    }

    /**
     * Resets the readers position to the last marked position. This must be used for varMark
     * to work properly.
     */
    private void varReset() throws IOException {
        reader.reset();
        read = 0;
    }

    /**
     * Reads the next character from the reader. If the reader was marked using {@link #varMark(int)}
     * and the read-ahead limit is reached, it will be doubled.
     *
     * @return The next character, or -1 if the end of the reader has been reached
     */
    private int varRead() throws IOException {
        if(marked <= 0) return reader.read();
        if(read >= marked) {
            reader.reset();
            reader.mark(marked <<= 1);
            reader.skip(marked >> 1);
        }
        int c = reader.read();
        if(c != -1) read++;
        return c;
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
