package edu.caltech.cs2.project05;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/** An HTMLTag object represents an HTML tag, such as <b> or </table>. */
public class HTMLTag {
    private static final Set<String> SELF_CLOSING_TAGS =
            new HashSet<String>(Arrays.asList(
                    "!doctype", "area", "base", "basefont", "br",
                    "col", "command", "embed", "hr", "img", "input",
                    "keygen", "link", "meta", "param", "wbr", "track", "?xml")
            );


    private record Attribute(String key, String value) {
        public String toString() {
            return key + (value != null ? "=\"" + value + "\"" : "");
        }

    }

    public final String element;
    private final String contents;
    private final HTMLTagType type;
    private final List<Attribute> attributes;
    public static final String INDENT_STRING = "    ";

    /**
     * Constructs an HTML tag with the given element (e.g. "table"), type
     * and content as internal content.
     */
    private HTMLTag(String element, HTMLTagType type, String contents) {
        if (type == HTMLTagType.COMMENT || type == HTMLTagType.CONTENT) {
            this.type = type;
            this.contents = element;
            this.element = null;
            this.attributes = new ArrayList<>();
            return;
        }

        this.contents = null;

        element = element.trim().replace("\r", "");

        Pattern TAG_NAME = Pattern.compile("^(\\S+)");
        Matcher m = TAG_NAME.matcher(element.trim());
        boolean b = m.find();
        this.element = m.group();
        element = m.replaceFirst("").trim();

        if (SELF_CLOSING_TAGS.contains(this.element.toLowerCase())) {
            this.type = HTMLTagType.SELF_CLOSING;
        }
        else {
            this.type = type;
        }

        this.attributes = new ArrayList<>();
        Pattern ATTRIBUTE_PATTERN = Pattern.compile("^(\\S+)\\s*=\"\\s*(([^\"]|\\\")+)\"");
        while (!element.isEmpty()) {
            m = ATTRIBUTE_PATTERN.matcher(element.trim());
            if (m.find()) {
                this.attributes.add(new Attribute(m.group(1), m.group(2)));
            }
            else {
                m = TAG_NAME.matcher(element.trim());
                m.find();
                this.attributes.add(new Attribute(m.group(0), null));
            }

            element = m.replaceFirst("").trim();

        }
    }

    /**
     * Constructs an HTML tag with the given element (e.g. "table") and type.
     */
    public HTMLTag(String element, HTMLTagType type) {
        this(element, type, null);
    }

    public HTMLTag(HTMLTagType type, String contents) {
        this.element = null;
        this.attributes = new ArrayList<>();
        this.type = type;
        this.contents = contents;
    }

    public String getElement() {
        return this.element;
    }

    /**
     * Returns true if this HTML tag is an "opening" (starting) tag.
     * Self-closing tags like <br /> are NOT considered to be "opening" tags.
     */
    public boolean isOpening() {
        return this.type == HTMLTagType.OPENING;
    }

    /**
     * Returns true if this HTML tag is an "closing" (finishing) tag.
     */
    public boolean isClosing() {
        return this.type == HTMLTagType.CLOSING;
    }

    /**
     * Returns true if this tag does not requires a matching closing tag,
     * which is the case for certain elements such as br and img.
     */
    public boolean isSelfClosing() {
        return this.type == HTMLTagType.SELF_CLOSING;
    }

    /**
     * Returns true if this tag is an HTML comment
     */
    public boolean isNotTag() {
        return this.type == HTMLTagType.COMMENT || this.type == HTMLTagType.CONTENT;
    }

    /**
     * Returns true if the given other tag matches this tag;
     * that is, if they have the same element but opposite types,
     * such as <body> and </body>.
     *
     * pre: If other is null, this method will throw a NullPointerException
     */
    public boolean matches(HTMLTag other) {
        boolean thisOpens = this.type == HTMLTagType.OPENING;
        boolean thisCloses = this.type == HTMLTagType.CLOSING;

        boolean otherOpens = other.type == HTMLTagType.OPENING;
        boolean otherCloses = other.type == HTMLTagType.CLOSING;

        return this.matchEquals(other) &&
                ((thisOpens && otherCloses) || (thisCloses && otherOpens));
    }

    /**
     * Returns true if the given other tag equals this tag;
     * that is, if they have the same element and the same types:
     * such as <body> and <body>, or </body> and </body>
     *
     * pre: If other is null, this method will throw a NullPointerException
     */
    public boolean matchEquals(HTMLTag other) {
        return (this.element == null && other.element == null) || (this.element != null && this.element.equalsIgnoreCase(other.element));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        HTMLTag other = (HTMLTag) obj;

        boolean typeEqual = (this.type == other.type);
        boolean elementEqual = (this.element == null && other.element == null) || (this.element != null && this.element.equalsIgnoreCase(other.element));
        boolean contentEqual = (this.contents == null && other.contents == null) || (this.contents != null && this.contents.equalsIgnoreCase(other.contents));
        boolean attributesEqual = (this.attributes.equals(other.attributes));
        return typeEqual && elementEqual && contentEqual && attributesEqual;
    }

    /**
     * Returns a tag that matches this tag, and has the same element.
     *
     * pre: If this tag is a self-closing tag, it matches itself
     */
    public HTMLTag getMatching() {
        if (this.type == HTMLTagType.SELF_CLOSING) {
            return new HTMLTag(this.element, HTMLTagType.SELF_CLOSING);
        } else if (this.type == HTMLTagType.OPENING) {
            return new HTMLTag(this.element, HTMLTagType.CLOSING);
        } else if (this.type == HTMLTagType.CLOSING) {
            return new HTMLTag(this.element, HTMLTagType.OPENING);
        }
        else {
            return null;
        }
    }

    /**
     * Returns a string representation of this HTML tag, such as "</table>".
     */
    public String toString() {
        boolean hasAttributes = this.attributes.size() > 0;
        String attr = (hasAttributes ? " " + String.join(" ", (List<String>)(this.attributes.stream().map(Attribute::toString).collect(Collectors.toList()))) : "");
        String contents = this.contents == null ? "" : this.contents;

        if (this.type == HTMLTagType.COMMENT) {
            return "<!--" + this.contents +  "-->";
        } else if (this.type == HTMLTagType.CONTENT) {
            return this.contents;
        } else if (this.type == HTMLTagType.OPENING) {
            return "<" + this.element + attr + ">";
        } else if (this.type == HTMLTagType.CLOSING) {
            return "</" + this.element + ">" + contents;
        } else if (this.type == HTMLTagType.SELF_CLOSING) {
            return "<" + this.element + attr + "/>";
        } else {
            System.err.println("Invalid Tag Type.");
            System.exit(1);
            return null;
        }
    }

    /**
     * Returns a String representation of the tag, with the given indent
     */
    public String toString(int indent) {
        StringBuilder result = new StringBuilder();
        for (int j = 0; j < indent; j++) {
            result.append(INDENT_STRING);
        }
        result.append(this);
        return result.toString();
    }

    public boolean isContent() {
        return this.type == HTMLTagType.CONTENT;
    }
}
