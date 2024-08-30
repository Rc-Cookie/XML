package de.rccookie.xml;

import java.util.Collections;
import java.util.Objects;

import de.rccookie.json.Json;
import de.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a text block inside an xml tree.
 */
public class Text extends Node {

    static {
        Json.registerDeserializer(Text.class, json -> new Text(json.asString()));
    }

    /**
     * The text.
     */
    @NotNull
    private String text;

    /**
     * Creates a new text node with the given text content.
     *
     * @param text The text content
     */
    public Text(@NotNull String text) {
        super("text", AttributeMap.EMPTY, Collections.emptyList());
        this.text = Arguments.checkNull(text);
    }

    @Override
    public @NotNull Node clone() {
        return new Text(text);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Text && ((Text) o).text.equals(text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }

    /**
     * Sets the text content of this text node.
     *
     * @param text The text to set
     */
    public void setText(@NotNull String text) {
        this.text = Arguments.checkNull(text);
    }

    /**
     * Returns this node's text.
     *
     * @return The text
     */
    @Override
    public @NotNull String text() {
        return text;
    }

    @Override
    void toString(StringBuilder str, FormattingOptions options) {
        boolean parentCode = options.html && parent != null && parent.tag.equals("code");
//        String text = html && !parentCode ? this.text.replaceAll("\\s+", " ") : this.text;
        if(parent != null && parent.tag.equals("script")) {
            if(options.xhtml)
                str.append(text.replace("&", XMLEncoder.encode("&")).replace("<", XMLEncoder.encode("<")));
            else str.append(text.replace("</script>", "</script\\>"));
        }
        else if(!parentCode && options.formatted) str.append(XMLEncoder.encode(text).replace("\n", '\n' + "  ".repeat(options.indent)));
        else XMLEncoder.encode(text, str);
    }

    @Override
    public Object toJson() {
        return text;
    }

    @Override
    boolean removeBlankText0() {
        if(text.isBlank()) {
            setParent(null);
            return true;
        }
        return false;
    }

    @Override
    void innerXML(StringBuilder str, FormattingOptions options) {
        toString(str, options);
    }

    @Override
    public void setInnerXML(@NotNull String xml, long options) {
        setText(XMLEncoder.decode(xml));
    }
}
