package de.rccookie.xml;

import de.rccookie.util.Arguments;
import de.rccookie.util.ViewModificationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

final class W3cNodeMapView implements NamedNodeMap {

    public static final NamedNodeMap EMPTY = new NamedNodeMap() {

        @Override
        public String toString() {
            return "{}";
        }

        @Override
        public Node getNamedItem(String name) {
            return null;
        }

        @Override
        public Node setNamedItem(Node arg) throws DOMException {
            throw new ViewModificationException();
        }

        @Override
        public Node removeNamedItem(String name) throws DOMException {
            throw new ViewModificationException();
        }

        @Override
        public Node item(int index) {
            if(index < 0) throw new IndexOutOfBoundsException(index);
            return null;
        }

        @Override
        public int getLength() {
            return 0;
        }

        @Override
        public Node getNamedItemNS(String namespaceURI, String localName) throws DOMException {
            return null;
        }

        @Override
        public Node setNamedItemNS(Node arg) throws DOMException {
            throw new ViewModificationException();
        }

        @Override
        public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
            throw new ViewModificationException();
        }
    };

    private final de.rccookie.xml.Node node;

    W3cNodeMapView(de.rccookie.xml.Node node) {
        this.node = Arguments.checkNull(node, "node");
    }

    @Override
    public String toString() {
        return node.attributes.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof W3cNodeMapView && node.attributes.equals(((W3cNodeMapView) obj).node.attributes);
    }

    @Override
    public int hashCode() {
        return node.attributes.hashCode();
    }

    @Override
    public Node getNamedItem(String name) {
        return new W3cAttrView(node, name);
    }

    @Override
    public Node setNamedItem(Node arg) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public Node removeNamedItem(String name) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public Node item(int index) {
        return node.attributes.keySet().stream().skip(index).findFirst().map(n -> new W3cAttrView(node, n)).orElse(null);
    }

    @Override
    public int getLength() {
        return node.attributes.size();
    }

    @Override
    public Node getNamedItemNS(String namespaceURI, String localName) throws DOMException {
        return getNamedItem(localName);
    }

    @Override
    public Node setNamedItemNS(Node arg) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
        throw new ViewModificationException();
    }
}
