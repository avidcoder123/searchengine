package edu.caltech.cs2.project05;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler {
    static private final Pattern HREF = Pattern.compile("href=\"[^\"]*\"");
    static private final Pattern HTTPS = Pattern.compile("(https:)?//[^/]+");
    static private final Pattern LAST_SLASH = Pattern.compile(".*/");
    static private final Map<String, String> availableFiles = getFilesList();
    static private final int HREF_LEN = 6;
    static private final String HTTPS_STRING = "https:";


    /** Given a starting link, visits all webpages of distance at most (maxDepth - 1) away,
     * storing each link as a key in a JSONObject, with its value being a JSONObject
     * containing that page's textual content, and another JSONObject containing a map
     * between each link we can go to from that link, and how many times that link occurs.
     */
    public static JSONObject crawl(String link, int maxDepth) throws FileNotFoundException {
        JSONObject links = new JSONObject();
        crawlPage(link, maxDepth, links);
        return links;
    }

    private static void crawlPage(String link, int maxDepth, JSONObject links) throws FileNotFoundException {
        String pageText = getPageText(fixLinkName(link));
        HTMLParser parser = new HTMLParser(pageText);
        HTMLManager manager = new HTMLManager(new Queue<>());
        for (HTMLTag tag = parser.next(); tag != null; tag = parser.next()) {
            manager.add(tag);
        }

        StringJoiner content = new StringJoiner(" ");

        String prevTag = "";
        Map<String, Integer> linkCounts = new HashMap<>();

        for (HTMLTag tag : manager.getTags()) {
            if (tag.isContent() && !prevTag.equals("script") && !prevTag.equals("style")) {
                content.add(tag.toString());
                prevTag = "";
            } else if (tag.isOpening()) {
                prevTag = tag.element;
                Matcher outLinkMatch = HREF.matcher(tag.toString());

                if (tag.element.equals("a") && outLinkMatch.find()) {
                    String outLink = outLinkMatch.group(0);
                    outLink = outLink.substring(HREF_LEN, outLink.length() - 1);
                    if (!ignoreHref(outLink)) {
                        String fullOutLink = "";
                        if (HTTPS.matcher(outLink).find()) {
                            fullOutLink = outLink;
                        } else if (outLink.startsWith("/")) {
                            Matcher m = HTTPS.matcher(link);
                            if (m.find()) {
                                fullOutLink = m.group(0) + outLink;
                            }
                        } else {
                            Matcher m = LAST_SLASH.matcher(link);
                            if (m.find()) {
                                fullOutLink = m.group(0) + outLink;
                            }
                        }
                        String fixedFullOutLink = fixLinkName(fullOutLink);
                        if (fixedFullOutLink != null && !fullOutLink.equals(link)) {
                            linkCounts.put(fullOutLink, linkCounts.getOrDefault(fullOutLink, 0) + 1);
                        }
                    }
                }
            } else if (tag.isClosing()) {
                prevTag = "";
            }
        }

        JSONObject linkCountsJson = new JSONObject();
        for (Map.Entry<String, Integer> entry : linkCounts.entrySet()) {
            linkCountsJson.put(entry.getKey(), new JSONIntegerValue(entry.getValue()));
        }

        JSONObject entry = new JSONObject();
        entry.put("content", new JSONStringValue(content.toString()));
        entry.put("links", linkCountsJson);
        links.put(link, entry);

        if (maxDepth > 1) {
            for (String l : linkCounts.keySet()) {
                if (!links.containsKey(l)) {
                    crawlPage(l, maxDepth - 1, links);
                }
            }
        }
    }

    /**
     * This is used for the tests and should not be edited.
     */
    public static String getPageText(String page) throws FileNotFoundException {
        File f = new File("./tests/largeData/" + convertToFileName(page));
        Scanner s = new Scanner(f);
        StringBuilder contents = new StringBuilder();
        while (s.hasNextLine()) {
            contents.append(s.nextLine());
        }
        s.close();
        return contents.toString();
    }

    /**
     * This is used for the tests and should not be edited.
     */
    public static boolean ignoreHref(String input) {
        String[] ignore = {"mailto:", "tel:", "sms:", "#", "javascript:"};
        boolean toReturn = false;
        for (String s : ignore) {
            toReturn = toReturn || input.startsWith(s);
        }
        return toReturn;
    }

    //Get the link with case insensitivity.
    public static String fixLinkName(String link) {
        if (!ignoreLink(link)) {
            return link;
        } else {
            return availableFiles.getOrDefault(link.toLowerCase(), null);
        }
    }

    private static Map<String, String> getFilesList() {
        File[] files = (new File("./tests/largeData")).listFiles();
        if (files == null) {
            throw new RuntimeException("Directory of webpages does not exist.");
        }
        //Map lowercased filenames to the corresponding file.
        Map<String, String> names = new HashMap<>();
        for (File file : files) {
            names.put(convertToLink(file.getName().toLowerCase()), convertToLink(file.getName()));
        }

        return names;
    }

    /**
     * This is used for the tests and should not be edited.
     */
    public static boolean ignoreLink(String link) {
        File f = new File("./tests/largeData/" + convertToFileName(link));
        return !f.exists();
    }

    private static String convertToLink(String str) {
        return str
                .replace("https@", "https:")
                .replace("http@", "http:")
                .replace("@", "/")
                .substring(0, str.length() - 5); //Cut off the .html
    }

    /**
     * This is used for the tests and should not be edited.
     */
    public static String convertToFileName(String str) {
        return str.replace("/", "@").replace(":", "@") + ".html";
    }

    /**
     * This is used for the tests and should not be edited.
     */
    public static void main(String[] args) {
        File file = new File("data/CProgramming2Output.txt");
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            String output = crawl("https://simple.wikipedia.org/wiki/C_(programming_language)", 2).dump();
            writer.write(output);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
