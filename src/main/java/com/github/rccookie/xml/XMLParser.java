package com.github.rccookie.xml;

import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

import com.github.rccookie.util.Arguments;

public class XMLParser implements Iterator<Node>, Iterable<Node>, AutoCloseable {

    static final Set<String> HTML_VOID_TAGS = Set.of("area", "base", "br", "col", "hr", "img", "input", "link", "meta", "param", "command", "keygen", "source");
    static final Set<String> HTML_POSSIBLY_UNCLOSED_TAGS = Set.of("html", "head", "body", "p", "li", "dt", "dd", "option", "thead", "th", "tbody", "tr", "td", "tfoot", "colgroup");
    static final Set<String> HTML_TAGS = Set.of("a", "abbr", "acronym", "address", "applet", "area", "article", "aside", "audio", "b", "base", "basefont", "bb", "bdo", "big",
            "blockquote", "body", "br", "button", "canvas", "caption", "center", "circle", "cite", "code", "col", "colgroup", "command", "datagrid", "datalist", "dd", "del",
            "details", "dfn", "dialog", "dir", "div", "dl", "dt", "em", "embed", "eventsource", "fieldset", "figcaption", "figure", "font", "footer", "form", "frame", "frameset",
            "h1", "h2", "h3", "h4", "h5", "h6", "head", "header", "hgroup", "hr", "html", "i", "iframe", "img", "input", "ins", "isindex", "kbd", "keygen", "label", "legend", "li",
            "link", "main", "map", "mark", "menu", "meta", "meter", "nav", "noframes", "noscript", "object", "ol", "optgroup", "option", "output", "p", "param", "path", "pre",
            "progress", "q", "rp", "rt", "ruby", "s", "samp", "script", "section", "select", "small", "source", "span", "strike", "strong", "style", "sub", "summary", "sup", "svg",
            "table", "tbody", "td", "template", "textarea", "tfoot", "th", "thead", "time", "title", "tr", "track", "tt", "u", "ul", "var", "video", "wbr");

    private final XMLReader xml;
    private boolean closed = false;
    private boolean firstNode = true;
    private boolean doctypeAllowed = true;

    private final Deque<String> hierarchy = new ArrayDeque<>();

    private Consumer<String> warningListener = w -> {};


    XMLParser(Reader reader, long options) {
        xml = new XMLReader(reader, options);
    }

    public XMLParser setWarningListener(Consumer<String> warningListener) {
        this.warningListener = Arguments.checkNull(warningListener);

        return this;
    }

    @Override
    public void close() {
        closed = true;
        xml.close();
    }

    @Override
    public Iterator<Node> iterator() {
        return this;
    }

    public Document parseAll() {
        if(closed) throw new IllegalStateException("Parser has been closed");
        if(!firstNode) throw new IllegalStateException("Can only parse document as the first parse action");
        Document document = new Document();
        hierarchy.clear();
        while(hasNext()) {
            Node next = parseNextNode();
            if(next instanceof XMLDeclaration)
                document.setXMLDeclaration((XMLDeclaration) next);
            else if(next instanceof Doctype)
                document.setDoctype((Doctype) next);
            else document.children.add(next);
        }
        return document;
    }

    @Override
    public boolean hasNext() {
        return !closed && !xml.skipToContent().isEmpty(); // Check if only remaining is an empty text
    }

    @Override
    public Node next() {
        if(closed) throw new IllegalStateException("Parser has been closed");
        if(!hasNext()) throw new XMLParseException("No value present", xml);
        hierarchy.clear();
        return parseNextNode();
    }

    private Node parseNextNode() {
        if(!xml.startsWith('<')) {
//            System.out.println("Start: " + xml.peekDescription());
            Text text = parseNextText();
            if(text != null) return text;
            return parseNextNode();
        }

        if(xml.skip().skipIf('!')) {
            if(xml.startsWith("--")) return parseNextComment();
            else if(xml.startsWithIgnoreCase("DOCTYPE")) return parseNextDoctype();
            else throw new XMLParseException(doctypeAllowed ? "--' or 'DOCTYPE" : "--", xml.peekDescription(), xml);
        }
        if(xml.startsWith('?')) return parseNextProlog();
        if(xml.tryFixErrors && xml.skipIf('/')) {
            Node node = new Node(parseNextKey("tag"));
            parseNextAttributes(node);
            warn("Closing tag '"+node.tag+"' never opened");
            xml.skipExpected('>');
            return node;
        }

        String tag = parseNextKey("tag");
        Node node = new Node(tag);
        if(xml.html && !HTML_TAGS.contains(tag))
            warn("Unknown html tag '"+tag+"'");

        parseNextAttributes(node);
        if(xml.skipIf('/')) {
            xml.skipExpected('>');
            return node;
        }
        xml.skipExpected('>');
        if(xml.html) {
            if(HTML_VOID_TAGS.contains(tag)) {
                xml.skipIf("</"+tag+'>');
                return node;
            }
            if(tag.equals("script")) {
                Text code = parseScriptContent();
                if(code != null)
                    node.children.add(code);
                return node;
            }
        }

        hierarchy.push(tag);
        while(!xml.skipToContent().isEmpty()) {
            if(!xml.startsWith("</"))
                node.children.add(parseNextNode());
            else if(xml.tryFixErrors && !xml.startsWith("</"+tag+'>')) {
                String nextTag = xml.peekClosingTag();
                if(hierarchy.contains(nextTag) && HTML_POSSIBLY_UNCLOSED_TAGS.contains(tag)) break; // There are probably closing tags missing
                else node.children.add(parseNextNode()); // There are probably too many closing tags
            }
            else break;
        }
        hierarchy.pop();

        if(xml.startsWith("</" + tag + '>'))
            xml.skip(tag.length() + 3);
        else if(xml.tryFixErrors) warn("Unclosed tag '"+tag+"'");
        else throw new XMLParseException("Incorrect closing tag, expected '"+tag+"'", xml);


        doctypeAllowed = false;

        return node;
    }

    private Text parseScriptContent() {
        StringBuilder str = new StringBuilder();
        while(!xml.startsWith("</script>"))
            str.append(xml.read());
        xml.skip(9);
        doctypeAllowed = false;
        String code = xml.trimWhitespaces ? str.toString().strip() : str.toString();
        return code.isEmpty() ? null : new Text(code);
    }

    private Text parseNextText() {
        int nextTag = xml.indexOf('<');
        StringBuilder str;
        if(nextTag != -1) str = new StringBuilder(xml.read(nextTag));
        else {
            str = new StringBuilder();
            while(!xml.isEmpty()) str.append(xml.read());
        }
        while(xml.trimWhitespaces && str.length() != 0 && Character.isWhitespace(str.charAt(str.length()-1)))
            str.deleteCharAt(str.length()-1);

        String text = formatText(str.toString());

        if(text.isEmpty()) return null;
        doctypeAllowed = false;
        return new Text(text);
    }

    private String formatText(String str) {
//        System.out.println("Length: " + str.length());
        if("\n".equals(str) || "\r".equals(str) || "\r\n".equals(str)) return "";
        return XMLEncoder.decode(xml.trimWhitespaces ? str.replaceAll("\\s+", " ") : str);
    }

    private Comment parseNextComment() {
        xml.skip(2); // --
        int end = xml.indexOf("-->");
        if(end == -1) throw new XMLParseException("Reached end of file during comment", xml);
        if(xml.indexOf("--") != end) throw new XMLParseException("'--' not allowed in XML comments", xml);
        Comment comment = new Comment(xml.read(end));
        xml.skip(3);
        return comment;
    }

    private Doctype parseNextDoctype() {
        if(!doctypeAllowed) throw new XMLParseException("'!DOCTYPE' not allowed here", xml);
        xml.skip(7).skipWhitespace().skipWhitespaces(true); // DOCTYPE_
        Doctype doctype = new Doctype(parseNextKey("root element tag"));
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
        if(c == '"') {
            String param1 = parseNextString();
            if(xml.skipIf('>')) {
                doctype.setLocation(param1);
                return doctype;
            }
            c = xml.skipWhitespace().skipWhitespaces(true).peek();
            if(c == '"') {
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
        String tag = parseNextKey("tag");
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

    private String parseNextKey(String type) {
        StringBuilder key = new StringBuilder();
        for(char c = xml.peek(); c!='>' && c!='=' && c!='?' && c!='/' && !Character.isWhitespace(c); c = xml.skip().peek())
            key.append(c);
        if(key.toString().isEmpty())
            throw new XMLParseException("["+type+"]", xml.peekDescription(), xml);
        return key.toString();
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

            String key = parseNextKey("attribute key");
            c = xml.peekNextNonWhitespace();
            if(xml.allowEmptyAttr && c != '=')
                node.attributes.put(key, "");
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
