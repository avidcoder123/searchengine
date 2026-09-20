package edu.caltech.cs2.project05;

// TODO: fill this in with your own HTMLManager!

import java.util.ArrayList;
import java.util.List;

/**
 * HTMLManager represents a single, possibly malformed segment of HTML as
 * a collection of HTMLTags. It provides a variety of methods for viewing and
 * manipulating this HTML.
 */


public class HTMLManager {
    private Queue<HTMLTag> q;

    /**
     * Constructs the HTMLManager given the Queue of HTMLTags passed
     * as a parameter.  If the given Queue is null, throws an
     * IllegalArgumentException.
     */
    public HTMLManager(Queue<HTMLTag> page) {
    }

    /**
     * Adds the given tag to the end of the collection of stored tags. If the
     * given tag is null, throws an IllegalArgumentException.
     */
    public void add(HTMLTag tag) {
        if (tag == null) throw new IllegalArgumentException("tag must be non-null");
    }

    /**
     * Returns a List of HTMLTags representing the current collection
     * of tags.
     */
    public List<HTMLTag> getTags() {
       return null;
    }

    /**
     * Returns a string version of the HTML page which is
     * indented by two spaces every time an open tag is
     * seen and unindenting by two spaces for every
     * closing tag.
     **/
    public String toString() {
        return null;
    }

    /**
     * Fixes the current collection of HTMLTags to be valid HTML. When
     * an unexpected closing tag is found, the method will insert closing
     * tags for all unexpectedly closed tags at that point. The best use case
     * is for HTML where the author forgot to close their tags.
     */
    public void fixHTML() {
    }
}
