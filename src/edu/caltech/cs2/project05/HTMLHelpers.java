package edu.caltech.cs2.project05;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public interface HTMLHelpers {
    static Queue<HTMLTag> parseText(String text) {
        Queue<HTMLTag> q = new Queue<>();
        HTMLParser parser = new HTMLParser(text);
        while (parser.hasNext()) {
            HTMLTag next = parser.next();
            if (next == null) break;
            q.enqueue(next);
        }
        return q;
    }

    String SITE = "en.wikipedia.org";

    static String getPageText(String title) {
        try {
            URLConnection u = new URL(
                    "https://" + SITE + "/w/api.php?action=parse&page=" + URLEncoder.encode(title.replace(" ", "_"), StandardCharsets.UTF_8) + "&prop=wikitext&format=xml"
            ).openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(u.getInputStream(), StandardCharsets.UTF_8));
            String xmlPage = in.lines().collect(Collectors.joining("\n"));
            int wikitext = xmlPage.indexOf("<wikitext");
            int pageStart = xmlPage.indexOf(">", wikitext);
            int pageEnd = xmlPage.lastIndexOf("</wikitext");
            if (pageStart == -1 || pageEnd == -1) {
                return null;
            }
            return decode(xmlPage.substring(pageStart + 1, pageEnd));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String decode(String in) {
        in = in.replace("&lt;", "<").replace("&gt;", ">"); // html tags
        in = in.replace("&quot;", "\"");
        in = in.replace("&#039;", "'");
        in = in.replace("&amp;", "&");
        in = in.replace("&nbsp;", " ");
        return in;
    }
}
