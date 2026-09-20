package edu.caltech.cs2.project05;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/** 
 * Parses a File, String, or URL into a List<HTMLTag>
 */
public class HTMLPage {
    public String unparsedPage;

    /** 
     * Parses the given input stream from the source with the given name 
     */
    private void parseStream(String name, InputStream stream) {
        try {
            /* Read the HTML */
            Reader in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuilder response = new StringBuilder();
            int c = in.read();
            while (c >= 0) {
                response.append((char)c);
                c = in.read();
            }
            this.unparsedPage = response.toString();
        } catch (IOException e) {
            System.err.println("The " + name + " is invalid.");
            System.exit(1);
        }

    }

    /** 
     * Creates a page based off the HTML at the given source URL 
     */
    public HTMLPage(URL url) {
        try {
            /* Create the GET request. */
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            parseStream("URL '" + url.toString() + "'", conn.getInputStream());
        } catch (IOException e) {
            System.err.println("The URL " + url.toString() + " is invalid.");
            System.exit(1);
        }
    }

    /**
     * Creates a page based off the given source File 
     */
    public HTMLPage(File file) {
        String filename = file.toString();
        try {
            parseStream("file " + filename  + "'", new FileInputStream(file));
        } catch (FileNotFoundException ee) {
            System.err.println("The file '" + filename + "' is invalid.");
            System.exit(1);
        }

    }

    /**
     * Creates a page based off the given source String 
     */ 
    public HTMLPage(String str) {
        this.unparsedPage = str;
    }

    /** 
     * Parses the source String and returns the List of HTMLTags 
     */
    public Queue<HTMLTag> parse() {
        Queue<HTMLTag> parsed = new Queue<>();
        HTMLParser parser = new HTMLParser(this.unparsedPage);
        while (parser.hasNext()) {
            parsed.enqueue(parser.next());
        }
        return parsed;
    } 
}
