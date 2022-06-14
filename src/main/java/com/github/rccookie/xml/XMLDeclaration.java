package com.github.rccookie.xml;

/**
 * A xml format declaration node.
 */
public class XMLDeclaration extends Prolog {

    /**
     * Creates a new xml format declaration node.
     */
    public XMLDeclaration() {
        super("xml");
    }

    /**
     * Returns the "version" attribute. May be null.
     *
     * @return The "version" attribute, or null
     */
    public String getVersion() {
        return attributes.get("version");
    }

    /**
     * Returns the "encoding" attribute. May be null.
     *
     * @return The "encoding" attribute, or null
     */
    public String getEncoding() {
        return attributes.get("encoding");
    }

    /**
     * Returns whether the "standalone" attribute is present and equal to "yes".
     *
     * @return Whether the standalone flag is set
     */
    public boolean isStandalone() {
        return "yes".equals(attributes.get("standalone"));
    }
}
