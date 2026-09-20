package edu.caltech.cs2.project05;

// TODO: fill this in with your own HTMLParser!

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a string of tags into HTMLTags. Iterates
 * over the given source String test
 */
public class HTMLParser implements Iterator<HTMLTag> {
    private static final Pattern TAG_PATTERN = Pattern.compile("<\\s*(?<closing>/)?\\s*(?<tagData>[^>]*[^/> ])\\s*(?<selfclosing>/)?\\s*>");
    private static final Pattern COMMENT_PATTERN = Pattern.compile("<!--(?<content>.*?)-->", Pattern.DOTALL);
    private static final Pattern CLOSING_TAG_PATTERN = Pattern.compile("<\\s*/\\s*(?<tagData>[^>/]+)\\s*>");
    private static final Pattern CLOSING_SCRIPT_PATTERN = Pattern.compile("<\\s*/\\s*script\\s*>");
    private String page;
    private String prevTag;


    /**
     * Creates an HTMLParser based off the given source String
     */
    public HTMLParser(String page) {
        this.page = page.trim();
        this.prevTag = "";
    }

    /**
     * Parses and returns a "normal" tag (any opening, closing, or self-closing
     * tag that is not a comment or content) based on the contents at the
     * beginning of the current page.
     *
     * In all cases, this method updates the field prevTag.  If the tag
     * being parsed is NOT an opening tag, it sets prevTag to an empty
     * String.  Otherwise, it gets the element (tag.getElement()) and
     * sets prevTag to that element name.
     **/
    public HTMLTag findNormalTag() {
        return null;
    }

    /**
     * Parses and returns a comment tag based on the contents
     * at the beginning of the current page. Always sets prevTag
     * to an empty String
     **/
    public HTMLTag findCommentTag() {
        return null;
    }

    /**
     * Parses and returns "content" (that is, the non-tag text
     * inside a innermost opening tag, that is before the corresponding
     * closing tag.
     *
     * To do this, this method:
     * 1) Special cases when the prevTag is a script tag and skips
     *    to the very next closing script tag.
     * 2) Otherwise, finds the index of the very next tag
     *    which does not have to match any other tag.
     * 3) Sets prevTag to an empty String.
     * 4) Returns a new content tag with the contents from the
     *    beginning of the remainder of the page all the way
     *    to the index found in (1) or (2).
     **/
    public HTMLTag findContent() {
        return null;
    }

    /**
     * Returns the next HTMLTag in the source String
     */
    public HTMLTag next() {
        this.page = this.page.trim();
        if (this.page.startsWith("<!--")) {
            return findCommentTag();
        }
        else if (this.page.startsWith("<") && !this.prevTag.equals("script")) {
            return findNormalTag();
        }
        else {
            return findContent();
        }
    }

    /**
     * Returns true if there is another HTMLTag in the source String
     * returns false otherwise.
     */
    public boolean hasNext() {
        return !this.page.isEmpty();
    }

    /**
     * Throws UnsupportedOperationException
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
