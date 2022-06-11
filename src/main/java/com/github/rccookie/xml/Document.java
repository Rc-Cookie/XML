package com.github.rccookie.xml;

public class Document extends Node {

    private XMLDeclaration xmlDeclaration;
    private Doctype doctype;

    public Document() {
        super(null, AttributeMap.EMPTY, null);
    }

    public XMLDeclaration getXmlDeclaration() {
        return xmlDeclaration;
    }

    public Doctype getDoctype() {
        return doctype;
    }

    public void setXMLDeclaration(XMLDeclaration xmlDeclaration) {
        this.xmlDeclaration = xmlDeclaration;
    }

    public void setDoctype(Doctype doctype) {
        this.doctype = doctype;
    }

    @Override
    void toString(StringBuilder str, int indent, boolean html, boolean inner) {
        if(xmlDeclaration != null) {
            xmlDeclaration.toString(str, indent, html, inner);
            if(indent >= 0) str.append('\n');
        }
        if(doctype != null) {
            doctype.toString(str, indent, html, inner);
            if(indent >= 0) str.append('\n');
        }
        innerXML(str, indent, html, inner);
    }

    @Override
    public Node getParent() {
        return null;
    }

    @Override
    public void setParent(Node parent) {
        throw new UnsupportedOperationException();
    }
}
