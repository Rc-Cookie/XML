package de.rccookie.xml;

import java.io.File;
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
import java.nio.file.Files;
import java.nio.file.Path;

import org.intellij.lang.annotations.Language;

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
     * Allow for <code>--</code> to occur within comments, which is usually not allowed.
     * <p>This is both a parsing and an output flag.</p>
     */
    public static final long ALLOW_DOUBLE_DASH_IN_COMMENT = 1 << 5;

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
     * Collapse child nodes into "...". <b>Waring:</b> this does omit most of the
     * data and is not intended for serialization, but mostly for debugging and the
     * <code>toString()</code> preview of a node!
     * <p>This is an output flag.</p>
     */
    public static final long COLLAPSE_INNER = 1 << 22;
    /**
     * Write HTML void tags (like <code>&lt;br></code>) as self-closing xml tags
     * (e.g. <code>&lt;br/></code> for XHTML compatability. Ignored if the output
     * is not in HTML.
     * <p>This is an output flag.</p>
     */
    public static final long SELF_CLOSE_VOID_TAGS = 1 << 23;
    /**
     * Insert a space before the self-closing sequence "/>" (e.g. <code>&lt;br /></code>
     * instead of <code>&lt;br/></code> (not restricted to HTML)).
     * <p>This is an output flag.</p>
     */
    public static final long SPACE_BEFORE_SELF_CLOSE = 1 << 24;

    // Special options

    /**
     * Parse / output in html format. Includes for example void tags etc.
     */
    static final long HTML_OPTION = 1 << 30;
    /**
     * Parse / output in xhtml format.
     */
    static final long XHTML_OPTION = 1L << 31;
    /**
     * Automatically detect whether the document uses html, xhtml or regular xml syntax
     * and set the other flags accordingly. Note that some other flags may be overridden
     * depending on the syntax of the actual document.
     */
    public static final long AUTO_DETECT_SYNTAX = 1L << 32;
    /**
     * Try to fix parsing errors, like missing closing tags, closing tags
     * without opening ones, attribute values without quotes and empty
     * attributes, even if not allowed. For writing, this will try to fix
     * errors like non-lowercase tag names in XHTML documents.
     * <p>This is parsing and output flag.</p>
     */
    public static final long TRY_FIX_ERRORS = 1 << 4;
    /**
     * Parse and output in html format. Additionally, this includes error fixes,
     * allows for empty attributes and allowed the use of "--" within comments.
     * <p>This is parsing and output flag.</p>
     */
    public static final long HTML = HTML_OPTION | ALLOW_EMPTY_ATTR | TRY_FIX_ERRORS | ALLOW_DOUBLE_DASH_IN_COMMENT | SELF_CLOSE_VOID_TAGS;
    /**
     * Parse and output in xhtml format, throwing errors on illegal formatting.
     * <p>This is parsing and output flag.</p>
     */
    public static final long XHTML_STRICT = XHTML_OPTION | COLLAPSE_EMPTY;
    /**
     * Parse and output in xhtml format, trying to avoid errors where possible.
     * <p>This is parsing and output flag.</p>
     */
    public static final long XHTML = XHTML_STRICT | TRY_FIX_ERRORS | ALLOW_DOUBLE_DASH_IN_COMMENT;
    /**
     * Default xml syntax. Does not include formatting.
     * <p>This is parsing and output flag.</p>
     */
    public static final long XML = COLLAPSE_EMPTY;
    /**
     * The default parsing mode; detects automatically whether html, xhtml or xml syntax is
     * used. Note that some other flags may be overridden depending on the syntax of the actual
     * document.
     */
    public static final long AUTO = AUTO_DETECT_SYNTAX | TRY_FIX_ERRORS;

    // ------------------------------------------------

//    private static final Set<Class<?>> FIXED_DESERIALIZERS = Set.of(
//            boolean.class, Boolean.class,
//            byte.class, Byte.class,
//            short.class, Short.class,
//            int.class, Integer.class,
//            long.class, Long.class,
//            float.class, Float.class,
//            double.class, Double.class,
//            char.class, Character.class,
//            String.class
//    );
//
//    private static final Map<Class<?>, Function<Node,?>> DESERIALIZERS = new HashMap<>();
//    static {
//        addTextSerializer(boolean.class, Boolean::parseBoolean);
//        addTextSerializer(Boolean.class, Boolean::parseBoolean);
//        addTextSerializer(byte.class, Byte::parseByte);
//        addTextSerializer(Byte.class, Byte::parseByte);
//        addTextSerializer(short.class, Short::parseShort);
//        addTextSerializer(Short.class, Short::parseShort);
//        addTextSerializer(int.class, Integer::parseInt);
//        addTextSerializer(Integer.class, Integer::parseInt);
//        addTextSerializer(long.class, Long::parseLong);
//        addTextSerializer(Long.class, Long::parseLong);
//        addTextSerializer(float.class, Float::parseFloat);
//        addTextSerializer(Float.class, Float::parseFloat);
//        addTextSerializer(double.class, Double::parseDouble);
//        addTextSerializer(Double.class, Double::parseDouble);
//        addTextSerializer(char.class, s -> {
//            if(s.isEmpty()) throw new IllegalArgumentException("Empty string for character");
//            if(s.length() != 1) throw new IllegalArgumentException("Too many characters for character");
//            return s.charAt(0);
//        });
//        addTextSerializer(Character.class, s -> {
//            if(s.isEmpty()) throw new IllegalArgumentException("Empty string for character");
//            if(s.length() != 1) throw new IllegalArgumentException("Too many characters for character");
//            return s.charAt(0);
//        });
//        addTextSerializer(String.class, Function.identity());
//    }
//    private static <T> void addTextSerializer(Class<T> type, Function<String,T> deserializer) {
//        DESERIALIZERS.put(type, t -> deserializer.apply(t.text()));
//    }



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
        return parse(xml, AUTO);
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
        return parseFile(file, AUTO);
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
    public static Document parseURL(@Language("http-url-reference") String url) {
        return parseURL(url, AUTO);
    }

    /**
     * Fetches the given url and parses the received input stream.
     *
     * @param url The url to fetch
     * @param options Parsing options
     * @return The parsed document
     */
    public static Document parseURL(@Language("http-url-reference") String url, long options) {
        return getURLParser(url, options).parseAll();
    }

    /**
     * Parses the given file.
     *
     * @param file The xml file to parse
     * @return The parsed document
     */
    public static Document parse(File file) {
        return parse(file.toPath());
    }

    /**
     * Parses the given file.
     *
     * @param file The xml file to parse
     * @param options Parsing options
     * @return The parsed document
     */
    public static Document parse(File file, long options) {
        return parse(file.toPath(), options);
    }

    /**
     * Parses the given file.
     *
     * @param file The xml file to parse
     * @return The parsed document
     */
    public static Document parse(Path file) {
        return parse(file, AUTO);
    }

    /**
     * Parses the given file.
     *
     * @param file The xml file to parse
     * @param options Parsing options
     * @return The parsed document
     */
    public static Document parse(Path file, long options) {
        return getParser(file, options).parseAll();
    }

    /**
     * Parses the given input stream.
     *
     * @param in The input stream to parse
     * @return The parsed document
     */
    public static Document parse(InputStream in) {
        return parse(in, AUTO);
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
        return parse(reader, AUTO);
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
        return getParser(xml, AUTO);
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
        return getFileParser(file, AUTO);
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
    public static XMLParser getURLParser(@Language("http-url-reference") String url) {
        return getURLParser(url, AUTO);
    }

    /**
     * Fetches the given url and creates a parser for the received input stream.
     *
     * @param url The url to fetch
     * @param options Parsing options
     * @return An unused xml parser
     */
    public static XMLParser getURLParser(@Language("http-url-reference") String url, long options) {
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
        return getParser(file.toPath());
    }

    /**
     * Creates a xml parser for the given file.
     *
     * @param file The xml file for the parser to parse
     * @param options Parsing options
     * @return An unused xml parser
     */
    public static XMLParser getParser(File file, long options) {
        return getParser(file.toPath(), options);
    }

    /**
     * Creates a xml parser for the given file.
     *
     * @param file The xml file for the parser to parse
     * @return An unused xml parser
     */
    public static XMLParser getParser(Path file) {
        return getParser(file, AUTO);
    }

    /**
     * Creates a xml parser for the given file.
     *
     * @param file The xml file for the parser to parse
     * @param options Parsing options
     * @return An unused xml parser
     */
    public static XMLParser getParser(Path file, long options) {
        try {
            return getParser(Files.newBufferedReader(file), options);
        } catch(IOException e) {
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
        return getParser(in, AUTO);
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
        return getParser(reader, AUTO);
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
        write(xml, file.toPath(), options);
    }

    /**
     * Writes the given xml tree into the specified file.
     *
     * @param xml The xml tree to write
     * @param file The file to write to
     * @param html Whether to format as html
     */
    public static void write(Node xml, Path file, boolean html) {
        write(xml, file, (html ? HTML : XML)|FORMATTED);
    }

    /**
     * Writes the given xml tree into the specified file.
     *
     * @param xml The xml tree to write
     * @param file The file to write to
     * @param options Output options
     */
    public static void write(Node xml, Path file, long options) {
        try(OutputStream out = Files.newOutputStream(file)) {
            write(xml, out, options);
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Writes the given xml tree into the specified output stream. The stream will
     * not be closed.
     *
     * @param xml The xml tree to write
     * @param out The output stream to write to
     * @param html Whether to format as html
     */
    public static void write(Node xml, OutputStream out, boolean html) {
        write(xml, out, (html ? HTML : XML)|FORMATTED);
    }

    /**
     * Writes the given xml tree into the specified output stream. The stream will
     * not be closed.
     *
     * @param xml The xml tree to write
     * @param out The output stream to write to
     * @param options Output options
     */
    public static void write(Node xml, OutputStream out, long options) {
        write(xml, new OutputStreamWriter(out), options);
    }

    /**
     * Writes the given xml tree into the specified writer. The writer will
     * not be closed.
     *
     * @param xml The xml tree to write
     * @param out The writer to write to
     * @param html Whether to format as html
     */
    public static void write(Node xml, Writer out, boolean html) {
        write(xml, out, (html ? HTML : XML)|FORMATTED);
    }

    /**
     * Writes the given xml tree into the specified writer. The writer will
     * not be closed.
     *
     * @param xml The xml tree to write
     * @param out The writer to write to
     * @param options Output options
     */
    public static void write(Node xml, Writer out, long options) {
        try {
            out.write(xml.toString(options));
            out.flush();
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // -------------------------------------------

//    public static Node toXML(Object obj) {
//        Arguments.checkNull(obj, "obj");
//        if(obj instanceof Node)
//            return (Node) obj;
//        if(obj instanceof XMLSerializable)
//            return ((XMLSerializable) obj).toXML();
//        if(FIXED_DESERIALIZERS.contains(obj.getClass()))
//            return new Text(obj.toString());
//        if(obj.getClass().isArray()) {
//            int l = Array.getLength(obj);
//            Node array = new Node("array");
//            array.attributes.put("length", ""+l);
//            for(int i=0; i<l; i++) {
//                Node entry = new Node("entry");
//                entry.children.add(toXML(Array.get(obj, i)));
//                array.children.add(entry);
//            }
//            return array;
//        }
//        if(obj instanceof Collection<?>) {
//            Collection<?> col = (Collection<?>) obj;
//            Node collection = new Node("collection");
//            for(Object o : col) {
//                Node entry = new Node("entry");
//                entry.children.add(toXML(o));
//                collection.children.add(entry);
//            }
//            return collection;
//        }
//        if(obj instanceof Map<?,?>) {
//            Node map = new Node("map");
//            ((Map<?,?>) obj).forEach((k,v) -> {
//                Node entry = new Node("entry");
//                Node key = new Node("key");
//                key.children.add(toXML(k));
//                Node value = new Node("value");
//                value.children.add(toXML(v));
//                entry.children.add(key);
//                entry.children.add(value);
//                map.children.add(entry);
//            });
//            return map;
//        }
//        if(obj.getClass().isEnum()) {
//            Node constant = new Node("enum-constant");
//            constant.attributes.put("value", obj.toString());
//            return constant;
//        }
//        throw new IllegalArgumentException(obj.getClass() + " is not XML serializable");
//    }

//    public static <T> T as(Class<T> type, Node xml) {
//
//    }
}
