package com.github.rccookie.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import com.github.rccookie.util.Console;

import org.xml.sax.SAXException;

public final class XML {

    public static final long INCLUDE_COMMENTS = 1;
    public static final long PRESERVE_WHITESPACES = 1 << 1;
    public static final long INCLUDE_PROCESSORS = 1 << 2;
    public static final long ALLOW_EMPTY_ATTR = 1 << 3;
    public static final long TRY_FIX_ERRORS = 1 << 4;
    public static final long HTML = 1 << 30 | ALLOW_EMPTY_ATTR | TRY_FIX_ERRORS;

    private XML() {
        throw new UnsupportedOperationException();
    }


    public static Document parse(String xml) {
        return parse(xml, 0);
    }

    public static Document parse(String xml, long options) {
        return getParser(xml, options).parseAll();
    }

    public static Document parseFile(String file) {
        return parse(file, 0);
    }

    public static Document parseFile(String file, long options) {
        return getFileParser(file, options).parseAll();
    }

    public static Document parseURL(String url) {
        return parseURL(url, 0);
    }

    public static Document parseURL(String url, long options) {
        return getURLParser(url, options).parseAll();
    }

    public static Document parse(File file) {
        return parse(file, 0);
    }

    public static Document parse(File file, long options) {
        return getParser(file, options).parseAll();
    }

    public static Document parse(InputStream in) {
        return parse(in, 0);
    }

    public static Document parse(InputStream in, long options) {
        return getParser(in, options).parseAll();
    }

    public static Document parse(Reader reader) {
        return parse(reader, 0);
    }

    public static Document parse(Reader reader, long options) {
        return getParser(reader, options).parseAll();
    }

    // ----------------------------------------------

    public static XMLParser getParser(String xml) {
        return getParser(xml, 0);
    }

    public static XMLParser getParser(String xml, long options) {
        return getParser(new StringReader(xml), options);
    }

    public static XMLParser getFileParser(String file) {
        return getFileParser(file, 0);
    }

    public static XMLParser getFileParser(String file, long options) {
        return getParser(new File(file), options);
    }

    public static XMLParser getURLParser(String url) {
        return getURLParser(url, 0);
    }

    public static XMLParser getURLParser(String url, long options) {
        try {
            return getParser(new URL(url).openStream(), options);
        } catch(MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static XMLParser getParser(File file) {
        return getParser(file, 0);
    }

    public static XMLParser getParser(File file, long options) {
        try {
            return getParser(new FileReader(file), options);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static XMLParser getParser(InputStream in) {
        return getParser(in, 0);
    }

    public static XMLParser getParser(InputStream in, long options) {
        return getParser(new InputStreamReader(in), options);
    }

    public static XMLParser getParser(Reader reader) {
        return new XMLParser(reader, 0);
    }

    public static XMLParser getParser(Reader reader, long options) {
        return new XMLParser(reader, options);
    }

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        parseFile("test.html", HTML|INCLUDE_COMMENTS).stream().filterType(Comment.class).map(Node::toInfoString).forEach(Console::log);
//        System.out.println(getFileParser("test.html", HTML).setWarningListener(System.err::println).parseAll().toHTML());
//        Stream<String> stream = Stream.of("a", "b", "c");
//        ListStream<String> listStream = ListStream.of(stream);
//        System.out.println(listStream.filter(s -> s != "b"));
//        System.out.println(listStream.first());
//        System.out.println(stream.isParallel());
    }
}
