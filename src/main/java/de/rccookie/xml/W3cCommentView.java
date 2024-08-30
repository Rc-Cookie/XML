package de.rccookie.xml;

import de.rccookie.util.ViewModificationException;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;

final class W3cCommentView extends AbstractW3cNodeView<Comment> implements org.w3c.dom.Comment {

    W3cCommentView(Comment node) {
        super(node);
    }

    @Override
    public String getData() throws DOMException {
        return node.getComment();
    }

    @Override
    public void setData(String data) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public int getLength() {
        return getData().length();
    }

    @Override
    public String substringData(int offset, int count) throws DOMException {
        return getData().substring(offset, Math.min(offset + count, getData().length()));
    }

    @Override
    public void appendData(String arg) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public void insertData(int offset, String arg) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public void deleteData(int offset, int count) throws DOMException {
        throw new ViewModificationException();
    }

    @Override
    public void replaceData(int offset, int count, String arg) throws DOMException {
        throw new ViewModificationException();
    }

    @NotNull
    @Override
    public String getNodeName() {
        return "#comment";
    }

    @Override
    public String getNodeValue() throws DOMException {
        return getData();
    }

    @Override
    public short getNodeType() {
        return COMMENT_NODE;
    }

    @Override
    public NamedNodeMap getAttributes() {
        return null;
    }

    @Override
    public String getPrefix() {
        return null;
    }

    @Override
    public String getLocalName() {
        return null;
    }
}
