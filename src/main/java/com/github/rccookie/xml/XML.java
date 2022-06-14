package com.github.rccookie.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Utility class for parsing and writing XML. This class also contains flag
 * constants for parsing and writing xml.
 */
public final class XML {

    // Parsing

    /**
     * Preserve comments while parsing.
     * <p>This is a parsing flag.</p>
     */
    public static final long INCLUDE_COMMENTS = 1;
    /**
     * Preserve whitespaces and newlines in text blocks.
     * <p>This is a parsing flag.</p>
     */
    public static final long PRESERVE_WHITESPACES = 1 << 1;
    /**
     * Include processor nodes when parsing.
     * <p>This is a parsing flag.</p>
     */
    public static final long INCLUDE_PROCESSORS = 1 << 2;
    /**
     * Allow node attributes that don't specify a value.
     * <p>This is a parsing flag.</p>
     */
    public static final long ALLOW_EMPTY_ATTR = 1 << 3;
    /**
     * Try to fix parsing errors, like missing closing tags, closing tags
     * without opening ones, attribute values without quotes and empty
     * attributes ,even if not allowed.
     * <p>This is a parsing flag.</p>
     */
    public static final long TRY_FIX_ERRORS = 1 << 4;

    // Writing

    /**
     * Format the output with newlines and indents.
     * <p>This is an output flag.</p>
     */
    public static final long FORMATTED = 1 << 20;
    /**
     * Collapse empty nodes (nodes without children) using /&gt; instead of
     * a full closing tag.
     * <p>This is an output flag.</p>
     */
    public static final long COLLAPSE_EMPTY = 1 << 21;
    /**
     * Collapse child nodes into "...". <b>Waring:</b> this does not generate
     * valid xml or html and is intended for logging!
     * <p>This is an output flag.</p>
     */
    public static final long COLLAPSE_INNER = 2 << 22;

    // Special options

    /**
     * Parse / output in html format. Includes for example void tags etc.
     */
    static final long HTML_OPTION = 1 << 30;
    /**
     * Parse and output in html format. Additionally, this includes error fixes
     * and allows for empty attributes.
     * <p>This is parsing and output flag.</p>
     */
    public static final long HTML = HTML_OPTION | ALLOW_EMPTY_ATTR | TRY_FIX_ERRORS;
    /**
     * Default xml syntax. Does not include formatting.
     * <p>This is parsing and output flag.</p>
     */
    public static final long XML = COLLAPSE_EMPTY;


    private XML() {
        throw new UnsupportedOperationException();
    }


    /**
     * Parses the given string.
     *
     * @param xml The xml string to parse
     * @return The parsed document
     */
    public static Document parse(String xml) {
        return parse(xml, 0);
    }

    /**
     * Parses the given string.
     *
     * @param xml The xml string to parse
     * @param options Parsing options
     * @return The parsed document
     */
    public static Document parse(String xml, long options) {
        return getParser(xml, options).parseAll();
    }

    /**
     * Parses the given file.
     *
     * @param file The xml file to parse
     * @return The parsed document
     */
    public static Document parseFile(String file) {
        return parse(file, 0);
    }

    /**
     * Parses the given file.
     *
     * @param file The xml file to parse
     * @param options Parsing options
     * @return The parsed document
     */
    public static Document parseFile(String file, long options) {
        return getFileParser(file, options).parseAll();
    }

    /**
     * Fetches the given url and parses the received input stream.
     *
     * @param url The url to fetch
     * @return The parsed document
     */
    public static Document parseURL(String url) {
        return parseURL(url, 0);
    }

    /**
     * Fetches the given url and parses the received input stream.
     *
     * @param url The url to fetch
     * @param options Parsing options
     * @return The parsed document
     */
    public static Document parseURL(String url, long options) {
        return getURLParser(url, options).parseAll();
    }

    /**
     * Parses the given file.
     *
     * @param file The xml file to parse
     * @return The parsed document
     */
    public static Document parse(File file) {
        return parse(file, 0);
    }

    /**
     * Parses the given file.
     *
     * @param file The xml file to parse
     * @param options Parsing options
     * @return The parsed document
     */
    public static Document parse(File file, long options) {
        return getParser(file, options).parseAll();
    }

    /**
     * Parses the given input stream.
     *
     * @param in The input stream to parse
     * @return The parsed document
     */
    public static Document parse(InputStream in) {
        return parse(in, 0);
    }

    /**
     * Parses the given input stream.
     *
     * @param in The input stream to parse
     * @param options Parsing options
     * @return The parsed document
     */
    public static Document parse(InputStream in, long options) {
        return getParser(in, options).parseAll();
    }

    /**
     * Parses the given reader.
     *
     * @param reader The reader to parse
     * @return The parsed document
     */
    public static Document parse(Reader reader) {
        return parse(reader, 0);
    }

    /**
     * Parses the given reader.
     *
     * @param reader The reader to parse
     * @param options Parsing options
     * @return The parsed document
     */
    public static Document parse(Reader reader, long options) {
        return getParser(reader, options).parseAll();
    }

    // ----------------------------------------------

    /**
     * Creates a xml parser for the given string.
     *
     * @param xml The xml string for the parser to parse
     * @return An unused xml parser
     */
    public static XMLParser getParser(String xml) {
        return getParser(xml, 0);
    }

    /**
     * Creates a xml parser for the given string.
     *
     * @param xml The xml string for the parser to parse
     * @param options Parsing options
     * @return An unused xml parser
     */
    public static XMLParser getParser(String xml, long options) {
        return getParser(new StringReader(xml), options);
    }

    /**
     * Creates a xml parser for the given file.
     *
     * @param file The xml file for the parser to parse
     * @return An unused xml parser
     */
    public static XMLParser getFileParser(String file) {
        return getFileParser(file, 0);
    }

    /**
     * Creates a xml parser for the given file.
     *
     * @param file The xml file for the parser to parse
     * @param options Parsing options
     * @return An unused xml parser
     */
    public static XMLParser getFileParser(String file, long options) {
        return getParser(new File(file), options);
    }

    /**
     * Fetches the given url and creates a parser for the received input stream.
     *
     * @param url The url to fetch
     * @return An unused xml parser
     */
    public static XMLParser getURLParser(String url) {
        return getURLParser(url, 0);
    }

    /**
     * Fetches the given url and creates a parser for the received input stream.
     *
     * @param url The url to fetch
     * @param options Parsing options
     * @return An unused xml parser
     */
    public static XMLParser getURLParser(String url, long options) {
        try {
            return getParser(new URL(url).openStream(), options);
        } catch(MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Creates a xml parser for the given file.
     *
     * @param file The xml file for the parser to parse
     * @return An unused xml parser
     */
    public static XMLParser getParser(File file) {
        return getParser(file, 0);
    }

    /**
     * Creates a xml parser for the given file.
     *
     * @param file The xml file for the parser to parse
     * @param options Parsing options
     * @return An unused xml parser
     */
    public static XMLParser getParser(File file, long options) {
        try {
            return getParser(new FileReader(file), options);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a xml parser for the given input stream.
     *
     * @param in The input stream for the parser to parse
     * @return An unused xml parser
     */
    public static XMLParser getParser(InputStream in) {
        return getParser(in, 0);
    }

    /**
     * Creates a xml parser for the given input stream.
     *
     * @param in The input stream for the parser to parse
     * @param options Parsing options
     * @return An unused xml parser
     */
    public static XMLParser getParser(InputStream in, long options) {
        return getParser(new InputStreamReader(in), options);
    }

    /**
     * Creates a xml parser for the given reader.
     *
     * @param reader The reader for the parser to parse
     * @return An unused xml parser
     */
    public static XMLParser getParser(Reader reader) {
        return getParser(reader, 0);
    }

    /**
     * Creates a xml parser for the given reader.
     *
     * @param reader The reader for the parser to parse
     * @param options Parsing options
     * @return An unused xml parser
     */
    public static XMLParser getParser(Reader reader, long options) {
        return new XMLParser(reader, options);
    }

    // ----------------------------------------------

    /**
     * Writes the given xml tree into the specified file.
     *
     * @param xml The xml tree to write
     * @param file The file to write to
     * @param html Whether to format as html
     */
    public static void write(Node xml, String file, boolean html) {
        write(xml, file, (html ? HTML : XML)|FORMATTED);
    }

    /**
     * Writes the given xml tree into the specified file.
     *
     * @param xml The xml tree to write
     * @param file The file to write to
     * @param options Output options
     */
    public static void write(Node xml, String file, long options) {
        write(xml, new File(file), options);
    }

    /**
     * Writes the given xml tree into the specified file.
     *
     * @param xml The xml tree to write
     * @param file The file to write to
     * @param html Whether to format as html
     */
    public static void write(Node xml, File file, boolean html) {
        write(xml, file, (html ? HTML : XML)|FORMATTED);
    }

    /**
     * Writes the given xml tree into the specified file.
     *
     * @param xml The xml tree to write
     * @param file The file to write to
     * @param options Output options
     */
    public static void write(Node xml, File file, long options) {
        try {
            write(xml, new FileOutputStream(file), options);
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Writes the given xml tree into the specified output stream.
     *
     * @param xml The xml tree to write
     * @param out The output stream to write to
     * @param html Whether to format as html
     */
    public static void write(Node xml, OutputStream out, boolean html) {
        write(xml, out, (html ? HTML : XML)|FORMATTED);
    }

    /**
     * Writes the given xml tree into the specified output stream.
     *
     * @param xml The xml tree to write
     * @param out The output stream to write to
     * @param options Output options
     */
    public static void write(Node xml, OutputStream out, long options) {
        write(xml, new OutputStreamWriter(out), options);
    }

    /**
     * Writes the given xml tree into the specified writer.
     *
     * @param xml The xml tree to write
     * @param out The writer to write to
     * @param html Whether to format as html
     */
    public static void write(Node xml, Writer out, boolean html) {
        write(xml, out, (html ? HTML : XML)|FORMATTED);
    }

    /**
     * Writes the given xml tree into the specified writer.
     *
     * @param xml The xml tree to write
     * @param out The writer to write to
     * @param options Output options
     */
    public static void write(Node xml, Writer out, long options) {
        try {
            out.write(xml.toString(options));
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
