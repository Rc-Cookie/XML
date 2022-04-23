package com.github.rccookie.xml;

import java.io.Reader;
import java.util.Iterator;

public class XMLParser implements Iterator<Node>, Iterable<Node>, AutoCloseable {

    public static final long INCLUDE_COMMENTS = 1;
    public static final long PRESERVE_WHITESPACES = 1 << 1;
    public static final long INCLUDE_PROCESSORS = 1 << 2;

    private final XMLReader xml;
    private boolean closed = false;
    private boolean firstNode = true;
    private boolean doctypeAllowed = true;


    XMLParser(Reader reader, long options) {
        xml = new XMLReader(reader, options);
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
        return parseNextNode();
    }

    private Node parseNextNode() {
        if(!xml.startsWith('<')) {
            System.out.println("Start: " + xml.peekDescription());
            Text text = parseNextText();
            if(text != null) return text;
            return parseNextNode();
        }

        if(xml.skip().skipIf('!')) {
            if(xml.startsWith("--")) return parseNextComment();
            else if(xml.startsWith("DOCTYPE")) return parseNextDoctype();
            else throw new XMLParseException(doctypeAllowed ? "--' or 'DOCTYPE" : "--", xml.peekDescription(), xml);
        }
        if(xml.startsWith('?')) return parseNextProlog();

        String tag = parseNextKey("tag");
        Node node = new Node(tag);
        parseNextAttributes(node);
        if(xml.skipIf('/')) {
            xml.skipExpected('>');
            return node;
        }
        xml.skipExpected('>');
        while(!xml.skipToContent().startsWith("</"))
            node.children.add(parseNextNode());
        if(!xml.skip(2).startsWith(tag))
            throw new XMLParseException("Incorrect closing tag, expected '"+tag+"'", xml);
        xml.skip(tag.length()).skipExpected('>');

        doctypeAllowed = false;

        return node;
    }

    private Text parseNextText() {
        int nextTag = xml.indexOf('<');
        StringBuilder str;
        if(nextTag != -1) str = new StringBuilder(xml.read(nextTag));
        else {
            str = new StringBuilder();
            while(!xml.isEmpty()) str.append(xml.read());
        }
        while(xml.trimWhitespaces && Character.isWhitespace(str.charAt(str.length()-1)))
            str.deleteCharAt(str.length()-1);

        String text = formatText(str.toString());
        System.out.println("Str:  '" + str + "'");
        System.out.println("Text: '" + text + "'");
        if(text.isEmpty()) return null;
        doctypeAllowed = false;
        return new Text(text);
    }

    private String formatText(String str) {
        System.out.println("Length: " + str.length());
        if("\n".equals(str) || "\r".equals(str) || "\r\n".equals(str)) return "";
        return (xml.trimWhitespaces ? str.replaceAll("\\s+", " ") : str)
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&");
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
            throw new XMLParseException("<"+type+">", xml.peekDescription(), xml);
        return key.toString();
    }

    private String parseNextString() {
        xml.skipExpected('"');
        int end = xml.indexOf('"');
        if(end == -1) throw new XMLParseException("Unclosed string literal", xml);
        String str = xml.read(end);
        xml.skip();
        return str;
    }

    private void parseNextAttributes(Node node) {
        while(true) {
            char c = xml.peek();
            if(c != '?' && c != '>' && c != '/') c = xml.skipWhitespace().skipWhitespaces(true).peek();
            if(c == '?' || c == '>' || c == '/') return;

            String key = parseNextKey("attribute key");
            xml.skipWhitespaces(true).skipExpected('=').skipWhitespaces(true);
            node.attributes.put(key, parseNextString());
        }
    }


//    public static void main(String[] args) throws Exception {
//        System.out.println(new XMLParser(new FileReader("C:\\Users\\Leon\\AppData\\Roaming\\JetBrains\\IntelliJIdea2021.3\\scratches\\scratch_1.xml"), INCLUDE_COMMENTS).parseAll());
//    }
}
