package com.github.rccookie.xml;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.IterableIterator;
import com.github.rccookie.util.ListStream;

public class Node implements Iterable<Node> {

    public final String tag;
    public final Map<String, String> attributes;

    public final List<Node> children;
    Node parent = null;


    public Node(String tag) {
        this(Arguments.checkNull(tag), null, null);
    }

    Node(String tag, AttributeMap attributes, List<Node> children) {
        this.tag = tag;
        this.attributes = attributes != null ? attributes : new AttributeMap();
        this.children = children != null ? children : new NodeList(this);
    }


    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        toString(str, Integer.MIN_VALUE, false, true);
        return str.toString();
    }

    public String toFormattedString() {
        StringBuilder str = new StringBuilder();
        toString(str, 0, false, true);
        return str.toString();
    }

    public String toHTML() {
        return toHTML(true);
    }

    public String toHTML(boolean formatted) {
        StringBuilder str = new StringBuilder();
        toString(str, formatted ? 0 : Integer.MIN_VALUE, true, true);
        return str.toString();
    }

    public String toInfoString() {
        StringBuilder str = new StringBuilder();
        toString(str, Integer.MIN_VALUE, false, false);
        return str.toString();
    }

    void toString(StringBuilder str, int indent, boolean html, boolean inner) {

        str.append('<').append(tag);
        ((AttributeMap) attributes).toString(str);

        if(children.isEmpty()) {
            if(!html) {
                str.append("/>");
                return;
            }
            else if(XMLParser.HTML_VOID_TAGS.contains(tag)) {
                str.append('>');
                return;
            }
        }

        str.append('>');
        boolean newline = indent >= 0 && ((children.size() == 1 && (!(children.get(0) instanceof Text) || (children.get(0).getText().contains("\n")))) || children.size() > 1);
        if(newline) str.append('\n').append("  ".repeat(indent+1));

        if(inner)
            innerXML(str, indent + 1, html, true);
        else str.append("...");

        if(newline)
            str.append('\n').append("  ".repeat(indent));
        str.append("</").append(tag).append('>');
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        if(parent != null)
            parent.children.add(this);
        else if(this.parent != null)
            this.parent.children.remove(this);
    }

    public void removeBlankText() {
        removeBlankText0();
    }

    boolean removeBlankText0() {
        for(int i=0; i<children.size(); i++)
            if(children.get(i).removeBlankText0()) i--;
        return false;
    }

    public void removeComments() {
        for(int i=0; i<children.size(); i++) {
            Node c = children.get(i);
            if(c instanceof Comment) children.remove(i--);
            else c.removeComments();
        }
    }

    public String innerXML() {
        StringBuilder str = new StringBuilder();
        innerXML(str, Integer.MIN_VALUE, false, true);
        return str.toString();
    }

    public String innerHTML() {
        StringBuilder str = new StringBuilder();
        innerXML(str, 0, true, true);
        return str.toString();
    }

    void innerXML(StringBuilder str, int indent, boolean html, boolean inner) {
        if(children.isEmpty()) return;

        for(int i=0; i<children.size(); i++) {
            if(i != 0 && indent >= 0) str.append('\n').append("  ".repeat(indent));
            children.get(i).toString(str, indent, html, inner);
        }
    }

    public void setInnerXML(String xml) {
        children.clear();
        for(Node node : XML.getParser(xml))
            children.add(node);
    }

    public String getText() {
        StringBuilder str = new StringBuilder();
        getText(str);
        return str.toString();
    }

    void getText(StringBuilder str) {
        for(Node c : children) c.getText(str);
    }

    public Node children(int index) {
        return children.get(index);
    }

    public String attribute(String name) {
        return attributes.get(name);
    }

    public ListStream<Node> stream() {
        return ListStream.of(iterator());
    }

    public ListStream<Node> elements() {
        return stream().skip(1).filter(n -> n.tag != null);
    }

    @Override
    public IterableIterator<Node> iterator() {
        return new IterableIterator<Node>() {
            Node next = Node.this;
            boolean nextReady = true;
            int i = -1;
            Iterator<Node> childIt = IterableIterator.empty();

            @Override
            public boolean hasNext() {
                updateNext();
                return next != null;
            }

            @Override
            public Node next() {
                updateNext();
                if(next == null) throw new NoSuchElementException();
                nextReady = false;
                return next;
            }

            void updateNext() {
                if(nextReady) return;
                nextReady = true;
                if(next == null) return;
                if(childIt.hasNext()) next = childIt.next();
                else {
                    while(i+1 < children.size() && !childIt.hasNext())
                        childIt = children(++i).iterator();
                    if(childIt.hasNext()) next = childIt.next();
                    else next = null;
                }
            }
        };
    }

    public Node getElementById(String id) {
        return getElementByAttr("id", id);
    }

    public Node getElementByAttr(String attribute, String value) {
        return getElementsByAttr(attribute, value).findAny().orElse(null);
    }

    public ListStream<Node> getElementsByAttr(String attribute, String value) {
        Arguments.checkNull(attribute);
        Arguments.checkNull(value);
        return elements().filter(n -> value.equals(n.attribute(attribute)));
    }

    public ListStream<Node> getElementsByTag(String name) {
        Arguments.checkNull(tag);
        return elements().filter(t -> tag.equals(t.tag));
    }

    public ListStream<Node> getElementsByClass(String name) {
        String[] classes = name.split("\\s+");
        return elements().filter(n -> {
            String cs = n.attribute("class");
            for(String c : classes)
                if(!cs.contains(c)) return false;
            return true;
        });
    }

    public ListStream<Node> getElementsByName(String name) {
        return getElementsByAttr("name", name);
    }

    public <T extends Node> ListStream<T> getElementsByType(Class<T> type) {
        return elements().filter(type::isInstance).map(type::cast);
    }
}
