package edu.caltech.cs2.project05;

import java.io.*;
import java.net.URI;
import java.net.URL;

//from geeks for geeks
//used on TA end to generate + save large test cases.
public class DownloadWebPage {

    public static void download(String page) {
        try {
            URI uri = new URI(page);
            URL url = uri.toURL();
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            BufferedWriter writer = new BufferedWriter((new FileWriter(convertToFileName(page))));

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }

            reader.close();
            writer.close();
        } catch (IllegalArgumentException e) {
            System.out.println("Error: The URL is invalid, try adding https://");
        } catch (IOException e) {
            System.out.println("Error: Unable to download the webpage.");
        } catch (Exception e) {
            System.out.println("Error: Invalid URI syntax.");
        }
    }

    public static String convertToFileName(String str) {
        return str.replace("/", "\u0d9e") + ".txt";
    }
}
