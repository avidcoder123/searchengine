package edu.caltech.cs2.project05;

// When it prompts you for a file name, if you type a simple string such 
// as "tests/test1.html" (without the quotes) it will just look on your hard
// disk in the same directory as your code or Eclipse project.
//
// If you type a string such as "http://www.google.com/index.html", it will 
// connect to that URL and download the HTML content from it.

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Scanner;

public class HTMLMain {
    public static void main(String[] args) {
        HTMLManager manager = new HTMLManager(new Queue<>());
        Scanner console = new Scanner(System.in);
        String choice = "s";
        
        while (true) {
            if (choice.startsWith("s")) {
                // prompt for page, then download it if it's a URL
                Queue<HTMLTag> tags = null;
                while (tags == null) {
                    String url = prompt(
                            console,
                            "Page URL or file name (blank for empty): ");
                    tags = loadTags(url);
                }
                manager = new HTMLManager(tags);
            } 
            else if (choice.startsWith("a")) {
                String tagText = prompt(
                        console,
                        "What tag (such as '<table>' or '</p>')? ");
                manager.add(stringToTag(tagText));
            } 
            else if (choice.startsWith("g")) {
                System.out.println(manager.getTags());
            }
            else if (choice.startsWith("f")) {
                manager.fixHTML();
            }
            else if (choice.startsWith("p")) {
                System.out.println(manager);
            }
            else if (choice.startsWith("q")) {
                break;
            }

            System.out.println();
            choice = prompt(
                    console,
                    "(a)dd, (g)etTags, (f)ixHTML, "
                    + "(s)et URL, (p)rint (q)uit? ");
            choice = choice.toLowerCase();
        }
    }

    /**
     * Prints the provided text, then obtains the next line from the given
     * text source.
     */
    public static String prompt(Scanner console, String prompt) {
        System.out.print(prompt);
        return console.nextLine().trim();
    }

    /**
     * Attempts to load tags from the given URL. 
     *
     * Pre:  The url must either be a valid url, or an empty string
     * Post: Returns a Queue of HTMLTags if the url is valid
     *       Returns an empty Queue if the url is an empty string
     *       Returns null and prints an error message otherwise
     */
    public static Queue<HTMLTag> loadTags(String url) {
        if (url.length() > 0) {
            HTMLPage parser = getParser(url);
            if (parser != null) {
                return parser.parse();
            }
            System.err.println("Couldn't resolve input. Try again!");
            return null;
        } 
        return new Queue<>();
    }

    /**
     * Converts a String to an HTMLTag object.
     * 
     * The String is assumed to be a valid HTML tag.
     */
    public static HTMLTag stringToTag(String tagText) {
        boolean isOpen = !tagText.contains("</");
        HTMLTagType type = isOpen ? HTMLTagType.OPENING : HTMLTagType.CLOSING;
        int split = tagText.indexOf(">");
        String element = tagText.substring(isOpen ? 1 : 2, split);
        return new HTMLTag(element, type);
    }
    
    /**
     * Attempts to get a new HTMLPage for the provided address
     * post: returns null if no source found the given address
     */
    public static HTMLPage getParser(String address) {
        HTMLPage result = null;
        try {
            result = new HTMLPage(URI.create(address).toURL());
            System.out.println("Found URL!");
        } catch (MalformedURLException e1) {
            result = new HTMLPage(new File(address));
            System.out.println("Found File!");
        }
        return result;
    }
}
