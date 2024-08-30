package de.rccookie.xml;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public XMLDeclaration(@Nullable String version, @Nullable String encoding, boolean standalone) {
        this();
        setVersion(version);
        setEncoding(encoding);
        setStandalone(standalone);
    }

    @Override
    public @NotNull XMLDeclaration clone() {
        XMLDeclaration copy = new XMLDeclaration();
        copy.attributes.putAll(attributes);
        return copy;
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

    /**
     * Sets the "version" attribute, or removes it if the specified value is <code>null</code>.
     *
     * @param version The version to set, or <code>null</code> to remove the attribute
     */
    public void setVersion(@Nullable String version) {
        if(version != null)
            attributes.put("version", version);
        else attributes.remove("version");
    }

    /**
     * Sets the "encoding" attribute, or removes it if the specified value is <code>null</code>.
     *
     * @param encoding The encoding to set, or <code>null</code> to remove the attribute
     */
    public void setEncoding(@Nullable String encoding) {
        if(encoding != null)
            attributes.put("encoding", encoding);
        else attributes.remove("encoding");
    }

    /**
     * Sets the "standalone" attribute to <code>"yes"</code> or <code>"no"</code>, depending
     * on the specified value.
     *
     * @param standalone Whether to set the standalone attribute to <code>"yes"</code>
     */
    public void setStandalone(boolean standalone) {
        attributes.put("standalone", standalone ? "yes" : "no");
    }

    @Override
    public Document getParent() {
        return (Document) super.getParent();
    }

    @Override
    public void setParent(@Nullable Node parent) {
        if(parent != null && !(parent instanceof Document))
            throw new IllegalArgumentException("Doctype can only be direct child of document");
        if(parent == null && this.parent != null)
            getParent().setXMLDeclaration(null);
        else if(parent != null)
            ((Document) parent).setXMLDeclaration(this);
    }
}
