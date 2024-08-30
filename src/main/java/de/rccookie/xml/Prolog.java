package de.rccookie.xml;

import java.util.Collections;
import java.util.Objects;

import de.rccookie.json.Json;
import de.rccookie.json.JsonObject;
import org.jetbrains.annotations.NotNull;

/**
 * A prolog node. Cannot have children.
 */
public class Prolog extends Node {

    static {
        Json.registerDeserializer(Prolog.class, json -> {
            String tag = json.getString("tag");
            if(tag.startsWith("?"))
                tag = tag.substring(1);
            Prolog p = tag.equals("xml") ? new XMLDeclaration() : new Prolog(tag);
            p.attributes.putAll(json.get("attributes").asMap(String.class));
            return p;
        });
    }

    /**
     * Creates a new prolog node with no attributes set.
     *
     * @param tag The type of prolog
     */
    public Prolog(@NotNull String tag) {
        super(tag, null, Collections.emptyList());
    }


    @Override
    public @NotNull Prolog clone() {
        Prolog copy = new Prolog(tag);
        copy.attributes.putAll(attributes);
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Prolog && ((Prolog) o).attributes.equals(attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributes);
    }

    @Override
    void toString(StringBuilder str, FormattingOptions options) {
        str.append("<?").append(tag);
        attributes.forEach((k,v) -> str.append(' ').append(k).append('=').append('"').append(v.replace("\"", "&quot;")).append('"'));
        str.append("?>");
    }

    @Override
    public Object toJson() {
        JsonObject json = new JsonObject("tag", "?"+tag);
        if(!attributes.isEmpty())
            json.put("attributes", attributes);
        return json;
    }

    @Override
    void innerXML(StringBuilder str, FormattingOptions options) {
        // No inner value
    }

    @Override
    public void setInnerXML(@NotNull String xml, long options) {
        throw new UnsupportedOperationException();
    }
}
