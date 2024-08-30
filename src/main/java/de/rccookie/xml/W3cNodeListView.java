package de.rccookie.xml;

import java.util.List;

import de.rccookie.util.Arguments;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class W3cNodeListView implements NodeList {

    static final NodeList EMPTY = new W3cNodeListView(List.of());

    private final List<? extends de.rccookie.xml.Node> list;

    W3cNodeListView(List<? extends de.rccookie.xml.Node> list) {
        this.list = Arguments.checkNull(list, "list");
    }

    @Override
    public String toString() {
        return list.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof W3cNodeListView && list.equals(((W3cNodeListView) obj).list);
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    @Override
    public Node item(int index) {
        return index < list.size() ? list.get(index).asW3cNode() : null;
    }

    @Override
    public int getLength() {
        return list.size();
    }
}
