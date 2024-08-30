package de.rccookie.xml;

import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import de.rccookie.util.Arguments;
import de.rccookie.util.Console;
import de.rccookie.util.IterableIterator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A parser for an xml formatted input. The parsing works single-pass and
 * can efficiently use stream input.
 */
public class XMLParser implements IterableIterator<Node>, AutoCloseable {

    /**
     * Html void tags (tags that must not be closed).
     */
    static final Set<String> HTML_VOID_TAGS = Set.of("area", "base", "br", "col", "hr", "img", "input", "link", "meta", "param", "command", "keygen", "source", "frame", "embed", "applet", "basefont");
    /**
     * Html tags that don't have to be closed.
     */
    static final Set<String> HTML_POSSIBLY_UNCLOSED_TAGS = Set.of("html", "head", "body", "p", "li", "dt", "dd", "option", "thead", "th", "tbody", "tr", "td", "tfoot", "colgroup");
    /**
     * List of valid html tags.
     */
    static final Set<String> HTML_TAGS = Set.of("a", "abbr", "acronym", "address", "applet", "area", "article", "aside", "audio", "b", "base", "basefont", "bb", "bdo", "big",
            "blockquote", "body", "br", "button", "canvas", "caption", "center", "circle", "cite", "code", "col", "colgroup", "command", "datagrid", "datalist", "dd", "del",
            "details", "dfn", "dialog", "dir", "div", "dl", "dt", "em", "embed", "eventsource", "fieldset", "figcaption", "figure", "font", "footer", "form", "frame", "frameset",
            "h1", "h2", "h3", "h4", "h5", "h6", "head", "header", "hgroup", "hr", "html", "i", "iframe", "img", "input", "ins", "isindex", "kbd", "keygen", "label", "legend", "li",
            "link", "main", "map", "mark", "menu", "meta", "meter", "nav", "noframes", "noscript", "object", "ol", "optgroup", "option", "output", "p", "param", "path", "pre",
            "progress", "q", "rp", "rt", "ruby", "s", "samp", "script", "section", "select", "small", "source", "span", "strike", "strong", "style", "sub", "summary", "sup", "svg",
            "table", "tbody", "td", "template", "textarea", "tfoot", "th", "thead", "time", "title", "tr", "track", "tt", "u", "ul", "var", "video", "wbr");

    static final Pattern XHTML_NS_PATTERN = Pattern.compile("https?://(?:www\\.)?w3\\.org/1999/xhtml/?");


    /**
     * The xml reader over the input source.
     */
    private final XMLReader xml;
    /**
     * Whether the parser has been closed.
     */
    private boolean closed = false;
    /**
     * Whether no node has been parsed, the next node will be the first one.
     */
    private boolean firstNode = true;
    /**
     * Whether the doctype declaration is still allowed.
     */
    private boolean doctypeAllowed = true;

    /**
     * Current tag hierarchy, as stack from root to current leaf.
     */
    private final Deque<String> hierarchy = new ArrayDeque<>();

    /**
     * The warning message listener.
     */
    private Consumer<String> warningListener = w -> {};


    /**
     * Creates a new xml parser.
     *
     * @param reader The input source
     * @param options Parsing options
     */
    XMLParser(Reader reader, long options) {
        xml = new XMLReader(reader, options);
    }

    /**
     * Sets the consumer that gets invoked when an error gets fixed.
     * Will only be used if the {@link XML#TRY_FIX_ERRORS} flag is set.
     *
     * @param warningListener The listener to set
     * @return This parser
     */
    public XMLParser setWarningListener(Consumer<String> warningListener) {
        this.warningListener = Arguments.checkNull(warningListener);
        return this;
    }

    /**
     * Closes this parser and the underlying input source.
     */
    @Override
    public void close() {
        if(closed) return;
        closed = true;
        xml.close();
    }


    /**
     * Parses the complete input source into a document and closes the parser.
     * This has to be the first parsing action on the parser.
     *
     * @return The parsed document
     */
    public Document parseAll() {
        if(closed) throw new IllegalStateException("Parser has been closed");
        if(!firstNode) throw new IllegalStateException("Can only parse document as the first parse action");
        Document document = new Document();
        while(hasNext()) {
            Node next = next(); // use 'next' for synchronization
            if(next instanceof XMLDeclaration)
                document.setXMLDeclaration((XMLDeclaration) next);
            else if(next instanceof Doctype)
                document.setDoctype((Doctype) next);
            else document.children.add(next);
        }
        try {
            close(); // Don't throw away the parsed document...
        } catch(Exception e) {
            Console.warn("Error while closing parser:");
            Console.warn(e);
        }
        if(xml.xhtml && document.getDoctype() == null) {
            if(xml.tryFixErrors)
                warn("DOCTYPE is required in XHTML documents");
            else throw new XMLParseException("DOCTYPE is required in XHTML documents");
        }
        return document;
    }

    /**
     * Returns whether the parser has more data to parse. This <b>does not</b> necessarily
     * mean that the remaining input will be parsable, it may still be invalid.
     *
     * @return Whether more data to parse is found
     */
    @Override
    public boolean hasNext() {
        return !closed && !xml.skipToContent().isEmpty(); // Check if only remaining is an empty text
    }

    /**
     * Parses the next node in the input source.
     *
     * @return The parsed node
     */
    @Override
    public synchronized Node next() {
        if(closed) throw new IllegalStateException("Parser has been closed");
        if(!hasNext()) throw new XMLParseException("No value present", xml);
        hierarchy.clear();
        return parseNextNode();
    }

    /**
     * Returns a stream over the nodes parsable from the input source.
     *
     * @return A stream over parsed nodes
     */
    @Override
    @NotNull
    public Stream<Node> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this, Spliterator.IMMUTABLE|Spliterator.NONNULL), false);
    }

    private Node parseNextNode() {
        if(!xml.startsWith('<')) {
            Text text = parseNextText("");
            return text != null ? text : parseNextNode();
        }

        if(xml.skip().skipIf('!')) { // No whitespaces allowed between any of <!DOCTYPE or <!--
            // <!...
            if(xml.startsWith("--")) return parseNextComment();
            else if(xml.startsWithIgnoreCase("DOCTYPE")) return parseNextDoctype();
            else throw new XMLParseException(doctypeAllowed ? "--' or 'DOCTYPE" : "--", xml.peekDescription(), xml);
        }
        if(xml.startsWith('?')) {
            Prolog prolog = parseNextProlog(); // Also no whitespaces between <?xml
            return prolog != null ? prolog : parseNextText("<?");
        }
        if(xml.tryFixErrors && xml.skipIf('/')) {
            // </... even though not a closing tag expected
            String tag = parseNextKey("tag", xml.tryFixErrors);
            if(tag == null)
                return parseNextText("</");
            Node node = new Node(tag);
            parseNextAttributes(node);
            warn("Closing tag '"+node.tag+"' never opened");
            xml.skipExpected('>');
            return node;
        }

        // <tag
        String tag = parseNextKey("tag", xml.tryFixErrors);
        if(tag == null)
            return parseNextText("<");

        Node node = new Node(tag);
        if((xml.html || xml.xhtml) && !HTML_TAGS.contains(tag))
            warn("Unknown html tag '" + tag + "'"); // Not strictly forbidden, even in xhtml

        // <tag ...>
        parseNextAttributes(node);
        if(xml.skipIf('/')) {
            // <tag/>
            xml.skipExpected('>'); // No space between />
            return node;
        }

        // Syntax can be detected from the root node
        if(xml.detectSyntax && hierarchy.isEmpty()) {
            if(!tag.equalsIgnoreCase("html"))
                // Not <html> -> neither html nor xhtml
                xml.xmlDetected();
            else if(node.attributes.containsKey("xmlns")) {
                // <html xmlns="http://www.w3.org/1999/xhtml"> -> xhtml
                if(XHTML_NS_PATTERN.matcher(node.attributes.get("xmlns")).matches())
                    xml.xhtmlDetected();
                // Other namespace -> neither html nor xhtml
                else xml.xmlDetected();
            }
            // <html> or <HTML> or similar -> assume html
            else xml.htmlDetected();
        }

        // <tag>
        xml.skipExpected('>');
        if(xml.html || xml.xhtml) {
            if((xml.html || xml.tryFixErrors) && HTML_VOID_TAGS.contains(tag)) {
                // <tag> unclosed or immediately closed
                String closing = xml.peekClosingTag();
                if(closing != null && closing.equalsIgnoreCase(tag))
                    xml.skipClosingTag(tag);
                return node;
            }
            if(tag.equals("script")) {
                // <script>...</script>
                Text code = parseScriptContent();
                if(code != null)
                    node.children.add(code);
                return node;
            }
            if(tag.equals("style")) {
                // <style>...</style>
                Text css = parseCSSContent();
                if(css != null)
                    node.children.add(css);
                return node;
            }
            if(tag.equals("pre"))
                xml.pushPreserveWhitespaces();
        }

        // <tag>...
        hierarchy.push(tag);

        String closingTag = null;
        while(!xml.skipToContent().isEmpty()) {
            closingTag = xml.peekClosingTag();
            if(closingTag == null)
                // <tag><child... or <tag>text
                node.children.add(parseNextNode());
            else {
                if(xml.tryFixErrors && !closingTag.equalsIgnoreCase(tag)) {
                    // <tag></closingTag>
                    if(hierarchy.stream().anyMatch(closingTag::equalsIgnoreCase))// && HTML_POSSIBLY_UNCLOSED_TAGS.contains(tag))
                        break; // There are probably closing tags missing
                    else node.children.add(parseNextNode()); // There are probably too many closing tags
                }
                else break;
            }
        }
        hierarchy.pop();

        assert closingTag != null;
        // <tag><children/></closingTag>
        if(((xml.html || xml.tryFixErrors) && closingTag.equalsIgnoreCase(tag)) || (!(xml.html || xml.tryFixErrors) && closingTag.equals(tag)))
            // <tag><children/></tag>
            xml.skipClosingTag(tag);
        else if(xml.tryFixErrors) warn("Unclosed tag '"+tag+"'");
        else throw new XMLParseException("Incorrect closing tag, expected </"+tag+">, got </"+closingTag+">", xml);

        if((xml.html || xml.xhtml) && tag.equals("pre"))
            xml.popPreserveWhitespaces();

        doctypeAllowed = false;
        return node;
    }

    // TODO: Parse strings and comments properly for js and css

    private Text parseScriptContent() {
        StringBuilder str = new StringBuilder();
        do {
            while(!xml.startsWith("</"))
                str.append(xml.read());
            String closingTag = xml.peekClosingTag();
            if(((xml.html || xml.tryFixErrors) && closingTag.equalsIgnoreCase("script")) ||
               (!(xml.html || xml.tryFixErrors) && closingTag.equals("script"))) break;
            str.append(xml.read()).append(xml.read());
        } while(true);
        xml.skipClosingTag("script");
        doctypeAllowed = false;
        String code = xml.trimWhitespaces() ? str.toString().strip() : str.toString();
        return code.isEmpty() ? null : new Text(code);
    }

    private Text parseCSSContent() {
        StringBuilder str = new StringBuilder();
        do {
            while(!xml.startsWith("</"))
                str.append(xml.read());
            String closingTag = xml.peekClosingTag();
            if(((xml.html || xml.tryFixErrors) && closingTag.equalsIgnoreCase("style")) ||
               (!(xml.html || xml.tryFixErrors) && closingTag.equals("style"))) break;
            str.append(xml.read()).append(xml.read());
        } while(true);
        xml.skipClosingTag("style");
        doctypeAllowed = false;
        String css = xml.trimWhitespaces() ? str.toString().strip() : str.toString();
        return css.isEmpty() ? null : new Text(css);
    }

    private Text parseNextText(String prefix) {
        int nextTag = xml.indexOf('<');
        StringBuilder str;
        if(nextTag != -1) {
            str = new StringBuilder(nextTag + prefix.length())
                    .append(xml.read(nextTag))
                    .append(prefix);
        }
        else {
            str = new StringBuilder(prefix);
            while(!xml.isEmpty())
                str.append(xml.read());
        }
        while(xml.trimWhitespaces() && str.length() != 0 && Character.isWhitespace(str.charAt(str.length()-1)))
            str.deleteCharAt(str.length()-1);

        String text = formatText(str.toString());

        if(text.isEmpty()) return null;
        if(doctypeAllowed)
            doctypeAllowed = text.equals("\r") || text.equals("\n") || text.equals("\r\n");
        return new Text(text);
    }

    private String formatText(String str) {
//        System.out.println("Length: " + str.length());
        if(xml.trimWhitespaces()  && ("\n".equals(str) || "\r".equals(str) || "\r\n".equals(str))) return "";
        return XMLEncoder.decode(xml.trimWhitespaces() ? str.replaceAll("\\s+", " ") : str);
    }

    private Comment parseNextComment() {
        xml.skip(2); // --
        int end = xml.indexOf("-->");
        if(end == -1) throw new XMLParseException("Reached end of file during comment", xml);
        if(!xml.allowDoubleDashInComments && xml.indexOf("--") != end)
            throw new XMLParseException("'--' not allowed in XML comments", xml);
        Comment comment = new Comment(xml.read(end));
        xml.skip(3);
        return comment;
    }

    private Doctype parseNextDoctype() {
        if(!doctypeAllowed) throw new XMLParseException("'!DOCTYPE' not allowed here", xml);
        xml.skip(7).skipWhitespace().skipWhitespaces(true); // DOCTYPE_
        Doctype doctype = new Doctype(parseNextKey("root element tag", false));

        if(xml.detectSyntax && !doctype.getRootElement().equalsIgnoreCase("html"))
            xml.xmlDetected();

        firstNode = false;
        if(xml.startsWith('>')) {
            xml.skip();
            return doctype;
        }
        xml.skipWhitespace().skipWhitespaces(true);
        char c = xml.peek();
        if(c == '>') {
            xml.skip();
            return doctype;
        }
        if(c != '[') {
            if(xml.startsWith("SYSTEM"))
                doctype.setLocationType(Doctype.LocationType.SYSTEM);
            else if(xml.startsWith("PUBLIC"))
                doctype.setLocationType(Doctype.LocationType.PUBLIC);
            else throw new XMLParseException("Illegal dtd location type", xml);
            xml.skip(6);
            if(xml.skipIf('>'))
                return doctype;
            c = xml.skipWhitespace().skipWhitespaces(true).peek();
        }
        if(c == '"' || c == '\'') {
            String param1 = parseNextString();
            if(xml.skipIf('>')) {
                doctype.setLocation(param1);
                return doctype;
            }
            c = xml.skipWhitespace().skipWhitespaces(true).peek();
            if(c == '"' || c == '\'') {
                doctype.setName(param1);
                doctype.setLocation(parseNextString());
                if(xml.skipIf('>')) return doctype;
                c = xml.skipWhitespace().skipWhitespaces(true).peek();
            }
        }
        if(c == '[') {
            int end = xml.indexOf(']');
            if(end == -1) throw new XMLParseException("Reached end of file during dtd", xml);
            doctype.setStructure(xml.read(end + 1));
            xml.skipWhitespaces(true);
        }
        xml.skipExpected('>');
        return doctype;
    }

    private Prolog parseNextProlog() {
        xml.skip(); // '?'
        String tag = parseNextKey("tag", xml.tryFixErrors);
        if(tag == null) return null;
        Prolog prolog;
        if("xml".equals(tag)) {
            if(!firstNode) throw new XMLParseException("XML declaration must be the first node", xml);
            prolog = new XMLDeclaration();
        }
        else {
            prolog = new Prolog(tag);
            doctypeAllowed = false;
        }
        firstNode = false;
        parseNextAttributes(prolog);
        xml.skipExpected('?').skipExpected('>');
        return prolog;
    }

    @Contract("_,false->!null")
    private String parseNextKey(String type, boolean allowMissing) {
        StringBuilder key = new StringBuilder();
        int i = 0;
        for(char c = xml.peek(); c!='>' && c!='=' && c!='?' && c!='/' && !Character.isWhitespace(c); c = xml.peek(++i)) {
//        for(char c = xml.peek(); c!='>' && c!='=' && c!='?' && c!='/' && !Character.isWhitespace(c); c = xml.skip().peek())
            if((i == 0 && !isStartNameChar(c)) || (i != 0 && !isNameChar(c))) {
                if(allowMissing) {
                    warn("Unescaped '<'");
                    return null;
                }
                xml.skip(i);
                throw new XMLParseException("Illegal character in "+type+": '"+c+"'", xml);
            }
            key.append(c);
        }
        xml.skip(i);
        if(key.toString().isEmpty()) {
            if(allowMissing) {
                warn("Unescaped '<'");
                return null;
            }
            throw new XMLParseException("[" + type + "]", xml.peekDescription(), xml);
        }
        if(xml.html || (xml.xhtml && xml.tryFixErrors))
            return key.toString().toLowerCase();
        else if(xml.xhtml) {
            String found = key.toString(), lower = found.toLowerCase();
            if(!found.equals(lower))
                throw new XMLParseException("XHTML tags and attribute keys must be in lowercase", xml);
            return found;
        }
        return key.toString();
    }

    private static boolean isStartNameChar(char c) {
        //noinspection UnnecessaryUnicodeEscape
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == ':' || c == '_' || c >= '\u00c0';
    }

    private static boolean isNameChar(char c) {
        //noinspection UnnecessaryUnicodeEscape
        return isStartNameChar(c) || c == '-' || c == '.' || (c >= '0' && c <= '9') || c == '\u00b7';
    }

    private String parseNextString() {
        char start = xml.peek();
        if(start != '"' && start != '\'') {
            if(xml.tryFixErrors) return parseNextLooseString();
            else throw new XMLParseException("\"' or ''", start, xml);
        }
        xml.skip();
        int end = xml.indexOf(start);
        if(end == -1) throw new XMLParseException("Unclosed string literal", xml);
        String str = xml.read(end);
        xml.skip();
        return XMLEncoder.decode(str);
    }

    private String parseNextLooseString() {
        StringBuilder key = new StringBuilder();
        for(char c = xml.peek(); c!='>' && !Character.isWhitespace(c); c = xml.skip().peek())
            key.append(c);
        return XMLEncoder.decode(key.toString());
    }

    private void parseNextAttributes(Node node) {
        while(true) {
            char c = xml.peek();
            if(c != '?' && c != '>' && c != '/') c = xml.skipWhitespace().skipWhitespaces(true).peek();
            if(c == '?' || c == '>' || c == '/') return;

            String key = parseNextKey("attribute key", false);
            c = xml.peekNextNonWhitespace();
            if((xml.tryFixErrors || xml.allowEmptyAttr) && c != '=') {
                if(!xml.allowEmptyAttr)
                    warn("Attribute without value");
                node.attributes.put(key, "");
            }
            else {
                xml.skipWhitespaces(true).skipExpected('=').skipWhitespaces(true);
                node.attributes.put(key, parseNextString());
            }
        }
    }

    private void warn(String msg) {
        warningListener.accept(msg + " (at " + xml.getPosition() + ')');
    }
}
