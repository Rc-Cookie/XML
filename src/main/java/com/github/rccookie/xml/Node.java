package com.github.rccookie.xml;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Node implements Iterable<Node> {

    public final String tag;
    public final Map<String, String> attributes;

    public final List<Node> children;
    Node parent = null;


    public Node(String tag) {
        this(Objects.requireNonNull(tag), null, null);
    }

    Node(String tag, Map<String, String> attributes, List<Node> children) {
        this.tag = tag;
        this.attributes = attributes != null ? attributes : new AttributeMap();
        this.children = children != null ? children : new NodeList(this);
    }


    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        toString(str, true);
        return str.toString();
    }

    void toString(StringBuilder str, boolean inner) {
        str.append('<').append(tag);
        attributes.forEach((k,v) -> str.append(' ').append(k).append("=\"").append(v.replace("\"", "&quot;")).append('"'));
        str.append('>');
        if(inner)
            innerXML(str, true);
        else if(!children.isEmpty())
            str.append("...");
        str.append("</").append(tag).append('>');
    }

    public String toInfoString() {
        StringBuilder str = new StringBuilder();
        toString(str, false);
        return str.toString();
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
        innerXML(str, true);
        return str.toString();
    }

    void innerXML(StringBuilder str, boolean inner) {
        for(Node n : children) n.toString(str, inner);
    }

    public void setInnerXML(String xml) {
        children.clear();
        // TODO
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

    @Override
    public Iterator<Node> iterator() {
        return children.iterator();
    }
}
