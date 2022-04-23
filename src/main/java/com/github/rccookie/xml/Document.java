package com.github.rccookie.xml;

import java.util.Collections;

public class Document extends Node {

    private XMLDeclaration xmlDeclaration;
    private Doctype doctype;

    public Document() {
        super(null, Collections.emptyMap(), null);
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
    void toString(StringBuilder str) {
        if(xmlDeclaration != null)
            xmlDeclaration.toString(str);
        if(doctype != null)
            doctype.toString(str);
        innerXML(str);
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
