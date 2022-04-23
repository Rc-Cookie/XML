package com.github.rccookie.xml;

public class XMLDeclaration extends Prolog {

    public XMLDeclaration() {
        super("xml");
    }

    public String getVersion() {
        return attributes.get("version");
    }

    public String getEncoding() {
        return attributes.get("encoding");
    }

    public boolean isStandalone() {
        return "yes".equals(attributes.get("standalone"));
    }
}
